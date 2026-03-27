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

import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Factory;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Profile;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.StreamableSymmetricEncryptionDecryptionService;

@DisplayName("ECHv42FileRegisterLoader loading")
class ECHv42FileRegisterLoaderTest {

	private static ECHv42FileRegisterLoader ecHv42FileRegisterLoader;

	@BeforeAll
	static void setupAll() {
		final char[] encryptionPassword = "LongPassword_Encryption1".toCharArray();
		final StreamableSymmetricEncryptionDecryptionService streamableSymmetricEncryptionDecryptionService = new StreamableSymmetricEncryptionDecryptionService(
				RandomFactory.createRandom(), Argon2Factory.createArgon2(Argon2Profile.STANDARD));

		ecHv42FileRegisterLoader = new ECHv42FileRegisterLoader(streamableSymmetricEncryptionDecryptionService, ".aes", encryptionPassword);
	}

	@Test
	@DisplayName("an eCH-0045 v4.2 file gives expected voters")
	void loadECH0045v4() {
		final Iterable<VotingPersonType> result = ecHv42FileRegisterLoader.loadECH0045v42(
				"./src/test/resources/ECHv42FileRegisterLoaderTest/file1.ech0045v4_2.one.xml");

		final List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.getFirst().getPerson());
	}

	@Test
	@DisplayName("an encrypted eCH-0045 v4.2 file gives expected voters")
	void loadECH0045v4Crypted() {
		final Iterable<VotingPersonType> result = ecHv42FileRegisterLoader.loadECH0045v42(
				"./src/test/resources/ECHv42FileRegisterLoaderTest/file1.ech0045v4_2.one.xml.aes");

		final List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.getFirst().getPerson());
	}

}
