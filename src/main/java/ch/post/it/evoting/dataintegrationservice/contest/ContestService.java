/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.swisspush.supermachine.BeanScanner.from;

import java.math.BigInteger;
import java.time.LocalDate;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.evoting.xmlns.parameter._4.AnswerTranslationType;
import ch.evoting.xmlns.parameter._4.ContestType.IncumbentText;
import ch.evoting.xmlns.parameter._4.DefaultAnswerTranslationType;
import ch.evoting.xmlns.parameter._4.ElectionType;
import ch.evoting.xmlns.parameter._4.EmptyCandidateTextInfo;
import ch.evoting.xmlns.parameter._4.SortingType;
import ch.post.it.evoting.dataintegrationservice.common.MarshallTool;
import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.parameter.ParameterMapper;
import ch.post.it.evoting.domain.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.BallotDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ElectionDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ExtendedAuthenticationKeysDefinitionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.IncumbentType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.TieBreakQuestionTypeWithReferences;
import ch.post.it.evoting.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.UiPropertiesType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoteInformationType;

@Service
public class ContestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContestService.class);

	private static final int MINIMAL_REQUIRED_LANGUAGES = 4;

	public ContestService() {
		//Intentionally left blank
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
				answerType = new BigInteger(PropertyProvider.getPropertyValue("votes.defaultAnswerType.standard", "2"));
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
				answerType = new BigInteger(PropertyProvider.getPropertyValue("votes.defaultAnswerType.tiebreak", "4"));
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
						throw new IllegalStateException("answerType '" + e.getKey() + "' not unique within the parameters");
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
						throw new IllegalStateException("answerType '" + e.getKey() + "' not unique within the parameters");
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
						throw new IllegalStateException("question id '" + e.getKey() + "' not unique within the parameters");
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
						throw new IllegalStateException("question id '" + e.getKey() + "' not unique within the parameters");
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
				final StandardBallotType question = ballot.getStandardBallot();
				if (question.getAnswer().isEmpty()) {
					question.getAnswer()
							.addAll(getStandardAnswers(question.getQuestionIdentification(), question.getAnswerType(), specificStandardAnswers,
									defaultStandardAnswers));
				}
			} else {
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
		}));
	}

	public ContestType build(final ContestType contest, final List<VoteInformationType> votes, final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {

		if (contestParameters.getSorting() != null) {
			final Map<String, BigInteger> electionSortDefinition = contestParameters.getSorting().getElection().stream()
					.collect(Collectors.toMap(SortingType.Election::getElectionIdentification, SortingType.Election::getSequenceNumber));
			elections.sort((o1, o2) -> {
				final BigInteger val1 = electionSortDefinition.getOrDefault(o1.getElection().getElectionIdentification(),
						BigInteger.valueOf(Integer.MAX_VALUE));
				final BigInteger val2 = electionSortDefinition.getOrDefault(o2.getElection().getElectionIdentification(),
						BigInteger.valueOf(Integer.MAX_VALUE));
				return val1.compareTo(val2);
			});

			final Map<String, BigInteger> voteSortDefinition = contestParameters.getSorting().getVote().stream()
					.collect(Collectors.toMap(SortingType.Vote::getVoteIdentification, SortingType.Vote::getSequenceNumber));
			votes.sort((o1, o2) -> {
				final BigInteger val1 = voteSortDefinition.getOrDefault(o1.getVote().getVoteIdentification(), BigInteger.valueOf(Integer.MAX_VALUE));
				final BigInteger val2 = voteSortDefinition.getOrDefault(o2.getVote().getVoteIdentification(), BigInteger.valueOf(Integer.MAX_VALUE));
				return val1.compareTo(val2);
			});
		}

		addVotesAnswers(votes, contestParameters);
		addVotesQuestionPositionIfNeeded(votes);
		addVotesStandardQuestionReferenceIfNeeded(votes);
		contest.getVoteInformation().addAll(votes);

		addElectionsOrderOfPrecedenceIfNeeded(elections);
		addEmptyListIfNeeded(elections);
		addCandidatesInEmptyListsIfNeeded(elections, contestParameters);
		addCandidatesListIdentifications(elections);
		warnIfCandidatesOutOfLists(elections);
		addCandidatePositionInCandidateOnlyElections(elections);
		addIncumbentTextIfNeeded(elections, contestParameters.getIncumbentText());

		addElectionsWriteIns(elections, contestParameters);
		addElectionsAccumulation(elections, contestParameters);
		addElectionsMinimalCandidateSelectionInList(elections, contestParameters);
		addElectionsUiProperties(elections, contestParameters);

		contest.getElectionInformation().addAll(elections);

		if (contestParameters.getEvotingFromDate() != null) {
			contest.setEvotingFromDate(contestParameters.getEvotingFromDate());
		}
		checkState(contest.getEvotingFromDate() != null, "evotingFromDate must be either defined in contestor in parameters file");

		if (contestParameters.getEvotingToDate() != null) {
			contest.setEvotingToDate(contestParameters.getEvotingToDate());
		}
		checkState(contest.getEvotingToDate() != null, "evotingFromDate must be either defined in contest or in parameters file");

		contest.setContestDefaultLanguage(LanguageType.fromValue(contestParameters.getDefaultLanguage().value()));

		if (contestParameters.getElectoralAuthority() != null) {
			contest.setElectoralAuthority(ParameterMapper.INSTANCE.map(contestParameters.getElectoralAuthority()));
		}

		if (contestParameters.getUiProperties() != null) {
			contest.setUiProperties(ParameterMapper.INSTANCE.map(contestParameters.getUiProperties()));
		} else if (elections.stream().anyMatch(e -> e.getElection().getUiProperties() == null)) {
			// UiProperties are mandatory for elections. Issue a warning if they are missing.
			// If an election has no uiproperties, then the global uiproperty has to be set.
			LOGGER.warn("Missing uiProperties are mandatory for elections.");
		}

		return contest;
	}

	private Map<String, BigInteger> getParametersMinimalCandidateSelectionInList(final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.getMinimalCandidateSelectionInList() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, ElectionType::getMinimalCandidateSelectionInList));
		}
	}

	private void addElectionsMinimalCandidateSelectionInList(final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, BigInteger> parametersMinimalCandidateSelectionInList = getParametersMinimalCandidateSelectionInList(contestParameters);
		elections.stream().map(ElectionInformationType::getElection).forEach(e -> e.setMinimalCandidateSelectionInList(
				parametersMinimalCandidateSelectionInList.getOrDefault(e.getElectionIdentification(), BigInteger.ONE)));
	}

	private void addCandidatePositionInCandidateOnlyElections(final List<ElectionInformationType> elections) {
		elections.stream().filter(this::isCandidateOnlyElection).forEach(e -> {
			final AtomicLong pos = new AtomicLong(1);
			e.getCandidate().forEach(c -> c.setPosition(BigInteger.valueOf(pos.getAndIncrement())));
		});
	}

	private boolean isCandidateOnlyElection(final ElectionInformationType election) {
		return election.getList() != null && election.getList().size() == 1 && election.getList().stream().allMatch(ListType::isListEmpty);
	}

	private void warnIfCandidatesOutOfLists(final List<ElectionInformationType> elections) {
		elections.forEach(e -> e.getCandidate().stream().filter(c -> e.getList().stream().flatMap(l -> l.getCandidatePosition().stream())
						.noneMatch(cp -> c.getCandidateIdentification().equalsIgnoreCase(cp.getCandidateIdentification())))
				.forEach(c -> LOGGER.warn("Candidate {} is not present in any list", c.getCandidateIdentification())));
	}

	private void addIncumbentTextIfNeeded(final List<ElectionInformationType> elections, final IncumbentText incumbentText) {
		from(elections).find(IncumbentType.class).stream().filter(IncumbentType::isIncumbent)
				.forEach(type -> type.setIncumbentText(ParameterMapper.INSTANCE.map(incumbentText)));
	}

	private void addVotesStandardQuestionReferenceIfNeeded(final List<VoteInformationType> votes) {
		from(votes).find(VariantBallotType.class).stream().forEach(vb -> {
			for (int qCounter = 0; qCounter < vb.getTieBreakQuestion().size(); qCounter++) {
				final TieBreakQuestionTypeWithReferences tbq = (TieBreakQuestionTypeWithReferences) vb.getTieBreakQuestion().get(qCounter);

				for (int aCounter = 0; aCounter < tbq.getAnswer().size() && aCounter < 2; aCounter++) {
					final TiebreakAnswerType tba = tbq.getAnswer().get(aCounter);
					if (tba.getStandardQuestionReference() == null) {
						tba.setStandardQuestionReference(aCounter == 0 ? tbq.getReferencedQuestion1() : tbq.getReferencedQuestion2());
						if (tba.getStandardQuestionReference() == null) {
							LOGGER.warn("ReferencedQuestion for tieBreakQuestion with identification {} and answer with identification {} is null",
									tbq.getQuestionIdentification(), tba.getAnswerIdentification());
						}
					}
				}
			}
		});
	}

	private void addCandidatesListIdentifications(final List<ElectionInformationType> elections) {
		from(elections).find(ListType.class).stream().forEach(list -> list.getCandidatePosition().forEach(p -> {
			if (p.getCandidateIdentification() == null) {
				p.setCandidateListIdentification(StringUtil.generateUUIDFromString(list.getListIdentification() + "." + p.getPositionOnList()));
			} else {
				p.setCandidateListIdentification(
						StringUtil.generateUUIDFromString(list.getListIdentification() + "." + p.getCandidateIdentification()));
			}
		}));
	}

	private void addEmptyListIfNeeded(final List<ElectionInformationType> elections) {
		elections.stream().filter(this::hasNoList).forEach(this::addEmptyList);
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
				.withListIndentureNumber("99").withListDescription(description).withListOrderOfPrecedence(BigInteger.valueOf(99)));

	}

	private void addCandidatesInEmptyListsIfNeeded(final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		elections.forEach(
				e -> e.getList().stream().filter(l -> l.isListEmpty() && (l.getCandidatePosition() == null || l.getCandidatePosition().isEmpty()))
						.forEach(l -> appendCandidates(l, e.getElection().getNumberOfMandates().intValue(), contestParameters)));
	}

	private void appendCandidates(final ListType l, final int numberOfMandates, final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
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
					.withPositionOnList(BigInteger.valueOf(i)).withCandidateTextOnPosition(text);

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

	private void addElectionsOrderOfPrecedenceIfNeeded(final List<ElectionInformationType> elections) {
		if (elections.stream().flatMap(e -> e.getList().stream()).anyMatch(l -> l.getListOrderOfPrecedence() == null)) {
			LOGGER.info("All fields listOrderOfPrecedence are not set, re-setting all with default value");
			elections.forEach(e -> {
				final AtomicLong counter = new AtomicLong();
				final boolean allAreNumeric = e.getList().stream().allMatch(l -> StringUtils.isNumeric(l.getListIndentureNumber()));
				if (allAreNumeric) {
					e.getList().stream().sorted(Comparator.comparing(list -> Integer.valueOf(list.getListIndentureNumber())))
							.forEach(l -> l.setListOrderOfPrecedence(BigInteger.valueOf(counter.incrementAndGet())));
				} else {
					e.getList().forEach(l -> l.setListOrderOfPrecedence(BigInteger.valueOf(counter.incrementAndGet())));
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

	private void addElectionsWriteIns(final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, Boolean> writeInsMap = getParametersWriteIns(contestParameters);
		elections.forEach(e -> e.getElection().setWriteInsAllowed(writeInsMap.getOrDefault(e.getElection().getElectionIdentification(), false)));
	}

	private Map<String, UiPropertiesType> getParametersElectionsUiProperties(final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() == null) {
			return new HashMap<>();
		} else {
			return contestParameters.getElections().getElection().stream().filter(e -> e.getUiProperties() != null)
					.collect(Collectors.toMap(ElectionType::getElectionIdentification, p -> ParameterMapper.INSTANCE.map(p.getUiProperties())));
		}
	}

	private void addElectionsUiProperties(final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, UiPropertiesType> writeUiPropertiesMap = getParametersElectionsUiProperties(contestParameters);
		elections.forEach(e -> {
			if (writeUiPropertiesMap.containsKey(e.getElection().getElectionIdentification())) {
				e.getElection().setUiProperties(writeUiPropertiesMap.get(e.getElection().getElectionIdentification()));
			}
		});
	}

	private void checkParametersOfElections(final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		if (contestParameters.getElections() != null) {
			final Set<String> electionsIds = elections.stream().map(e -> e.getElection().getElectionIdentification()).collect(Collectors.toSet());
			contestParameters.getElections().getElection().forEach(e -> {
				if (!electionsIds.contains(e.getElectionIdentification())) {
					throw new IllegalStateException(
							"election identifier '" + e.getElectionIdentification() + "' defined in the parameter file doesn't exist in the contest");
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

	private void addElectionsAccumulation(final List<ElectionInformationType> elections,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {
		final Map<String, BigInteger> accumulationMap = getParametersAccumulation(contestParameters);
		elections.forEach(e -> {
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
			if (vb.getStandardQuestion().stream().anyMatch(b -> b.getQuestionPosition() == null)) {
				final AtomicLong counter = new AtomicLong();
				LOGGER.info("Variant ballot '{}' : missing standard question positions; resetting all values", h.keySet().stream().findFirst().get());
				vb.getStandardQuestion().forEach(q -> q.setQuestionPosition(BigInteger.valueOf(counter.incrementAndGet())));
			}
			if (vb.getTieBreakQuestion().stream().anyMatch(b -> b.getQuestionPosition() == null)) {
				final AtomicLong counter = new AtomicLong();
				LOGGER.info("Variant ballot '{}' : missing tie-break question positions; resetting all values",
						h.keySet().stream().findFirst().get());
				vb.getTieBreakQuestion().forEach(q -> q.setQuestionPosition(BigInteger.valueOf(counter.incrementAndGet())));
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
		checkState(list.stream().map(getLanguage).distinct().count() >= MINIMAL_REQUIRED_LANGUAGES, "%s must contain at least 4 distinct languages",
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
		contest.getElectionInformation().forEach(e -> {
			checkState(e.getElection().getElectionDescription() != null, "ElectionDescription must be defined in source file");

			checkLanguageCount(e.getElection().getElectionDescription().getElectionDescriptionInfo(),
					ElectionDescriptionInformationType.ElectionDescriptionInfo::getLanguage, e.getElection().getElectionIdentification(), "election",
					"electionDescription");
			e.getCandidate().forEach(c -> {
				checkState(c.getCandidateText() != null, "CandidateText must be defined in source file");

				checkLanguageCount(c.getCandidateText().getCandidateTextInfo(), CandidateTextInformationType.CandidateTextInfo::getLanguage,
						c.getCandidateIdentification(), "candidate", "candidateText");
				if (c.getOccupationalTitle() != null) {
					checkLanguageCount(c.getOccupationalTitle().getOccupationalTitleInfo(),
							OccupationalTitleInformationType.OccupationalTitleInfo::getLanguage, c.getCandidateIdentification(), "candidate",
							"occupationalTitle");
				}
			});
			e.getList().forEach(l -> {
				checkLanguageCount(l.getListDescription().getListDescriptionInfo(), ListDescriptionInformationType.ListDescriptionInfo::getLanguage,
						l.getListIdentification(), "list", "listDescription");
				l.getCandidatePosition().forEach(cp -> checkLanguageCount(cp.getCandidateTextOnPosition().getCandidateTextInfo(),
						CandidateTextInformationType.CandidateTextInfo::getLanguage, cp.getCandidateIdentification(), "candidate",
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
					VoteDescriptionInformationType.VoteDescriptionInfo::getLanguage, v.getVote().getVoteIdentification(), "vote", "voteDescription");
			v.getVote().getBallot().forEach(b -> {
				if (b.getBallotDescription() != null) {
					checkLanguageCount(b.getBallotDescription().getBallotDescriptionInfo(),
							BallotDescriptionInformationType.BallotDescriptionInfo::getLanguage, b.getBallotIdentification(), "ballot",
							"ballotDescription");
				}
				if (b.getStandardBallot() != null) {
					//standardBallot
					checkLanguageCount(b.getStandardBallot().getBallotQuestion().getBallotQuestionInfo(),
							BallotQuestionType.BallotQuestionInfo::getLanguage, b.getStandardBallot().getQuestionIdentification(), "question",
							"ballotQuestion");
					b.getStandardBallot().getAnswer().forEach(
							a -> checkLanguageCount(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(), "answer",
									"answer"));
				} else {
					//variantBallot
					b.getVariantBallot().getStandardQuestion().forEach(sq -> {
						checkLanguageCount(sq.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionType.BallotQuestionInfo::getLanguage,
								sq.getQuestionIdentification(), "question", "ballotQuestion");
						sq.getAnswer().forEach(
								a -> checkLanguageCount(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(), "answer",
										"answer"));
					});
					b.getVariantBallot().getTieBreakQuestion().forEach(tbq -> {
						checkLanguageCount(tbq.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionType.BallotQuestionInfo::getLanguage,
								tbq.getQuestionIdentification(), "question", "ballotQuestion");
						tbq.getAnswer().forEach(
								a -> checkLanguageCount(a.getAnswerInfo(), AnswerInformationType::getLanguage, a.getAnswerIdentification(), "answer",
										"answer"));
					});
				}
			});
		});
	}

	private void checkContestExists(final ContestType c) {
		checkState(!c.getElectionInformation().isEmpty() || !c.getVoteInformation().isEmpty(), "neither election nor vote is defined");
	}

	private void checkEVotingDates(final ContestType c) {

		if (PropertyProvider.getPropertyValue("bypass.checkConsistency.dates", "false").equalsIgnoreCase("true")) {
			LOGGER.warn("As defined by configuration, bypassing the evotingDates consistency check");
			return;
		}

		final GregorianCalendar dtFrom = c.getEvotingFromDate().toGregorianCalendar();
		final GregorianCalendar dtTo = c.getEvotingToDate().toGregorianCalendar();

		checkState(dtFrom.getTime().after(new Date()), "eVotingFromDate must be future");
		checkState(dtTo.getTime().after(new Date()), "eVotingToDate must be future");

		checkState(dtFrom.getTime().before(dtTo.getTime()), "eVotingFromDate must precede eVotingToDate");

		final int minDelta = Integer.parseInt(PropertyProvider.getPropertyValue("eVotingFromTo.min.delta.allowed"));
		final LocalDate dtFromLocal = LocalDate.of(dtFrom.get(Calendar.YEAR), dtFrom.get(Calendar.MONTH) + 1, dtFrom.get(Calendar.DAY_OF_MONTH));
		final LocalDate dtToLocal = LocalDate.of(dtTo.get(Calendar.YEAR), dtTo.get(Calendar.MONTH) + 1, dtTo.get(Calendar.DAY_OF_MONTH));
		final long daysBetween = DAYS.between(dtFromLocal, dtToLocal);

		checkState(daysBetween >= minDelta, "the number of days between 'eVoting date from' and 'eVoting date to' must be at least %s", minDelta);

		if (c.getEvotingToDate().toGregorianCalendar().getTime().after(c.getContestDate().toGregorianCalendar().getTime())) {
			LOGGER.warn("The date of the contest should follow the eVoting date from.");
		}
	}

	private void checkContestIdentifiersUnique(final ContestType c) {
		checkState(c.getElectionInformation().stream().map(e -> e.getElection().getElectionIdentification()).allMatch(new HashSet<>()::add),
				"electionIdentification must be unique");

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

		c.getElectionInformation().forEach(
				e -> checkState(e.getCandidate().stream().map(CandidateType::getCandidateIdentification).allMatch(new HashSet<>()::add),
						"election '%s' : candidateIdentification must be unique", e.getElection().getElectionIdentification()));

		c.getElectionInformation().forEach(e -> checkState(e.getList().stream().map(ListType::getListIdentification).allMatch(new HashSet<>()::add),
				"election '%s' : listIdentification must be unique", e.getElection().getElectionIdentification()));
		c.getElectionInformation().forEach(e -> e.getList().stream().flatMap(
						lst -> lst.getCandidatePosition().stream().map(cp -> Map.entry(lst.getListIdentification(), cp.getPositionOnList())).toList()
								.stream()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
				.filter(entry -> entry.getValue() > 1).findFirst().ifPresent(entry -> {
					throw new IllegalStateException(
							String.format("the position %s in the list '%s' is not unique", entry.getKey().getValue(), entry.getKey().getKey()));
				}));

		c.getElectionInformation().forEach(e -> {
			final Set<String> candidateIds = e.getCandidate().stream().map(CandidateType::getCandidateIdentification).collect(Collectors.toSet());
			final Set<String> candidateInListIds = e.getList().stream().flatMap(l -> l.getCandidatePosition().stream())
					.map(CandidatePositionType::getCandidateIdentification).filter(Objects::nonNull).collect(Collectors.toSet());
			candidateInListIds.stream().filter(id -> !candidateIds.contains(id)).findFirst().ifPresent(i -> {
				throw new IllegalStateException(String.format("candidate identifier '%s' found in a list but not in the list of candidate(s)", i));
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

		c.getElectionInformation().forEach(e -> {
			final Set<String> listIds = e.getList().stream().map(ListType::getListIdentification).collect(Collectors.toSet());
			final Set<String> listUnionIds = e.getListUnion().stream().flatMap(l -> l.getReferencedList().stream()).collect(Collectors.toSet());
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
		c.getElectionInformation().forEach(e -> {
			final long limit = e.getElection().getCandidateAccumulation().longValue();
			e.getList().forEach(
					list -> list.getCandidatePosition().stream().map(CandidatePositionType::getCandidateIdentification).filter(Objects::nonNull)
							.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).values().stream()
							.max(Comparator.comparingLong(Long::longValue)).ifPresent(
									value -> checkState(value <= limit, "same candidate found more than %s time(s). [listIdentification : %s]", limit,
											list.getListIdentification())));
		});
	}

	private void checkSameNamesOfCandidates(final ContestType c) {
		c.getElectionInformation().forEach(e -> e.getCandidate().stream()
				.collect(Collectors.groupingBy(cand -> new MutablePair<>(cand.getFirstName(), cand.getFamilyName()), Collectors.counting()))
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
								"vote '%s' variant ballot tiebreak question %s has more than one blank answer", v.getVote().getVoteIdentification(),
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
		checkAnswerConsistency(c);
		checkParametersOfElections(c.getElectionInformation(), contestParameters);
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
}
