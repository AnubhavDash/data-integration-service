/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.dataintegrationservice.common.MarshallTool;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteType;

class ContestBuilderTest {

	private final ContestService contestService = new ContestService();
	@Test
	void cleanHtmlTags() {
		ContestType contest = new ContestType().withVoteInformation(
				new VoteInformationType().withVote(
						new VoteType().withBallot(
								new BallotType().withStandardBallot(
										new StandardBallotType().withBallotQuestion(
												new BallotQuestionType().withBallotQuestionInfo(
														new BallotQuestionType.BallotQuestionInfo().withBallotQuestion("test <B>NOT_REMOVED</B>")
																.withLanguage(
																		LanguageType.FR
																)
												))))));

		contest = contestService.cleanHtmlTags(contest);
		final String result = MarshallTool.marshall(contest, "contest");
		assertTrue(result.contains("NOT_REMOVED"));
		assertFalse(result.contains("&lt;"));
	}

	@Test
	void testUUIDUniqueness() {
		/* the output must be the same, regardless of time and/or host */
		final String input = "0123456789-abcdefg-&+-èü[{#@¢";
		final String output = "91c269fc-4dfb-3da2-abc0-f442cf3019df";
		assertEquals(output, StringUtil.generateUUIDFromString(input));
	}
}

