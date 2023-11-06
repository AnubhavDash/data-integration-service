/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class MarshallTool {

	private MarshallTool() {
		//Intentionally left blank
	}

	public static <T> String marshall(final T source, final String elementName) {
		try {
			final JAXBContext context = JAXBContext.newInstance(source.getClass());
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();

			final QName name = new QName(source.getClass().getPackage().getAnnotation(jakarta.xml.bind.annotation.XmlSchema.class).namespace(),
					elementName);
			context.createMarshaller().marshal(new JAXBElement<>(name, (Class<T>) source.getClass(), source), bos);

			return bos.toString(StandardCharsets.UTF_8);
		} catch (final JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T unmarshall(final String source, final Class<T> clazz) {
		try {
			final JAXBContext context = JAXBContext.newInstance(clazz);
			final ByteArrayInputStream bis = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));

			final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

			final XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(bis);

			return context.createUnmarshaller().unmarshal(reader, clazz).getValue();
		} catch (final JAXBException | XMLStreamException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
