/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ ConfigurationAnonymizedReproducibleCase1Test.class, ConfigurationAnonymizedReproducibleCase2Test.class,
		ConfigurationAnonymizedReproducibleCase3Test.class })
public class ConfigurationAnonymizedReproducibleTestSuite {
}
