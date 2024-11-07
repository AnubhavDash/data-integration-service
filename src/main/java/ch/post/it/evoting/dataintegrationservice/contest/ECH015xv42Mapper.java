/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

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
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PartyAffiliationformationType;
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
public interface
ECH015xv42Mapper {
	ECH015xv42Mapper INSTANCE = Mappers.getMapper(ECH015xv42Mapper.class);

	Logger LOGGER = LoggerFactory.getLogger(ECH015xv42Mapper.class);

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
			ContestType mappedContest, ch.ech.xmlns.ech_0155._5.ContestType contest);

	default ContestType convert(final ch.ech.xmlns.ech_0155._5.ContestType contest) {
		final ContestType newContest = new ContestType();
		addContestInformation(newContest, contest);
		if (contest.getEVotingPeriod() != null) {
			newContest.setEvotingFromDate(contest.getEVotingPeriod().getEVotingPeriodFrom());
			newContest.setEvotingToDate(contest.getEVotingPeriod().getEVotingPeriodTill());
		}
		return newContest;
	}

	ContestDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.ContestDescriptionInformationType value);

	ContestDescriptionInformationType.ContestDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._5.ContestDescriptionInformationType.ContestDescriptionInfo value);

	ElectionInformationType convert(ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation ei);

	@Mapping(target = "writeInsAllowed", ignore = true)
	@Mapping(target = "candidateAccumulation", ignore = true)
	ElectionType convert(ch.ech.xmlns.ech_0155._5.ElectionType et);

	ElectionDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType value);

	@Mapping(target = "electionDescriptionShort", expression = "java( mapElectionDescriptionShort(\"electionDescriptionShort\", value) )")
	ElectionDescriptionInformationType.ElectionDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType.ElectionDescriptionInfo value);

	ReferencedElectionInformationType convert(ch.ech.xmlns.ech_0155._5.ReferencedElectionInformationType value);

	@Mapping(target = "dwellingAddress", source = "dwellingAddress")
	@Mapping(target = "position", ignore = true)
	@Mapping(target = "incumbent.incumbent", source = "incumbentYesNo")
	@Mapping(target = "referenceOnPosition", source = "candidateReference")
	@Mapping(target = "eligibility", constant = "EXPLICIT")
	@Mapping(target = "title", expression = "java( ct.getTitle() != null && ct.getTitle().length() > 0 ? ct.getTitle() : null )")
	@Mapping(target = "firstName", expression = "java( ct.getFirstName() != null && ct.getFirstName().length() > 0 ? ct.getFirstName() : ct.getCallName() )")
	CandidateType convert(ch.ech.xmlns.ech_0155._5.CandidateType ct);

	PartyAffiliationformationType convert(ch.ech.xmlns.ech_0155._5.PartyAffiliationInformationType pat);

	PartyAffiliationformationType.PartyAffiliationInfo convert(ch.ech.xmlns.ech_0155._5.PartyAffiliationInformationType.PartyAffiliationInfo pai);

	DwellingAddressType convert(ch.ech.xmlns.ech_0010._6.AddressInformationType address);

	CandidateType.Swiss map(ch.ech.xmlns.ech_0155._5.CandidateType.Swiss value);

	default CandidateTextInformationType convert(final ch.ech.xmlns.ech_0155._5.CandidateTextInformationType value) {
		final CandidateTextInformationType candidateTextInformationType = new CandidateTextInformationType();

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
			final List<ch.ech.xmlns.ech_0155._5.CandidateTextInformationType.CandidateTextInfo> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		return list.stream().map(this::convert).toList();
	}

	default CandidateTextInformationType.CandidateTextInfo convert(
			final ch.ech.xmlns.ech_0155._5.CandidateTextInformationType.CandidateTextInfo value) {
		if (value == null) {
			return null;
		}
		final CandidateTextInformationType.CandidateTextInfo candidateTextInfo = new CandidateTextInformationType.CandidateTextInfo();
		candidateTextInfo.setLanguage(convert(value.getLanguage()));
		candidateTextInfo.setCandidateText(StringUtil.isNullOrEmpty(value.getCandidateText()) ? "No candidate text" : value.getCandidateText());
		return candidateTextInfo;
	}

	OccupationalTitleInformationType convert(ch.ech.xmlns.ech_0155._5.OccupationalTitleInformationType value);

	OccupationalTitleInformationType.OccupationalTitleInfo convert(
			ch.ech.xmlns.ech_0155._5.OccupationalTitleInformationType.OccupationalTitleInfo value);

	@Mapping(target = "varListText1", ignore = true)
	@Mapping(target = "varListText2", ignore = true)
	ListType convert(ch.ech.xmlns.ech_0155._5.ListType list);

	@Mapping(target = "candidateListIdentification", ignore = true)
	CandidatePositionType convert(ch.ech.xmlns.ech_0155._5.CandidatePositionInformationType value);

	ListDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.ListDescriptionInformationType value);

	ListDescriptionInformationType.ListDescriptionInfo convert(ch.ech.xmlns.ech_0155._5.ListDescriptionInformationType.ListDescriptionInfo value);

	ListUnionType convert(ch.ech.xmlns.ech_0155._5.ListUnionType lu);

	ListUnionDescriptionType convert(ch.ech.xmlns.ech_0155._5.ListUnionDescriptionType ludt);

	ListUnionDescriptionType.ListUnionDescriptionInfo convert(ch.ech.xmlns.ech_0155._5.ListUnionDescriptionType.ListUnionDescriptionInfo value);

	@Mapping(target = "vote.voteIdentification", source = "v.vote.voteIdentification")
	@Mapping(target = "vote.domainOfInfluence", source = "v.vote.domainOfInfluenceIdentification")
	@Mapping(target = "vote.voteDescription", source = "v.vote.voteDescription")
	@Mapping(target = "vote.ballot", source = "v.ballot")
	VoteInformationType convert(ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation v);

	VoteDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.VoteDescriptionInformationType value);

	VoteDescriptionInformationType.VoteDescriptionInfo convert(ch.ech.xmlns.ech_0155._5.VoteDescriptionInformationType.VoteDescriptionInfo value);

	default ElectionGroupBallotType convert(final ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup e) {
		final ElectionGroupBallotType electionGroupBallotType = new ElectionGroupBallotType();

		electionGroupBallotType.setElectionGroupIdentification(e.getElectionGroupIdentification());
		electionGroupBallotType.withDomainOfInfluence(e.getDomainOfInfluence().getDomainOfInfluenceIdentification());
		electionGroupBallotType.setElectionGroupDescription(
				electionGroupDescriptionTypeToElectionGroupDescriptionInformationType(e.getElectionGroupDescription()));
		if (e.getElectionGroupPosition() != null) {
			electionGroupBallotType.setElectionGroupPosition(e.getElectionGroupPosition().intValueExact());
		}
		electionGroupBallotType.setElectionInformation(electionInformationListToElectionInformationTypeList(e.getElectionInformation()));

		return electionGroupBallotType;
	}

	List<ElectionInformationType> electionInformationListToElectionInformationTypeList(
			List<ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation> electionInformation);

	@Mapping(target = "electionGroupDescriptionInfo", source = "electionDescriptionInfo")
	ElectionGroupDescriptionInformationType electionGroupDescriptionTypeToElectionGroupDescriptionInformationType(
			ch.ech.xmlns.ech_0155._5.ElectionGroupDescriptionType electionGroupDescription);

	// This method is called from a MapStruct java expression
	default String mapElectionDescriptionShort(
			final String fieldName,
			final ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo) {
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
			ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo);

	@Mapping(target = "election.electionIdentification", source = "electionInformation.election.electionIdentification")
	@Mapping(target = "election.typeOfElection", source = "electionInformation.election.typeOfElection")
	@Mapping(target = "election.electionDescription", source = "electionInformation.election.electionDescription")
	@Mapping(target = "election.numberOfMandates", source = "electionInformation.election.numberOfMandates")
	@Mapping(target = "election.writeInsAllowed", ignore = true)
	@Mapping(target = "election.candidateAccumulation", ignore = true)
	@Mapping(target = "election.referencedElection", source = "electionInformation.election.referencedElection")
	@Mapping(target = "candidate", source = "electionInformation.candidate")
	@Mapping(target = "list", source = "electionInformation.list")
	@Mapping(target = "listUnion", source = "electionInformation.listUnion")
	ElectionInformationType convert(String domainOfInfluenceIdentification,
			ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation electionInformation);

	default BallotType convert(final ch.ech.xmlns.ech_0155._5.ElectronicBallotType bt) {
		if (bt == null) {
			return null;
		}

		final BallotType ballotType = new BallotType();

		ballotType.setBallotIdentification(bt.getElectronicBallotIdentification());

		Optional.ofNullable(bt.getElectronicBallotPosition())
				.map(BigInteger::intValueExact)
				.ifPresent(ballotType::setBallotPosition);

		ballotType.setBallotDescription(Optional.ofNullable(bt.getElectronicBallotDescription())
				.map(ballotDescription -> convert(ballotDescription, bt))
				.orElseGet(() -> {
					LOGGER.warn(
							"The ballot {} lacks a ballotDescriptionInformation tag. We recommend to always provide a ballotDescriptionInformation tag.",
							bt.getElectronicBallotDescription());
					return convert(createEmptyBallotDescriptionInformationType(bt), bt);
				}));

		ballotType.setStandardBallot(convert(bt.getStandardElectronicBallot(), bt));
		ballotType.setVariantBallot(convert(bt.getVariantElectronicBallot(), bt));

		return ballotType;
	}

	BallotDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType value,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	default BallotDescriptionInformationType.BallotDescriptionInfo convert(
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo bdi,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		final BallotDescriptionInformationType.BallotDescriptionInfo result = new BallotDescriptionInformationType.BallotDescriptionInfo();
		result.setLanguage(convert(bdi.getLanguage()));

		final Optional<String> ballotDescriptionLong = Optional.ofNullable(bdi.getElectronicBallotDescriptionLong());
		final Optional<String> ballotDescriptionShort = Optional.ofNullable(bdi.getElectronicBallotDescriptionShort());

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
	@Mapping(target = "questionNumber", source = "electronicBallotQuestionNumber")
	StandardQuestionType convert(ch.ech.xmlns.ech_0155._5.QuestionInformationType qit,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	@Mapping(target = "answerType", source = "tbit.answerInformation.answerType")
	@Mapping(target = "answer", source = "tbit.answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "tbit.tieBreakQuestionNumber")
	void appendTieBreakQuestionType(
			@MappingTarget
			TieBreakQuestionType tb, ch.ech.xmlns.ech_0155._5.TieBreakInformationType tbit,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	default TieBreakQuestionType convert(final ch.ech.xmlns.ech_0155._5.TieBreakInformationType tbit,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		final TieBreakQuestionTypeWithReferences result = new TieBreakQuestionTypeWithReferences();
		appendTieBreakQuestionType(result, tbit, ballotType);
		result.setReferencedQuestion1(tbit.getReferencedQuestion1());
		result.setReferencedQuestion2(tbit.getReferencedQuestion2());
		return result;
	}

	@Mapping(target = "ballotQuestionInfo", source = "tieBreakQuestionInfo")
	BallotQuestionType convert(ch.ech.xmlns.ech_0155._5.TieBreakQuestionType tbqt,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	default BallotQuestionType.BallotQuestionInfo convert(final ch.ech.xmlns.ech_0155._5.TieBreakQuestionType.TieBreakQuestionInfo tbqi,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		final BallotQuestionType.BallotQuestionInfo result = new BallotQuestionType.BallotQuestionInfo();

		result.setLanguage(convert(tbqi.getLanguage()));
		if (tbqi.getTieBreakQuestionTitle() != null) {
			result.setBallotQuestionTitle(tbqi.getTieBreakQuestionTitle());
		} else {
			checkState(ballotType.getElectronicBallotDescription() != null,
					"Ballot description is missing. A ballot must include a description if no tie-break question title is available.");

			final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo ballotDescriptionInfo = ballotType.getElectronicBallotDescription()
					.getElectronicBallotDescriptionInfo().stream()
					.filter(bdi -> bdi.getLanguage().equals(tbqi.getLanguage()))
					.collect(MoreCollectors.onlyElement());

			result.setBallotQuestionTitle(getDescription(ballotDescriptionInfo));
		}
		result.setBallotQuestion(tbqi.getTieBreakQuestion());

		return result;
	}

	@Mapping(target = "standardQuestion", source = "questionInformation")
	@Mapping(target = "tieBreakQuestion", source = "tieBreakInformation")
	VariantBallotType convert(ch.ech.xmlns.ech_0155._5.ElectronicBallotType.VariantElectronicBallot vb,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	@Mapping(target = "answerType", source = "answerInformation.answerType")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "electronicBallotQuestionNumber")
	StandardBallotType convert(ch.ech.xmlns.ech_0155._5.ElectronicBallotType.StandardElectronicBallot sb,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	@Mapping(target = "answerIdentification", source = "answerIdentification")
	@Mapping(target = "answerPosition", source = "answerSequenceNumber")
	@Mapping(target = "answerInfo", source = "answerTextInformation")
	@Mapping(target = "standardAnswerType", expression = "java( mapStandardAnswerType(answerOption.getAnswerSequenceNumber()) )")
	StandardAnswerType convertStandardAnswer(ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType answerOption);

	@Mapping(target = "answerIdentification", source = "answerIdentification")
	@Mapping(target = "answerPosition", source = "answerSequenceNumber")
	@Mapping(target = "answerInfo", source = "answerTextInformation")
	TiebreakAnswerType convertTiebreakAnswer(ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType answerOption);

	default String mapStandardAnswerType(final BigInteger answerSequenceNumber) {
		return switch (answerSequenceNumber.intValue()) {
			case 1 -> "YES";
			case 2 -> "NO";
			case 3 -> "EMPTY";
			default -> throw new InvalidParameterException("Unknown value for answerSequenceNumber:" + answerSequenceNumber);
		};
	}

	@Mapping(target = "ballotQuestionInfo", source = "electronicBallotQuestionInfo")
	BallotQuestionType convert(final ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType bqt,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	default BallotQuestionType.BallotQuestionInfo convert(final ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo bqi,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		final BallotQuestionType.BallotQuestionInfo result = new BallotQuestionType.BallotQuestionInfo();
		result.setLanguage(convert(bqi.getLanguage()));
		if (bqi.getElectronicBallotQuestionTitle() != null) {
			result.setBallotQuestionTitle(bqi.getElectronicBallotQuestionTitle());
		} else {
			checkState(ballotType.getElectronicBallotDescription() != null,
					"Ballot description is missing. A ballot must include a description if no ballot question title is available.");

			final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo ballotDescriptionInfo =
					ballotType.getElectronicBallotDescription().getElectronicBallotDescriptionInfo().stream()
							.filter(bdi -> bdi.getLanguage().equals(bqi.getLanguage()))
							.collect(MoreCollectors.onlyElement());

			result.setBallotQuestionTitle(getDescription(ballotDescriptionInfo));
		}
		result.setBallotQuestion(bqi.getElectronicBallotQuestion());

		return result;
	}

	@Mapping(target = "answer", source = "answerText")
	AnswerInformationType convert(ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType.AnswerTextInformation answer);

	default LanguageType convert(final String value) {
		return LanguageType.fromValue(value.toLowerCase());
	}

	private ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType createEmptyBallotDescriptionInformationType(
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		LOGGER.info("Creation of an empty ballotDescriptionInformation tag from ballot questions for ballot {}.",
				ballotType.getElectronicBallotIdentification());

		final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType ballotDescriptionInformationType = new ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType();

		final List<ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo> ballotQuestionInfoList = getBallotQuestionInfoList(ballotType);
		if (ballotQuestionInfoList.isEmpty()) {
			throw new IllegalStateException("Unable to create an empty ballotDescriptionInformation tag from ballot questions.");
		}
		ballotDescriptionInformationType.setElectronicBallotDescriptionInfo(createBallotDescriptionInfoList(ballotQuestionInfoList));

		return ballotDescriptionInformationType;
	}

	private List<ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo> getBallotQuestionInfoList(
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		if (ballotType.getStandardElectronicBallot() != null && ballotType.getStandardElectronicBallot().getElectronicBallotQuestion() != null) {
			return ballotType.getStandardElectronicBallot().getElectronicBallotQuestion().getElectronicBallotQuestionInfo();
		} else if (ballotType.getVariantElectronicBallot() != null && ballotType.getVariantElectronicBallot().getQuestionInformation() != null) {
			return ballotType.getVariantElectronicBallot().getQuestionInformation().getFirst().getElectronicBallotQuestion().getElectronicBallotQuestionInfo();
		} else {
			return List.of();
		}
	}

	private List<ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo> createBallotDescriptionInfoList(
			final List<ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo> ballotQuestionInfoList) {
		return ballotQuestionInfoList.stream()
				.map(ballotQuestionInfo -> {
					final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo ballotDescriptionInfo = new ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo();
					ballotDescriptionInfo.setLanguage(ballotQuestionInfo.getLanguage());
					return ballotDescriptionInfo;
				})
				.toList();
	}

	private static String getDescription(
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo ballotDescriptionInfo) {
		if (ballotDescriptionInfo.getElectronicBallotDescriptionShort() != null) {
			return ballotDescriptionInfo.getElectronicBallotDescriptionShort();
		} else if (ballotDescriptionInfo.getElectronicBallotDescriptionLong() != null) {
			return StringUtils.left(ballotDescriptionInfo.getElectronicBallotDescriptionLong(), 100);
		} else {
			throw new IllegalStateException("Could not extract description from BallotDescriptionInfo");
		}
	}

	private static String getBallotQuestionTitle(final String language, final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		LOGGER.warn("The ballot {} lacks of descriptions. We will use the ballot questions to fill those descriptions.",
				ballotType.getElectronicBallotIdentification());

		if (ballotType.getStandardElectronicBallot() != null && ballotType.getStandardElectronicBallot().getElectronicBallotQuestion() != null) {
			return getBallotQuestionTitleFromInfo(language, ballotType.getStandardElectronicBallot().getElectronicBallotQuestion().getElectronicBallotQuestionInfo());
		} else if (ballotType.getVariantElectronicBallot() != null && ballotType.getVariantElectronicBallot().getQuestionInformation() != null) {
			return getBallotQuestionTitleFromInfo(language,
					ballotType.getVariantElectronicBallot().getQuestionInformation().getFirst().getElectronicBallotQuestion().getElectronicBallotQuestionInfo());
		} else {
			throw new IllegalStateException("Could not extract question title.");
		}
	}

	private static String getBallotQuestionTitleFromInfo(final String language,
			final List<ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo> ballotQuestionInfoList) {
		return ballotQuestionInfoList.stream()
				.filter(bdi -> bdi.getLanguage().equals(language))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						String.format("Ballot question info is not available in expected language. [language: %s]", language)))
				.getElectronicBallotQuestionTitle();
	}
}
