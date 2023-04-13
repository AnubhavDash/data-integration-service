/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.output;

import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.HeaderType;

@Service
public class HeaderService {

	public HeaderType build(final Long voterCount) {
		try {
			final HeaderType result = new HeaderType();

			final GregorianCalendar gc = new GregorianCalendar();
			result.setFileDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
			result.setVoterTotal(BigInteger.valueOf(voterCount));
			return result;
		} catch (final DatatypeConfigurationException e) {
			throw new IllegalArgumentException("Unable to build the header", e);
		}
	}
}
