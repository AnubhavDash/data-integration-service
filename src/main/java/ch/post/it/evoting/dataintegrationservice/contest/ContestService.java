/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.function.Predicate.not;
import static org.swisspush.supermachine.BeanScanner.from;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.MoreCollectors;
import com.google.common.collect.Streams;

import ch.evoting.xmlns.parameter._5.AnswerTranslationType;
import ch.evoting.xmlns.parameter._5.ContestType.IncumbentText;
import ch.evoting.xmlns.parameter._5.DefaultAnswerTranslationType;
import ch.evoting.xmlns.parameter._5.DisplayCandidateLineStyle;
import ch.evoting.xmlns.parameter._5.DisplayListLineStyle;
import ch.evoting.xmlns.parameter._5.ElectionType;
import ch.evoting.xmlns.parameter._5.PositionText;
import ch.evoting.xmlns.parameter._5.SortingType;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.dataintegrationservice.common.MarshallTool;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.TieBreakQuestionTypeWithReferences;
import ch.post.it.evoting.dataintegrationservice.parameter.ParameterMapper;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateTimeUtils;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateUtils;
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
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyPositionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeysDefinitionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.IncumbentTextInfoType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.IncumbentType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PartyAffiliationformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PositionTextInfoType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.RoleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VarListTextInfoType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.WriteInPositionType;

