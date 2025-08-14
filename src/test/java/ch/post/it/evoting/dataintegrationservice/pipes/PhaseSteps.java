/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public abstract class PhaseSteps {
	protected URI uri(final String pipeName) {
		try {
			return getClass().getResource("/pipes/" + pipeName + ".groovy").toURI();
		} catch (final URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected String marshall(final Object obj) {
		final StringWriter w = new StringWriter();
		try {
			JAXBContext.newInstance(obj.getClass()).createMarshaller().marshal(obj, w);
		} catch (final JAXBException e) {
			throw new IllegalArgumentException(e);
		}
		return w.toString();
	}
}
