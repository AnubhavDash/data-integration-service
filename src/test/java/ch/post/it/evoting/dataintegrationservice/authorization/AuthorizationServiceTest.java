/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.dataintegrationservice.domain.VoterCountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateTimeUtils;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CountingCircleType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.DomainOfInfluenceType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteType;

class AuthorizationServiceTest {

	AuthorizationService authorizationService = new AuthorizationService(900, 1000, false);

	private static XMLGregorianCalendar dateTime(final String dt) {
		return LocalDateTimeUtils.toXMLGregorianCalendar(LocalDateTime.parse(dt));
	}

	private static VoterTypeWithRole createVoter(final String countingCircleId) {
		final VoterDomainOfInfluence doi = new VoterDomainOfInfluence();
		doi.setLocalId("doi-1");
		doi.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.MU);

		final VoterCountingCircle cc = new VoterCountingCircle();
		cc.setId(countingCircleId);
		cc.setName("CC-" + countingCircleId);

		final VoterDomainOfInfluenceInfo doiInfo = new VoterDomainOfInfluenceInfo();
		doiInfo.setDomainOfInfluence(doi);
		doiInfo.setCountingCircle(cc);

		final VoterTypeWithRole voter = new VoterTypeWithRole();
		voter.setDomainOfInfluenceInfos(new ArrayList<>(List.of(doiInfo)));
		voter.withVoterIdentification("voter-1");
		voter.withPerson(new PersonType().withMunicipality(new PersonType.Municipality().withMunicipalityId(1111)));

