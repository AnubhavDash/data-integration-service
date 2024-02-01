/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.ech.xmlns.ech_0155._4.ElectionGroupDescriptionType;
import ch.ech.xmlns.ech_0157._4.EventInitialDelivery;
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
ECH015xv4Mapper {
	ECH015xv4Mapper INSTANCE = Mappers.getMapper(ECH015xv4Mapper.class);

	Logger LOGGER = LoggerFactory.getLogger(ECH015xv4Mapper.class);

	/* Ignore above properties because they will be manually filled in ContestBuilder class */
	@Mapping(target = "evotingFromDate", ignore = true)
	@Mapping(target = "evotingToDate", ignore = true)
	@Mapping(target = "contestDefaultLanguage", ignore = true)
	@Mapping(target = "electionGroupBallot", ignore = true)
	@Mapping(target = "voteInformation", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "adminBoard", ignore = true)
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

	ElectionInformationType convert(ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation ei);

	@Mapping(target = "writeInsAllowed", ignore = true)
	@Mapping(target = "candidateAccumulation", ignore = true)
	ElectionType convert(ch.ech.xmlns.ech_0155._4.ElectionType et);

	ElectionDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType value);

	ElectionDescriptionInformationType.ElectionDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType.ElectionDescriptionInfo value);

	ReferencedElectionInformationType convert(ch.ech.xmlns.ech_0155._4.ReferencedElectionInformationType value);

	@Mapping(target = "dwellingAddress", source = "dwellingAddress")
	@Mapping(target = "position", ignore = true)
	@Mapping(target = "incumbent.incumbent", source = "incumbentYesNo")
	@Mapping(target = "referenceOnPosition", source = "candidateReference")
	@Mapping(target = "title", expression = "java( ct.getTitle() != null && ct.getTitle().length() > 0 ? ct.getTitle() : null )")
	CandidateType convert(ch.ech.xmlns.ech_0155._4.CandidateType ct);

	PartyAffiliationformationType convert(ch.ech.xmlns.ech_0155._4.PartyAffiliationformationType pat);

	PartyAffiliationformationType.PartyAffiliationInfo convert(ch.ech.xmlns.ech_0155._4.PartyAffiliationformationType.PartyAffiliationInfo pai);

	DwellingAddressType convert(AddressInformationType address);

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

	@Mapping(target = "listEmpty", expression = "java( list.getCandidatePosition().isEmpty() )")
	@Mapping(target = "varListText1", ignore = true)
	@Mapping(target = "varListText2", ignore = true)
	ListType convert(ch.ech.xmlns.ech_0155._4.ListType list);

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

	default ElectionGroupBallotType convert(final EventInitialDelivery.ElectionGroupBallot e) {
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
			List<EventInitialDelivery.ElectionGroupBallot.ElectionInformation> electionInformation);

	@Mapping(target = "electionGroupDescriptionInfo", source = "electionDescriptionInfo")
	ElectionGroupDescriptionInformationType electionGroupDescriptionTypeToElectionGroupDescriptionInformationType(
			ElectionGroupDescriptionType electionGroupDescription);

	@Mapping(target = "electionGroupDescription", source = "electionDescription")
	@Mapping(target = "electionGroupDescriptionShort", source = "electionDescriptionShort")
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
	@Mapping(target = "listUnion", source = "electionInformation.listUnion")
	ElectionInformationType convert(String domainOfInfluenceIdentification,
			EventInitialDelivery.ElectionGroupBallot.ElectionInformation electionInformation);

	BallotType convert(ch.ech.xmlns.ech_0155._4.BallotType bt);

	BallotDescriptionInformationType convert(ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType value);

	BallotDescriptionInformationType.BallotDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType.BallotDescriptionInfo value);

	@Mapping(target = "answerType", source = "answerInformation.answerType")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "ballotQuestionNumber")
	StandardQuestionType convert(ch.ech.xmlns.ech_0155._4.QuestionInformationType qit);

	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	@Mapping(target = "answerType", source = "tbit.answerInformation.answerType")
	@Mapping(target = "answer", source = "tbit.answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "tbit.tieBreakQuestionNumber")
	void appendTieBreakQuestionType(
			@MappingTarget
			TieBreakQuestionType tb, ch.ech.xmlns.ech_0155._4.TieBreakInformationType tbit);

	default TieBreakQuestionType convert(final ch.ech.xmlns.ech_0155._4.TieBreakInformationType tbit) {
		final TieBreakQuestionTypeWithReferences result = new TieBreakQuestionTypeWithReferences();
		appendTieBreakQuestionType(result, tbit);
		result.setReferencedQuestion1(tbit.getReferencedQuestion1());
		result.setReferencedQuestion2(tbit.getReferencedQuestion2());
		return result;
	}

	@Mapping(target = "ballotQuestionInfo", source = "tieBreakQuestionInfo")
	BallotQuestionType convert(ch.ech.xmlns.ech_0155._4.TieBreakQuestionType tbqt);

	@Mapping(target = "ballotQuestionTitle", source = "tieBreakQuestionTitle")
	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	BallotQuestionType.BallotQuestionInfo convert(ch.ech.xmlns.ech_0155._4.TieBreakQuestionType.TieBreakQuestionInfo value);

	@Mapping(target = "standardQuestion", source = "questionInformation")
	@Mapping(target = "tieBreakQuestion", source = "tieBreakInformation")
	VariantBallotType convert(ch.ech.xmlns.ech_0155._4.BallotType.VariantBallot vb);

	@Mapping(target = "answerType", source = "answerInformation.answerType")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "ballotQuestionNumber")
	StandardBallotType convert(ch.ech.xmlns.ech_0155._4.BallotType.StandardBallot sb);

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
	BallotQuestionType convert(ch.ech.xmlns.ech_0155._4.BallotQuestionType bqt);

	BallotQuestionType.BallotQuestionInfo convert(ch.ech.xmlns.ech_0155._4.BallotQuestionType.BallotQuestionInfo value);

	@Mapping(target = "answer", source = "answerText")
	AnswerInformationType convert(ch.ech.xmlns.ech_0155._4.AnswerOptionIdentificationType.AnswerTextInformation answer);

	default LanguageType convert(final String value) {
		return LanguageType.fromValue(value.toLowerCase());
	}
}