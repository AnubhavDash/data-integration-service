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
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.domain.xml.XsdConstants;
import ch.post.it.evoting.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ObjectFactory;

class XmlTransformerTest {

	@Test
	void testToXml() throws Exception {
		toXml("XmlTransformerTest/unittest-config-4-3.xml", XsdConstants.CANTON_CONFIG_XSD);
	}

	public void toXml(String configName, String schemeName) throws Exception {

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configName);
		String should = IOUtils.toString(inputStream, "UTF-8");

		URL schemaPath = getClass().getResource(schemeName);
		checkState(schemaPath != null, "resource not found");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final String formatPropertyName = "formatOutput";
		String originalValue = System.getProperty(formatPropertyName);
		System.setProperty(formatPropertyName, "false");

		XmlTransformer.toXml(outputStream,
				ObjectFactory.class,
				schemaPath,
				unmarshall(Configuration.class, should),
				null, null);

		System.setProperty(formatPropertyName, originalValue == null ? "" : originalValue);

		String result = outputStream.toString("UTF-8");
		assertNotNull(result);
		assertEquals(should, result);
	}

	protected Object unmarshall(Class clazz, String obj) {
		try {
			return JAXBContext.newInstance(clazz).createUnmarshaller().unmarshal(new ByteArrayInputStream(obj.getBytes("UTF-8")));
		} catch (JAXBException | UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
}