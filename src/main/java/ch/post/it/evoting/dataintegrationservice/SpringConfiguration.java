/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureKeystore;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureKeystoreFactory;
import ch.post.it.evoting.cryptoprimitives.utils.VerificationResult;
import ch.post.it.evoting.dataintegrationservice.keystore.KeystoreRepository;
import ch.post.it.evoting.evotinglibraries.direct.trust.KeystoreValidator;

@Configuration
@ComponentScan(basePackages = { "ch.post.it.evoting", "pipes" })
@PropertySource("application.properties")
public class SpringConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);

	@Value("${direct.trust.keystore.location.canton}")
	private String keystoreLocationCanton;
	@Value("${direct.trust.keystore.password.location.canton}")
	private String keystorePasswordLocationCanton;

	@Bean
	public KeystoreRepository keystoreRepository() {
		return new KeystoreRepository(keystoreLocationCanton, keystorePasswordLocationCanton);
	}

	@Bean
	public SignatureKeystore<Alias> keystoreServiceCanton(final KeystoreRepository repository) throws
			IOException {
		final Alias canton = Alias.CANTON;
		return SignatureKeystoreFactory.createSignatureKeystore(repository.getCantonKeyStore(), "PKCS12", repository.getCantonKeystorePassword(),
				keystore -> {
					final VerificationResult result = KeystoreValidator.validateKeystore(keystore, canton);
					if (!result.isVerified()) {
						LOGGER.error("Keystore validation failed:");
						result.getErrorMessages().descendingIterator().forEachRemaining(LOGGER::error);
					}
					return result.isVerified();
				}, canton);
	}
}
