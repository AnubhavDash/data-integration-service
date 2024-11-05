/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ch.post.it.evoting.cryptoprimitives.signing.SignatureKeystore;
import ch.post.it.evoting.dataintegrationservice.keystore.KeystoreRepository;
import ch.post.it.evoting.evotinglibraries.direct.trust.SignatureKeystoreFactory;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;

@Configuration
@ComponentScan(basePackages = { "ch.post.it.evoting", "pipes" })
public class SpringConfiguration {

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
		return SignatureKeystoreFactory.createSignatureKeystore(repository.getCantonKeyStore(), repository.getCantonKeystorePassword(),
				repository.getKeystoreAlias());
	}
}