		return voter;
	}

	private static ContestType createContest(final String fromDate, final String toDate) {
		return new ContestType()
				.withContestIdentification("contest-1")
				.withVoteInformation(new VoteInformationType().withVote(new VoteType().withDomainOfInfluence("doi-1")))
				.withEvotingFromDate(dateTime(fromDate))
				.withEvotingToDate(dateTime(toDate));
	}

	@Test
	void appendAuthorization() {
		AuthorizationsType authorizations = new AuthorizationsType();
		final AuthorizationType auth = new AuthorizationType()
				.withAuthorizationIdentification("id")
				.withAuthorizationName("Toto").withAuthorizationObject(
						new AuthorizationObjectType()
								.withDomainOfInfluence(new DomainOfInfluenceType().withDomainOfInfluenceIdentification("doi"))
								.withCountingCircle(new CountingCircleType().withCountingCircleIdentification("cc"))
				);

		authorizations = authorizationService.appendAuthorization(authorizations, auth);
		authorizations = authorizationService.appendAuthorization(authorizations, auth);
		assertEquals(1, authorizations.getAuthorization().size());
	}

	@Nested
	@DisplayName("evotingTestBallotBoxesFromDate validation")
	class EvotingTestBallotBoxesFromDateTest {

		private static final String EVOTING_FROM = "2077-08-20T08:00:00";
		private static final String EVOTING_TO = "2077-08-25T18:00:00";
		private static final List<String> USED_DOI_IDS = List.of("doi-1");

		// Counting circle id >= 1000 makes the voter a test voter
		private static final String TEST_CC_ID = "1000";
		// Counting circle id < 1000 makes the voter a non-test voter
		private static final String NON_TEST_CC_ID = "100";

		@Test
		@DisplayName("null testBallotBoxesFromDate: test voter gets contest evotingFromDate")
		void nullTestBallotBoxesFromDate_testVoterGetsContestFromDate() {
			final ContestType contest = createContest(EVOTING_FROM, EVOTING_TO);
			final ch.evoting.xmlns.parameter._5.ContestType contestParams = new ch.evoting.xmlns.parameter._5.ContestType();

			final AuthorizationType result = authorizationService.build(createVoter(TEST_CC_ID), USED_DOI_IDS, contest, contestParams);

			assertEquals(contest.getEvotingFromDate(), result.getAuthorizationFromDate());
		}

		@Test
		@DisplayName("valid testBallotBoxesFromDate before evotingToDate: test voter gets testBallotBoxesFromDate")
		void validTestBallotBoxesFromDate_testVoterGetsTestDate() {
			final ContestType contest = createContest(EVOTING_FROM, EVOTING_TO);
			final ch.evoting.xmlns.parameter._5.ContestType contestParams = new ch.evoting.xmlns.parameter._5.ContestType();
			final XMLGregorianCalendar testFromDate = dateTime("2077-08-19T06:00:00");
			contestParams.setEvotingTestBallotBoxesFromDate(testFromDate);

			final AuthorizationType result = authorizationService.build(createVoter(TEST_CC_ID), USED_DOI_IDS, contest, contestParams);

			assertEquals(testFromDate, result.getAuthorizationFromDate());
		}

		@Test
		@DisplayName("valid testBallotBoxesFromDate: non-test voter still gets contest evotingFromDate")
		void validTestBallotBoxesFromDate_nonTestVoterGetsContestFromDate() {
			final ContestType contest = createContest(EVOTING_FROM, EVOTING_TO);
			final ch.evoting.xmlns.parameter._5.ContestType contestParams = new ch.evoting.xmlns.parameter._5.ContestType();
			contestParams.setEvotingTestBallotBoxesFromDate(dateTime("2077-08-19T06:00:00"));

			final AuthorizationType result = authorizationService.build(createVoter(NON_TEST_CC_ID), USED_DOI_IDS, contest, contestParams);

			assertEquals(contest.getEvotingFromDate(), result.getAuthorizationFromDate());
		}

		@Test
		@DisplayName("testBallotBoxesFromDate equal to evotingToDate throws IllegalStateException")
		void testBallotBoxesFromDateEqualToEvotingToDate_throws() {
			final ContestType contest = createContest(EVOTING_FROM, EVOTING_TO);
			final ch.evoting.xmlns.parameter._5.ContestType contestParams = new ch.evoting.xmlns.parameter._5.ContestType();
			contestParams.setEvotingTestBallotBoxesFromDate(dateTime(EVOTING_TO));
			final VoterTypeWithRole voter = createVoter(TEST_CC_ID);

			final IllegalStateException exception = assertThrows(IllegalStateException.class,
					() -> authorizationService.build(voter, USED_DOI_IDS, contest, contestParams));
			final String expectedErrorMessage = "The test ballot boxes must open before the productive ballot boxes close (evotingToDate). [evotingTestBallotBoxesFromDate: %s, evotingToDate: %s]".formatted(
					EVOTING_TO, EVOTING_TO);
			assertEquals(expectedErrorMessage, Throwables.getRootCause(exception).getMessage());
		}

		@Test
		@DisplayName("testBallotBoxesFromDate after evotingToDate throws IllegalStateException")
		void testBallotBoxesFromDateAfterEvotingToDate_throws() {
			final ContestType contest = createContest(EVOTING_FROM, EVOTING_TO);
			final ch.evoting.xmlns.parameter._5.ContestType contestParams = new ch.evoting.xmlns.parameter._5.ContestType();
			final String tooLateTestBallotBoxesFromDate = "2077-08-26T00:00:00";
			contestParams.setEvotingTestBallotBoxesFromDate(dateTime(tooLateTestBallotBoxesFromDate));
			final VoterTypeWithRole voter = createVoter(TEST_CC_ID);

			final IllegalStateException exception = assertThrows(IllegalStateException.class,
					() -> authorizationService.build(voter, USED_DOI_IDS, contest, contestParams));
			final String expectedErrorMessage = "The test ballot boxes must open before the productive ballot boxes close (evotingToDate). [evotingTestBallotBoxesFromDate: %s, evotingToDate: %s]".formatted(
					tooLateTestBallotBoxesFromDate, EVOTING_TO);
			assertEquals(expectedErrorMessage, Throwables.getRootCause(exception).getMessage());
		}

		@Test
		@DisplayName("testBallotBoxesFromDate after evotingFromDate but before evotingToDate succeeds with warning")
		void testBallotBoxesFromDateAfterEvotingFromDate_succeeds() {
			final ContestType contest = createContest(EVOTING_FROM, EVOTING_TO);
			final ch.evoting.xmlns.parameter._5.ContestType contestParams = new ch.evoting.xmlns.parameter._5.ContestType();
			final XMLGregorianCalendar testFromDate = dateTime("2077-08-22T10:00:00");
			contestParams.setEvotingTestBallotBoxesFromDate(testFromDate);

			final AuthorizationType result = authorizationService.build(createVoter(TEST_CC_ID), USED_DOI_IDS, contest, contestParams);

			assertEquals(testFromDate, result.getAuthorizationFromDate());
		}
	}

}