@Service
public class ContestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContestService.class);

	private static final String VALUE_NOT_UNIQUE_WITHIN_PARAMETERS_MESSAGE = "%s '%s' not unique within the parameters";

	private static final List<String> LANGUAGES = Arrays.stream(LanguageType.values()).map(LanguageType::value).toList();
	private static final int DEFAULT_VALUE = 0;
	private static final int MINIMUM_REQUIRED_ANSWERS = 3;

	private static final BigInteger ELECTION_MAJORITY_TYPE = BigInteger.TWO;
	private static final BigInteger ELECTION_PROPORTIONAL_TYPE = BigInteger.ONE;
	private static final BigInteger ELECTION_RELATION_PRIMARY = BigInteger.ONE;
	private static final BigInteger ELECTION_RELATION_SECONDARY = BigInteger.TWO;
	private static final String EMPTY_ANSWER = "EMPTY";
	private static final int EMPTY_LIST_ORDER_OF_PRECEDENCE = 99;
	private static final String EMPTY_LIST_IDENTURE_NUMBER = "99";

	private final boolean bypassCheckConsistencyDates;
	private final int evotingFromToMinDeltaAllowed;

	public ContestService(
			@Value("${bypass.checkConsistency.dates}")
			final boolean bypassCheckConsistencyDates,
			@Value("${eVotingFromTo.min.delta.allowed}")
			final int evotingFromToMinDeltaAllowed) {
		this.bypassCheckConsistencyDates = bypassCheckConsistencyDates;
		this.evotingFromToMinDeltaAllowed = evotingFromToMinDeltaAllowed;
	}

	/**
	 * Gets the standard answers for a question according to its answer type.
	 *
	 * @param questionIdentification the identifier of the question for which to get the answers
	 * @param answerType             the answer type of the question. Should be one of 2, 4, 5, 6, 7, or 8 (undefined).
	 * @param specificAnswers        the map of question identifications to standard answer lists from the contest parameters.
	 * @param typedAnswers           the map of answer types to standard answer lists from the default parameters.
	 * @return the list of standard answers matching the {@code answerType}.
	 * @throws IllegalStateException if there is no answerType and no answerOptionIdentification for the standard question.
	 */
	private List<StandardAnswerType> getStandardAnswers(final String questionIdentification, final BigInteger answerType,
			final Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> specificAnswers,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> typedAnswers) {
		List<ch.evoting.xmlns.parameter._5.StandardAnswerType> paramAnswers = specificAnswers.get(questionIdentification);
		if ((paramAnswers == null || paramAnswers.isEmpty())) {
			checkState(answerType != null,
					"There is no answerType and no answerOptionIdentification for the standard question identified by '%s'. At least one of them must be set.",
					questionIdentification);
			paramAnswers = typedAnswers.get(answerType);
			checkState(paramAnswers != null && !paramAnswers.isEmpty(), "Process stopped. answerType %s in question %s is not supported", answerType,
					questionIdentification);
		}
		return ParameterMapper.INSTANCE.mapStandard(paramAnswers, questionIdentification);
	}

	private List<TiebreakAnswerType> getTiebreakAnswers(final String questionIdentification, final BigInteger answerType,
			final Map<String, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> specificAnswers,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> typedAnswers) {
		List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType> paramAnswers = specificAnswers.get(questionIdentification);
		if ((paramAnswers == null || paramAnswers.isEmpty())) {
			checkState(answerType != null,
					"There is no answerType and no answerOptionIdentification for the tiebreak question identified by '%s'. At least one of them must be set.",
					questionIdentification);
			paramAnswers = typedAnswers.get(answerType);
			checkState(paramAnswers != null && !paramAnswers.isEmpty(), "Process stopped. answerType %s in question %s is not supported", answerType,
					questionIdentification);
		}
		return ParameterMapper.INSTANCE.mapTiebreak(paramAnswers, questionIdentification);
	}

	private Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> getDefaultStandardAnswers(
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> defaultAnswers;
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

	private Map<BigInteger, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> getDefaultTiebreakAnswers(
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> defaultAnswers;
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

	private Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> getSpecificStandardAnswers(
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		final Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> specificAnswers;
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

	private Map<String, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> getSpecificTiebreakAnswers(
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		final Map<String, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> specificAnswers;
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

	/**
	 * Adds the answers to the votes' questions if needed.
	 * <p>
	 * There are three sources for the answers. Answers should be provided directly in the eCH, but if they are not, the election event specific
	 * params are checked for answer options. If it also does not contain answer options specific to a question, the default parameters are checked
	 * for answer options matching the question's answer type.
	 * </p>
	 *
	 * @param votes             the list of votes for which to potentially add answers
	 * @param contestParameters the contest's parameters to be used for mapping the answers to the questions if needed.
	 */
	private void addVotesAnswers(final List<VoteInformationType> votes, final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> defaultStandardAnswers = getDefaultStandardAnswers(
				contestParameters);
		final Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> specificStandardAnswers = getSpecificStandardAnswers(
				contestParameters);
		final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> defaultTiebreakAnswers = getDefaultTiebreakAnswers(
				contestParameters);
		final Map<String, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> specificTiebreakAnswers = getSpecificTiebreakAnswers(
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

	private void addVotesAnswersVariantBallot(final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> defaultStandardAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> specificStandardAnswers,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> defaultTiebreakAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType>> specificTiebreakAnswers, final BallotType ballot) {
		ballot.getVariantBallot().getStandardQuestion().forEach(question -> {
			final List<StandardAnswerType> answers = question.getAnswer();
			validateStandardAnswers(answers, question.getAnswerType(), question.getQuestionIdentification(), defaultStandardAnswers,
					specificStandardAnswers);
		});
		ballot.getVariantBallot().getTieBreakQuestion().forEach(question -> {
			final List<TiebreakAnswerType> answers = question.getAnswer();
			if (answers.isEmpty()) {
				answers.addAll(getTiebreakAnswers(question.getQuestionIdentification(), question.getAnswerType(),
						specificTiebreakAnswers, defaultTiebreakAnswers));
				LOGGER.warn("No tiebreak answers were provided with the question. Added answers from parameters. [questionIdentification: {}]",
						question.getQuestionIdentification());
			} else {
				checkState(answers.size() >= MINIMUM_REQUIRED_ANSWERS, "The tieBreakQuestion %s has only %s answers. It must have at least %s.",
						question.getQuestionIdentification(), answers.size(), MINIMUM_REQUIRED_ANSWERS);

				answers.sort(Comparator.comparingInt(TiebreakAnswerType::getAnswerPosition));
				answers.forEach(answer -> answer.setHiddenAnswer(answer == answers.getLast()));
			}

			final Set<Integer> answerPositions = answers.stream().map(TiebreakAnswerType::getAnswerPosition).collect(Collectors.toSet());
			final Integer minPosition = answerPositions.stream().min(Integer::compareTo).orElse(Integer.MAX_VALUE);
			final Integer maxPosition = answerPositions.stream().max(Integer::compareTo).orElse(Integer.MIN_VALUE);
			checkState(answerPositions.size() == answers.size() && (minPosition == 0 || minPosition == 1)
							&& maxPosition - minPosition == answers.size() - 1,
					"Variant ballot tiebreak question '%s' : the answer sequence number must be a strictly increasing sequence from 0 to %s or from 1 to %s.",
					question.getQuestionIdentification(), answers.size() - 1, answers.size());

			if (minPosition == 0) {
				answers.forEach(answer -> answer.setAnswerPosition(answer.getAnswerPosition() + 1));
				LOGGER.warn("The variant ballot tiebreak question '{}' answer positions start from 0. They have been changed to start from 1.",
						question.getQuestionIdentification());
			}
		});
	}

	private void addVotesAnswersStandardBallot(final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> defaultStandardAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> specificStandardAnswers, final BallotType ballot) {
		final StandardBallotType question = ballot.getStandardBallot();
		final List<StandardAnswerType> answers = question.getAnswer();
		validateStandardAnswers(answers, question.getAnswerType(), question.getQuestionIdentification(), defaultStandardAnswers,
				specificStandardAnswers);
	}

	/**
	 * Validates the standard answers and sets the {@code hiddenAnswer} attribute of each answer.
	 * <p>
	 * The answer with {@code standardAnswerType} "EMPTY" is hidden, all others are not. If the standard answers are not set, they are retrieved from
	 * the parameters file according to the answer type (see {@link ContestService#getStandardAnswers(String, BigInteger, Map, Map)}). The list of
	 * standard answers must contain at least {@link ContestService#MINIMUM_REQUIRED_ANSWERS} answers and the answer positions must range from 1 to
	 * <i>n</i>, where <i>n</i> is the number of answers.
	 * </p>
	 *
	 * @param answers                the standard answers belonging to the question.
	 * @param answerType             the answer type associated with the question.
	 * @param questionIdentification the identifier of the question.
	 * @throws IllegalStateException if the number of answers is smaller than {@link ContestService#MINIMUM_REQUIRED_ANSWERS} or if the
	 *                               answerPositions are not numbered correctly.
	 */
	private void validateStandardAnswers(final List<StandardAnswerType> answers, final BigInteger answerType, final String questionIdentification,
			final Map<BigInteger, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> defaultStandardAnswers,
			final Map<String, List<ch.evoting.xmlns.parameter._5.StandardAnswerType>> specificStandardAnswers) {
		if (answers.isEmpty()) {
			answers.addAll(getStandardAnswers(questionIdentification, answerType,
					specificStandardAnswers, defaultStandardAnswers));
			LOGGER.warn("No standard answers were provided with the question. Added answers from parameters. [questionIdentification: {}]",
					questionIdentification);
		} else {
			checkState(answers.size() >= MINIMUM_REQUIRED_ANSWERS,
					"The variant ballot standard question %s has only %s answers. It must have at least %s.",
					questionIdentification, answers.size(), MINIMUM_REQUIRED_ANSWERS);

			answers.sort(Comparator.comparingInt(StandardAnswerType::getAnswerPosition));
			answers.forEach(answer -> answer.setHiddenAnswer(answer.getStandardAnswerType().equals(EMPTY_ANSWER)));
		}

		final Set<Integer> answerPositions = answers.stream().map(StandardAnswerType::getAnswerPosition).collect(Collectors.toSet());

		final Integer minPosition = answerPositions.stream().min(Integer::compareTo).orElse(Integer.MAX_VALUE);
		final Integer maxPosition = answerPositions.stream().max(Integer::compareTo).orElse(Integer.MIN_VALUE);
		checkState(
				answerPositions.size() == answers.size() && (minPosition == 0 || minPosition == 1) && maxPosition - minPosition == answers.size() - 1,
				"Variant ballot standard question '%s' : the answer sequence number must be a strictly increasing sequence from 0 to %s or from 1 to %s.",
				questionIdentification, answers.size() - 1, answers.size());

		if (minPosition == 0) {
			answers.forEach(answer -> answer.setAnswerPosition(answer.getAnswerPosition() + 1));
			LOGGER.warn("The variant ballot standard question '{}' answer positions start from 0. They have been changed to start from 1.",
					questionIdentification);
		}
	}

	public ContestType build(final ContestType contest, final List<VoteInformationType> votes, final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {

		addElectionGroupIdAndDescriptionIfNeeded(electionGroups);

		fillPositionIfDefinedInParameters(votes, electionGroups, contestParameters);

		addVotesAnswers(votes, contestParameters);
		checkVotesQuestionsPositionsConsistency(votes);
		checkVotesBallotsPositionsConsistency(votes);
		addVotesStandardQuestionReferenceIfNeeded(votes);
		addVotesQuestionNumberIfNeeded(votes);

		addListsOrderOfPrecedenceIfNeeded(electionGroups);

		addEmptyListIfNeeded(electionGroups);
		addEmptyListOrderOfPrecedenceIfNeeded(electionGroups);

		addCandidatesInEmptyListsIfNeeded(electionGroups, contestParameters);
		addCandidatesListIdentifications(electionGroups);
		checkCandidateTextsAreSet(electionGroups);
		warnIfCandidatesOutOfLists(electionGroups);
		addCandidatePositionIfNeeded(electionGroups);
		addIncumbentTextIfNeeded(electionGroups, contestParameters.getIncumbentText());

		addAbstentionAllowed(contestParameters, votes, electionGroups);

		addElectionsWriteIns(electionGroups, contestParameters);
		addElectionsImplicitWriteInCandidatesIfNeeded(electionGroups, contestParameters);
		addElectionsAccumulation(electionGroups, contestParameters);
		addElectionsMinimalCandidateSelectionInList(electionGroups, contestParameters);
		addElectionRulesExplanation(electionGroups, contestParameters);

		addVotesAndElectionGroupPositionIfNeeded(votes, electionGroups);
		checkElectionsPositionsConsistency(electionGroups);

		sortElectionsAndVotes(electionGroups, votes);

		addCandidateEligibilityFromParameters(electionGroups, contestParameters);
		addDisplayListLines(electionGroups, contestParameters);
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

	private void addAbstentionAllowed(final ch.evoting.xmlns.parameter._5.ContestType contestParameters, final List<VoteInformationType> votes,
			final List<ElectionGroupBallotType> electionGroups) {

		final boolean abstentionAllowed = Boolean.TRUE.equals(contestParameters.isAbstentionAllowed());

		votes.forEach(vote -> vote.getVote().setAbstentionAllowed(abstentionAllowed));
		electionGroups.forEach(electionGroupBallotType -> electionGroupBallotType.setAbstentionAllowed(abstentionAllowed));
	}

	private void addCandidateEligibilityFromParameters(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
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

	record ElectionDisplayCandidateLineStyleTuple(String electionIdentification, DisplayCandidateLineStyle displayCandidateLineStyle) {
	}

	private void addDisplayCandidateLines(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {

		final List<ElectionDisplayCandidateLineStyleTuple> electionDisplayCandidateLineStyleTuples =
				contestParameters.getElections() != null ?
						contestParameters.getElections().getElection().stream()
								.map(e -> new ElectionDisplayCandidateLineStyleTuple(e.getElectionIdentification(), e.getDisplayCandidateLineStyle()))
								.toList() :
						new ArrayList<>();

		final DisplayCandidateLineStyle defaultDisplayCandidateLineStyle = contestParameters.getDefaultDisplayCandidateLineStyle();

		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(electionInformationType -> {
					final String electionIdentification = electionInformationType.getElection().getElectionIdentification();
					final DisplayCandidateLineStyle specificDisplayCandidateLineStyle = getSpecificDisplayCandidateLineStyle(
							electionDisplayCandidateLineStyleTuples, electionIdentification);
					final boolean noSpecificDisplayCandidateLineStyle = specificDisplayCandidateLineStyle == null;

					electionInformationType.getCandidate()
							.forEach(candidateType ->
									addDisplayCandidateLineStyle(candidateType, noSpecificDisplayCandidateLineStyle, defaultDisplayCandidateLineStyle,
											specificDisplayCandidateLineStyle, electionIdentification)
							);
				});
	}

	private void addDisplayCandidateLineStyle(final CandidateType candidateType, final boolean noSpecificDisplayCandidateLineStyle,
			final DisplayCandidateLineStyle defaultDisplayCandidateLineStyle, final DisplayCandidateLineStyle specificDisplayCandidateLineStyle,
			final String electionIdentification) {

		final DisplayCandidateLineStyle effectiveDisplayCandidateLineStyle =
				noSpecificDisplayCandidateLineStyle ? defaultDisplayCandidateLineStyle : specificDisplayCandidateLineStyle;

		switch (effectiveDisplayCandidateLineStyle) {
		case DEFAULT -> addDefaultDisplayCandidateLinesStyle(candidateType);
		case DEFAULT_COMMA_SEPARATED -> addDefaultCommaSeparatedDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION -> addWithPartyAffiliationDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION_EXTRA_LINE -> addWithPartyAffiliationExtraLineDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION_EXTRA_LINE_COMMA_SEPARATED ->
				addWithPartyAffiliationExtraLineCommaSeparatedDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION_BRACKETS_NONUMBER -> addWithPartyAffiliationBracketsNoNumberDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION_BRACKETS -> addWithPartyAffiliationBracketsDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION_BELOW_BRACKETS_NONUMBER -> addWithPartyAffiliationBelowBracketsNoNumberDisplayCandidateLinesStyle(candidateType);
		case WITH_PARTY_AFFILIATION_BELOW_BRACKETS -> addWithPartyAffiliationBelowBracketsDisplayCandidateLinesStyle(candidateType);
		default -> throw new IllegalStateException(String.format(
				"Unknown displayCandidateLineStyle. [displayCandidateLineStyle: %s, electionId: %s, candidateId: %s]",
				effectiveDisplayCandidateLineStyle, electionIdentification, candidateType.getCandidateIdentification()));
		}
	}

	private DisplayCandidateLineStyle getSpecificDisplayCandidateLineStyle(
			final List<ElectionDisplayCandidateLineStyleTuple> electionDisplayCandidateLineStyleTuples, final String electionIdentification) {
		final List<DisplayCandidateLineStyle> matchingDisplayCandidateLineStyles = electionDisplayCandidateLineStyleTuples.stream()
				.filter(electionDisplayCandidateLineStyleTuple -> electionDisplayCandidateLineStyleTuple.electionIdentification()
						.equals(electionIdentification))
				.map(ElectionDisplayCandidateLineStyleTuple::displayCandidateLineStyle)
				.toList();

		checkState(matchingDisplayCandidateLineStyles.size() <= 1);

		if (matchingDisplayCandidateLineStyles.isEmpty()) {
			return null;
		} else {
			return matchingDisplayCandidateLineStyles.getFirst();
		}
	}

	/**
	 * Generates the default display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName callName"</li>
	 *     <li>Line 2: empty</li>
	 *     <li>Line 3: empty</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addDefaultDisplayCandidateLinesStyle(final CandidateType candidate) {
		generateCandidateDisplayLines1To3(candidate, "");
	}

	/**
	 * Generates the default comma separated display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName, callName"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addDefaultCommaSeparatedDisplayCandidateLinesStyle(final CandidateType candidate) {
		generateCandidateDisplayLines1To3(candidate, ",");
	}

	/**
	 * Generates display lines 1 to 3 for a candidate, using the given delimiter between family name and call name.
	 * <ul>
	 * 		<li>Line 1: "position familyName&lt;delimiter&gt; callName"</li>
	 * 		<li>Line 2: "candidateText"</li>
	 * 	    <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 * </ul>
	 * @param candidate the candidate for which to generate the display lines
	 * @param delimiter the delimiter to use between family name and call name (e.g., space or comma)
	 */
	private void generateCandidateDisplayLines1To3(final CandidateType candidate, final String delimiter) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = Arrays.stream(LanguageType.values())
				.map(languageType -> {
					final String displayLine1 = String.join(" ", candidate.getReferenceOnPosition(), candidate.getFamilyName() + delimiter,
							candidate.getCallName());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(languageType)
							.withDisplayLineText(displayLine1);
				})
				.toList();
		setCandidateDisplayLines1To3(candidate, displayLineInfoList1);
	}

	/**
	 * Generates the with party affiliation display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName callName partyAffiliationLong"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationDisplayCandidateLinesStyle(final CandidateType candidate) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = candidate.getPartyAffiliation()
				.getPartyAffiliationInfo().stream()
				.map(partyAffiliationInfo -> {
					if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
						throw new IllegalStateException(
								String.format("Party affiliation not provided. [displayCandidateLineStyle: %s, candidateId: %s]",
										DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION, candidate.getCandidateIdentification()));
					}

					final String displayLine1 = String.join(" ", candidate.getReferenceOnPosition(), candidate.getFamilyName(),
							candidate.getCallName(), partyAffiliationInfo.getPartyAffiliationLong());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(partyAffiliationInfo.getLanguage())
							.withDisplayLineText(displayLine1);
				})
				.toList();
		setCandidateDisplayLines1To3(candidate, displayLineInfoList1);
	}

	/**
	 * Generates the with party affiliation extra line display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName callName"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: "partyAffiliationLong"</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationExtraLineDisplayCandidateLinesStyle(final CandidateType candidate) {
		generateCandidateDisplayLines1To3(candidate, "");
		setDisplayCandidateLineWithAffiliationExtraLine(candidate);
	}

	/**
	 * Generates the with party affiliation extra line comma separated display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName, callName"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: "partyAffiliationLong"</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationExtraLineCommaSeparatedDisplayCandidateLinesStyle(final CandidateType candidate) {
		generateCandidateDisplayLines1To3(candidate, ",");
		setDisplayCandidateLineWithAffiliationExtraLine(candidate);
	}

	/**
	 * Sets display line 4 for a candidate with the party affiliation.
	 * <ul>
	 *     <li>Line 4: "partyAffiliationLong"</li>
	 * </ul>
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void setDisplayCandidateLineWithAffiliationExtraLine(final CandidateType candidate) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList4 = candidate.getPartyAffiliation()
				.getPartyAffiliationInfo().stream()
				.map(partyAffiliationInfo -> {
					if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
						throw new IllegalStateException(
								String.format("Party affiliation not provided. [displayCandidateLineStyle: %s, candidateId: %s]",
										DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION_EXTRA_LINE, candidate.getCandidateIdentification()));
					}

					final String displayLine4 = partyAffiliationInfo.getPartyAffiliationLong();
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(partyAffiliationInfo.getLanguage())
							.withDisplayLineText(displayLine4);
				})
				.toList();
		candidate.setDisplayCandidateLine4(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList4));
	}

	private void setDisplayCandidateLineWithAffiliationBelowLineBrackets(final CandidateType candidate) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList5 = candidate.getPartyAffiliation()
				.getPartyAffiliationInfo().stream()
				.map(partyAffiliationInfo -> {
					if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
						throw new IllegalStateException(
								String.format("Party affiliation not provided. [displayCandidateLineStyle: %s, candidateId: %s]",
										DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION_BELOW_BRACKETS, candidate.getCandidateIdentification()));
					}

					final String displayLine5 = "(%s)".formatted(partyAffiliationInfo.getPartyAffiliationLong());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(partyAffiliationInfo.getLanguage())
							.withDisplayLineText(displayLine5);
				})
				.toList();
		candidate.setDisplayCandidateLine5(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList5));
	}

	/**
	 * Generates the with party affiliation brackets no number display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "familyName callName (partyAffiliationLong)"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationBracketsNoNumberDisplayCandidateLinesStyle(final CandidateType candidate) {

		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = candidate.getPartyAffiliation()
				.getPartyAffiliationInfo().stream()
				.map(partyAffiliationInfo -> {
					if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
						throw new IllegalStateException(
								String.format("Party affiliation not provided. [displayCandidateLineStyle: %s, candidateId: %s]",
										DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION_BRACKETS_NONUMBER, candidate.getCandidateIdentification()));
					}

					final String displayLine1 = "%s %s (%s)".formatted(candidate.getFamilyName(),
							candidate.getCallName(), partyAffiliationInfo.getPartyAffiliationLong());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(partyAffiliationInfo.getLanguage())
							.withDisplayLineText(displayLine1);
				})
				.toList();
		setCandidateDisplayLines1To3(candidate, displayLineInfoList1);

	}

	/**
	 * Generates the with party affiliation brackets display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName callName (partyAffiliationLong)"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: empty</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationBracketsDisplayCandidateLinesStyle(final CandidateType candidate) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = candidate.getPartyAffiliation()
				.getPartyAffiliationInfo().stream()
				.map(partyAffiliationInfo -> {
					if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
						throw new IllegalStateException(
								String.format("Party affiliation not provided. [displayCandidateLineStyle: %s, candidateId: %s]",
										DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION, candidate.getCandidateIdentification()));
					}

					final String displayLine1 = "%s %s %s (%s)".formatted(candidate.getReferenceOnPosition(), candidate.getFamilyName(),
							candidate.getCallName(), partyAffiliationInfo.getPartyAffiliationLong());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(partyAffiliationInfo.getLanguage())
							.withDisplayLineText(displayLine1);
				})
				.toList();
		setCandidateDisplayLines1To3(candidate, displayLineInfoList1);
	}

	/**
	 * Generates the with party affiliation below brackets no numbers display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "familyName callName"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: (partyAffiliationLong)</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationBelowBracketsNoNumberDisplayCandidateLinesStyle(final CandidateType candidate) {
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList1 = Arrays.stream(LanguageType.values())
				.map(languageType -> {
					final String displayLine1 = String.join(" ", candidate.getFamilyName(), candidate.getCallName());
					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(languageType)
							.withDisplayLineText(displayLine1);
				})
				.toList();
		setCandidateDisplayLines1To3(candidate, displayLineInfoList1);
		setDisplayCandidateLineWithAffiliationBelowLineBrackets(candidate);
	}

	/**
	 * Generates the with party affiliation below brackets display candidate lines style:
	 * <ul>
	 *     <li>Line 1: "position familyName callName"</li>
	 *     <li>Line 2: "candidateText"</li>
	 * 	   <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 *     <li>Line 4: empty</li>
	 *     <li>Line 5: (partyAffiliationLong)</li>
	 * </ul>
	 *
	 * @param candidate the candidate for which to generate the display lines
	 */
	private void addWithPartyAffiliationBelowBracketsDisplayCandidateLinesStyle(final CandidateType candidate) {

		generateCandidateDisplayLines1To3(candidate, "");
		final List<DisplayLineInformationType.DisplayLineInfo> displayLineInfoList5 = candidate.getPartyAffiliation()
				.getPartyAffiliationInfo().stream()
				.map(partyAffiliationInfo -> {
					if (partyAffiliationInfo.getPartyAffiliationLong() == null) {
						if (partyAffiliationInfo.getPartyAffiliationShort() != null) {
							partyAffiliationInfo.setPartyAffiliationLong(partyAffiliationInfo.getPartyAffiliationShort());
						} else {
							throw new IllegalStateException(
									String.format("Party affiliation not provided. [displayCandidateLineStyle: %s, candidateId: %s]",
											DisplayCandidateLineStyle.WITH_PARTY_AFFILIATION_BRACKETS_NONUMBER,
											candidate.getCandidateIdentification()));
						}
					}

					return new DisplayLineInformationType.DisplayLineInfo()
							.withLanguage(partyAffiliationInfo.getLanguage())
							.withDisplayLineText("(" + partyAffiliationInfo.getPartyAffiliationLong() + ")");
				})
				.toList();
		candidate.setDisplayCandidateLine5(new DisplayLineInformationType().withDisplayLineInfo(displayLineInfoList5));
	}

	/**
	 * Sets display lines 1 to 3 for a candidate.
	 * <ul>
	 *     <li>Line 1: as provided in {@code displayLineInfoList1}</li>
	 *     <li>Line 2: "candidateText"</li>
	 *     <li>Line 3: "incumbentText" if the candidate is an incumbent</li>
	 * </ul>
	 * @param candidate the candidate for which to set the display lines
	 * @param displayLineInfoList1 the display line info list for line 1 in all languages
	 */
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

		votes.sort(Comparator.comparingInt(vote -> vote.getVote().getVotePosition()));
	}

	private void fillPositionIfDefinedInParameters(final List<VoteInformationType> votes, final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {

		final SortingType sorting = contestParameters.getSorting();
		if (sorting != null) {
			final List<SortingType.ElectionGroup> sortingElectionGroups = sorting.getElectionGroup();
			final List<SortingType.Vote> sortingVotes = sorting.getVote();
			final int totalNbrOfPositions = sortingElectionGroups.size() + sortingVotes.size();

			checkState(votes.size() == sortingVotes.size() && electionGroups.size() == sortingElectionGroups.size(),
					"Partial sorting is not supported. The positions must be defined either for all votes and all election groups or for none.");

			final TreeSet<Integer> sortedPositions = Stream.concat(
							sortingElectionGroups.stream().map(electionGroup -> electionGroup.getPositionNumber().intValueExact()),
							sortingVotes.stream().map(vote -> vote.getPositionNumber().intValueExact()))
					.collect(Collectors.toCollection(TreeSet::new));

			final Integer minPosition = sortedPositions.getFirst();
			final Integer maxPosition = sortedPositions.getLast();

			checkState(sortedPositions.size() == totalNbrOfPositions && (minPosition == 0 || minPosition == 1)
							&& maxPosition - minPosition == totalNbrOfPositions - 1,
					"The sorting positions must be a strictly increasing sequence from 0 to %s or from 1 to %s.", totalNbrOfPositions - 1,
					totalNbrOfPositions);

			if (minPosition == 0) {
				sortingElectionGroups.forEach(
						electionGroup -> electionGroup.setPositionNumber(electionGroup.getPositionNumber().add(BigInteger.ONE)));
				sortingVotes.forEach(vote -> vote.setPositionNumber(vote.getPositionNumber().add(BigInteger.ONE)));
				LOGGER.warn("The sorting positions start from 0. They have been changed to start from 1.");
			}

			sortingElectionGroups.forEach(positionParameter -> {
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

			sortingVotes.forEach(positionParameter -> {
				final String voteIdentification = positionParameter.getVoteIdentification();
				final int position = positionParameter.getPositionNumber().intValueExact();
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

	private void addVotesAndElectionGroupPositionIfNeeded(final List<VoteInformationType> votes, final List<ElectionGroupBallotType> electionGroups) {
		final AtomicInteger counter = new AtomicInteger(1);
		votes.forEach(vote -> {
			if (vote.getVote().getVotePosition() == DEFAULT_VALUE) {
				vote.getVote().setVotePosition(counter.getAndIncrement());
			}
		});
		electionGroups.forEach(electionGroup -> {
			if (electionGroup.getElectionGroupPosition() == DEFAULT_VALUE) {
				electionGroup.setElectionGroupPosition(counter.getAndIncrement());
			}
		});
	}

	private void checkElectionsPositionsConsistency(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups
				.forEach(electionGroupBallot -> {
					final int totalNbrOfElections = electionGroupBallot.getElectionInformation().size();
					final String electionGroupIdentification = electionGroupBallot.getElectionGroupIdentification();

					final long electionsWithoutPosition = electionGroupBallot.getElectionInformation().stream()
							.filter(electionInformation -> electionInformation.getElection().getElectionPosition() == DEFAULT_VALUE)
							.count();

					if (electionsWithoutPosition == 0 || electionsWithoutPosition == 1) {
						LOGGER.debug("Election group '{}' : all elections have a position; checking monotonicity", electionGroupIdentification);

						final TreeSet<Integer> sortedElectionsPositions = electionGroupBallot.getElectionInformation().stream()
								.map(electionInformation -> electionInformation.getElection().getElectionPosition())
								.collect(Collectors.toCollection(TreeSet::new));

						final Integer minPosition = sortedElectionsPositions.getFirst();
						final Integer maxPosition = sortedElectionsPositions.getLast();

						checkState(sortedElectionsPositions.size() == totalNbrOfElections && (minPosition == 0 || minPosition == 1)
										&& maxPosition - minPosition == totalNbrOfElections - 1,
								"Election group '%s' : elections positions must be a strictly increasing sequence from 0 to %s or from 1 to %s",
								electionGroupIdentification, totalNbrOfElections - 1, totalNbrOfElections);

						if (minPosition == 0) {
							electionGroupBallot.getElectionInformation().forEach(electionInformation -> {
								final int electionPosition = electionInformation.getElection().getElectionPosition();
								electionInformation.getElection().setElectionPosition(electionPosition + 1);
							});
							LOGGER.warn("The election group '{}' elections positions start from 0. They have been changed to start from 1.",
									electionGroupIdentification);
						}

						// Positions sequence is valid, sort for display.
						electionGroupBallot.getElectionInformation()
								.sort(Comparator.comparingInt(electionInformation -> electionInformation.getElection().getElectionPosition()));
					} else if (electionsWithoutPosition == totalNbrOfElections) {
						LOGGER.warn("Election group '{}' : missing all elections positions; setting all values", electionGroupIdentification);
						final AtomicInteger counter = new AtomicInteger();
						electionGroupBallot.getElectionInformation().stream()
								.map(ElectionInformationType::getElection)
								.forEach(election -> election.setElectionPosition(counter.incrementAndGet()));
					} else {
						throw new IllegalStateException(String.format(
								"Election group '%s' : all elections must have a position or none of them", electionGroupIdentification));
					}
				});
	}

	private void addElectionGroupIdAndDescriptionIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.forEach(electionGroup -> {
			final List<ElectionInformationType> electionsInformation = electionGroup.getElectionInformation();

			electionsInformation.forEach(electionInformation ->
					checkState(electionInformation.getElection().getElectionDescription() != null,
							"Missing election description. [electionIdentification: %s]",
							electionInformation.getElection().getElectionIdentification())
			);

			final ElectionGroupDescriptionInformationType electionGroupDescription = electionGroup.getElectionGroupDescription();

			if (electionsInformation.size() == 1) {
				final ElectionInformationType electionInformation = electionsInformation.getFirst();

				if (StringUtils.isEmpty(electionGroup.getElectionGroupIdentification())) {
					final String electionIdentification = electionInformation.getElection().getElectionIdentification();

					electionGroup.setElectionGroupIdentification(electionIdentification);

					LOGGER.warn(
							"Election group with single election is missing election group identification. Used election identification as election group identification. [electionIdentification: {}]",
							electionIdentification);
				}

				if (electionGroupDescription == null) {
					final ElectionDescriptionInformationType electionDescription = electionInformation.getElection().getElectionDescription();
					// Election group identification may have been updated by previous if statement.
					final String electionGroupIdentification = electionGroup.getElectionGroupIdentification();

					electionGroup.setElectionGroupDescription(convertElectionDescriptionToElectionGroupDescription(electionDescription));

					LOGGER.warn(
							"Election group with single election is missing election group description. Used election description as election group description. [electionGroupIdentification: {}]",
							electionGroupIdentification);
				}
			} else {
				// If there are multiple elections, the election group identification and description must be provided.
				final String electionGroupIdentification = electionGroup.getElectionGroupIdentification();

				checkState(!StringUtils.isEmpty(electionGroupIdentification),
						"Missing election group identification. [contained electionIdentification: %s]",
						electionsInformation.getFirst().getElection().getElectionIdentification());
				checkState(electionGroupDescription != null, "Missing election group description. [electionGroupIdentification: %s]",
						electionGroupIdentification);
			}
		});
	}

	private ElectionGroupDescriptionInformationType convertElectionDescriptionToElectionGroupDescription(
			final ElectionDescriptionInformationType electionDescription) {

		return new ElectionGroupDescriptionInformationType().withElectionGroupDescriptionInfo(
				electionDescription.getElectionDescriptionInfo().stream()
						.map(electionDescriptionInfo ->
								new ElectionGroupDescriptionInformationType.ElectionGroupDescriptionInfo()
										.withLanguage(electionDescriptionInfo.getLanguage())
										.withElectionGroupDescription(electionDescriptionInfo.getElectionDescription())
										.withElectionGroupDescriptionShort(electionDescriptionInfo.getElectionDescriptionShort()))
						.toList()
		);
	}

	private Map<String, BigInteger> getParametersMinimalCandidateSelectionInList(
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.getMinimalCandidateSelectionInList() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::getMinimalCandidateSelectionInList));
		}
	}

	private void addElectionsMinimalCandidateSelectionInList(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		final Map<String, BigInteger> parametersMinimalCandidateSelectionInList = getParametersMinimalCandidateSelectionInList(contestParameters);
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.map(ElectionInformationType::getElection).forEach(e -> e.setMinimalCandidateSelectionInList(
						parametersMinimalCandidateSelectionInList.getOrDefault(e.getElectionIdentification(), BigInteger.ONE).intValueExact()));
	}

	private void addElectionRulesExplanation(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
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

	private void addCandidatePositionIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final List<Integer> usedPositions = e.getCandidate().stream()
							.map(CandidateType::getPosition)
							.toList();

					final AtomicInteger counter = new AtomicInteger(1);
					e.getCandidate().forEach(c -> {
						if (c.getPosition() == DEFAULT_VALUE) {
							while (usedPositions.contains(counter.get())) {
								counter.getAndIncrement();
							}
							c.setPosition(counter.getAndIncrement());
						}
					});
				});
	}

	private boolean isCandidateOnlyElection(final ElectionInformationType election) {
		return election.getList() != null && election.getList().isEmpty();
	}

	private void warnIfCandidatesOutOfLists(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.filter(not(this::isCandidateOnlyElection))
				.forEach(e -> e.getCandidate().stream().filter(c -> e.getList().stream().flatMap(l -> l.getCandidatePosition().stream())
								.noneMatch(cp -> c.getCandidateIdentification().equalsIgnoreCase(cp.getCandidateIdentification())))
						.forEach(c -> LOGGER.warn("Candidate {} is not present in any list", c.getCandidateIdentification())));
	}

	private void addIncumbentTextIfNeeded(final List<ElectionGroupBallotType> elections, final IncumbentText incumbentText) {
		from(elections).find(IncumbentType.class).stream().filter(IncumbentType::isIncumbent)
				.filter(incumbentType -> incumbentType.getIncumbentText() == null)
				.forEach(type -> type.setIncumbentText(ParameterMapper.INSTANCE.map(incumbentText)));
	}

	record ElectionDisplayListLineStyleTuple(String electionIdentification, DisplayListLineStyle displayListLineStyle) {
	}

	private void addDisplayListLines(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {

		final List<ElectionDisplayListLineStyleTuple> electionDisplayListLineStyleTuples =
				contestParameters.getElections() != null ?
						contestParameters.getElections().getElection().stream()
								.map(e -> new ElectionDisplayListLineStyleTuple(e.getElectionIdentification(), e.getDisplayListLineStyle()))
								.toList() :
						new ArrayList<>();

		final DisplayListLineStyle defaultDisplayListLineStyle = contestParameters.getDefaultDisplayListLineStyle();

		electionGroups.stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.forEach(electionInformationType -> {

					final String electionIdentification = electionInformationType.getElection().getElectionIdentification();
					final DisplayListLineStyle specificDisplayListLineStyle = getSpecificDisplayListLineStyle(
							electionDisplayListLineStyleTuples, electionIdentification);
					final boolean noSpecificDisplayListLineStyle = specificDisplayListLineStyle == null;

					final List<ListUnionType> listUnions = electionInformationType.getListUnion();
					electionInformationType.getList().forEach(listType -> {

						if ((DisplayListLineStyle.DEFAULT.equals(defaultDisplayListLineStyle)
								&& noSpecificDisplayListLineStyle) || DisplayListLineStyle.DEFAULT.equals(
								specificDisplayListLineStyle)) {
							addDisplayListLine1(listType);
							addDisplayListLine2(listType, listUnions);
							addDisplayListLine3(listType, listUnions);
						} else if ((DisplayListLineStyle.WITHOUT_SUBLISTUNION.equals(defaultDisplayListLineStyle)
								&& noSpecificDisplayListLineStyle) || DisplayListLineStyle.WITHOUT_SUBLISTUNION.equals(
								specificDisplayListLineStyle)) {
							addDisplayListLine1(listType);
							addDisplayListLine2(listType, listUnions);
						} else {
							throw new IllegalStateException(String.format(
									"Unknown displayListLineStyle. [displayListLineStyle: %s, electionId: %s, listId: %s]",
									defaultDisplayListLineStyle, electionIdentification, listType.getListIdentification()));
						}
					});
				});
	}

	private DisplayListLineStyle getSpecificDisplayListLineStyle(
			final List<ElectionDisplayListLineStyleTuple> electionDisplayListLineStyleTuples, final String electionIdentification) {
		final List<DisplayListLineStyle> matchingDisplayListLineStyles = electionDisplayListLineStyleTuples.stream()
				.filter(electionDisplayListLineStyleTuple -> electionDisplayListLineStyleTuple.electionIdentification()
						.equals(electionIdentification))
				.map(ElectionDisplayListLineStyleTuple::displayListLineStyle)
				.toList();

		checkState(matchingDisplayListLineStyles.size() <= 1);

		if (matchingDisplayListLineStyles.isEmpty()) {
			return null;
		} else {
			return matchingDisplayListLineStyles.getFirst();
		}
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
					}
				}
			}
		});
	}

	private String getReferencedQuestion(final TieBreakQuestionTypeWithReferences tbq, final int answerNumber) {
		return answerNumber == 0 ? tbq.getReferencedQuestion1() : tbq.getReferencedQuestion2();
	}

	private void addCandidatesListIdentifications(final List<ElectionGroupBallotType> elections) {
		from(elections).find(ListType.class).stream().forEach(list -> list.getCandidatePosition().forEach(p -> p.setCandidateListIdentification(
				StringUtil.generateUUIDFromString(list.getListIdentification() + "." + p.getCandidateIdentification()))));
	}

	private void checkCandidateTextsAreSet(final List<ElectionGroupBallotType> electionGroups) {
		final Predicate<CandidateType> hasText = c -> c.getCandidateText() != null && !c.getCandidateText().getCandidateTextInfo().isEmpty();

		electionGroups.stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.forEach(e -> {
					if (e.getCandidate().isEmpty()) {
						return;
					}

					final boolean allCandidatesHaveText = e.getCandidate().stream().allMatch(hasText);
					final boolean noCandidateHasText = e.getCandidate().stream().noneMatch(hasText);

					if (!allCandidatesHaveText && !noCandidateHasText) {
						throw new IllegalStateException(
								"The field candidateText for one or more candidates was not provided: [electionIdentification: %s, candidateId: %s]".formatted(
										e.getElection().getElectionIdentification(),
										e.getCandidate().stream().filter(not(hasText)).findFirst().map(CandidateType::getCandidateIdentification)
												.orElseThrow()));
					}

					if (noCandidateHasText) {
						e.getCandidate().forEach(c -> {
							final CandidateTextInformationType candidateTextInformationType = new CandidateTextInformationType();

							final List<CandidateTextInformationType.CandidateTextInfo> list = Arrays.stream(LanguageType.values())
									.map(languageType -> new CandidateTextInformationType.CandidateTextInfo().withLanguage(languageType)
											.withCandidateText("No candidate text"))
									.toList();

							candidateTextInformationType.setCandidateTextInfo(list);
							c.setCandidateText(candidateTextInformationType);
						});
						LOGGER.warn(
								"The candidateText in the election were not provided and dummy texts were assigned automatically. [electionIdentification: {}]",
								e.getElection().getElectionIdentification());
					}
				});
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

		electionType.withEmptyList(new EmptyListType()
				.withListIdentification(StringUtil.generateUUIDFromString(electionType.getElection().getElectionIdentification()))
				.withListIndentureNumber(EMPTY_LIST_IDENTURE_NUMBER)
				.withListOrderOfPrecedence(EMPTY_LIST_ORDER_OF_PRECEDENCE)
				.withListDescription(description));
	}

	private void addCandidatesInEmptyListsIfNeeded(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final EmptyListType emptyList = e.getEmptyList();
					if (emptyList.getEmptyPosition() == null || emptyList.getEmptyPosition().isEmpty()) {
						addEmptyPositions(emptyList, e.getElection().getNumberOfMandates(), contestParameters);
					}
				});
	}

	private void addEmptyPositions(final EmptyListType emptyList, final int numberOfMandates,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		for (int i = 1; i <= numberOfMandates; i++) {
			final String candidatePosition = String.format("%02d", i);

			final PositionTextInfoType text = new PositionTextInfoType();
			for (final LanguageType lang : LanguageType.values()) {
				final String emptyPositionText = getEmptyPositionText(lang, contestParameters);
				final String value = emptyPositionText != null ? emptyPositionText : candidatePosition;
				text.withPositionText(new PositionTextInfoType.PositionText()
						.withLanguage(lang)
						.withPositionTextLong(value)
						.withPositionTextShort(StringUtils.left(value, 100)));
			}

			final EmptyPositionType emptyPosition = new EmptyPositionType()
					.withEmptyPositionIdentification(StringUtil.generateUUIDFromString(emptyList.getListIdentification() + "." + i))
					.withPositionOnList(i)
					.withEmptyPositionTextInfo(text);

			emptyList.getEmptyPosition().add(emptyPosition);
		}
	}

	private String getEmptyPositionText(final LanguageType language, final ch.evoting.xmlns.parameter._5.ContestType contest) {
		if (contest.getEmptyPositionTextInfo() != null) {
			final PositionText emptyPositionText = contest.getEmptyPositionTextInfo().getPositionText().stream()
					.filter(positionText -> positionText.getLanguage().value().equals(language.value()))
					.findFirst()
					.orElse(null);
			if (emptyPositionText != null) {
				return emptyPositionText.getText();
			}
		}
		return null;
	}

	private void addListsOrderOfPrecedenceIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		if (electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.flatMap(e -> e.getList().stream()).anyMatch(l -> l.getListOrderOfPrecedence() == DEFAULT_VALUE)) {
			LOGGER.info("All fields listOrderOfPrecedence are not set, re-setting all with default value");

			electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream()).forEach(e -> {
				final AtomicInteger counter = new AtomicInteger();
				final boolean allAreNumeric = e.getList().stream().allMatch(l -> StringUtils.isNumeric(l.getListIndentureNumber()));
				if (allAreNumeric) {
					e.getList().stream().sorted(Comparator.comparing(list -> Integer.valueOf(list.getListIndentureNumber())))
							.forEach(l -> {
								final int listOrderOfPrecedence = counter.incrementAndGet();
								l.setListOrderOfPrecedence(listOrderOfPrecedence);
								LOGGER.debug("Set listOrderOfPrecedence of list. [value: {}, list identification: {}]", listOrderOfPrecedence,
										l.getListIdentification());
							});
				} else {
					e.getList().forEach(l -> l.setListOrderOfPrecedence(counter.incrementAndGet()));
				}
			});
		}
	}

	private static void addEmptyListOrderOfPrecedenceIfNeeded(final List<ElectionGroupBallotType> electionGroups) {
		electionGroups.stream()
				.flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.map(ElectionInformationType::getEmptyList)
				.filter(Objects::nonNull)
				.filter(l -> l.getListOrderOfPrecedence() == DEFAULT_VALUE)
				.forEach(l -> {
					l.setListOrderOfPrecedence(EMPTY_LIST_ORDER_OF_PRECEDENCE);
					LOGGER.debug("Set listOrderOfPrecedence of empty list. [value: {}, list identification: {}]", EMPTY_LIST_ORDER_OF_PRECEDENCE,
							l.getListIdentification());
				});
	}

	private Map<String, Boolean> getParametersWriteIns(final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.isWriteInsAllowed() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::isWriteInsAllowed));
		}
	}

	private void addElectionsWriteIns(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {

		final List<PositionTextInfoType.PositionText> positionTexts = Arrays.stream(LanguageType.values())
				.map(lang -> {
					final String text = getReturnCodeWriteInDescriptionText(lang, contestParameters);
					return new PositionTextInfoType.PositionText()
							.withLanguage(lang)
							.withPositionTextLong(text)
							.withPositionTextShort(StringUtils.left(text, 100));
				})
				.toList();

		final PositionTextInfoType returnCodeWriteInDescription = new PositionTextInfoType()
				.withPositionText(positionTexts);

		final Map<String, Boolean> writeInsMap = getParametersWriteIns(contestParameters);
		electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					final boolean useWriteIns = writeInsMap.getOrDefault(e.getElection().getElectionIdentification(), false);
					e.getElection().setWriteInsAllowed(useWriteIns);
					if (useWriteIns) {
						final int numberOfMandates = e.getElection().getNumberOfMandates();
						for (int i = 1; i <= numberOfMandates; i++) {
							e.getWriteInPosition().add(new WriteInPositionType()
									.withWriteInPositionIdentification(
											StringUtil.generateUUIDFromString(e.getElection().getElectionIdentification() + i))
									.withPosition(i)
									.withReturnCodeWriteInDescriptionInfo(returnCodeWriteInDescription));
						}
					}
				});
	}

	private void addElectionsImplicitWriteInCandidatesIfNeeded(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		if (contestParameters.getElections() != null) {
			electionGroups.stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
					.forEach(electionInformationType ->
							contestParameters.getElections().getElection().stream()
									.filter(electionType -> electionType.getElectionIdentification()
											.equals(electionInformationType.getElection().getElectionIdentification()))
									.findFirst()
									.ifPresent(election ->
											electionInformationType.setImplicitWriteInCandidate(election.getImplicitWriteInCandidate()))
					);
		}
	}

	private String getReturnCodeWriteInDescriptionText(final LanguageType language, final ch.evoting.xmlns.parameter._5.ContestType contest) {
		if (contest.getReturnCodeWriteInDescriptionInfo() != null) {
			final PositionText returnCodeWriteInDescriptionText = contest.getReturnCodeWriteInDescriptionInfo().getPositionText().stream()
					.filter(positionText -> positionText.getLanguage().value().equals(language.value()))
					.findFirst()
					.orElse(null);
			if (returnCodeWriteInDescriptionText != null) {
				return returnCodeWriteInDescriptionText.getText();
			}
		}
		return null;
	}

	private void checkParametersOfElections(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
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

	private Map<String, BigInteger> getParametersAccumulation(final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.getCandidateAccumulation() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::getCandidateAccumulation));
		}
	}

	private void addElectionsAccumulation(final List<ElectionGroupBallotType> electionGroups,
			final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
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

	private void checkVotesQuestionsPositionsConsistency(final List<VoteInformationType> votesInformation) {
		votesInformation.stream()
				.flatMap(voteInformation -> voteInformation.getVote().getBallot().stream())
				.filter(ballot -> ballot.getVariantBallot() != null)
				.forEach(ballot -> {
					final VariantBallotType variantBallot = ballot.getVariantBallot();
					final String ballotIdentification = ballot.getBallotIdentification();

					final int nbrOfStandardQuestions = variantBallot.getStandardQuestion().size();
					final int nbrOfTieBreakQuestions = variantBallot.getTieBreakQuestion().size();
					final int totalNbrOfQuestions = nbrOfStandardQuestions + nbrOfTieBreakQuestions;

					final long standardQuestionsWithoutPosition = variantBallot.getStandardQuestion().stream()
							.filter(standardQuestion -> standardQuestion.getQuestionPosition() == DEFAULT_VALUE)
							.count();
					final long tieBreakQuestionsWithoutPosition = variantBallot.getTieBreakQuestion().stream()
							.filter(tieBreakQuestion -> tieBreakQuestion.getQuestionPosition() == DEFAULT_VALUE)
							.count();
					final long questionsWithoutPosition = standardQuestionsWithoutPosition + tieBreakQuestionsWithoutPosition;

					if (questionsWithoutPosition == 0 || questionsWithoutPosition == 1) {
						LOGGER.debug("Variant ballot '{}' : all questions have a position; checking monotonicity", ballotIdentification);

						final TreeSet<Integer> sortedQuestionPositions = Stream.concat(
										variantBallot.getStandardQuestion().stream().map(StandardQuestionType::getQuestionPosition),
										variantBallot.getTieBreakQuestion().stream().map(TieBreakQuestionType::getQuestionPosition))
								.collect(Collectors.toCollection(TreeSet::new));

						final Integer minPosition = sortedQuestionPositions.getFirst();
						final Integer maxPosition = sortedQuestionPositions.getLast();

						checkState(sortedQuestionPositions.size() == totalNbrOfQuestions && (minPosition == 0 || minPosition == 1)
										&& maxPosition - minPosition == totalNbrOfQuestions - 1,
								"Variant ballot '%s' : questions positions must be a strictly increasing sequence from 0 to %s or from 1 to %s",
								ballotIdentification, totalNbrOfQuestions - 1, totalNbrOfQuestions);

						if (minPosition == 0) {
							variantBallot.getStandardQuestion().forEach(standardQuestion -> {
								final int questionPosition = standardQuestion.getQuestionPosition();
								standardQuestion.setQuestionPosition(questionPosition + 1);
							});
							variantBallot.getTieBreakQuestion().forEach(tieBreakQuestion -> {
								final int questionPosition = tieBreakQuestion.getQuestionPosition();
								tieBreakQuestion.setQuestionPosition(questionPosition + 1);
							});
							LOGGER.warn("The variant ballot '{}' questions positions start from 0. They have been changed to start from 1.",
									ballotIdentification);
						}

						// Positions sequence is valid, sort for display.
						variantBallot.getStandardQuestion().sort(Comparator.comparingInt(StandardQuestionType::getQuestionPosition));
						variantBallot.getTieBreakQuestion().sort(Comparator.comparingInt(TieBreakQuestionType::getQuestionPosition));
					} else if (questionsWithoutPosition == totalNbrOfQuestions) {
						LOGGER.warn("Variant ballot '{}' : missing all questions positions; setting all values", ballotIdentification);
						final AtomicInteger counter = new AtomicInteger();
						variantBallot.getStandardQuestion()
								.forEach(standardQuestion -> standardQuestion.setQuestionPosition(counter.incrementAndGet()));
						variantBallot.getTieBreakQuestion()
								.forEach(tieBreakQuestion -> tieBreakQuestion.setQuestionPosition(counter.incrementAndGet()));
					} else {
						throw new IllegalStateException(String.format(
								"Variant ballot '%s' : missing position for some questions; either all questions must have a position or none",
								ballotIdentification));
					}
				});
	}

	private void checkVotesBallotsPositionsConsistency(final List<VoteInformationType> votesInformation) {
		votesInformation
				.forEach(voteInformation -> {
					final VoteType vote = voteInformation.getVote();
					final List<BallotType> ballots = vote.getBallot();
					final String voteIdentification = vote.getVoteIdentification();
					final int totalNbrOfBallots = ballots.size();

					final long ballotsWithoutPosition = ballots.stream()
							.filter(ballot -> ballot.getBallotPosition() == DEFAULT_VALUE)
							.count();

					if (ballotsWithoutPosition == 0 || ballotsWithoutPosition == 1) {
						LOGGER.debug("Vote '{}' : all ballots have a position; checking monotonicity", voteIdentification);

						final TreeSet<Integer> sortedBallotPositions = ballots.stream()
								.map(BallotType::getBallotPosition)
								.collect(Collectors.toCollection(TreeSet::new));

						final Integer minPosition = sortedBallotPositions.first();
						final Integer maxPosition = sortedBallotPositions.last();

						checkState(sortedBallotPositions.size() == totalNbrOfBallots && (minPosition == 0 || minPosition == 1)
										&& maxPosition - minPosition == totalNbrOfBallots - 1,
								"Vote '%s' : ballots positions must be a strictly increasing sequence from 0 to %s or from 1 to %s",
								voteIdentification,
								totalNbrOfBallots - 1, totalNbrOfBallots);

						if (minPosition == 0) {
							ballots.forEach(ballot -> {
								final int ballotPosition = ballot.getBallotPosition();
								ballot.setBallotPosition(ballotPosition + 1);
							});
							LOGGER.warn("The vote '{}' ballots positions start from 0. They have been changed to start from 1.", voteIdentification);
						}

						// Positions sequence is valid, sort for display.
						ballots.sort(Comparator.comparing(BallotType::getBallotPosition));
					} else if (ballotsWithoutPosition == totalNbrOfBallots) {
						LOGGER.warn("Vote '{}' : missing all ballots positions; setting all values", voteIdentification);
						final AtomicInteger counter = new AtomicInteger();
						ballots.forEach(ballot -> ballot.setBallotPosition(counter.incrementAndGet()));
					} else {
						throw new IllegalStateException(String.format(
								"Vote '%s' : missing position for some ballots; either all ballots must have a position or none",
								voteIdentification));
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

	private <T> void checkLanguageCorrectness(final List<T> list, final Function<T, LanguageType> getLanguage, final String name) {
		final List<LanguageType> languages = Arrays.stream(LanguageType.values()).sorted().toList();
		checkState(list.stream().map(getLanguage).sorted().toList().equals(languages),
				"%s must contain exactly the defined languages %s", name, LANGUAGES);
	}

	private <T> void checkLanguageCorrectness(final List<T> list, final Function<T, LanguageType> getLanguage, final String id,
			final String elementName,
			final String fieldName) {
		final List<LanguageType> languages = Arrays.stream(LanguageType.values()).sorted().toList();
		checkState(list.stream().map(getLanguage).sorted().toList().equals(languages),
				"%s '%s' : %s must contain exactly the defined languages %s", elementName, id, fieldName,
				LANGUAGES);
	}

	private void checkLanguagesConsistency(final ContestType contest) {
		// contest
		checkState(contest.getContestDescription() != null, "ContestDescription must be defined in source file");

		checkLanguageCorrectness(contest.getContestDescription().getContestDescriptionInfo(),
				ContestDescriptionInformationType.ContestDescriptionInfo::getLanguage, "contestDescription");

		// election group
		contest.getElectionGroupBallot().forEach(eg -> {
			checkState(eg.getElectionGroupDescription() != null, "ElectionGroupDescription must be defined");

			checkLanguageCorrectness(eg.getElectionGroupDescription().getElectionGroupDescriptionInfo(),
					ElectionGroupDescriptionInformationType.ElectionGroupDescriptionInfo::getLanguage,
					eg.getElectionGroupIdentification(),
					"electionGroupBallot",
					"electionGroupDescription");
		});

		//election
		contest.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.forEach(e -> {
					checkState(e.getElection().getElectionDescription() != null, "ElectionDescription must be defined in source file");

					checkLanguageCorrectness(e.getElection().getElectionDescription().getElectionDescriptionInfo(),
							ElectionDescriptionInformationType.ElectionDescriptionInfo::getLanguage, e.getElection().getElectionIdentification(),
							"election",
							"electionDescription");

					final String CANDIDATE_ELEMENT_NAME = "candidate";
					final String LIST_ELEMENT_NAME = "list";

					e.getCandidate().forEach(c -> {
						checkState(c.getCandidateText() != null, "CandidateText must be defined in source file");

						checkLanguageCorrectness(c.getCandidateText().getCandidateTextInfo(),
								CandidateTextInformationType.CandidateTextInfo::getLanguage,
								c.getCandidateIdentification(), CANDIDATE_ELEMENT_NAME, "candidateText");
						if (c.getOccupationalTitle() != null) {
							checkLanguageCorrectness(c.getOccupationalTitle().getOccupationalTitleInfo(),
									OccupationalTitleInformationType.OccupationalTitleInfo::getLanguage, c.getCandidateIdentification(),
									CANDIDATE_ELEMENT_NAME,
									"occupationalTitle");
						}

						if (c.getRole() != null) {
							checkLanguageCorrectness(c.getRole().getRoleInfo(),
									RoleInformationType.RoleInfo::getLanguage,
									c.getCandidateIdentification(),
									CANDIDATE_ELEMENT_NAME,
									"role");
						}

						if (c.getPartyAffiliation() != null) {
							checkLanguageCorrectness(c.getPartyAffiliation().getPartyAffiliationInfo(),
									PartyAffiliationformationType.PartyAffiliationInfo::getLanguage,
									c.getCandidateIdentification(),
									CANDIDATE_ELEMENT_NAME,
									"partyAffiliation");
						}

						if (c.getIncumbent() != null && c.getIncumbent().getIncumbentText() != null) {
							checkLanguageCorrectness(c.getIncumbent().getIncumbentText().getIncumbentTextInfo(),
									IncumbentTextInfoType::getLanguage,
									c.getCandidateIdentification(),
									CANDIDATE_ELEMENT_NAME,
									"incumbentText");
						}
					});
					e.getList().forEach(l -> {
						checkLanguageCorrectness(l.getListDescription().getListDescriptionInfo(),
								ListDescriptionInformationType.ListDescriptionInfo::getLanguage,
								l.getListIdentification(), LIST_ELEMENT_NAME, "listDescription");
						l.getCandidatePosition().forEach(cp -> checkLanguageCorrectness(cp.getCandidateTextOnPosition().getCandidateTextInfo(),
								CandidateTextInformationType.CandidateTextInfo::getLanguage, cp.getCandidateIdentification(),
								CANDIDATE_ELEMENT_NAME,
								"candidateTextOnPosition"));

						if (l.getVarListText1() != null) {
							checkLanguageCorrectness(l.getVarListText1().getVarListTextInfo(),
									VarListTextInfoType::getLanguage,
									l.getListIdentification(),
									LIST_ELEMENT_NAME,
									"varListText1");
						}

						if (l.getVarListText2() != null) {
							checkLanguageCorrectness(l.getVarListText2().getVarListTextInfo(),
									VarListTextInfoType::getLanguage,
									l.getListIdentification(),
									LIST_ELEMENT_NAME,
									"varListText2");
						}
					});
					e.getListUnion().forEach(l -> checkLanguageCorrectness(l.getListUnionDescription().getListUnionDescriptionInfo(),
							ListUnionDescriptionType.ListUnionDescriptionInfo::getLanguage, l.getListUnionIdentification(), "list-union",
							"listUnionDescription"));
				});
		// vote
		contest.getVoteInformation().forEach(v -> {
			checkState(v.getVote().getVoteDescription() != null, "VoteDescription must be defined in source file");

			checkLanguageCorrectness(v.getVote().getVoteDescription().getVoteDescriptionInfo(),
					VoteDescriptionInformationType.VoteDescriptionInfo::getLanguage, v.getVote().getVoteIdentification(), "vote",
					"voteDescription");
			v.getVote().getBallot().forEach(b -> {
				if (b.getBallotDescription() != null) {
					checkLanguageCorrectness(b.getBallotDescription().getBallotDescriptionInfo(),
							BallotDescriptionInformationType.BallotDescriptionInfo::getLanguage, b.getBallotIdentification(), "ballot",
							"ballotDescription");
				}

				final String QUESTION_ELEMENT_NAME = "question";
				final String ANSWER_ELEMENT_NAME = "answer";
				final String ANSWER_FIELD_NAME = "answer";
				final String BALLOT_QUESTION_FIELD_NAME = "ballotQuestion";

				if (b.getStandardBallot() != null) {
					//standardBallot
					checkLanguageCorrectness(b.getStandardBallot().getBallotQuestion().getBallotQuestionInfo(),
							BallotQuestionType.BallotQuestionInfo::getLanguage, b.getStandardBallot().getQuestionIdentification(),
							QUESTION_ELEMENT_NAME,
							BALLOT_QUESTION_FIELD_NAME);
					b.getStandardBallot().getAnswer().forEach(
							a -> checkLanguageCorrectness(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(),
									ANSWER_ELEMENT_NAME,
									ANSWER_FIELD_NAME));
				} else {
					//variantBallot
					b.getVariantBallot().getStandardQuestion().forEach(sq -> {
						checkLanguageCorrectness(sq.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionType.BallotQuestionInfo::getLanguage,
								sq.getQuestionIdentification(), QUESTION_ELEMENT_NAME, BALLOT_QUESTION_FIELD_NAME);
						sq.getAnswer().forEach(
								a -> checkLanguageCorrectness(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(),
										ANSWER_ELEMENT_NAME,
										ANSWER_FIELD_NAME));
					});
					b.getVariantBallot().getTieBreakQuestion().forEach(tbq -> {
						checkLanguageCorrectness(tbq.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionType.BallotQuestionInfo::getLanguage,
								tbq.getQuestionIdentification(), QUESTION_ELEMENT_NAME, BALLOT_QUESTION_FIELD_NAME);
						tbq.getAnswer().forEach(
								a -> checkLanguageCorrectness(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(),
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
		final LocalDateTime evotingFromDate = LocalDateTimeUtils.fromXMLGregorianCalendar(c.getEvotingFromDate());
		final LocalDateTime evotingToDate = LocalDateTimeUtils.fromXMLGregorianCalendar(c.getEvotingToDate());

		// These are mandatory checks that cannot be bypassed.
		checkState(evotingToDate.isAfter(LocalDateTimeUtils.now()), "evotingToDate must be in the future.");
		checkState(evotingFromDate.isBefore(evotingToDate), "evotingFromDate must be before evotingToDate.");

		if (bypassCheckConsistencyDates) {
			LOGGER.warn("As defined by configuration, bypassing the evoting dates consistency checks...");
			return;
		}

		checkState(evotingFromDate.isAfter(LocalDateTimeUtils.now()), "evotingFromDate must be in the future.");

		final LocalDate dtFromLocal = evotingFromDate.toLocalDate();
		final LocalDate dtToLocal = evotingToDate.toLocalDate();
		final long daysBetween = DAYS.between(dtFromLocal, dtToLocal);

		checkState(daysBetween >= evotingFromToMinDeltaAllowed,
				"the number of days between 'eVoting date from' and 'eVoting date to' must be at least %s", evotingFromToMinDeltaAllowed);

		final LocalDate contestDate = LocalDateUtils.fromXMLGregorianCalendar(c.getContestDate());
		if (evotingToDate.isAfter(contestDate.atStartOfDay())) {
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
				final List<StandardAnswerType> answers = b.getStandardBallot().getAnswer();
				checkState(answers.stream()
								.filter(a -> a.getStandardAnswerType() != null && a.getStandardAnswerType().equals(EMPTY_ANSWER)).count() <= 1,
						"vote '%s' standard ballot question has more than one blank answer", v.getVote().getVoteIdentification());
				checkEmptyAnswerTexts(answers, b.getStandardBallot().getQuestionIdentification());
			}
			if (b.getVariantBallot() != null) {
				b.getVariantBallot().getStandardQuestion().forEach(q -> {
					final List<StandardAnswerType> answers = q.getAnswer();
					checkState(
							answers.stream().filter(a -> a.getStandardAnswerType() != null && a.getStandardAnswerType().equals(EMPTY_ANSWER))
									.count()
									<= 1, "vote '%s' variant ballot standard question %s has more than one blank answer",
							v.getVote().getVoteIdentification(), q.getQuestionPosition());
					checkEmptyAnswerTexts(answers, q.getQuestionIdentification());
				});

				b.getVariantBallot().getTieBreakQuestion().forEach(
						q -> checkState(q.getAnswer().stream().filter(TiebreakAnswerType::isHiddenAnswer).count() == 1,
								"vote '%s' variant ballot tiebreak question %s has not exactly one hidden answer",
								v.getVote().getVoteIdentification(),
								q.getQuestionPosition()));
			}
		}));
	}

	private static void checkEmptyAnswerTexts(final List<StandardAnswerType> answers, final String questionId) {
		answers.stream()
				.filter(answer -> EMPTY_ANSWER.equals(answer.getStandardAnswerType()))
				.collect(MoreCollectors.toOptional())
				.ifPresentOrElse(emptyAnswer -> checkState(emptyAnswer.getAnswerInfo().stream().allMatch(answerInfo -> {
					final LanguageType language = answerInfo.getLanguage();
					return switch (language) {
						case LanguageType.DE -> answerInfo.getAnswer().equals("Leer");
						case LanguageType.FR -> answerInfo.getAnswer().equals("Blanc");
						case LanguageType.IT -> answerInfo.getAnswer().equals("Bianco");
						case LanguageType.RM -> answerInfo.getAnswer().equals("Vid");
					};
				})), () -> {
					throw new IllegalStateException(
							String.format("The question '%s' must have an empty answer.", questionId));
				});
	}

	public void checkConsistency(final ContestType c, final ch.evoting.xmlns.parameter._5.ContestType contestParameters) {
		checkContestExists(c);
		checkEVotingDates(c);
		checkLanguagesConsistency(c);
		checkContestIdentifiersUnique(c);
		checkLimitOfSameOccurrencesOfCandidateInList(c);
		checkSameNamesOfCandidates(c);
		checkReferenceOnPositionOnCandidateIsFilled(c);
		checkAnswerConsistency(c);
		checkParametersOfElections(c.getElectionGroupBallot(), contestParameters);
		checkPrimarySecondaryElectionConsistency(c);
		checkVoteIdentificationsDistinctness(c);
		checkElectionIdentifiersDistinctness(c);
		checkProportionalElectionConsistency(c);
		checkMajoralElectionConsistency(c);
		checkWriteInsConsistency(c);
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
		checkState(secondaryCandidates.stream()
						.filter(c -> c.getEligibility() == EligibilityType.EXPLICIT)
						.allMatch(c -> primaryCandidateIds.contains(c.getCandidateIdentification())),
				"At least one non already elected candidate in the secondary election is not candidate in the primary election. [electionGroupId: %s]",
				eg.getElectionGroupIdentification());

		checkState(secondaryCandidates.stream()
						.filter(c -> c.getEligibility() == EligibilityType.IMPLICIT)
						.noneMatch(c -> primaryCandidateIds.contains(c.getCandidateIdentification())),
				"At least one implicit candidate in the secondary election is also a candidate in the primary election. [electionGroupId: %s]",
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

	private void checkReferenceOnPositionOnCandidateIsFilled(final ContestType contest) {
		contest.getElectionGroupBallot().stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.forEach(e -> {
					final List<CandidateType> candidates = e.getCandidate();
					if (candidates.isEmpty()) {
						return;
					}

					final List<CandidateType> candidatesWithoutReferenceOnPosition = candidates.stream()
							.filter(c -> StringUtil.isNullOrEmpty(c.getReferenceOnPosition()))
							.toList();

					if (candidates.size() == candidatesWithoutReferenceOnPosition.size()) {
						IntStream.rangeClosed(1, candidates.size())
								.forEach(i -> candidates.get(i - 1).setReferenceOnPosition(String.valueOf(i)));
						LOGGER.warn(
								"The candidateReference in the election were not provided and were assigned automatically. [electionIdentification: {}]",
								e.getElection().getElectionIdentification());
					} else if (!candidatesWithoutReferenceOnPosition.isEmpty()) {
						throw new IllegalStateException(
								"The field candidateReference for one or more candidates was not provided: [electionIdentification: %s, candidateId: %s]".formatted(
										e.getElection().getElectionIdentification(),
										candidatesWithoutReferenceOnPosition.getFirst().getCandidateIdentification()));
					}
				});
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

		checkState(hasNoDuplicates(ImmutableList.from(identifications)), "The votes contain duplicate identifications");
	}

	private void checkElectionIdentifiersDistinctness(final ContestType c) {
		final List<ElectionGroupBallotType> electionGroupBallot = c.getElectionGroupBallot();

		final List<String> identifications = electionGroupBallot.stream()
				.flatMap(eg -> checkElectionIdentifiersDistinctnessPerElection(eg).stream())
				.toList();

		checkState(hasNoDuplicates(ImmutableList.from(identifications)), "The elections contain duplicate identifications");
	}

	private List<String> checkElectionIdentifiersDistinctnessPerElection(final ElectionGroupBallotType electionGroupBallot) {
		return electionGroupBallot.getElectionInformation().stream()
				.map(electionInformation -> {
					final String electionIdentification = electionInformation.getElection().getElectionIdentification();
					final Stream<String> listIdentifications = electionInformation.getList().stream().map(ListType::getListIdentification);

					final List<String> identifications = Streams.concat(Stream.of(electionIdentification), listIdentifications)
							.toList();
					checkState(hasNoDuplicates(ImmutableList.from(identifications)),
							"The election contains duplicate identifications. [election id: %s]",
							electionIdentification);
					return identifications;
				}).toList().getFirst();
	}

	private void checkProportionalElectionConsistency(final ContestType contestType) {
		final String electionIds = contestType.getElectionGroupBallot().stream()
				.flatMap(electionGroupBallot -> electionGroupBallot.getElectionInformation().stream())
				.map(ElectionInformationType::getElection)
				.filter(election -> ELECTION_PROPORTIONAL_TYPE.equals(election.getTypeOfElection()))
				.filter(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType::isWriteInsAllowed)
				.map(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType::getElectionIdentification)
				.collect(Collectors.joining(", "));

		checkState(electionIds.isEmpty(), "Proportional elections cannot have write-in candidates. [electionIds: %s]", electionIds);
	}

	private void checkMajoralElectionConsistency(final ContestType contestType) {
		final String electionIds = contestType.getElectionGroupBallot().stream()
				.flatMap(electionGroupBallot -> electionGroupBallot.getElectionInformation().stream())
				.map(ElectionInformationType::getElection)
				.filter(election -> ELECTION_MAJORITY_TYPE.equals(election.getTypeOfElection()))
				.filter(election -> election.getCandidateAccumulation().intValueExact() != 1)
				.map(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType::getElectionIdentification)
				.collect(Collectors.joining(", "));

		checkState(electionIds.isEmpty(), "Candidate accumulation must be 1 for majoral elections. [electionIds: %s]", electionIds);
	}

	private void checkWriteInsConsistency(final ContestType contestType) {
		contestType.getElectionGroupBallot().stream()
				.flatMap(electionGroupBallot -> electionGroupBallot.getElectionInformation().stream())
				.forEach(electionInformation -> {
					if (electionInformation.getElection().isWriteInsAllowed()) {
						checkState(electionInformation.getWriteInPosition() != null
										&& electionInformation.getWriteInPosition().size() == electionInformation.getElection().getNumberOfMandates(),
								"The election allows write-ins and must contain as many write-in positions as there are mandates. [electionId: %s, numberOfMandates: %s]",
								electionInformation.getElection().getElectionIdentification(),
								electionInformation.getElection().getNumberOfMandates());
					} else {
						checkState(electionInformation.getWriteInPosition() == null || electionInformation.getWriteInPosition().isEmpty(),
								"The election does not allow write-ins and must therefore not have any write-in positions. [electionId: %s]",
								electionInformation.getElection().getElectionIdentification());
						checkState(electionInformation.getImplicitWriteInCandidate() == null || electionInformation.getWriteInPosition().isEmpty(),
								"The election does not allow write-ins and must therefore not have any implicit write-in candidates. [electionId: %s]",
								electionInformation.getElection().getElectionIdentification());
					}
				});
	}

	public ContestType sanitize(final ContestType contest) {
		final String asString = MarshallTool.marshall(contest, "contest");

		final String htmlSanitized = asString
				// html tags
				.replaceAll("(?s)&lt;[^&]*?&gt;", "");
		if (!asString.equals(htmlSanitized)) {
			LOGGER.warn("Contest contained HTML tags which have been removed. [contestIdentification: {}]", contest.getContestIdentification());
		}

		final String mdImagesSanitized = htmlSanitized
				// MD images
				.replaceAll("!\\[[^\\]]*]\\([^)]*\\)", "");
		if (!htmlSanitized.equals(mdImagesSanitized)) {
			LOGGER.warn("Contest contained MD image links which have been removed. [contestIdentification: {}]", contest.getContestIdentification());
		}

		final String mdLinksSanitized = mdImagesSanitized
				//MD links
				.replaceAll("\\[[^\\]]*]\\([^)]*\\)", "");
		if (!mdImagesSanitized.equals(mdLinksSanitized)) {
			LOGGER.warn("Contest contained MD links which have been removed. [contestIdentification: {}]", contest.getContestIdentification());
		}

		return MarshallTool.unmarshall(mdLinksSanitized, ContestType.class);
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