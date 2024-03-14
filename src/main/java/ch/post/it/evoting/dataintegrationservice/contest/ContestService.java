/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.swisspush.supermachine.BeanScanner.from;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.MoreCollectors;
import com.google.common.collect.Streams;

import ch.evoting.xmlns.parameter._4.AnswerTranslationType;
import ch.evoting.xmlns.parameter._4.ContestType.IncumbentText;
import ch.evoting.xmlns.parameter._4.DefaultAnswerTranslationType;
import ch.evoting.xmlns.parameter._4.DisplayCandidateLineStyle;
import ch.evoting.xmlns.parameter._4.ElectionType;
import ch.evoting.xmlns.parameter._4.EmptyCandidateTextInfo;
import ch.post.it.evoting.dataintegrationservice.common.MarshallTool;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.TieBreakQuestionTypeWithReferences;
import ch.post.it.evoting.dataintegrationservice.parameter.ParameterMapper;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.DisplayLineInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionRulesExplanationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EligibilityType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeysDefinitionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.IncumbentType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.WriteInCandidateType;

@Service
public class ContestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContestService.class);

	private static final String VALUE_NOT_UNIQUE_WITHIN_PARAMETERS_MESSAGE = "%s '%s' not unique within the parameters";

	private static final int MINIMAL_REQUIRED_LANGUAGES = 4;
	private static final int DEFAULT_VALUE = 0;

	private static final BigInteger ELECTION_MAJORITY_TYPE = BigInteger.TWO;
	private static final BigInteger ELECTION_RELATION_PRIMARY = BigInteger.ONE;
	private static final BigInteger ELECTION_RELATION_SECONDARY = BigInteger.TWO;

	private final boolean byPassCheckConsistencyDates;
	private final int votesDefaultAnswerTypeStandard;
	private final int votesDefaultAnswerTypeTiebreak;
	private final int evotingFromToMinDeltaAllowed;

	public ContestService(
			@Value("${bypass.checkConsistency.dates}")
			final boolean byPassCheckConsistencyDates,
			@Value("${votes.defaultAnswerType.standard}")
			final int votesDefaultAnswerTypeStandard,
			@Value("${votes.defaultAnswerType.tiebreak}")
			final int votesDefaultAnswerTypeTiebreak,
			@Value("${eVotingFromTo.min.delta.allowed}")
			final int evotingFromToMinDeltaAllowed) {
		this.byPassCheckConsistencyDates = byPassCheckConsistencyDates;
		this.votesDefaultAnswerTypeStandard = votesDefaultAnswerTypeStandard;
		this.votesDefaultAnswerTypeTiebreak = votesDefaultAnswerTypeTiebreak;
		this.evotingFromToMinDeltaAllowed = evotingFromToMinDeltaAllowed;
	}

	private List<StandardAnswerType> getStandardAnswers(final String questionIdentification, BigInteger answerType,
			final Map<String, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> specificAnswers,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> typedAnswers) {
		List<ch.evoting.xmlns.parameter._4.StandardAnswerType> paramAnswers = specificAnswers.get(questionIdentification);
		if ((paramAnswers == null || paramAnswers.isEmpty())) {
			if (answerType == null) {
				LOGGER.warn(
						"There is no answerType and answerOptionIdentifcation for the question identified by '{}'. Taking answerType 2 as default.",
						questionIdentification);
				answerType = BigInteger.valueOf(votesDefaultAnswerTypeStandard);
			}
			paramAnswers = typedAnswers.get(answerType);
			checkState(paramAnswers != null && !paramAnswers.isEmpty(), "Process stopped. answerType %s in question %s is not supported", answerType,
					questionIdentification);
		}
		return ParameterMapper.INSTANCE.mapStandard(paramAnswers, questionIdentification);
	}

	private List<TiebreakAnswerType> getTiebreakAnswers(final String questionIdentification, BigInteger answerType,
			final Map<String, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> specificAnswers,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> typedAnswers) {
		List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType> paramAnswers = specificAnswers.get(questionIdentification);
		if ((paramAnswers == null || paramAnswers.isEmpty())) {
			if (answerType == null) {
				LOGGER.warn(
						"There is no answerType and answerOptionIdentifcation for the question identified by '{}'. Taking answerType 4 as default.",
						questionIdentification);
				answerType = BigInteger.valueOf(votesDefaultAnswerTypeTiebreak);
			}
			paramAnswers = typedAnswers.get(answerType);
			checkState(paramAnswers != null && !paramAnswers.isEmpty(), "Process stopped. answerType %s in question %s is not supported", answerType,
					questionIdentification);
		}
		return ParameterMapper.INSTANCE.mapTiebreak(paramAnswers, questionIdentification);
	}

	private Map<BigInteger, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> getDefaultStandardAnswers(
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> defaultAnswers;
		if (contestParameters.getDefaultAnswerTranslations() == null) {
			defaultAnswers = new HashMap<>();
		} else {
			contestParameters.getDefaultAnswerTranslations().getAnswerType().stream()
					.map(DefaultAnswerTranslationType.AnswerType::getAnswerTypeIdentification)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
					.findFirst().ifPresent(e -> {
						throw new IllegalStateException(String.format(VALUE_NOT_UNIQUE_WITHIN_PARAMETERS_MESSAGE, "answerType", e.getKey()));
					});
			defaultAnswers = contestParameters.getDefaultAnswerTranslations().getAnswerType().stream().collect(
					Collectors.toMap(DefaultAnswerTranslationType.AnswerType::getAnswerTypeIdentification,
							DefaultAnswerTranslationType.AnswerType::getStandardAnswer));
		}
		return defaultAnswers;
	}

	private Map<BigInteger, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> getDefaultTiebreakAnswers(
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> defaultAnswers;
		if (contestParameters.getDefaultAnswerTranslations() == null) {
			defaultAnswers = new HashMap<>();
		} else {
			contestParameters.getDefaultAnswerTranslations().getAnswerType().stream()
					.map(DefaultAnswerTranslationType.AnswerType::getAnswerTypeIdentification)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
					.findFirst().ifPresent(e -> {
						throw new IllegalStateException(String.format(VALUE_NOT_UNIQUE_WITHIN_PARAMETERS_MESSAGE, "answerType", e.getKey()));
					});
			defaultAnswers = contestParameters.getDefaultAnswerTranslations().getAnswerType().stream().collect(
					Collectors.toMap(DefaultAnswerTranslationType.AnswerType::getAnswerTypeIdentification,
							DefaultAnswerTranslationType.AnswerType::getTiebreakAnswer));
		}
		return defaultAnswers;
	}

	private Map<String, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> getSpecificStandardAnswers(
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> specificAnswers;
		if (contestParameters.getAnswerTranslations() == null) {
			specificAnswers = new HashMap<>();
		} else {
			contestParameters.getAnswerTranslations().getQuestion().stream()
					.collect(Collectors.groupingBy(AnswerTranslationType.Question::getQuestionIdentification, Collectors.counting())).entrySet()
					.stream().filter(e -> e.getValue() > 1).findFirst().ifPresent(e -> {
						throw new IllegalStateException(String.format(VALUE_NOT_UNIQUE_WITHIN_PARAMETERS_MESSAGE, "questionId", e.getKey()));
					});
			specificAnswers = contestParameters.getAnswerTranslations().getQuestion().stream().collect(
					Collectors.toMap(AnswerTranslationType.Question::getQuestionIdentification, AnswerTranslationType.Question::getStandardAnswer));
		}
		return specificAnswers;
	}

	private Map<String, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> getSpecifiTiebreakAnswers(
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> specificAnswers;
		if (contestParameters.getAnswerTranslations() == null) {
			specificAnswers = new HashMap<>();
		} else {
			contestParameters.getAnswerTranslations().getQuestion().stream()
					.collect(Collectors.groupingBy(AnswerTranslationType.Question::getQuestionIdentification, Collectors.counting())).entrySet()
					.stream().filter(e -> e.getValue() > 1).findFirst().ifPresent(e -> {
						throw new IllegalStateException(String.format(VALUE_NOT_UNIQUE_WITHIN_PARAMETERS_MESSAGE, "questionId", e.getKey()));
					});
			specificAnswers = contestParameters.getAnswerTranslations().getQuestion().stream().collect(
					Collectors.toMap(AnswerTranslationType.Question::getQuestionIdentification, AnswerTranslationType.Question::getTiebreakAnswer));
		}
		return specificAnswers;
	}

	private void addVotesAnswers(final List<VoteInformationType> votes, final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> defaultStandardAnswers = getDefaultStandardAnswers(
				contestParameters);
		final Map<String, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> specificStandardAnswers = getSpecificStandardAnswers(
				contestParameters);
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> defaultTiebreakAnswers = getDefaultTiebreakAnswers(
				contestParameters);
		final Map<String, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> specificTiebreakAnswers = getSpecifiTiebreakAnswers(
				contestParameters);

		votes.forEach(vote -> vote.getVote().getBallot().forEach(ballot -> {
			if (ballot.getStandardBallot() != null) {
				addVotesAnswersStandardBallot(defaultStandardAnswers, specificStandardAnswers, ballot);
			} else {
				addVotesAnswersVariantBallot(defaultStandardAnswers, specificStandardAnswers, defaultTiebreakAnswers, specificTiebreakAnswers,
						ballot);
			}
		}));
	}

	private void addVotesAnswersVariantBallot(final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> defaultStandardAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> specificStandardAnswers,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> defaultTiebreakAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType>> specificTiebreakAnswers, final BallotType ballot) {
		ballot.getVariantBallot().getStandardQuestion().forEach(question -> {
			if (question.getAnswer().isEmpty()) {
				question.getAnswer()
						.addAll(getStandardAnswers(question.getQuestionIdentification(), question.getAnswerType(), specificStandardAnswers,
								defaultStandardAnswers));
			}
		});
		ballot.getVariantBallot().getTieBreakQuestion().forEach(question -> {
			if (question.getAnswer().isEmpty()) {
				question.getAnswer()
						.addAll(getTiebreakAnswers(question.getQuestionIdentification(), question.getAnswerType(), specificTiebreakAnswers,
								defaultTiebreakAnswers));
			}
		});
	}

	private void addVotesAnswersStandardBallot(final Map<BigInteger, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> defaultStandardAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._4.StandardAnswerType>> specificStandardAnswers, final BallotType ballot) {
		final StandardBallotType question = ballot.getStandardBallot();
		if (question.getAnswer().isEmpty()) {
			question.getAnswer()
					.addAll(getStandardAnswers(question.getQuestionIdentification(), question.getAnswerType(), specificStandardAnswers,
							defaultStandardAnswers));
		}
	}

	public ContestType build(final ContestType contest, final List<VoteInformationType> votes, final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {

		addElectionGroupIdAndDescriptionIfNeeded(electionGroups);

		fillPositionIfDefinedInParameters(votes, electionGroups, contestParameters);

		addVotesAnswers(votes, contestParameters);
		addVotesQuestionPositionIfNeeded(votes);
		addVotesStandardQuestionReferenceIfNeeded(votes);
		addVotesPositionIfNeeded(votes);
		addVotesQuestionNumberIfNeeded(votes);

		addElectionsOrderOfPrecedenceIfNeeded(electionGroups);
		addEmptyListIfNeeded(electionGroups);
		addCandidatesInEmptyListsIfNeeded(electionGroups, contestParameters);
		addCandidatesListIdentifications(electionGroups);
		warnIfCandidatesOutOfLists(electionGroups);
		addCandidatePositionInCandidateOnlyElections(electionGroups);
		addIncumbentTextIfNeeded(electionGroups, contestParameters.getIncumbentText());

		addElectionsWriteIns(electionGroups, contestParameters);
		addElectionsAccumulation(electionGroups, contestParameters);
		addElectionsMinimalCandidateSelectionInList(electionGroups, contestParameters);
		addElectionRulesExplanation(electionGroups, contestParameters);

		addElectionGroupPositionIfNeeded(electionGroups);
		addElectionPositionIfNeeded(electionGroups);

		sortElectionsAndVotes(electionGroups, votes);

		addCandidateEligibilityFromParameters(electionGroups, contestParameters);
		addDisplayListLines(electionGroups);
		addDisplayCandidateLines(electionGroups, contestParameters);

		contest.getElectionGroupBallot().addAll(electionGroups);
		contest.getVoteInformation().addAll(votes);

		if (contestParameters.getEvotingFromDate() != null) {
			contest.setEvotingFromDate(contestParameters.getEvotingFromDate());
		}
		checkState(contest.getEvotingFromDate() != null, "evotingFromDate must be either defined in contest or in parameters file");

		if (contestParameters.getEvotingToDate() != null) {
			contest.setEvotingToDate(contestParameters.getEvotingToDate());
		}
		checkState(contest.getEvotingToDate() != null, "evotingFromDate must be either defined in contest or in parameters file");

		contest.setContestDefaultLanguage(LanguageType.fromValue(contestParameters.getDefaultLanguage().value()));

		contest.setAdminBoard(ParameterMapper.INSTANCE.map(contestParameters.getAdminBoard()));

		contest.setElectoralBoard(ParameterMapper.INSTANCE.map(contestParameters.getElectoralBoard()));

		// check to avoid list identification duplicates
		contest.getElectionGroupBallot().stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.flatMap(electionInformationType -> electionInformationType.getList().stream())
				.map(ListType::getListIdentification)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
				.filter(e -> e.getValue() > 1)
				.findFirst()
				.ifPresent(e -> {
					throw new IllegalStateException(String.format("list identifier '%s' is not unique within the contest", e.getKey()));
				});

		return contest;
	}

	private void addCandidateEligibilityFromParameters(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() != null) {
			contestParameters.getElections().getElection().stream()
					.filter(e -> e.getCandidatesEligibility() != null)
					.forEach(
							e -> e.getCandidatesEligibility().getCandidateEligibility()
									.forEach(can -> electionGroups.stream().flatMap(eg -> eg.getElectionInformation().stream())
											.filter(ele -> ele.getElection().getElectionIdentification().equals(e.getElectionIdentification()))
											.flatMap(ele -> ele.getCandidate().stream())
											.filter(c -> c.getCandidateIdentification().equals(can.getCandidateIdentification()))
											.findFirst().orElseThrow(() -> new IllegalStateException(String.format(
													"Unable to find candidate to set eligibility. [electionId: %s, candidateId: %s]",
													e.getElectionIdentification(), can.getCandidateIdentification())))
											.setEligibility(EligibilityType.fromValue(can.getEligibility().value()))));
		}
	}

	private void addDisplayCandidateLines(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() != null) {
			final DisplayCandidateLineStyle defaultDisplayCandidateLineStyle = contestParameters.getDefaultDisplayCandidateLineStyle();
			final List<ElectionType> style1Elections = contestParameters.getElections().getElection().stream()
					.filter(electionType -> (electionType.getDisplayCandidateLineStyle() == null && defaultDisplayCandidateLineStyle.equals(
							DisplayCandidateLineStyle.DEFAULT)) || DisplayCandidateLineStyle.DEFAULT.equals(
							electionType.getDisplayCandidateLineStyle()))
					.toList();
			addDisplayCandidateLinesStyle1(style1Elections, electionGroups);

			final List<ElectionType> style2Elections = contestParameters.getElections().getElection().stream()
					.filter(electionType -> (electionType.getDisplayCandidateLineStyle() == null && defaultDisplayCandidateLineStyle.equals(
							DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION)) || DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION.equals(
							electionType.getDisplayCandidateLineStyle()))
					.toList();
			addDisplayCandidateLinesStyle2(style2Elections, electionGroups);

			final List<ElectionType> style3Elections = contestParameters.getElections().getElection().stream()
					.filter(electionType -> (electionType.getDisplayCandidateLineStyle() == null && defaultDisplayCandidateLineStyle.equals(
							DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION_EXTRA_LINE))
							|| DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION_EXTRA_LINE.equals(electionType.getDisplayCandidateLineStyle()))
					.toList();
			addDisplayCandidateLinesStyle3(style3Elections, electionGroups);
		}
	}

	private void addDisplayCandidateLinesStyle1(final List<ElectionType> elections, final List<ElectionGroupBallotType> electionGroups) {
		elections.stream().flatMap(e -> electionGroups.stream().flatMap(eg -> eg.getElectionInformation().stream())
						.filter(ele -> ele.getElection().getElectionIdentification().equals(e.getElectionIdentification()))
						.flatMap(ele -> ele.getCandidate().stream()))
				.forEach(candidate -> {
					final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = Arrays.stream(LanguageType.values())
							.map(languageType -> {
								final String displayLine1 = String.join(" ", candidate.getReferenceOnPosition(), candidate.getFamilyName(),
										candidate.getCallName());
								return new DisplayLineInformationType.DisplayLineInfo()
										.withLanguage(languageType)
										.withDisplayLineText(displayLine1);
							})
							.toList();
					setCandidateDisplayLines1To3(candidate, displayLineInfoList1);
				});
	}

	private void addDisplayCandidateLinesStyle2(final List<ElectionType> elections, final List<ElectionGroupBallotType> electionGroups) {
		elections.stream().flatMap(e -> electionGroups.stream().flatMap(eg -> eg.getElectionInformation().stream())
						.filter(ele -> ele.getElection().getElectionIdentification().equals(e.getElectionIdentification()))
						.flatMap(ele -> ele.getCandidate().stream()))
				.forEach(candidate -> {
					final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = candidate.getPartyAffiliation()
							.getPartyAffiliationInfo().stream()
							.map(partyAffiliationInfo -> {
								if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
									throw new IllegalStateException(
											String.format("Party affiliation not provided. [displayCandidateLineStyle: 2, candidateId: %s]",
													candidate.getCandidateIdentification()));
								}

								final String displayLine1 = String.join(" ", candidate.getReferenceOnPosition(), candidate.getFamilyName(),
										candidate.getCallName(), partyAffiliationInfo.getPartyAffiliationLong());
								return new DisplayLineInformationType.DisplayLineInfo()
										.withLanguage(partyAffiliationInfo.getLanguage())
										.withDisplayLineText(displayLine1);
							})
							.toList();
					setCandidateDisplayLines1To3(candidate, displayLineInfoList1);
				});
	}

	private void addDisplayCandidateLinesStyle3(final List<ElectionType> elections, final List<ElectionGroupBallotType> electionGroups) {
		elections.stream().flatMap(e -> electionGroups.stream().flatMap(eg -> eg.getElectionInformation().stream())
						.filter(ele -> ele.getElection().getElectionIdentification().equals(e.getElectionIdentification()))
						.flatMap(ele -> ele.getCandidate().stream()))
				.forEach(candidate -> {
					final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = Arrays.stream(LanguageType.values())
							.map(languageType -> {
								final String displayLine1 = String.join(" ", candidate.getReferenceOnPosition(), candidate.getFamilyName(),
										candidate.getCallName());
								return new DisplayLineInformationType.DisplayLineInfo()
										.withLanguage(languageType)
										.withDisplayLineText(displayLine1);
							})
							.toList();
					setCandidateDisplayLines1To3(candidate, displayLineInfoList1);

					final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList4 = candidate.getPartyAffiliation()
							.getPartyAffiliationInfo().stream()
							.map(partyAffiliationInfo -> {
								if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
									throw new IllegalStateException(
											String.format("Party affiliation not provided. [displayCandidateLineStyle: 3, candidateId: %s]",
													candidate.getCandidateIdentification()));
								}

								final String displayLine4 = partyAffiliationInfo.getPartyAffiliationLong();
								return new DisplayLineInformationType.DisplayLineInfo()
										.withLanguage(partyAffiliationInfo.getLanguage())
										.withDisplayLineText(displayLine4);
							})
							.toList();
					candidate.setDisplayCandidateLine4(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList4));
				});
	}

	private void setCandidateDisplayLines1To3(final CandidateType candidate,
			final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1) {
		candidate.setDisplayCandidateLine1(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList1));

		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList2 = candidate.getCandidateText()
				.getCandidateTextInfo().stream()
				.map(candidateTextInfo -> new DisplayLineInformationType.DisplayLineInfo()
						.withLanguage(candidateTextInfo.getLanguage())
						.withDisplayLineText(candidateTextInfo.getCandidateText()))
				.toList();
		candidate.setDisplayCandidateLine2(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList2));

		if (candidate.getIncumbent().isIncumbent()) {
			final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList3 = candidate.getIncumbent().getIncumbentText()
					.getIncumbentTextInfo().stream()
					.map(incumbentTextInfoType -> new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(incumbentTextInfoType.getLanguage())
							.withDisplayLineText(incumbentTextInfoType.getIncumbentText()))
					.toList();
			candidate.setDisplayCandidateLine3(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList3));
		}
	}

	private void sortElectionsAndVotes(final List<ElectionGroupBallotType> electionGroups, final List<VoteInformationType> votes) {
		electionGroups.stream()
				.map(ElectionGroupBallotType::getElectionInformation)
				.forEach(list -> list.sort(Comparator.comparingInt(ei -> ei.getElection().getElectionPosition())));

		electionGroups.sort(Comparator.comparingInt(ElectionGroupBallotType::getElectionGroupPosition));

		votes.sort(Comparator.comparingInt(vote -> vote.getVote().getVotePosition().intValueExact()));
	}

	private void fillPositionIfDefinedInParameters(final List<VoteInformationType> votes, final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getSorting() != null) {
			contestParameters.getSorting().getElectionGroup().forEach(positionParameter -> {
				final String electionGroupIdentification = positionParameter.getElectionGroupIdentification();
				final int position = positionParameter.getPositionNumber().intValueExact();
				electionGroups.stream()
						.filter(eg -> eg.getElectionGroupIdentification().equals(electionGroupIdentification))
						.findFirst()
						.orElseThrow(() -> new IllegalStateException(String.format(
								"Unknown electionGroupIdentification defined in sorting definition of parameters file. [electionGroupIdentification: %s",
								electionGroupIdentification)))
						.setElectionGroupPosition(position);
			});

			contestParameters.getSorting().getElection().forEach(positionParameter -> {
				final String electionIdentification = positionParameter.getElectionIdentification();
				final int position = positionParameter.getPositionNumber().intValueExact();
				electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
						.filter(e -> e.getElection().getElectionIdentification().equals(electionIdentification))
						.findFirst()
						.orElseThrow(() -> new IllegalStateException(String.format(
								"Unknown electionIdentification defined in sorting definition of parameters file. [electionIdentification: %s",
								electionIdentification)))
						.getElection().setElectionPosition(position);
			});

			contestParameters.getSorting().getVote().forEach(positionParameter -> {
				final String voteIdentification = positionParameter.getVoteIdentification();
				final BigInteger position = positionParameter.getPositionNumber();
				votes.stream()
						.filter(vote -> vote.getVote().getVoteIdentification().equals(voteIdentification))
						.findFirst()
						.orElseThrow(() -> new IllegalStateException(String.format(
								"Unknown voteIdentification defined in sorting definition of parameters file. [voteIdentification: %s",
								voteIdentification)))
						.getVote().setVotePosition(position);
			});
		}
	}

	private void addVotesQuestionNumberIfNeeded(final List<VoteInformationType> votes) {
		votes.stream()
				.flatMap(vi -> vi.getVote().getBallot().stream())
				.forEach(b -> {
					if (b.getStandardBallot() != null && StringUtils.isEmpty(b.getStandardBallot().getQuestionNumber())) {
						b.getStandardBallot().setQuestionNumber(String.format("%d", b.getBallotPosition()));
					}
					if (b.getVariantBallot() != null) {
						final AtomicInteger counter = new AtomicInteger(0);
						b.getVariantBallot().getStandardQuestion().forEach(sq -> {
							if (StringUtils.isEmpty(sq.getQuestionNumber())) {
								sq.setQuestionNumber(
										String.format("%s%s", b.getBallotPosition(), getLetter(counter.getAndIncrement())));
							}
						});
						b.getVariantBallot().getTieBreakQuestion().forEach(tq -> {
							if (StringUtils.isEmpty(tq.getQuestionNumber())) {
								tq.setQuestionNumber(
										String.format("%s%s", b.getBallotPosition(), getLetter(counter.getAndIncrement())));
							}
						});
					}
				});
	}

	private char getLetter(final int value) {
		return (char) ('a' + value);
	}

	private void addVotesPositionIfNeeded(final List<VoteInformationType> votes) {
		final List<Long> usedPositions = votes.stream()
				.map(vote -> vote.getVote().getVotePosition())
				.filter(Objects::nonNull)
				.map(BigInteger::longValueExact)
				.toList();

		final AtomicLong counter = new AtomicLong(1);
		votes.forEach(vote -> {
			if (vote.getVote().getVotePosition() == null) {
				while (usedPositions.contains(counter.get())) {
					counter.getAndIncrement();
				}
				vote.getVote().setVotePosition(BigInteger.valueOf(counter.getAndIncrement()));
			}
		});
	}

	private void addElectionGroupPositionIfNeeded(final List<ElectionGroupBallotType> electionGroups) {

		final List<Integer> usedPositions = electionGroups.stream()
				.map(ElectionGroupBallotType::getElectionGroupPosition)
				.filter(val -> val > 0)
				.toList();

		final AtomicInteger counter = new AtomicInteger(1);
		electionGroups.forEach(electionGroup -> {
			if (electionGroup.getElectionGroupPosition() == DEFAULT_VALUE) {
				while (usedPositions.contains(counter.get())) {
					counter.getAndIncrement();
				}
				electionGroup.setElectionGroupPosition(counter.getAndIncrement());
			}
		});
	}

	private void addElectionPositionIfNeeded(final List<ElectionGroupBallotType> electionGroups) {

		electionGroups.forEach(eg -> {
			final List<Integer> usedPositions = eg.getElectionInformation().stream()
					.map(ei -> ei.getElection().getElectionPosition())
					.filter(val -> val > 0)
					.toList();

			final AtomicInteger counter = new AtomicInteger(1);
			eg.getElectionInformation().forEach(ei -> {
				if (ei.getElection().getElectionPosition() == DEFAULT_VALUE) {
					while (usedPositions.contains(counter.get())) {
						counter.getAndIncrement();
					}
					ei.getElection().setElectionPosition(counter.getAndIncrement());
				}
			});
		});
	}

	private void addElectionGroupIdAndDescriptionIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.forEach(electionGroup -> {
			if (StringUtils.isEmpty(electionGroup.getElectionGroupIdentification())) {
				electionGroup.setElectionGroupIdentification(determineElectionGroupIdentification(electionGroup.getElectionInformation()));
			}

			if (electionGroup.getElectionGroupDescription() == null) {
				electionGroup.setElectionGroupDescription(convertElectionDescriptionToElectionGroupDescription(
						electionGroup.getElectionInformation().getFirst().getElection().getElectionDescription()));
			}
		});
	}

	private ElectionGroupDescriptionInformationType convertElectionDescriptionToElectionGroupDescription(
			final ElectionDescriptionInformationType electionDescription) {
		return new ElectionGroupDescriptionInformationType().withElectionGroupDescriptionInfo(
				electionDescription.getElectionDescriptionInfo().stream().map(electionDescriptionInfo -> {
					final ElectionGroupDescriptionInformationType.ElectionGroupDescriptionInfo res = new ElectionGroupDescriptionInformationType.ElectionGroupDescriptionInfo();
					res.setLanguage(electionDescriptionInfo.getLanguage());
					res.setElectionGroupDescription(electionDescriptionInfo.getElectionDescription());
					res.setElectionGroupDescriptionShort(electionDescriptionInfo.getElectionDescriptionShort());
					return res;
				}).toList());
	}

	private String determineElectionGroupIdentification(final List<ElectionInformationType> electionInformation) {
		if (electionInformation.size() == 1) {
			return electionInformation.getFirst().getElection().getElectionIdentification();
		} else {
			return StringUtil.generateUUIDFromString(
					electionInformation.stream().map(ei -> ei.getElection().getElectionIdentification()).reduce("", (s1, s2) -> s1 + s2)
			);
		}
	}

	private Map<String, BigInteger> getParametersMinimalCandidateSelectionInList(
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.getMinimalCandidateSelectionInList() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::getMinimalCandidateSelectionInList));
		}
	}

	private void addElectionsMinimalCandidateSelectionInList(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, BigInteger> parametersMinimalCandidateSelectionInList = getParametersMinimalCandidateSelectionInList(contestParameters);
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.map(ElectionInformationType::getElection).forEach(e -> e.setMinimalCandidateSelectionInList(
						parametersMinimalCandidateSelectionInList.getOrDefault(e.getElectionIdentification(), BigInteger.ONE).intValueExact()));
	}

	private void addElectionRulesExplanation(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() != null) {
			contestParameters.getElections()
					.getElection().stream()
					.filter(electionType -> electionType.getElectionRulesExplanation() != null)
					.forEach(electionType -> {
						final String electionIdentification = electionType.getElectionIdentification();
						electionGroups.stream()
								.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
								.map(ElectionInformationType::getElection)
								.filter(Objects::nonNull)
								.filter(election -> election.getElectionIdentification().equals(electionIdentification))
								.forEach(election -> {
									final List<ElectionRulesExplanationType.DisplayLineInfo> displayLineInfoList = electionType.getElectionRulesExplanation()
											.getDisplayLineInfo().stream()
											.map(displayLineInfo -> new ElectionRulesExplanationType.DisplayLineInfo()
													.withLanguage(LanguageType.fromValue(displayLineInfo.getLanguage().value()))
													.withDisplayLineText(displayLineInfo.getDisplayLineText()))
											.toList();
									final ElectionRulesExplanationType electionRulesExplanationType = new ElectionRulesExplanationType()
											.withDisplayLineInfo(displayLineInfoList);
									election.setElectionRulesExplanation(electionRulesExplanationType);
								});
					});
		}
	}

	private void addCandidatePositionInCandidateOnlyElections(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.filter(this::isCandidateOnlyElection).forEach(e -> {
					final AtomicInteger pos = new AtomicInteger(1);
					e.getCandidate().forEach(c -> c.setPosition(pos.getAndIncrement()));
				});
	}

	private boolean isCandidateOnlyElection(final ElectionInformationType election) {
		return election.getList() != null && election.getList().size() == 1 && election.getList().stream().allMatch(ListType::isListEmpty);
	}

	private void warnIfCandidatesOutOfLists(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.filter(e -> e.getList().stream().anyMatch(l -> !l.isListEmpty()))
				.forEach(e -> e.getCandidate().stream().filter(c -> e.getList().stream().flatMap(l -> l.getCandidatePosition().stream())
								.noneMatch(cp -> c.getCandidateIdentification().equalsIgnoreCase(cp.getCandidateIdentification())))
						.forEach(c -> LOGGER.warn("Candidate {} is not present in any list", c.getCandidateIdentification())));
	}

	private void addIncumbentTextIfNeeded(final List<ElectionGroupBallotType> elections, final IncumbentText incumbentText) {
		from(elections).find(IncumbentType.class).stream().filter(IncumbentType::isIncumbent)
				.filter(incumbentType -> incumbentType.getIncumbentText() == null)
				.forEach(type -> type.setIncumbentText(ParameterMapper.INSTANCE.map(incumbentText)));
	}

	private void addDisplayListLines(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.forEach(electionInformationType -> {
					final List<ListUnionType> listUnions = electionInformationType.getListUnion();
					electionInformationType.getList().forEach(listType -> {
						addDisplayListLine1(listType);
						addDisplayListLine2(listType, listUnions);
						addDisplayListLine3(listType, listUnions);
					});
				});
	}

	private void addDisplayListLine1(final ListType list) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = list.getListDescription().getListDescriptionInfo()
				.stream()
				.map(listDescriptionInfo -> {
					final String displayLine = String.join(" ", list.getListIndentureNumber(), listDescriptionInfo.getListDescription());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(listDescriptionInfo.getLanguage())
							.withDisplayLineText(displayLine);
				})
				.toList();
		list.setDisplayListLine1(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList1));
	}

	private void addDisplayListLine2(final ListType listType, final List<ListUnionType> listUnionTypes) {
		listUnionTypes.stream()
				.filter(listUnionType -> listUnionType.getListUnionType().equals(BigInteger.ONE))
				.filter(listUnionType -> listUnionType.getReferencedList().stream()
						.anyMatch(referencedListId -> referencedListId.equals(listType.getListIdentification())))
				.forEach(listUnionType -> {
					final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList = listUnionType.getListUnionDescription()
							.getListUnionDescriptionInfo().stream()
							.map(description -> new DisplayLineInformationType.DisplayLineInfo()
									.withLanguage(description.getLanguage())
									.withDisplayLineText(description.getListUnionDescription()))
							.toList();
					listType.setDisplayListLine2(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList));
				});
	}

	private void addDisplayListLine3(final ListType listType, final List<ListUnionType> listUnionTypes) {
		listUnionTypes.stream()
				.filter(listUnionType -> listUnionType.getListUnionType().equals(BigInteger.TWO))
				.filter(listUnionType -> listUnionType.getReferencedList().stream()
						.anyMatch(referencedListId -> referencedListId.equals(listType.getListIdentification())))
				.forEach(listUnionType -> {
					final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList = listUnionType.getListUnionDescription()
							.getListUnionDescriptionInfo().stream()
							.map(description -> new DisplayLineInformationType.DisplayLineInfo()
									.withLanguage(description.getLanguage())
									.withDisplayLineText(description.getListUnionDescription()))
							.toList();
					listType.setDisplayListLine3(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList));
				});
	}

	private void addVotesStandardQuestionReferenceIfNeeded(final List<VoteInformationType> votes) {
		from(votes).find(VariantBallotType.class).stream().forEach(vb -> {
			for (int qCounter = 0; qCounter < vb.getTieBreakQuestion().size(); qCounter++) {
				final TieBreakQuestionTypeWithReferences tbq = (TieBreakQuestionTypeWithReferences) vb.getTieBreakQuestion().get(qCounter);

				for (int aCounter = 0; aCounter < tbq.getAnswer().size() && aCounter < 2; aCounter++) {
					final TiebreakAnswerType tba = tbq.getAnswer().get(aCounter);
					if (tba.getStandardQuestionReference() == null) {
						tba.setStandardQuestionReference(getReferencedQuestion(tbq, aCounter));
						if (tba.getStandardQuestionReference() == null) {
							LOGGER.warn(
									"ReferencedQuestion for tieBreakQuestion with identification {} and answer with identification {} is null",
									tbq.getQuestionIdentification(), tba.getAnswerIdentification());
						}
					}
				}
			}
		});
	}

	private String getReferencedQuestion(final TieBreakQuestionTypeWithReferences tbq, final int answerNumber) {
		return answerNumber == 0 ? tbq.getReferencedQuestion1() : tbq.getReferencedQuestion2();
	}

	private void addCandidatesListIdentifications(final List<ElectionGroupBallotType> elections) {
		from(elections).find(ListType.class).stream().forEach(list -> list.getCandidatePosition().forEach(p -> {
			if (p.getCandidateIdentification() == null) {
				p.setCandidateListIdentification(StringUtil.generateUUIDFromString(list.getListIdentification() + "." + p.getPositionOnList()));
			} else {
				p.setCandidateListIdentification(
						StringUtil.generateUUIDFromString(list.getListIdentification() + "." + p.getCandidateIdentification()));
			}
		}));
	}

	private void addEmptyListIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.filter(this::hasNoList).forEach(this::addEmptyList);
	}

	private boolean hasNoList(final ElectionInformationType election) {
		return election.getList().isEmpty();
	}

	private void addEmptyList(final ElectionInformationType electionType) {
		final ListDescriptionInformationType description = new ListDescriptionInformationType();
		for (final LanguageType lang : LanguageType.values()) {
			// No translation needed, as the empty list will not be visible.
			description.withListDescriptionInfo(
					new ListDescriptionInformationType.ListDescriptionInfo().withLanguage(lang).withListDescriptionShort("Leer")
							.withListDescription("Leere Liste"));
		}

		electionType.withList(new ListType().withListEmpty(true)
				.withListIdentification(StringUtil.generateUUIDFromString(electionType.getElection().getElectionIdentification()))
				.withListIndentureNumber("99").withListDescription(description).withListOrderOfPrecedence(99));

	}

	private void addCandidatesInEmptyListsIfNeeded(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> e.getList().stream()
						.filter(l -> l.isListEmpty() && (l.getCandidatePosition() == null || l.getCandidatePosition().isEmpty()))
						.forEach(l -> appendCandidates(l, e.getElection().getNumberOfMandates(), contestParameters)));
	}

	private void appendCandidates(final ListType l, final int numberOfMandates,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		for (int i = 1; i <= numberOfMandates; i++) {
			final String candidatePosition = String.format("%02d", i);

			final boolean customNumberingOnEmptyPositions =
					contestParameters.isCustomNumberingForEmptyPositions() != null && contestParameters.isCustomNumberingForEmptyPositions();
			final String refOnPosition = customNumberingOnEmptyPositions ?
					candidatePosition :
					StringUtil.concat(l.getListIndentureNumber(), candidatePosition, ".");

			final CandidateTextInformationType text = new CandidateTextInformationType();
			for (final LanguageType lang : LanguageType.values()) {
				final String emptyCandidateText = getEmptyCandidateText(lang, contestParameters);
				final String value = emptyCandidateText != null ? emptyCandidateText : candidatePosition;
				text.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo().withLanguage(lang).withCandidateText(value));
			}

			final CandidatePositionType candidate = new CandidatePositionType().withCandidateReferenceOnPosition(refOnPosition)
					.withPositionOnList(i).withCandidateTextOnPosition(text);

			l.getCandidatePosition().add(candidate);
		}
	}

	private String getEmptyCandidateText(final LanguageType language, final ch.evoting.xmlns.parameter._4.ContestType contest) {
		if (contest.getEmptyCandidateText() != null) {
			final EmptyCandidateTextInfo emptyCandidateTextInfo = contest.getEmptyCandidateText().getEmptyCandidateTextInfo().stream()
					.filter(ti -> ti.getLanguage().value().equals(language.value())).findFirst().orElse(null);
			if (emptyCandidateTextInfo != null) {
				return emptyCandidateTextInfo.getText();
			}
		}
		return null;
	}

	private void addElectionsOrderOfPrecedenceIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		if (electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.flatMap(e -> e.getList().stream()).anyMatch(l -> l.getListOrderOfPrecedence() == DEFAULT_VALUE)) {
			LOGGER.info("All fields listOrderOfPrecedence are not set, re-setting all with default value");

			electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream()).forEach(e -> {
				final AtomicInteger counter = new AtomicInteger();
				final boolean allAreNumeric = e.getList().stream().allMatch(l -> StringUtils.isNumeric(l.getListIndentureNumber()));
				if (allAreNumeric) {
					e.getList().stream().sorted(Comparator.comparing(list -> Integer.valueOf(list.getListIndentureNumber())))
							.forEach(l -> l.setListOrderOfPrecedence(counter.incrementAndGet()));
				} else {
					e.getList().forEach(l -> l.setListOrderOfPrecedence(counter.incrementAndGet()));
				}
			});
		}
	}

	private Map<String, Boolean> getParametersWriteIns(final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.isWriteInsAllowed() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::isWriteInsAllowed));
		}
	}

	private void addElectionsWriteIns(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, Boolean> writeInsMap = getParametersWriteIns(contestParameters);
		electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final boolean useWriteIns = writeInsMap.getOrDefault(e.getElection().getElectionIdentification(), false);
					e.getElection().setWriteInsAllowed(useWriteIns);
					if (useWriteIns) {
						final int numberOfMandates = e.getElection().getNumberOfMandates();
						for (int i = 1; i <= numberOfMandates; i++) {
							e.getWriteInCandidate().add(new WriteInCandidateType()
									.withWriteInCandidateIdentification(
											StringUtil.generateUUIDFromString(e.getElection().getElectionIdentification() + i))
									.withPosition(i));
						}
					}
				});
	}

	private void checkParametersOfElections(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() != null) {
			final Set<String> electionsIds = electionGroups.stream()
					.flatMap(e -> e.getElectionInformation().stream())
					.map(e -> e.getElection().getElectionIdentification()).collect(Collectors.toSet());
			contestParameters.getElections().getElection().forEach(e -> {
				if (!electionsIds.contains(e.getElectionIdentification())) {
					throw new IllegalStateException(
							"election identifier '" + e.getElectionIdentification()
									+ "' defined in the parameter file doesn't exist in the contest");
				}
			});
		}
	}

	private Map<String, BigInteger> getParametersAccumulation(final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.getCandidateAccumulation() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::getCandidateAccumulation));
		}
	}

	private void addElectionsAccumulation(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, BigInteger> accumulationMap = getParametersAccumulation(contestParameters);
		electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					if (accumulationMap.containsKey(e.getElection().getElectionIdentification())) {
						e.getElection().setCandidateAccumulation(accumulationMap.get(e.getElection().getElectionIdentification()));
					} else {
						e.getElection().setCandidateAccumulation(BigInteger.valueOf(1)); // default
					}
				});
	}

	private void addVotesQuestionPositionIfNeeded(final List<VoteInformationType> votes) {
		votes.stream().flatMap(v -> v.getVote().getBallot().stream()).filter(b -> b.getVariantBallot() != null).map(b -> {
			final Map<String, VariantBallotType> hm = new HashMap<>();
			hm.put(b.getBallotIdentification(), b.getVariantBallot());
			return hm;
		}).forEach(h -> {
			final VariantBallotType vb = h.values().stream().findFirst().get();
			if (vb.getStandardQuestion().stream().anyMatch(b -> b.getQuestionPosition() == DEFAULT_VALUE)) {
				final AtomicInteger counter = new AtomicInteger();
				LOGGER.info("Variant ballot '{}' : missing standard question positions; resetting all values",
						h.keySet().stream().findFirst().get());
				vb.getStandardQuestion().forEach(q -> q.setQuestionPosition(counter.incrementAndGet()));
			}
			if (vb.getTieBreakQuestion().stream().anyMatch(b -> b.getQuestionPosition() == DEFAULT_VALUE)) {
				final AtomicInteger counter = new AtomicInteger();
				LOGGER.info("Variant ballot '{}' : missing tie-break question positions; resetting all values",
						h.keySet().stream().findFirst().get());
				vb.getTieBreakQuestion().forEach(q -> q.setQuestionPosition(counter.incrementAndGet()));
			}
		});
	}

	public ContestType checkSame(final ContestType c1, final ContestType c2) {
		if (!c1.getContestIdentification().equals(c2.getContestIdentification())) {
			throw new IllegalStateException(String.format("Different contests identification defined (%s, %s)", c1.getContestIdentification(),
					c2.getContestIdentification()));
		}
		return c1;
	}

	private <T> void checkLanguageCount(final List<T> list, final Function<T, ?> getLanguage, final String name) {
		checkState(list.stream().map(getLanguage).distinct().count() >= MINIMAL_REQUIRED_LANGUAGES,
				"%s must contain at least 4 distinct languages",
				name);
	}

	private <T> void checkLanguageCount(final List<T> list, final Function<T, ?> getLanguage, final String id, final String elementName,
			final String fieldName) {
		checkState(list.stream().map(getLanguage).distinct().count() >= MINIMAL_REQUIRED_LANGUAGES,
				"%s '%s' : %s must contain at least 4 distinct languages", elementName, id, fieldName);
	}

	private void checkLanguagesConsistency(final ContestType contest) {
		// contest
		checkState(contest.getContestDescription() != null, "ContestDescription must be defined in source file");

		checkLanguageCount(contest.getContestDescription().getContestDescriptionInfo(),
				ContestDescriptionInformationType.ContestDescriptionInfo::getLanguage, "contestDescription");

		//election
		contest.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					checkState(e.getElection().getElectionDescription() != null, "ElectionDescription must be defined in source file");

					checkLanguageCount(e.getElection().getElectionDescription().getElectionDescriptionInfo(),
							ElectionDescriptionInformationType.ElectionDescriptionInfo::getLanguage, e.getElection().getElectionIdentification(),
							"election",
							"electionDescription");

					final String CANDIDATE_ELEMENT_NAME = "candidate";

					e.getCandidate().forEach(c -> {
						checkState(c.getCandidateText() != null, "CandidateText must be defined in source file");

						checkLanguageCount(c.getCandidateText().getCandidateTextInfo(),
								CandidateTextInformationType.CandidateTextInfo::getLanguage,
								c.getCandidateIdentification(), CANDIDATE_ELEMENT_NAME, "candidateText");
						if (c.getOccupationalTitle() != null) {
							checkLanguageCount(c.getOccupationalTitle().getOccupationalTitleInfo(),
									OccupationalTitleInformationType.OccupationalTitleInfo::getLanguage, c.getCandidateIdentification(),
									CANDIDATE_ELEMENT_NAME,
									"occupationalTitle");
						}
					});
					e.getList().forEach(l -> {
						checkLanguageCount(l.getListDescription().getListDescriptionInfo(),
								ListDescriptionInformationType.ListDescriptionInfo::getLanguage,
								l.getListIdentification(), "list", "listDescription");
						l.getCandidatePosition().forEach(cp -> checkLanguageCount(cp.getCandidateTextOnPosition().getCandidateTextInfo(),
								CandidateTextInformationType.CandidateTextInfo::getLanguage, cp.getCandidateIdentification(),
								CANDIDATE_ELEMENT_NAME,
								"candidateTextOnPosition"));
					});
					e.getListUnion().forEach(l -> checkLanguageCount(l.getListUnionDescription().getListUnionDescriptionInfo(),
							ListUnionDescriptionType.ListUnionDescriptionInfo::getLanguage, l.getListUnionIdentification(), "list-union",
							"listUnionDescription"));
				});
		// vote
		contest.getVoteInformation().forEach(v -> {
			checkState(v.getVote().getVoteDescription() != null, "VoteDescription must be defined in source file");

			checkLanguageCount(v.getVote().getVoteDescription().getVoteDescriptionInfo(),
					VoteDescriptionInformationType.VoteDescriptionInfo::getLanguage, v.getVote().getVoteIdentification(), "vote",
					"voteDescription");
			v.getVote().getBallot().forEach(b -> {
				if (b.getBallotDescription() != null) {
					checkLanguageCount(b.getBallotDescription().getBallotDescriptionInfo(),
							BallotDescriptionInformationType.BallotDescriptionInfo::getLanguage, b.getBallotIdentification(), "ballot",
							"ballotDescription");
				}

				final String QUESTION_ELEMENT_NAME = "question";
				final String ANSWER_ELEMENT_NAME = "answer";
				final String ANSWER_FIELD_NAME = "answer";
				final String BALLOT_QUESTION_FIELD_NAME = "ballotQuestion";

				if (b.getStandardBallot() != null) {
					//standardBallot
					checkLanguageCount(b.getStandardBallot().getBallotQuestion().getBallotQuestionInfo(),
							BallotQuestionType.BallotQuestionInfo::getLanguage, b.getStandardBallot().getQuestionIdentification(),
							QUESTION_ELEMENT_NAME,
							BALLOT_QUESTION_FIELD_NAME);
					b.getStandardBallot().getAnswer().forEach(
							a -> checkLanguageCount(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(),
									ANSWER_ELEMENT_NAME,
									ANSWER_FIELD_NAME));
				} else {
					//variantBallot
					b.getVariantBallot().getStandardQuestion().forEach(sq -> {
						checkLanguageCount(sq.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionType.BallotQuestionInfo::getLanguage,
								sq.getQuestionIdentification(), QUESTION_ELEMENT_NAME, BALLOT_QUESTION_FIELD_NAME);
						sq.getAnswer().forEach(
								a -> checkLanguageCount(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(),
										ANSWER_ELEMENT_NAME,
										ANSWER_FIELD_NAME));
					});
					b.getVariantBallot().getTieBreakQuestion().forEach(tbq -> {
						checkLanguageCount(tbq.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionType.BallotQuestionInfo::getLanguage,
								tbq.getQuestionIdentification(), QUESTION_ELEMENT_NAME, BALLOT_QUESTION_FIELD_NAME);
						tbq.getAnswer().forEach(
								a -> checkLanguageCount(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(),
										ANSWER_ELEMENT_NAME,
										ANSWER_FIELD_NAME));
					});
				}
			});
		});
	}

	private void checkContestExists(final ContestType c) {
		checkState(!c.getElectionGroupBallot().isEmpty() || !c.getVoteInformation().isEmpty(), "neither election nor vote is defined");
	}

	private void checkEVotingDates(final ContestType c) {

		if (byPassCheckConsistencyDates) {
			LOGGER.warn("As defined by configuration, bypassing the evotingDates consistency check");
			return;
		}

		final GregorianCalendar dtFrom = c.getEvotingFromDate().toGregorianCalendar();
		final GregorianCalendar dtTo = c.getEvotingToDate().toGregorianCalendar();

		checkState(dtFrom.getTime().after(new Date()), "eVotingFromDate must be future");
		checkState(dtTo.getTime().after(new Date()), "eVotingToDate must be future");

		checkState(dtFrom.getTime().before(dtTo.getTime()), "eVotingFromDate must precede eVotingToDate");

		final LocalDate dtFromLocal = LocalDate.of(dtFrom.get(Calendar.YEAR), dtFrom.get(Calendar.MONTH) + 1, dtFrom.get(Calendar.DAY_OF_MONTH));
		final LocalDate dtToLocal = LocalDate.of(dtTo.get(Calendar.YEAR), dtTo.get(Calendar.MONTH) + 1, dtTo.get(Calendar.DAY_OF_MONTH));
		final long daysBetween = DAYS.between(dtFromLocal, dtToLocal);

		checkState(daysBetween >= evotingFromToMinDeltaAllowed,
				"the number of days between 'eVoting date from' and 'eVoting date to' must be at least %s", evotingFromToMinDeltaAllowed);

		if (c.getEvotingToDate().toGregorianCalendar().getTime().after(c.getContestDate().toGregorianCalendar().getTime())) {
			LOGGER.warn("The date of the contest should follow the eVoting date from.");
		}
	}

	private void checkContestIdentifiersUnique(final ContestType c) {
		checkState(c.getElectionGroupBallot().stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.map(e -> e.getElection().getElectionIdentification())
				.allMatch(new HashSet<>()::add), "electionIdentification must be unique");

		checkState(c.getVoteInformation().stream().map(v -> v.getVote().getVoteIdentification()).allMatch(new HashSet<>()::add),
				"voteIdentification must be unique");

		c.getVoteInformation().stream().flatMap(vote -> vote.getVote().getBallot().stream()).map(BallotType::getBallotIdentification)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
				.findFirst().ifPresent(e -> {
					throw new IllegalStateException(String.format("ballot identifier '%s' is not unique within the contest", e.getKey()));
				});

		c.getVoteInformation().stream().flatMap(vote -> vote.getVote().getBallot().stream().flatMap(
						b -> Stream.concat(extractIfExist(b, BallotType::getStandardBallot).map(StandardBallotType::getQuestionIdentification),
								extractIfExist(b, BallotType::getVariantBallot).flatMap(
										vb -> Stream.concat(vb.getStandardQuestion().stream().map(StandardQuestionType::getQuestionIdentification),
												vb.getTieBreakQuestion().stream().map(TieBreakQuestionType::getQuestionIdentification))))))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
				.findFirst().ifPresent(e -> {
					throw new IllegalStateException(String.format("question identifier '%s' is not unique within the contest", e.getKey()));
				});

		c.getVoteInformation().stream().flatMap(vote -> vote.getVote().getBallot().stream().flatMap(b -> Stream.concat(
						extractIfExist(b, BallotType::getStandardBallot).flatMap(sb -> sb.getAnswer().stream())
								.map(StandardAnswerType::getAnswerIdentification), extractIfExist(b, BallotType::getVariantBallot).flatMap(
								vb -> Stream.concat(vb.getStandardQuestion().stream().flatMap(q -> q.getAnswer().stream())
												.map(StandardAnswerType::getAnswerIdentification),
										vb.getTieBreakQuestion().stream().flatMap(q -> q.getAnswer().stream())
												.map(TiebreakAnswerType::getAnswerIdentification))))))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
				.findFirst().ifPresent(e -> {
					throw new IllegalStateException(String.format("answer identifier '%s' is not unique within the contest", e.getKey()));
				});

		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream()).forEach(
				e -> checkState(e.getCandidate().stream().map(CandidateType::getCandidateIdentification).allMatch(new HashSet<>()::add),
						"election '%s' : candidateIdentification must be unique", e.getElection().getElectionIdentification()));

		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> checkState(e.getList().stream().map(ListType::getListIdentification).allMatch(new HashSet<>()::add),
						"election '%s' : listIdentification must be unique", e.getElection().getElectionIdentification()));
		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> e.getList().stream().flatMap(
								lst -> lst.getCandidatePosition().stream().map(cp -> Map.entry(lst.getListIdentification(), cp.getPositionOnList()))
										.toList()
										.stream()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
						.filter(entry -> entry.getValue() > 1).findFirst().ifPresent(entry -> {
							throw new IllegalStateException(
									String.format("the position %s in the list '%s' is not unique", entry.getKey().getValue(),
											entry.getKey().getKey()));
						}));

		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final Set<String> candidateIds = e.getCandidate().stream().map(CandidateType::getCandidateIdentification)
							.collect(Collectors.toSet());
					final Set<String> candidateInListIds = e.getList().stream().flatMap(l -> l.getCandidatePosition().stream())
							.map(CandidatePositionType::getCandidateIdentification).filter(Objects::nonNull).collect(Collectors.toSet());
					candidateInListIds.stream().filter(id -> !candidateIds.contains(id)).findFirst().ifPresent(i -> {
						throw new IllegalStateException(
								String.format("candidate identifier '%s' found in a list but not in the list of candidate(s)", i));
					});
				});

		from(c).find(ListUnionType.class).stream().map(ListUnionType::getListUnionIdentification)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
				.findFirst().ifPresent(e -> {
					throw new IllegalStateException(String.format("the list-union identifier '%s' is not unique", e.getKey()));
				});

		from(c).find(ListUnionType.class).stream()
				.flatMap(lst -> lst.getReferencedList().stream().map(ref -> Map.entry(lst.getListUnionIdentification(), ref)).toList().stream())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1)
				.findFirst().ifPresent(e -> {
					throw new IllegalStateException(
							String.format("the referenced list '%s' in the list-union '%s' is not unique", e.getKey().getValue(), e.getKey().getKey()));
				});

		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final Set<String> listIds = e.getList().stream().map(ListType::getListIdentification).collect(Collectors.toSet());
					final Set<String> listUnionIds = e.getListUnion().stream().flatMap(l -> l.getReferencedList().stream())
							.collect(Collectors.toSet());
					listUnionIds.stream().filter(id -> !listIds.contains(id)).findFirst().ifPresent(i -> {
						throw new IllegalStateException(String.format("referenced list identifier '%s' found in a list-union does not exist", i));
					});
				});
	}

	private <T, U> Stream<U> extractIfExist(final T t, final Function<T, U> getter) {
		return Stream.of(t).flatMap(optional(getter));
	}

	private <T, U> Function<T, Stream<U>> optional(final Function<T, U> getter) {
		return (T t) -> Stream.of(t).map(getter).filter(Objects::nonNull);
	}

	private void checkLimitOfSameOccurrencesOfCandidateInList(final ContestType c) {
		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final long limit = e.getElection().getCandidateAccumulation().longValue();
					e.getList().forEach(
							list -> list.getCandidatePosition().stream().map(CandidatePositionType::getCandidateIdentification)
									.filter(Objects::nonNull)
									.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).values().stream()
									.max(Comparator.comparingLong(Long::longValue)).ifPresent(
											value -> checkState(value <= limit,
													"same candidate found more than %s time(s). [listIdentification : %s]", limit,
													list.getListIdentification())));
				});
	}

	private void checkSameNamesOfCandidates(final ContestType c) {
		c.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> e.getCandidate().stream()
						.collect(Collectors.groupingBy(cand -> new MutablePair<>(cand.getFirstName(), cand.getFamilyName()),
								Collectors.counting()))
						.entrySet().stream().filter(entry -> entry.getValue() > 1).forEach(dup -> LOGGER.warn(
								String.format("same candidate '%s' '%s' name found multiple times. [ElectionId : %s]", dup.getKey().getLeft(),
										dup.getKey().getRight(), e.getElection().getElectionIdentification()))));
	}

	private void checkAnswerConsistency(final ContestType c) {
		c.getVoteInformation().forEach(v -> v.getVote().getBallot().forEach(b -> {
			if (b.getStandardBallot() != null) {
				checkState(b.getStandardBallot().getAnswer().stream()
								.filter(a -> a.getStandardAnswerType() != null && a.getStandardAnswerType().equals("EMPTY")).count() <= 1,
						"vote '%s' standard ballot question has more than one blank answer", v.getVote().getVoteIdentification());
			}
			if (b.getVariantBallot() != null) {
				b.getVariantBallot().getStandardQuestion().forEach(q -> checkState(
						q.getAnswer().stream().filter(a -> a.getStandardAnswerType() != null && a.getStandardAnswerType().equals("EMPTY")).count()
								<= 1, "vote '%s' variant ballot standard question %s has more than one blank answer",
						v.getVote().getVoteIdentification(), q.getQuestionPosition()));
				b.getVariantBallot().getTieBreakQuestion().forEach(
						q -> checkState(q.getAnswer().stream().filter(a -> a.getStandardQuestionReference() == null).count() <= 1,
								"vote '%s' variant ballot tiebreak question %s has more than one blank answer",
								v.getVote().getVoteIdentification(),
								q.getQuestionPosition()));
			}
		}));
	}

	public void checkConsistency(final ContestType c, final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		checkContestExists(c);
		checkEVotingDates(c);
		checkLanguagesConsistency(c);
		checkContestIdentifiersUnique(c);
		checkLimitOfSameOccurrencesOfCandidateInList(c);
		checkSameNamesOfCandidates(c);
		checkReferenceOnPositionOnCandidateIsFilled(c);
		checkEmptyListExists(c);
		checkAnswerConsistency(c);
		checkParametersOfElections(c.getElectionGroupBallot(), contestParameters);
		checkPrimarySecondaryElectionConsistency(c);
		checkVoteIdentificationsDistinctness(c);
		checkElectionIdentifiersDistinctness(c);
	}

	private PrimarySecondaryTuple mapToPrimarySecondaryContainer(final ElectionGroupBallotType eg) {
		checkState(eg.getElectionInformation().size() == 2, "electionGroup does not contain 2 elections. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());

		checkState(eg.getElectionInformation().stream().allMatch(e -> e.getElection().getReferencedElection().size() == 1),
				"the elections in the electionGroup does not all contain a referenced election. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());

		final ElectionInformationType primary = eg.getElectionInformation().stream()
				.filter(e -> e.getElection().getReferencedElection().stream()
						.anyMatch(ref -> ref.getElectionRelation().equals(ELECTION_RELATION_SECONDARY)))
				.collect(MoreCollectors.onlyElement());

		final ElectionInformationType secondary = eg.getElectionInformation().stream()
				.filter(e -> e.getElection().getReferencedElection().stream()
						.anyMatch(ref -> ref.getElectionRelation().equals(ELECTION_RELATION_PRIMARY)))
				.collect(MoreCollectors.onlyElement());

		checkState(primary.getElection().getReferencedElection().stream().anyMatch(
						ref -> ref.getElectionRelation().equals(ELECTION_RELATION_SECONDARY) && ref.getReferencedElection()
								.equals(secondary.getElection().getElectionIdentification())),
				"The primary election has no relation to the secondary election. [primaryElectionId: %s, secondaryElectionId: %s]",
				primary.getElection().getElectionIdentification(), secondary.getElection().getElectionIdentification());

		checkState(secondary.getElection().getReferencedElection().stream().anyMatch(
						ref -> ref.getElectionRelation().equals(ELECTION_RELATION_PRIMARY) && ref.getReferencedElection()
								.equals(primary.getElection().getElectionIdentification())),
				"The secondary election has no relation to the primary election. [primaryElectionId: %s, secondaryElectionId: %s]",
				primary.getElection().getElectionIdentification(), secondary.getElection().getElectionIdentification());

		return new PrimarySecondaryTuple(primary, secondary);
	}

	private void checkPrimarySecondaryElectionConsistency(final ContestType c) {
		c.getElectionGroupBallot().stream()
				.filter(eg -> eg.getElectionInformation().size() > 1)
				.forEach(eg -> {
					final PrimarySecondaryTuple primarySecondaryTuple = mapToPrimarySecondaryContainer(eg);

					checkPrimarySecondaryElectionMajorityElectionConsistency(eg);
					checkPrimarySecondaryElectionNumberOfMandatesConsistency(eg, primarySecondaryTuple);
					checkPrimarySecondaryElectionWriteInsAllowedConsistency(eg, primarySecondaryTuple);
					checkPrimarySecondaryElectionCandidatesConsistency(eg, primarySecondaryTuple);
				});
	}

	private void checkPrimarySecondaryElectionMajorityElectionConsistency(final ElectionGroupBallotType eg) {
		eg.getElectionInformation().forEach(e -> {
			checkState(e.getElection().getTypeOfElection().equals(ELECTION_MAJORITY_TYPE),
					"The election is not of type Majority. [electionId: %s]",
					e.getElection().getElectionIdentification());

			checkState(e.getElection().getCandidateAccumulation().equals(BigInteger.ONE),
					"The candidateAccumulation should be set to 1 for majority election. [electionId: %s]",
					e.getElection().getElectionIdentification());

			checkState(e.getElection().getMinimalCandidateSelectionInList() == 1,
					"The minimalCandidateSelectionInList should be set to 1 for majority election. [electionId: %s]",
					e.getElection().getElectionIdentification());
		});
	}

	private void checkPrimarySecondaryElectionCandidatesConsistency(final ElectionGroupBallotType eg,
			final PrimarySecondaryTuple primarySecondaryTuple) {
		final List<String> primaryCandidateIds = primarySecondaryTuple.primary().getCandidate().stream().map(
				CandidateType::getCandidateIdentification).toList();

		final List<CandidateType> secondaryCandidates = primarySecondaryTuple.secondary().getCandidate();
		checkState(secondaryCandidates.stream().allMatch(
						c -> (primaryCandidateIds.contains(c.getCandidateIdentification()) && c.getEligibility() == EligibilityType.EXPLICIT)
								|| c.getEligibility() == EligibilityType.IMPLICIT
								|| c.getEligibility() == EligibilityType.IMPLICIT_DISCREET),
				"At least one non already elected candidate in the secondary election is not candidate in the primary election. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());

		checkState(secondaryCandidates.stream()
						.filter(c -> c.getEligibility() == EligibilityType.IMPLICIT || c.getEligibility() == EligibilityType.IMPLICIT_DISCREET)
						.noneMatch(c -> primaryCandidateIds.contains(c.getCandidateIdentification())),
				"At least one implicit or implicit discreet candidate in the secondary election is also a candidate in the primary election. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());

		final List<CandidateType> implicitCandidates = secondaryCandidates.stream()
				.filter(c -> c.getEligibility() == EligibilityType.IMPLICIT)
				.toList();
		checkState(secondaryCandidates.stream()
						.filter(c -> c.getEligibility() == EligibilityType.IMPLICIT_DISCREET)
						.noneMatch(implicitCandidates::contains),
				"At least one candidate in the secondary election is both implicitly eligible and discreet. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());
	}

	private void checkPrimarySecondaryElectionWriteInsAllowedConsistency(final ElectionGroupBallotType eg,
			final PrimarySecondaryTuple primarySecondaryTuple) {
		checkState(primarySecondaryTuple.primary().getElection().isWriteInsAllowed() == primarySecondaryTuple.secondary().getElection()
						.isWriteInsAllowed(),
				"The writeInsAllowed parameter should be the same value for both primary and secondary elections. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());
	}

	private void checkPrimarySecondaryElectionNumberOfMandatesConsistency(final ElectionGroupBallotType eg,
			final PrimarySecondaryTuple primarySecondaryTuple) {
		checkState(primarySecondaryTuple.secondary().getElection().getNumberOfMandates() <= primarySecondaryTuple.primary().getElection()
						.getNumberOfMandates(), "The secondary election must have less or an equal numberOfMandates. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());
	}

	private void checkReferenceOnPositionOnCandidateIsFilled(final ContestType c) {
		c.getElectionGroupBallot().stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.flatMap(e -> e.getCandidate().stream())
				.forEach(candidate -> checkState(!StringUtil.isNullOrEmpty(candidate.getReferenceOnPosition()),
						"the referenceOnPosition for the candidate must be provided. [candidateId: %s]", candidate.getCandidateIdentification()));
	}

	private void checkEmptyListExists(final ContestType c) {
		c.getElectionGroupBallot().stream().flatMap(eg -> eg.getElectionInformation().stream())
				.forEach(e -> checkState(e.getList().stream().anyMatch(ListType::isListEmpty),
						"The election does not contains an empty list [electionIdentification: %s]",
						e.getElection().getElectionIdentification()));
	}

	private void checkVoteIdentificationsDistinctness(final ContestType c) {
		final List<VoteInformationType> voteInformation = c.getVoteInformation();

		final List<String> identifications = voteInformation.stream().flatMap(v -> {
			final String voteIdentification = v.getVote().getVoteIdentification();
			final Stream<String> ballotIdentifications = v.getVote().getBallot().stream().map(BallotType::getBallotIdentification);
			final Stream<String> standardBallotQuestionIdentifications = v.getVote().getBallot().stream()
					.filter(b -> b.getStandardBallot() != null)
					.map(b -> b.getStandardBallot().getQuestionIdentification());
			final Stream<String> variantBallotQuestionIdentifications = v.getVote().getBallot().stream().filter(b -> b.getVariantBallot() != null)
					.flatMap(b -> Streams.concat(
							b.getVariantBallot().getStandardQuestion().stream().map(StandardQuestionType::getQuestionIdentification),
							b.getVariantBallot().getTieBreakQuestion().stream().map(TieBreakQuestionType::getQuestionIdentification)));

			return Streams.concat(Stream.of(voteIdentification), ballotIdentifications, standardBallotQuestionIdentifications,
					variantBallotQuestionIdentifications);
		}).toList();

		checkState(hasNoDuplicates(identifications), "The votes contain duplicate identifications");
	}

	private void checkElectionIdentifiersDistinctness(final ContestType c) {
		final List<ElectionGroupBallotType> electionGroupBallot = c.getElectionGroupBallot();

		final List<String> identifications = electionGroupBallot.stream()
				.flatMap(eg -> checkElectionIdentifiersDistinctnessPerElection(eg).stream())
				.toList();

		checkState(hasNoDuplicates(identifications), "The elections contain duplicate identifications");
	}

	private List<String> checkElectionIdentifiersDistinctnessPerElection(final ElectionGroupBallotType electionGroupBallot) {
		return electionGroupBallot.getElectionInformation().stream()
				.map(electionInformation -> {
					final String electionIdentification = electionInformation.getElection().getElectionIdentification();
					final Stream<String> listIdentifications = electionInformation.getList().stream().map(ListType::getListIdentification);
					final Stream<String> candidateIdentifications = electionInformation.getCandidate().stream()
							.map(CandidateType::getCandidateIdentification);

					final List<String> identifications = Streams.concat(Stream.of(electionIdentification), listIdentifications,
									candidateIdentifications)
							.toList();
					checkState(hasNoDuplicates(identifications), "The election contains duplicate identifications. [election id: %s]",
							electionIdentification);
					return identifications;
				}).toList().getFirst();
	}

	public ContestType cleanHtmlTags(final ContestType contest) {
		String asString = MarshallTool.marshall(contest, "contest");

		asString = asString.replaceAll("(?s)&lt;.*?&gt;", "");

		return MarshallTool.unmarshall(asString, ContestType.class);
	}

	public ContestType setExtendedAuthenticationKeyNames(final ContestType contestType, final List<String> keyNames) {
		if (!keyNames.isEmpty()) {
			final ExtendedAuthenticationKeysDefinitionType definition = new ExtendedAuthenticationKeysDefinitionType();

			keyNames.forEach(k -> definition.getKeyName().add(k));

			contestType.setExtendedAuthenticationKeys(definition);
		}
		return contestType;
	}

	public ContestType filterContest(final ContestType contest, final List<String> usedDOIs) {
		final List<VoteInformationType> usedVotes = contest.getVoteInformation().stream()
				.filter(vi -> usedDOIs.contains(vi.getVote().getDomainOfInfluence())).toList();

		final List<ElectionGroupBallotType> usedElections = contest.getElectionGroupBallot().stream()
				.filter(eg -> usedDOIs.contains(eg.getDomainOfInfluence())).toList();

		contest.setVoteInformation(usedVotes);
		contest.setElectionGroupBallot(usedElections);

		return contest;
	}

	private record PrimarySecondaryTuple(ElectionInformationType primary, ElectionInformationType secondary) {
	}
}
