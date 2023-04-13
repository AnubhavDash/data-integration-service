/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.output;

import java.util.Comparator;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rits.cloning.Cloner;

import ch.post.it.evoting.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.HeaderType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.RegisterTypeWithVoterStream;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterType;

@Service
public class ConfigService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);
	private static final Cloner cloner = new Cloner();

	public Configuration build(final ContestType contest, final AuthorizationsType authorizations, final Stream<VoterType> voters,
			final HeaderType header) {

		final Configuration result = new Configuration();
		result.setHeader(header);
		result.setContest(contest);
		result.setAuthorizations(authorizations);
		result.setRegister(new RegisterTypeWithVoterStream(voters));

		checkAndAdaptIfNeededEVotingFromDate(contest, authorizations);

		return result;
	}

	private void checkAndAdaptIfNeededEVotingFromDate(final ContestType contest, final AuthorizationsType authorizations) {
		final XMLGregorianCalendar firstEvotingFromDate = authorizations.getAuthorization().stream()
				.map(AuthorizationType::getAuthorizationFromDate)
				.min(Comparator.comparing(XMLGregorianCalendar::toGregorianCalendar))
				.orElse(null);
		if (firstEvotingFromDate != null && contest.getEvotingFromDate().compare(firstEvotingFromDate) > 0) {
			LOGGER.info("EvotingFromDate of contest is adapted ({}) to match with test/control ballotboxes", firstEvotingFromDate);
			contest.setEvotingFromDate(firstEvotingFromDate);
		}
	}

	public VoterType anonymize(final VoterType voter) {
		final VoterType result = cloner.deepClone(voter);
		result.setPerson(null);
		return result;
	}
}
