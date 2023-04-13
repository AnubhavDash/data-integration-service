/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public abstract class PhaseSteps {
	protected URI uri(String pipeName) {
		try {
			return getClass().getResource("/pipes/" + pipeName + ".groovy").toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected String marshall(Object obj) {
		StringWriter w = new StringWriter();
		try {
			JAXBContext.newInstance(obj.getClass()).createMarshaller().marshal(obj, w);
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
		return w.toString();
	}
}
