/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Factory;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Profile;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.dataintegrationservice.DataIntegrationService;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.StreamableSymmetricEncryptionDecryptionService;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeyType;

@SpringBootTest(properties = {
		"bypass.checkConsistency.dates=true",
		"formatOutput=true",
		"input.encryption-password=LongPassword_Encryption1"
})
@ActiveProfiles("test")
class ECH0045v40EGovTokenIT {

	private static final String CONFIGURATION_ANONYMIZED = "configuration-anonymized.xml";
	private static final String TEST_FILE_DIRECTORY = "target/test-classes/eGovTokenTest";
	private static final XmlFileRepository<Configuration> xmlFileRepository = new XmlFileRepository<>();

	@TempDir
	protected static Path temporaryDirectory;

	@Autowired
	private DataIntegrationService dataIntegrationService;

	@DynamicPropertySource
	static void registerPgProperties(final DynamicPropertyRegistry registry) {
		registry.add("directory.source", temporaryDirectory::toString);
		registry.add("directory.target", temporaryDirectory::toString);
	}

	@BeforeAll
	static void setupAll() throws IOException {
		encryptEch();
	}

	@Test
	@DisplayName("configuration anonymized contains expected eGovToken fields")
	void happyPath() {
		assertDoesNotThrow(() -> dataIntegrationService.run());

		final Configuration configuration = xmlFileRepository.read(temporaryDirectory.resolve(CONFIGURATION_ANONYMIZED),
				XsdConstants.CANTON_CONFIG_XSD, Configuration.class);

		final ExtendedAuthenticationKeyType extendedAuthenticationKeyType = configuration.getRegister().getVoter().getFirst()
				.getExtendedAuthenticationKeys().getExtendedAuthenticationKey().getFirst();
		final String name = extendedAuthenticationKeyType.getName();
		final String value = extendedAuthenticationKeyType.getValue();

		assertEquals("eGovToken", name);
		assertEquals("00000000000000000000000000000000", value);
	}

	private static void encryptEch() throws IOException {
		final StreamableSymmetricEncryptionDecryptionService streamableSymmetricEncryptionDecryptionService = new StreamableSymmetricEncryptionDecryptionService(
				RandomFactory.createRandom(), Argon2Factory.createArgon2(Argon2Profile.STANDARD));
		final char[] encryptionPassword = "LongPassword_Encryption1".toCharArray();
		try (final FileInputStream fileInputStream = new FileInputStream("%s/ech0045v4-test.xml".formatted(TEST_FILE_DIRECTORY));
				final FileOutputStream fileOutputStream = new FileOutputStream("%s/ech0045v4-test.xml.aes".formatted(temporaryDirectory.toString()))) {
			streamableSymmetricEncryptionDecryptionService.genStreamCiphertext(fileOutputStream, fileInputStream, encryptionPassword, ImmutableByteArray.EMPTY);
		}

		try (final FileInputStream fileInputStream = new FileInputStream("%s/eCH0159v4_votes.xml".formatted(TEST_FILE_DIRECTORY));
				final FileOutputStream fileOutputStream = new FileOutputStream("%s/eCH0159v4_votes.xml".formatted(temporaryDirectory.toString()))) {
			fileInputStream.transferTo(fileOutputStream);
		}

		try (final FileInputStream fileInputStream = new FileInputStream("%s/file.param.xml".formatted(TEST_FILE_DIRECTORY));
				final FileOutputStream fileOutputStream = new FileOutputStream("%s/file.param.xml".formatted(temporaryDirectory.toString()))) {
			fileInputStream.transferTo(fileOutputStream);
		}
	}

}
