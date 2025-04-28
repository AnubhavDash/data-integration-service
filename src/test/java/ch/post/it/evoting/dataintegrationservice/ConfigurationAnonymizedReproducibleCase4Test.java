/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DisplayName("Generating a configuration anonymized file")
class ConfigurationAnonymizedReproducibleCase4Test extends ConfigurationAnonymizedReproducibleTestBase {

	private static final String datasetName = "Post_E2E_DEV_10000";

	@DynamicPropertySource
	static void registerPgProperties(final DynamicPropertyRegistry registry) {
		registerPgProperties(registry, datasetName);
	}

	@Test
	@DisplayName("with dataset " + datasetName + " does not throw and is reproducible.")
	void happyPath() {
		sameConfigurationAnonymizedTest( datasetName);
	}

}
