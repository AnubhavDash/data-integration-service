/*
 * (c) Copyright 2023 Swiss Post Ltd.
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ObjectFactory;

class XmlTransformerTest {

	@Test
	void testToXml() throws Exception {
		toXml("XmlTransformerTest/unittest-config-5-0.xml", XsdConstants.CANTON_CONFIG_XSD);
	}

	public void toXml(final String configName, final String schemeName) throws Exception {

		final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configName);
		final String should = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

		final URL schemaPath = getClass().getResource(schemeName);
		checkState(schemaPath != null, "resource not found");

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final String formatPropertyName = "formatOutput";
		final String originalValue = System.getProperty(formatPropertyName);
		System.setProperty(formatPropertyName, "false");

		XmlTransformer.toXml(outputStream,
				ObjectFactory.class,
				schemaPath,
				unmarshall(Configuration.class, should),
				null, null);

		System.setProperty(formatPropertyName, originalValue == null ? "" : originalValue);

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