/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StringUtil {

	private StringUtil() {
		//intentionally left blank
	}

	public static String concat(final String s1, final String s2, final String separator) {
		final StringBuilder sb = new StringBuilder();
		final boolean s1Filled = !isNullOrEmpty(s1);
		final boolean s2Filled = !isNullOrEmpty(s2);
		final boolean bothFilled = s1Filled && s2Filled;
		if (s1Filled) {
			sb.append(s1);
		}
		if (bothFilled) {
			sb.append(separator);
		}
		if (s2Filled) {
			sb.append(s2);
		}
		return sb.toString();
	}

	public static boolean isNullOrEmpty(final String value) {
		return value == null || value.isEmpty();
	}

	public static String generateUUIDFromString(final String value) {
		return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)).toString();
	}

	public static boolean isInteger(final String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (final Exception _) {
			return false;
		}
	}
}
