/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class CustomValidationEventHandler implements ValidationEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomValidationEventHandler.class);

	@Override
	public boolean handleEvent(final ValidationEvent event) {
		checkNotNull(event);

		final String location = getLocation(event);

		switch (event.getSeverity()) {
		case ValidationEvent.WARNING:
			LOGGER.warn("Validation warning. [location: {}, message: {}]", location, event.getMessage());
			break;
		case ValidationEvent.ERROR, ValidationEvent.FATAL_ERROR:
			throw new IllegalStateException(String.format("Validation error. [location: %s, message: %s]", location, event.getMessage()));
		default:
			LOGGER.error("Unknown validation event. [location: {}, message: {}]", location, event.getMessage());
		}

		return true;
	}

	private String getLocation(final ValidationEvent event) {
		final StringBuilder msg = new StringBuilder();

		final ValidationEventLocator locator = event.getLocator();

		if (locator != null) {

			final URL url = locator.getURL();
			final Object obj = locator.getObject();
			final Node node = locator.getNode();
			final int line = locator.getLineNumber();
			final int columnNumber = locator.getColumnNumber();

			if (url != null || line != -1) {
				msg.append("line ").append(line);
				msg.append(", column ").append(columnNumber);
				if (url != null) {
					msg.append(" of ").append(url);
				}
			} else if (obj != null) {
				msg.append(" obj: ").append(obj);
			} else if (node != null) {
				msg.append(" node: ").append(node);
			}
		} else {
			msg.append("Unknown location");
		}

		return msg.toString();
	}

}
