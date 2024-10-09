/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.evoting.xmlns.parameter._4.BirthYearAuthenticationKeyTypeImpl;
import ch.evoting.xmlns.parameter._4.Parameter;
import ch.galinet.xml.xmlmerge.XmlMerge;
import ch.galinet.xml.xmlmerge.config.AttributeMergeConfigurer;
import ch.galinet.xml.xmlmerge.config.ConfigurableXmlMerge;

class ParameterLoaderTest {

	private final ParameterLoader parameterLoader = new ParameterLoader();

	@Test
	void loadParameter() {
		final Parameter param = parameterLoader.loadParameter("./src/test/resources/ParameterLoaderTest/file.param.xml");
		assertTrue(param.getPlugins().getAfterReadVoter().getPlugins().stream()
				.anyMatch(p -> p.getClass().equals(BirthYearAuthenticationKeyTypeImpl.class)));
	}

	@Test
	void mergeTest() throws Exception {
		final InputStream is1 = this.getClass().getResourceAsStream("/ParameterLoaderTest/mergeTest/file.param.xml");
		final InputStream is2 = this.getClass().getResourceAsStream("/ParameterLoaderTest/mergeTest/default.param.xml");
		final XmlMerge merger = new ConfigurableXmlMerge(new AttributeMergeConfigurer());

		final InputStream out = merger.merge(new InputStream[] { is1, is2 });

		final String result = new String(out.readAllBytes(), StandardCharsets.UTF_8);

		final URL resource = this.getClass().getResource("/ParameterLoaderTest/mergeTest/ParameterLoaderTest_merge.xml");
		final String expected = new String(Files.readAllBytes(Paths.get(resource.toURI())), StandardCharsets.UTF_8);

		assertEquals(expected, result);
	}
}
