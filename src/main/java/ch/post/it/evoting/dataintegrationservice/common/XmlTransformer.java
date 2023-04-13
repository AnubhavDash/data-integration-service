/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;

public class XmlTransformer {

	private XmlTransformer() {
		//Intentionally left blank
	}

	public static void toXml(final OutputStream os, final Class jaxbFactoryClass, final URL xsdSchema, final Object jaxbElement,
			final QName rootElementName,
			final Marshaller.Listener listener) {
		try {
			final XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();

			final XMLStreamWriter2 wrTemp = (XMLStreamWriter2) factory.createXMLStreamWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

			final boolean bypassXsd = PropertyProvider.getPropertyValue("bypass.checkOutputXSDValidation", "false").equalsIgnoreCase("true");
			if (xsdSchema != null && !bypassXsd) {
				final XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
				final XMLValidationSchema sv = sf.createSchema(xsdSchema);
				wrTemp.validateAgainst(sv);
			}

			final boolean indent = "true".equalsIgnoreCase(PropertyProvider.getPropertyValue("formatOutput", "false"));
			final XMLStreamWriter writer = indent ? new IndentingXMLStreamWriter(wrTemp) : wrTemp;

			final Marshaller marshaller = JAXBContext.newInstance(jaxbFactoryClass).createMarshaller();
			marshaller.setListener(listener);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			writer.writeStartDocument("UTF-8", "1.0");

			if (rootElementName == null) {
				marshaller.marshal(jaxbElement, writer);
			} else {
				marshaller.marshal(new JAXBElement(rootElementName, jaxbElement.getClass(), jaxbElement), writer);
			}

			writer.close();
			wrTemp.close();
		} catch (final JAXBException | XMLStreamException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
