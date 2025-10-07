/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

@Component
public class XmlTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlTransformer.class);

	private final boolean formatOutput;
	private final boolean bypassCheckOutputXSDValidation;

	public XmlTransformer(
			@Value("${formatOutput}")
			final boolean formatOutput,
			@Value("${bypass.checkOutputXSDValidation}")
			final boolean bypassCheckOutputXSDValidation) {
		this.formatOutput = formatOutput;
		this.bypassCheckOutputXSDValidation = bypassCheckOutputXSDValidation;
	}

	public <T> void toXml(final OutputStream os, final Class<?> jaxbFactoryClass, final URL xsdSchema, final T jaxbElement,
			final QName rootElementName) {
		try {
			final XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();

			final XMLStreamWriter2 wrTemp = (XMLStreamWriter2) factory.createXMLStreamWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

			if (xsdSchema != null && !bypassCheckOutputXSDValidation) {
				final XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
				final XMLValidationSchema sv = sf.createSchema(xsdSchema);
				wrTemp.validateAgainst(sv);
			} else {
				if (xsdSchema == null) {
					LOGGER.info("No XSD schema provided for output, skipping XSD schema validation.");
				} else {
					LOGGER.warn("Bypassing XSD schema validation for output as per configuration.");
				}
			}

			final XMLStreamWriter writer = formatOutput ? new IndentingXMLStreamWriter(wrTemp) : wrTemp;

			final Marshaller marshaller = JAXBContext.newInstance(jaxbFactoryClass).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			writer.writeStartDocument("UTF-8", "1.0");

			if (rootElementName == null) {
				marshaller.marshal(jaxbElement, writer);
			} else {
				marshaller.marshal(new JAXBElement<T>(rootElementName, (Class<T>) jaxbElement.getClass(), jaxbElement), writer);
			}

			writer.close();
			wrTemp.close();
		} catch (final JAXBException | XMLStreamException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
