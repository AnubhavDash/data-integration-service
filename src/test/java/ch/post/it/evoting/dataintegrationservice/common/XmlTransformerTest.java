/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ObjectFactory;

@Disabled("Order of attributes get switched, potentially differences in whitespaces")
class XmlTransformerTest {

	private final XmlTransformer xmlTransformer = new XmlTransformer(true, false);

	@Test
	void testToXml() throws Exception {
		toXml("testdata/election-events/Post_E2E_DEV/D1/data-integration-service/configuration-anonymized.xml", XsdConstants.CANTON_CONFIG_XSD);
	}

	public void toXml(final String configName, final String schemeName) throws Exception {

		final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configName);
		final String should = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

		final URL schemaPath = getClass().getResource(schemeName);
		checkState(schemaPath != null, "resource not found");

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		xmlTransformer.toXml(outputStream,
				ObjectFactory.class,
				schemaPath,
				unmarshall(Configuration.class, should),
				null);

		final String result = outputStream.toString(StandardCharsets.UTF_8);
		assertNotNull(result);
		assertEquals(should, result);
	}

	protected Object unmarshall(final Class clazz, final String obj) {
		try {
			return JAXBContext.newInstance(clazz).createUnmarshaller().unmarshal(new ByteArrayInputStream(obj.getBytes(StandardCharsets.UTF_8)));
		} catch (final JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
