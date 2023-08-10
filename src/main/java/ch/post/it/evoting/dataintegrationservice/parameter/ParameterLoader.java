/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import static com.google.common.base.Preconditions.checkState;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.evoting.xmlns.parameter._4.ObjectFactory;
import ch.evoting.xmlns.parameter._4.Parameter;
import ch.evoting.xmlns.parameter._4.PluginObjectFactory;
import ch.galinet.xml.xmlmerge.config.AttributeMergeConfigurer;
import ch.galinet.xml.xmlmerge.config.ConfigurableXmlMerge;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

@Component
public class ParameterLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterLoader.class);

	private static final String DEFAULT_PARAMETER_PATH = "xml/default.param.xml";
	@SuppressWarnings("java:S1075")
	private static final String PARAMETER_SCHEMA_PATH = "/xsd/evoting-parameter-4-7.xsd";

	public Parameter loadParameter(final String fileLocation) {
		LOGGER.debug("Loading parameter : {}", fileLocation);

		try (final InputStream defaultFile = ParameterLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PARAMETER_PATH);
				final InputStream file = new FileInputStream(fileLocation)) {

			checkState(defaultFile != null, "Resource %s not found", DEFAULT_PARAMETER_PATH);

			final URL schemaPath = ParameterLoader.class.getResource(PARAMETER_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource %s not found", PARAMETER_SCHEMA_PATH);

			final InputStream merged = new ConfigurableXmlMerge(new AttributeMergeConfigurer()).merge(new InputStream[] { file, defaultFile });

			final Parameter[] result = new Parameter[1];

			final Stax2Wrapper wrapper = new Stax2Wrapper(merged,
					schemaPath,
					ObjectFactory.class, new PluginObjectFactory());
			wrapper.addElementListener("/parameter", Parameter.class, p -> result[0] = p);

			wrapper.process();

			return result[0];
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load Parameter", e);
		}
	}
}
