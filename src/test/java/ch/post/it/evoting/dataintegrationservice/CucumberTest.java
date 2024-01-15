/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.java.Before;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@SuppressWarnings("squid:S2187")
public class CucumberTest {

	@Before
	public void resetRegisterService() {
		DataIntegrationServiceApplication.resetContext();
	}
}
