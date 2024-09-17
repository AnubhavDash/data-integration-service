/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static java.util.function.Predicate.not;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.TieBreakQuestionTypeWithReferences;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.DwellingAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyPositionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PartyAffiliationformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PositionTextInfoType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ReferencedElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

@Mapper(imports = { UUID.class, LanguageType.class })
public interface ECH015xv4Mapper {

	ECH015xv4Mapper INSTANCE = Mappers.getMapper(ECH015xv4Mapper.class);

	Logger LOGGER = LoggerFactory.getLogger(ECH015xv4Mapper.class);

	/* Ignore above properties because they will be manually filled in ContestBuilder class */
	@Mapping(target = "evotingFromDate", ignore = true)
	@Mapping(target = "evotingToDate", ignore = true)
	@Mapping(target = "contestDefaultLanguage", ignore = true)
	@Mapping(target = "electionGroupBallot", ignore = true)
	@Mapping(target = "voteInformation", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "electoralBoard", ignore = true)
	void addContestInformation(
			@MappingTarget
			ContestType mappedContest, ch.ech.xmlns.ech_0155._4.ContestType contest);

	default ContestType convert(final ch.ech.xmlns.ech_0155._4.ContestType contest) {
		final ContestType newContest = new ContestType();
		addContestInformation(newContest, contest);
		if (contest.getEVotingPeriod() != null) {
			newContest.setEvotingFromDate(contest.getEVotingPeriod().getEVotingPeriodFrom());
			newContest.setEvotingToDate(contest.getEVotingPeriod().getEVotingPeriodTill());
		}
		return newContest;
	}

	ContestDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.ContestDescriptionInformationType value);

	ContestDescriptionInformationType.ContestDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._4.ContestDescriptionInformationType.ContestDescriptionInfo value);

	@Mapping(target = "writeInsAllowed", ignore = true)
	@Mapping(target = "candidateAccumulation", ignore = true)
	ElectionType convert(ch.ech.xmlns.ech_0155._4.ElectionType et);

	ElectionDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType value);

	@Mapping(target = "electionDescriptionShort", expression = "java( mapElectionDescriptionShort(\"electionDescriptionShort\", value) )")
	ElectionDescriptionInformationType.ElectionDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType.ElectionDescriptionInfo value);

	ReferencedElectionInformationType convert(ch.ech.xmlns.ech_0155._4.ReferencedElectionInformationType value);

	@Mapping(target = "dwellingAddress", source = "dwellingAddress")
	@Mapping(target = "position", ignore = true)
	@Mapping(target = "incumbent.incumbent", source = "incumbentYesNo")
	@Mapping(target = "referenceOnPosition", source = "candidateReference")
	@Mapping(target = "eligibility", constant = "EXPLICIT")
	@Mapping(target = "title", expression = "java( ct.getTitle() != null && ct.getTitle().length() > 0 ? ct.getTitle() : null )")
	@Mapping(target = "firstName", expression = "java( ct.getFirstName() != null && ct.getFirstName().length() > 0 ? ct.getFirstName() : ct.getCallName() )")
	CandidateType convert(ch.ech.xmlns.ech_0155._4.CandidateType ct);

	PartyAffiliationformationType convert(ch.ech.xmlns.ech_0155._4.PartyAffiliationformationType pat);

	PartyAffiliationformationType.PartyAffiliationInfo convert(ch.ech.xmlns.ech_0155._4.PartyAffiliationformationType.PartyAffiliationInfo pai);

	DwellingAddressType convert(ch.ech.xmlns.ech_0010._6.AddressInformationType address);

	CandidateType.Swiss map(ch.ech.xmlns.ech_0155._4.CandidateType.Swiss value);

	default CandidateTextInformationType convert(final ch.ech.xmlns.ech_0155._4.CandidateTextInformationType value) {
		final CandidateTextInformationType candidateTextInformationType = new ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType();

		if (value == null) {
			final List<CandidateTextInformationType.CandidateTextInfo> list = new ArrayList<>();
			list.add(new CandidateTextInformationType.CandidateTextInfo().withLanguage(LanguageType.DE).withCandidateText("No candidate text"));
			list.add(new CandidateTextInformationType.CandidateTextInfo().withLanguage(LanguageType.FR).withCandidateText("No candidate text"));
			list.add(new CandidateTextInformationType.CandidateTextInfo().withLanguage(LanguageType.IT).withCandidateText("No candidate text"));
			list.add(new CandidateTextInformationType.CandidateTextInfo().withLanguage(LanguageType.RM).withCandidateText("No candidate text"));
			candidateTextInformationType.setCandidateTextInfo(list);
		} else {
			candidateTextInformationType.setCandidateTextInfo(toCandidateTextInfoList(value.getCandidateTextInfo()));
		}
		return candidateTextInformationType;
	}

	default List<CandidateTextInformationType.CandidateTextInfo> toCandidateTextInfoList(
			final List<ch.ech.xmlns.ech_0155._4.CandidateTextInformationType.CandidateTextInfo> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		return list.stream().map(this::convert).toList();
	}

	default CandidateTextInformationType.CandidateTextInfo convert(
			final ch.ech.xmlns.ech_0155._4.CandidateTextInformationType.CandidateTextInfo value) {
		if (value == null) {
			return null;
		}
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType.CandidateTextInfo candidateTextInfo = new ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType.CandidateTextInfo();
		candidateTextInfo.setLanguage(convert(value.getLanguage()));
		candidateTextInfo.setCandidateText(StringUtil.isNullOrEmpty(value.getCandidateText()) ? "No candidate text" : value.getCandidateText());
		return candidateTextInfo;
	}

	OccupationalTitleInformationType convert(ch.ech.xmlns.ech_0155._4.OccupationalTitleInformationType value);

	OccupationalTitleInformationType.OccupationalTitleInfo convert(
			ch.ech.xmlns.ech_0155._4.OccupationalTitleInformationType.OccupationalTitleInfo value);

	@Mapping(target = "varListText1", ignore = true)
	@Mapping(target = "varListText2", ignore = true)
	ListType convert(ch.ech.xmlns.ech_0155._4.ListType list);

	default List<ListType> convert(final List<ch.ech.xmlns.ech_0155._4.ListType> list) {
		return list.stream().filter(not(ch.ech.xmlns.ech_0155._4.ListType::isIsEmptyList)).map(this::convert).toList();
	}

	@Mapping(target = "candidateListIdentification", ignore = true)
	CandidatePositionType convert(ch.ech.xmlns.ech_0155._4.CandidatePositionInformationType value);

	ListDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.ListDescriptionInformationType value);

	ListDescriptionInformationType.ListDescriptionInfo convert(ch.ech.xmlns.ech_0155._4.ListDescriptionInformationType.ListDescriptionInfo value);

	ListUnionType convert(ch.ech.xmlns.ech_0155._4.ListUnionType lu);

	ListUnionDescriptionType convert(ch.ech.xmlns.ech_0155._4.ListUnionDescriptionType ludt);

	ListUnionDescriptionType.ListUnionDescriptionInfo convert(ch.ech.xmlns.ech_0155._4.ListUnionDescriptionType.ListUnionDescriptionInfo value);

	@Mapping(target = "vote.voteIdentification", source = "v.vote.voteIdentification")
	@Mapping(target = "vote.domainOfInfluence", source = "v.vote.domainOfInfluenceIdentification")
	@Mapping(target = "vote.voteDescription", source = "v.vote.voteDescription")
	@Mapping(target = "vote.ballot", source = "v.ballot")
	VoteInformationType convert(ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation v);

	VoteDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.VoteDescriptionInformationType value);

	VoteDescriptionInformationType.VoteDescriptionInfo convert(ch.ech.xmlns.ech_0155._4.VoteDescriptionInformationType.VoteDescriptionInfo value);

	default ElectionGroupBallotType convert(final ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot e) {
		final ElectionGroupBallotType electionGroupBallotType = new ElectionGroupBallotType();

		electionGroupBallotType.setElectionGroupIdentification(e.getElectionGroupIdentification());
		electionGroupBallotType.withDomainOfInfluence(e.getDomainOfInfluenceIdentification());
		electionGroupBallotType.setElectionGroupDescription(
				electionGroupDescriptionTypeToElectionGroupDescriptionInformationType(e.getElectionGroupDescription()));
		if (e.getElectionGroupPosition() != null) {
			electionGroupBallotType.setElectionGroupPosition(e.getElectionGroupPosition().intValueExact());
		}
		electionGroupBallotType.setElectionInformation(electionInformationListToElectionInformationTypeList(e.getElectionInformation()));

		return electionGroupBallotType;
	}

	List<ElectionInformationType> electionInformationListToElectionInformationTypeList(
			List<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation> electionInformation);

	@Mapping(target = "electionGroupDescriptionInfo", source = "electionDescriptionInfo")
	ElectionGroupDescriptionInformationType electionGroupDescriptionTypeToElectionGroupDescriptionInformationType(
			ch.ech.xmlns.ech_0155._4.ElectionGroupDescriptionType electionGroupDescription);

	// This method is called from a MapStruct java expression
	default String mapElectionDescriptionShort(
			final String fieldName,
			final ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo) {
		if (electionDescriptionInfo.getElectionDescriptionShort() != null && !electionDescriptionInfo.getElectionDescriptionShort().isEmpty()) {
			return electionDescriptionInfo.getElectionDescriptionShort();
		} else {
			LOGGER.warn("{} is missing. Filling its value with a truncated value of the full description.", fieldName);
			return electionDescriptionInfo.getElectionDescription()
					.substring(0, Math.min(electionDescriptionInfo.getElectionDescription().length(), 100));
		}
	}

	@Mapping(target = "electionGroupDescription", source = "electionDescription")
	@Mapping(target = "electionGroupDescriptionShort", expression = "java( mapElectionDescriptionShort(\"electionGroupDescriptionShort\", electionDescriptionInfo) )")
	ElectionGroupDescriptionInformationType.ElectionGroupDescriptionInfo electionDescriptionInfoToElectionGroupDescriptionInfo(
			ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo);

	@Mapping(target = "election.electionIdentification", source = "electionInformation.election.electionIdentification")
	@Mapping(target = "election.typeOfElection", source = "electionInformation.election.typeOfElection")
	@Mapping(target = "election.electionDescription", source = "electionInformation.election.electionDescription")
	@Mapping(target = "election.numberOfMandates", source = "electionInformation.election.numberOfMandates")
	@Mapping(target = "election.writeInsAllowed", ignore = true)
	@Mapping(target = "election.candidateAccumulation", ignore = true)
	@Mapping(target = "election.referencedElection", source = "electionInformation.election.referencedElection")
	@Mapping(target = "candidate", source = "electionInformation.candidate")
	@Mapping(target = "list", source = "electionInformation.list")
	@Mapping(target = "emptyList", expression = "java( mapEmptyList(electionInformation.getList()) )")
	@Mapping(target = "listUnion", source = "electionInformation.listUnion")
	ElectionInformationType convert(ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation electionInformation);

	default EmptyListType mapEmptyList(final List<ch.ech.xmlns.ech_0155._4.ListType> lists) {
		// The eCH does not have an empty list in case of majority elections.
		if (lists.isEmpty()) {
			return null;
		}

		final ch.ech.xmlns.ech_0155._4.ListType emptyListSource = lists.stream()
				.filter(ch.ech.xmlns.ech_0155._4.ListType::isIsEmptyList)
				.collect(MoreCollectors.onlyElement());

		final List<EmptyPositionType> emptyCandidatePositions = emptyListSource.getCandidatePosition().stream()
				.map(this::convert)
				.map(candidatePositionType -> {
					final CandidateTextInformationType candidateTextOnPosition = candidatePositionType.getCandidateTextOnPosition();
					final List<PositionTextInfoType.PositionText> positionTexts = candidateTextOnPosition.getCandidateTextInfo().stream()
							.map(candidateTextInfo ->
									new PositionTextInfoType.PositionText()
											.withLanguage(candidateTextInfo.getLanguage())
											.withPositionTextLong(candidateTextInfo.getCandidateText())
											.withPositionTextShort(StringUtils.left(candidateTextInfo.getCandidateText(), 100)))
							.toList();
					final PositionTextInfoType positionTextType = new PositionTextInfoType()
							.withPositionText(positionTexts);
					return new EmptyPositionType()
							.withEmptyPositionIdentification(candidatePositionType.getCandidateListIdentification())
							.withPositionOnList(candidatePositionType.getPositionOnList())
							.withEmptyPositionTextInfo(positionTextType);
				})
				.toList();

		final EmptyListType emptyListType = new EmptyListType()
				.withListIdentification(emptyListSource.getListIdentification())
				.withListIndentureNumber(emptyListSource.getListIndentureNumber())
				.withListDescription(convert(emptyListSource.getListDescription()))
				.withEmptyPosition(emptyCandidatePositions);
		if (emptyListSource.getListOrderOfPrecedence() != null) {
			emptyListType.withListOrderOfPrecedence(emptyListSource.getListOrderOfPrecedence().intValueExact());
		}

		return emptyListType;
	}

	default BallotType convert(final ch.ech.xmlns.ech_0155._4.BallotType bt) {
		if (bt == null) {
			return null;
		}

		final BallotType ballotType = new BallotType();

		ballotType.setBallotIdentification(bt.getBallotIdentification());

		Optional.ofNullable(bt.getBallotPosition())
				.map(BigInteger::intValueExact)
				.ifPresent(ballotType::setBallotPosition);

		ballotType.setBallotDescription(Optional.ofNullable(bt.getBallotDescription())
				.map(ballotDescription -> convert(ballotDescription, bt))
				.orElseGet(() -> {
					LOGGER.warn(
							"The ballot {} lacks a ballotDescriptionInformation tag. We recommend to always provide a ballotDescriptionInformation tag.",
							bt.getBallotIdentification());
					return convert(createEmptyBallotDescriptionInformationType(bt), bt);
				}));

		ballotType.setStandardBallot(convert(bt.getStandardBallot(), bt));
		ballotType.setVariantBallot(convert(bt.getVariantBallot(), bt));

		return ballotType;
	}

	BallotDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType value,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	default BallotDescriptionInformationType.BallotDescriptionInfo convert(
			final ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo bdi,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		final BallotDescriptionInformationType.BallotDescriptionInfo result = new BallotDescriptionInformationType.BallotDescriptionInfo();
		result.setLanguage(convert(bdi.getLanguage()));

		final Optional<String> ballotDescriptionLong = Optional.ofNullable(bdi.getBallotDescriptionLong());
		final Optional<String> ballotDescriptionShort = Optional.ofNullable(bdi.getBallotDescriptionShort());

		result.setBallotDescriptionShort(ballotDescriptionShort.or(() -> {
					ballotDescriptionLong.ifPresent(description -> LOGGER.warn(
							"The ballotDescriptionShort is missing. Using the truncated ballotDescriptionLong instead. [ballotDescriptionLong: {}]",
							description));
					return ballotDescriptionLong;
				})
				.map(description -> StringUtils.left(description, 100))
				.orElseGet(() -> getBallotQuestionTitle(bdi.getLanguage(), ballotType)));

		result.setBallotDescriptionLong(ballotDescriptionLong.or(() -> {
					ballotDescriptionShort.ifPresent(description -> LOGGER.warn(
							"The ballotDescriptionLong is missing. Using the ballotDescriptionShort instead. [ballotDescriptionShort: {}]",
							description));
					return ballotDescriptionShort;
				})
				.orElseGet(() -> getBallotQuestionTitle(bdi.getLanguage(), ballotType)));

		return result;
	}

	@Mapping(target = "answerType", source = "answerInformation.answerType")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "ballotQuestionNumber")
	StandardQuestionType convert(ch.ech.xmlns.ech_0155._4.QuestionInformationType qit,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	@Mapping(target = "answerType", source = "tbit.answerInformation.answerType")
	@Mapping(target = "answer", source = "tbit.answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "tbit.tieBreakQuestionNumber")
	void appendTieBreakQuestionType(
			@MappingTarget
			TieBreakQuestionType tb, ch.ech.xmlns.ech_0155._4.TieBreakInformationType tbit,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	default TieBreakQuestionType convert(final ch.ech.xmlns.ech_0155._4.TieBreakInformationType tbit,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		final TieBreakQuestionTypeWithReferences result = new TieBreakQuestionTypeWithReferences();
		appendTieBreakQuestionType(result, tbit, ballotType);
		result.setReferencedQuestion1(tbit.getReferencedQuestion1());
		result.setReferencedQuestion2(tbit.getReferencedQuestion2());
		return result;
	}

	@Mapping(target = "ballotQuestionInfo", source = "tieBreakQuestionInfo")
	BallotQuestionType convert(ch.ech.xmlns.ech_0155._4.TieBreakQuestionType tbqt,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	default BallotQuestionType.BallotQuestionInfo convert(final ch.ech.xmlns.ech_0155._4.TieBreakQuestionType.TieBreakQuestionInfo tbqi,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		final BallotQuestionType.BallotQuestionInfo result = new BallotQuestionType.BallotQuestionInfo();

		result.setLanguage(convert(tbqi.getLanguage()));
		if (tbqi.getTieBreakQuestionTitle() != null) {
			result.setBallotQuestionTitle(tbqi.getTieBreakQuestionTitle());
		} else {
			checkState(ballotType.getBallotDescription() != null,
					"Ballot description is missing. A ballot must include a description if no tie-break question title is available.");

			final ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo ballotDescriptionInfo = ballotType.getBallotDescription()
					.getBallotDescriptionInfo().stream()
					.filter(bdi -> bdi.getLanguage().equals(tbqi.getLanguage()))
					.collect(MoreCollectors.onlyElement());

			result.setBallotQuestionTitle(getDescription(ballotDescriptionInfo));
		}
		result.setBallotQuestion(tbqi.getTieBreakQuestion());

		return result;
	}

	@Mapping(target = "standardQuestion", source = "questionInformation")
	@Mapping(target = "tieBreakQuestion", source = "tieBreakInformation")
	VariantBallotType convert(ch.ech.xmlns.ech_0155._4.BallotType.VariantBallot vb,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	@Mapping(target = "answerType", source = "answerInformation.answerType")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "ballotQuestionNumber")
	StandardBallotType convert(ch.ech.xmlns.ech_0155._4.BallotType.StandardBallot sb,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	@Mapping(target = "answerIdentification", source = "answerIdentification")
	@Mapping(target = "answerPosition", source = "answerSequenceNumber")
	@Mapping(target = "answerInfo", source = "answerTextInformation")
	@Mapping(target = "standardAnswerType", expression = "java( mapStandardAnswerType(answerOption.getAnswerSequenceNumber()) )")
	StandardAnswerType convertStandardAnswer(ch.ech.xmlns.ech_0155._4.AnswerOptionIdentificationType answerOption);

	@Mapping(target = "answerIdentification", source = "answerIdentification")
	@Mapping(target = "answerPosition", source = "answerSequenceNumber")
	@Mapping(target = "answerInfo", source = "answerTextInformation")
	TiebreakAnswerType convertTiebreakAnswer(ch.ech.xmlns.ech_0155._4.AnswerOptionIdentificationType answerOption);

	default String mapStandardAnswerType(final BigInteger answerSequenceNumber) {
		return switch (answerSequenceNumber.intValue()) {
			case 1 -> "YES";
			case 2 -> "NO";
			case 3 -> "EMPTY";
			default -> throw new InvalidParameterException("Unknown value for answerSequenceNumber:" + answerSequenceNumber);
		};
	}

	@Mapping(target = "ballotQuestionInfo", source = "ballotQuestionInfo")
	BallotQuestionType convert(final ch.ech.xmlns.ech_0155._4.BallotQuestionType bqt,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType);

	default BallotQuestionType.BallotQuestionInfo convert(final ch.ech.xmlns.ech_0155._4.BallotQuestionType.BallotQuestionInfo bqi,
			@Context
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		final BallotQuestionType.BallotQuestionInfo result = new BallotQuestionType.BallotQuestionInfo();
		result.setLanguage(convert(bqi.getLanguage()));
		if (bqi.getBallotQuestionTitle() != null) {
			result.setBallotQuestionTitle(bqi.getBallotQuestionTitle());
		} else {
			checkState(ballotType.getBallotDescription() != null,
					"Ballot description is missing. A ballot must include a description if no ballot question title is available.");

			final ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo ballotDescriptionInfo =
					ballotType.getBallotDescription().getBallotDescriptionInfo().stream()
							.filter(bdi -> bdi.getLanguage().equals(bqi.getLanguage()))
							.collect(MoreCollectors.onlyElement());

			result.setBallotQuestionTitle(getDescription(ballotDescriptionInfo));
		}
		result.setBallotQuestion(bqi.getBallotQuestion());

		return result;
	}

	@Mapping(target = "answer", source = "answerText")
	AnswerInformationType convert(ch.ech.xmlns.ech_0155._4.AnswerOptionIdentificationType.AnswerTextInformation answer);

	default LanguageType convert(final String value) {
		return LanguageType.fromValue(value.toLowerCase());
	}

	private ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType createEmptyBallotDescriptionInformationType(
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		LOGGER.info("Creation of an empty ballotDescriptionInformation tag from ballot questions for ballot {}.",
				ballotType.getBallotIdentification());

		final ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType ballotDescriptionInformationType = new ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType();

		final List<ch.ech.xmlns.ech_0155._4.BallotQuestionType.BallotQuestionInfo> ballotQuestionInfoList = getBallotQuestionInfoList(ballotType);
		if (ballotQuestionInfoList.isEmpty()) {
			throw new IllegalStateException("Unable to create an empty ballotDescriptionInformation tag from ballot questions.");
		}
		ballotDescriptionInformationType.setBallotDescriptionInfo(createBallotDescriptionInfoList(ballotQuestionInfoList));

		return ballotDescriptionInformationType;
	}

	private List<ch.ech.xmlns.ech_0155._4.BallotQuestionType.BallotQuestionInfo> getBallotQuestionInfoList(
			final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		if (ballotType.getStandardBallot() != null && ballotType.getStandardBallot().getBallotQuestion() != null) {
			return ballotType.getStandardBallot().getBallotQuestion().getBallotQuestionInfo();
		} else if (ballotType.getVariantBallot() != null && ballotType.getVariantBallot().getQuestionInformation() != null) {
			return ballotType.getVariantBallot().getQuestionInformation().getFirst().getBallotQuestion().getBallotQuestionInfo();
		} else {
			return List.of();
		}
	}

	private List<ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo> createBallotDescriptionInfoList(
			final List<ch.ech.xmlns.ech_0155._4.BallotQuestionType.BallotQuestionInfo> ballotQuestionInfoList) {
		return ballotQuestionInfoList.stream()
				.map(ballotQuestionInfo -> {
					final ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo ballotDescriptionInfo = new ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo();
					ballotDescriptionInfo.setLanguage(ballotQuestionInfo.getLanguage());
					return ballotDescriptionInfo;
				})
				.toList();
	}

	private static String getDescription(
			final ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo ballotDescriptionInfo) {
		if (ballotDescriptionInfo.getBallotDescriptionShort() != null) {
			return ballotDescriptionInfo.getBallotDescriptionShort();
		} else if (ballotDescriptionInfo.getBallotDescriptionLong() != null) {
			return StringUtils.left(ballotDescriptionInfo.getBallotDescriptionLong(), 100);
		} else {
			throw new IllegalStateException("Could not extract description from BallotDescriptionInfo");
		}
	}

	private static String getBallotQuestionTitle(final String language, final ch.ech.xmlns.ech_0155._4.BallotType ballotType) {
		LOGGER.warn("The ballot {} lacks of descriptions. We will use the ballot questions to fill those descriptions.",
				ballotType.getBallotIdentification());

		if (ballotType.getStandardBallot() != null && ballotType.getStandardBallot().getBallotQuestion() != null) {
			return getBallotQuestionTitleFromInfo(language, ballotType.getStandardBallot().getBallotQuestion().getBallotQuestionInfo());
		} else if (ballotType.getVariantBallot() != null && ballotType.getVariantBallot().getQuestionInformation() != null) {
			return getBallotQuestionTitleFromInfo(language,
					ballotType.getVariantBallot().getQuestionInformation().getFirst().getBallotQuestion().getBallotQuestionInfo());
		} else {
			throw new IllegalStateException("Could not extract question title.");
		}
	}

	private static String getBallotQuestionTitleFromInfo(final String language,
			final List<ch.ech.xmlns.ech_0155._4.BallotQuestionType.BallotQuestionInfo> ballotQuestionInfoList) {
		return ballotQuestionInfoList.stream()
				.filter(bdi -> bdi.getLanguage().equals(language))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						String.format("Ballot question info is not available in expected language. [language: %s]", language)))
				.getBallotQuestionTitle();
	}
}
