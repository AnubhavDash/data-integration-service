/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import ch.post.it.evoting.dataintegrationservice.DataIntegrationService;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeyType;

@SpringBootTest(properties = {
		"bypass.checkConsistency.dates=true",
		"formatOutput=true"
})
@ActiveProfiles("test")
class ECH0045v40EGovTokenIT {

	private static final String CONFIGURATION_ANONYMIZED = "configuration-anonymized.xml";
	private static final XmlFileRepository<Configuration> xmlFileRepository = new XmlFileRepository<>();

	@TempDir
	protected static Path temporaryDirectory;

	@Autowired
	private DataIntegrationService dataIntegrationService;

	@DynamicPropertySource
	static void registerPgProperties(final DynamicPropertyRegistry registry) {
		registry.add("directory.source", () -> "target/test-classes/eGovTokenTest");
		registry.add("directory.target", temporaryDirectory::toString);
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

}
