/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.evoting.xmlns.parameter._4.BirthYearAuthenticationKeyTypeImpl;
import ch.evoting.xmlns.parameter._4.Parameter;

class ParameterLoaderTest {

	private final ParameterLoader parameterLoader = new ParameterLoader();

	@Test
	void loadParameter() {
		final Parameter param = parameterLoader.loadParameter("./src/test/resources/ParameterLoaderTest/file.param.xml");
		assertTrue(param.getPlugins().getAfterReadVoter().getPlugins().stream()
				.anyMatch(p -> p.getClass().equals(BirthYearAuthenticationKeyTypeImpl.class)));
	}
}
