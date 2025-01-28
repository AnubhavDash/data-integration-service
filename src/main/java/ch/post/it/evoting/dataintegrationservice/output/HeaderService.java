/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.output;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.evotinglibraries.domain.LocalDateTimeUtils;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.HeaderType;

@Service
public class HeaderService {

	public HeaderType build(final Long voterCount) {
		final HeaderType result = new HeaderType();
		result.setFileDate(LocalDateTimeUtils.toXMLGregorianCalendar(LocalDateTimeUtils.now()));
		result.setVoterTotal(voterCount.intValue());
		return result;
	}
}
