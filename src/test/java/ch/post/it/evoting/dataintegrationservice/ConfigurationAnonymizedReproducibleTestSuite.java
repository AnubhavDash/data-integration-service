/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ ConfigurationAnonymizedReproducibleCase1Test.class, ConfigurationAnonymizedReproducibleCase2Test.class,
		ConfigurationAnonymizedReproducibleCase3Test.class, ConfigurationAnonymizedReproducibleCase4Test.class, ConfigurationAnonymizedReproducibleCase5Test.class })
public class ConfigurationAnonymizedReproducibleTestSuite {
}
