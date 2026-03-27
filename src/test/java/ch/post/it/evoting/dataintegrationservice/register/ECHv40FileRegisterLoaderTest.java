/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._4.VotingPersonType;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Factory;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Profile;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.StreamableSymmetricEncryptionDecryptionService;

@DisplayName("ECHv40FileRegisterLoader loading")
class ECHv40FileRegisterLoaderTest {

	private static ECHv40FileRegisterLoader ecHv40FileRegisterLoader;

	@BeforeAll
	static void setupAll() {
		final char[] encryptionPassword = "LongPassword_Encryption1".toCharArray();
		final StreamableSymmetricEncryptionDecryptionService streamableSymmetricEncryptionDecryptionService = new StreamableSymmetricEncryptionDecryptionService(
				RandomFactory.createRandom(), Argon2Factory.createArgon2(Argon2Profile.STANDARD));

		ecHv40FileRegisterLoader = new ECHv40FileRegisterLoader(streamableSymmetricEncryptionDecryptionService, ".aes", encryptionPassword);
	}

	@Test
	@DisplayName("an ECH0045v4 file gives expected voters")
	void loadECH0045v4() {
		final Iterable<VotingPersonType> result = ecHv40FileRegisterLoader.loadECH0045v4(
				"./src/test/resources/ECHv4FileRegisterLoaderTest/file1.ech0045v4.one.xml");

		final List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.getFirst().getPerson());
	}

	@Test
	@DisplayName("an encrypted ECH0045v4 file gives expected voters")
	void loadECH0045v4Crypted() {
		final Iterable<VotingPersonType> result = ecHv40FileRegisterLoader.loadECH0045v4(
				"./src/test/resources/ECHv4FileRegisterLoaderTest/file1.ech0045v4.one.xml.aes");

		final List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.getFirst().getPerson());
	}

}
