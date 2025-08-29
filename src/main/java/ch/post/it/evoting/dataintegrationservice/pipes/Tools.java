/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean;
import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getProperty;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.namespace.QName;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.common.XmlTransformer;
import ch.post.it.evoting.dataintegrationservice.domain.RegisterTypeWithVoterStream;
import ch.post.it.evoting.dataintegrationservice.input.DirectoryScanner;
import ch.post.it.evoting.dataintegrationservice.keystore.KeystoreRepository;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.XMLSignatureService;
import ch.post.it.evoting.evotinglibraries.toolbox.OutputToInputStreamConverter;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

import li.chee.reactive.plumber.Plumbing;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public abstract class Tools extends Plumbing {

	private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

	static {
		if (System.getProperty("reactor.bufferSize.small") == null) {
			System.setProperty("reactor.bufferSize.small", getBufferSizeProperty());
		}
	}

	public static Predicate<String> fileType(final String type) {
		return (String s) -> DirectoryScanner.filterType(type).test(s);
	}

	public static Consumer<Configuration> saveConfigXML(final String suffixName) {
		final URL xsd = Tools.class.getResource(XsdConstants.CANTON_CONFIG_XSD);
		checkState(xsd != null, "Unable to load XSD file to validate config");
		return o -> saveXML(Configuration.class, null, xsd, String.format("%s.xml", StringUtil.concat("configuration", suffixName, "-"))).accept(o);
	}

	public static Consumer<RegisterTypeWithVoterStream> saveInvalidRegisterXML() {
		return o -> {
			LOGGER.warn("Invalid voters have been generated!");
			saveXML(RegisterTypeWithVoterStream.class, new QName("register"), null, "invalidVoters.xml").accept(o);
		};
	}

	public static <V> Consumer<V> saveXML(final Class<V> jaxbFactoryClass, final QName rootElementName, final URL xsdSchema, final String filename) {
		return o -> {
			final Path path = Path.of(getProperty("directory.target", String.class), filename);
			try (final OutputToInputStreamConverter converter = new OutputToInputStreamConverter();
					final InputStream is = converter.convert(
							os -> getBean(XmlTransformer.class).toXml(os, jaxbFactoryClass, xsdSchema, o, rootElementName));
					final OutputStream fos = Files.newOutputStream(path)) {

				if (jaxbFactoryClass.equals(Configuration.class)) {
					final KeystoreRepository keystoreRepository = getBean(KeystoreRepository.class);
					final InputStream keyStoreStream = keystoreRepository.getCantonKeyStore();
					final char[] keystorePassword = keystoreRepository.getCantonKeystorePassword();
					final Alias keystoreAlias = keystoreRepository.getKeystoreAlias();
					final KeyStore keyStore = getKeyStore(keyStoreStream, keystorePassword);
					final PrivateKey privateKey = (PrivateKey) keyStore.getKey(keystoreAlias.get(), keystorePassword);
					getBean(XMLSignatureService.class).genXMLSignature(is, fos, privateKey);
				} else {
					is.transferTo(fos);
				}
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		};
	}

	public static <T> Function<T, Mono<Boolean>> hasElements(final Flux<?> flux) {
		return t -> flux.hasElements();
	}

	public static Consumer<Throwable> die(final String message) {
		return error -> {
			LOGGER.error("Error : {}", message, error);
			throw new IllegalStateException(message, error);
		};
	}

	public static <T> Function<Flux<T>, Flux<T>> logAsInfo(final String message) {
		LOGGER.info(message);
		return flux -> flux;
	}

	public static <T> Function<Flux<T>, Flux<T>> errorIfEmpty(final String message) {
		return flux -> flux.switchIfEmpty((Publisher<? extends T>) empty().doOnComplete(() -> {
			throw new IllegalStateException(message);
		}));
	}

	public static Consumer<Object> logItemAsInfo(final String message) {
		return o -> LOGGER.info(message, o);
	}

	public static <T> BiFunction<T, List<Function<T, T>>, T> apply() {
		return (item, plugins) -> plugins.stream().reduce(Function.identity(), Function::andThen).apply(item);
	}

	public static <T, U> BiFunction<Tuple2<T, U>, List<BiFunction<T, U, T>>, T> applyTuple() {
		return (item, plugins) -> plugins.stream().reduce(
				(t, u) -> t,
				(f1, f2) -> (t, u) -> f1.apply(f2.apply(t, u), u)).apply(item.getT1(), item.getT2());
	}

	public static <T, U> Function<Tuple2<T, U>, T> unzip() {
		return Tuple2::getT1;
	}

	private static KeyStore getKeyStore(final InputStream keyStoreStream, final char[] password)
			throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		final KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(keyStoreStream, password);
		return keyStore;
	}

	private static String getBufferSizeProperty() {
		LOGGER.debug("Reading application.yaml");
		final URL url = Tools.class.getResource("/application.yaml");
		if (url != null) {
			final Path path;
			try {
				path = Path.of(url.toURI());
			} catch (final URISyntaxException e) {
				throw new RuntimeException("Failed to convert URL to URI for application.yaml", e);
			}
			try {
				final List<String> properties = Files.readAllLines(path);
				final int index = properties.indexOf("reactor:");
				if ("bufferSize:".equals(properties.get(index + 1).trim()) && properties.get(index + 2).trim().startsWith("small:")) {
					final String[] parts = properties.get(index + 2).split(" ");
					final String value = parts[parts.length - 1];
					LOGGER.debug("reactor.bufferSize.small={}", value);
					return value;
				}
			} catch (final IOException e) {
				throw new UncheckedIOException("Could not read provided path: %s".formatted(path), e);
			}
		}
		return "1024";
	}
}
