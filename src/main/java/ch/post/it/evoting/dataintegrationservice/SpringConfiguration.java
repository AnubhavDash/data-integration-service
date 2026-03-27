/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Factory;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Profile;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.dataintegrationservice.keystore.KeystoreRepository;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.StreamableSymmetricEncryptionDecryptionService;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.XMLSignatureService;

@Configuration
@ComponentScan(basePackages = { "ch.post.it.evoting", "pipes" })
public class SpringConfiguration {

	@Value("${direct-trust.keystore.location}")
	private String keystoreLocationCanton;
	@Value("${direct-trust.password.location}")
	private String keystorePasswordLocationCanton;

	@Bean
	public KeystoreRepository keystoreRepository() {
		return new KeystoreRepository(keystoreLocationCanton, keystorePasswordLocationCanton);
	}

	@Bean
	public XMLSignatureService xmlSignatureService() {
		return new XMLSignatureService();
	}

	@Bean
	public StreamableSymmetricEncryptionDecryptionService streamableSymmetricEncryptionDecryptionService() {
		return new StreamableSymmetricEncryptionDecryptionService(RandomFactory.createRandom(), Argon2Factory.createArgon2(Argon2Profile.STANDARD));
	}
}
