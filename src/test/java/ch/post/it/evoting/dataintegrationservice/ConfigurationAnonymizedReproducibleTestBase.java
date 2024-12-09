/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;

import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.HashFactory;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.hashable.HashableCantonConfigFactory;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

@SpringBootTest(properties = {
		"bypass.checkConsistency.dates=true",
		"formatOutput=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
abstract class ConfigurationAnonymizedReproducibleTestBase {
	private static final Hash hash = HashFactory.createHash();
	private static final String configurationAnonymized = "configuration-anonymized.xml";
	private static final XmlFileRepository<Configuration> xmlFileRepository = new XmlFileRepository<>();
	protected static final String testData = "target/test-classes/testdata/election-events";

	@TempDir
	protected static Path temporaryDirectory;

	@Autowired
	private DataIntegrationService dataIntegrationService;

	protected static void registerPgProperties(final DynamicPropertyRegistry registry, final String datasetName) {
		registry.add("directory.source", () -> String.format("%s/%s/D0", testData, datasetName));
		registry.add("directory.target", temporaryDirectory::toString);
	}

	protected void sameConfigurationAnonymizedTest(final String datasetName) {

		assertDoesNotThrow(() -> dataIntegrationService.run());

		final Configuration regenerated = xmlFileRepository.read(temporaryDirectory.resolve(configurationAnonymized), XsdConstants.CANTON_CONFIG_XSD,
				Configuration.class);

		final Configuration original = load(datasetName);

		// Ignore fileDate field (use original fileDate in re-generated file).
		regenerated.getHeader().setFileDate(original.getHeader().getFileDate());

		assertTrue(sameConfigurationAnonymized(original, regenerated));
	}

	private static boolean sameConfigurationAnonymized(final Configuration expected, final Configuration actual) {
		final Hashable hashableExpected = HashableCantonConfigFactory.fromConfiguration(expected);
		final Hashable hashableActual = HashableCantonConfigFactory.fromConfiguration(actual);

		return Objects.equals(hash.recursiveHash(hashableExpected), hash.recursiveHash(hashableActual));
	}

	private static Configuration load(final String datasetName) {
		return xmlFileRepository.read(Paths.get(testData, datasetName, "D1/data-integration-service", configurationAnonymized), XsdConstants.CANTON_CONFIG_XSD,
				Configuration.class);
	}

}
