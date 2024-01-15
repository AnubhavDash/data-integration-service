/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {

	private static final DatatypeFactory FACTORY;

	static {
		try {
			//factory take a lot of time to be launched, so defining it statically just once.
			FACTORY = DatatypeFactory.newInstance();
		} catch (final DatatypeConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	private DateUtil() {
		//Intentionally left blank
	}

	public static XMLGregorianCalendar parseToXMLGregorianCalendar(final String xsDate) {
		final String format = "yyyy-MM-dd HH:mm:ss";
		try {
			return toXmlGregorianCalendar(new SimpleDateFormat(format).parse(xsDate));
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static XMLGregorianCalendar toXmlGregorianCalendar(final Date date) {
		if (date == null) {
			return null;
		}
		final GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return FACTORY.newXMLGregorianCalendar(c);
	}
}
