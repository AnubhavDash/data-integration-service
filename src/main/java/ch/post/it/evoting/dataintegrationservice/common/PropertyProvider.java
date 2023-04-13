/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class PropertyProvider {

	static final Properties properties = new Properties();

	static {
		try {
			properties.load(PropertyProvider.class.getClassLoader().getResourceAsStream("application.properties"));
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private PropertyProvider() {
		//Intentionally left blank
	}

	public static String getPropertyValue(final String key, final String defaultValue) {
		return System.getProperty(key, getPropertyValue0(key, defaultValue));
	}

	public static String getPropertyValue(final String key) {
		return getPropertyValue(key, null);
	}

	private static String getPropertyValue0(final String key, final String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
