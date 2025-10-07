/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DisplayName("Generating a configuration anonymized file")
class ConfigurationAnonymizedReproducibleCase1Test extends ConfigurationAnonymizedReproducibleTestBase {

	private static final String DATASET_NAME = "Post_E2E_DEV";

	@DynamicPropertySource
	static void registerPgProperties(final DynamicPropertyRegistry registry) {
		registerPgProperties(registry, DATASET_NAME);
	}

	@Test
	@DisplayName("with dataset " + DATASET_NAME + " does not throw and is reproducible.")
	void happyPath() {
		sameConfigurationAnonymizedTest(DATASET_NAME);
	}

}
