/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
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
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionTypeWithReferences;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

@Mapper(imports = UUID.class)
public interface ECH015xv3Mapper {
	ECH015xv3Mapper INSTANCE = Mappers.getMapper(ECH015xv3Mapper.class);

	Logger LOGGER = LoggerFactory.getLogger(ECH015xv3Mapper.class);

	/* Ignore above properties because they will be manually filled in ContestBuilder class */
	@Mapping(target = "evotingFromDate", ignore = true)
	@Mapping(target = "evotingToDate", ignore = true)
	@Mapping(target = "contestDefaultLanguage", ignore = true)
	@Mapping(target = "electionInformation", ignore = true)
	@Mapping(target = "voteInformation", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "electoralAuthority", ignore = true)
	ContestType convert(ch.ech.xmlns.ech_0155._3.ContestType contest);

	ContestDescriptionInformationType convert(ch.ech.xmlns.ech_0155._3.ContestDescriptionInformationType value);

	ContestDescriptionInformationType.ContestDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._3.ContestDescriptionInformationType.ContestDescriptionInfo value);

	ElectionInformationType convert(ch.ech.xmlns.ech_0157._3.EventInitialDelivery.ElectionInformation ei);

	@Mapping(target = "writeInsAllowed", ignore = true)
	@Mapping(target = "candidateAccumulation", ignore = true)
	ElectionType convert(ch.ech.xmlns.ech_0155._3.ElectionType et);

	ElectionDescriptionInformationType convert(ch.ech.xmlns.ech_0155._3.ElectionDescriptionInformationType value);

	ElectionDescriptionInformationType.ElectionDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._3.ElectionDescriptionInformationType.ElectionDescriptionInfo value);

	ReferencedElectionInformationType convert(ch.ech.xmlns.ech_0155._3.ReferencedElectionInformationType value);

	@Mapping(target = "dwellingAddress", source = "dwellingAddress")
	@Mapping(target = "position", ignore = true)
	@Mapping(target = "incumbent.incumbent", source = "incumbentYesNo")
	@Mapping(target = "referenceOnPosition", source = "candidateReference")
	CandidateType convert(ch.ech.xmlns.ech_0155._3.CandidateType ct);

	PartyAffiliationformationType convert(ch.ech.xmlns.ech_0155._3.PartyAffiliationformationType pat);

	PartyAffiliationformationType.PartyAffiliationInfo convert(ch.ech.xmlns.ech_0155._3.PartyAffiliationformationType.PartyAffiliationInfo pai);

	DwellingAddressType convert(AddressInformationType address);

	CandidateType.Swiss map(ch.ech.xmlns.ech_0155._3.CandidateType.Swiss value);

	default CandidateTextInformationType convert(final ch.ech.xmlns.ech_0155._3.CandidateTextInformationType value) {
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
			final List<ch.ech.xmlns.ech_0155._3.CandidateTextInformationType.CandidateTextInfo> list) {
		if (list == null) {
			return null;
		}
		return list.stream().map(this::convert).toList();
	}

	default CandidateTextInformationType.CandidateTextInfo convert(
			final ch.ech.xmlns.ech_0155._3.CandidateTextInformationType.CandidateTextInfo value) {
		if (value == null) {
			return null;
		}
		final CandidateTextInformationType.CandidateTextInfo candidateTextInfo = new CandidateTextInformationType.CandidateTextInfo();
		candidateTextInfo.setLanguage(convert(value.getLanguage()));
		candidateTextInfo.setCandidateText(StringUtil.isNullOrEmpty(value.getCandidateText()) ? "No candidate text" : value.getCandidateText());
		return candidateTextInfo;
	}

	OccupationalTitleInformationType convert(ch.ech.xmlns.ech_0155._3.OccupationalTitleInformationType value);

	OccupationalTitleInformationType.OccupationalTitleInfo convert(
			ch.ech.xmlns.ech_0155._3.OccupationalTitleInformationType.OccupationalTitleInfo value);

	@Mapping(target = "listEmpty", expression = "java( list.getCandidatePosition().isEmpty() )")
	@Mapping(target = "varListText1", ignore = true)
	@Mapping(target = "varListText2", ignore = true)
	ListType convert(ch.ech.xmlns.ech_0155._3.ListType list);

	@Mapping(target = "candidateListIdentification", ignore = true)
	CandidatePositionType convert(ch.ech.xmlns.ech_0155._3.CandidatePositionInformationType value);

	ListDescriptionInformationType convert(ch.ech.xmlns.ech_0155._3.ListDescriptionInformationType value);

	ListDescriptionInformationType.ListDescriptionInfo convert(ch.ech.xmlns.ech_0155._3.ListDescriptionInformationType.ListDescriptionInfo value);

	ListUnionType convert(ch.ech.xmlns.ech_0155._3.ListUnionType lu);

	ListUnionDescriptionType convert(ch.ech.xmlns.ech_0155._3.ListUnionDescriptionType ludt);

	ListUnionDescriptionType.ListUnionDescriptionInfo convert(ch.ech.xmlns.ech_0155._3.ListUnionDescriptionType.ListUnionDescriptionInfo value);

	@Mapping(target = "vote.voteIdentification", source = "v.vote.voteIdentification")
	@Mapping(target = "vote.domainOfInfluence", source = "v.vote.domainOfInfluence")
	@Mapping(target = "vote.voteDescription", source = "v.vote.voteDescription")
	@Mapping(target = "vote.ballot", source = "v.ballot")
	VoteInformationType convert(ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation v);

	VoteDescriptionInformationType convert(ch.ech.xmlns.ech_0155._3.VoteDescriptionInformationType value);

	VoteDescriptionInformationType.VoteDescriptionInfo convert(ch.ech.xmlns.ech_0155._3.VoteDescriptionInformationType.VoteDescriptionInfo value);

	BallotType convert(ch.ech.xmlns.ech_0155._3.BallotType bt);

	BallotDescriptionInformationType convert(ch.ech.xmlns.ech_0155._3.BallotDescriptionInformationType value);

	BallotDescriptionInformationType.BallotDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._3.BallotDescriptionInformationType.BallotDescriptionInfo value);

	@Mapping(target = "answerType", source = "answerType.answerType")
	@Mapping(target = "answer", ignore = true)
	StandardQuestionType convert(ch.ech.xmlns.ech_0155._3.QuestionInformationType qit);

	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	@Mapping(target = "answerType", source = "tbit.answerType.answerType")
	@Mapping(target = "answer", ignore = true)
	void appendTieBreakQuestionType(
			@MappingTarget
			TieBreakQuestionType tb, ch.ech.xmlns.ech_0155._3.TieBreakInformationType tbit);

	default TieBreakQuestionType convert(final ch.ech.xmlns.ech_0155._3.TieBreakInformationType tbit) {
		final TieBreakQuestionTypeWithReferences result = new TieBreakQuestionTypeWithReferences();
		appendTieBreakQuestionType(result, tbit);
		result.setReferencedQuestion1(tbit.getReferencedQuestion1());
		result.setReferencedQuestion2(tbit.getReferencedQuestion2());
		return result;
	}

	@Mapping(target = "ballotQuestionInfo", source = "tieBreakQuestionInfo")
	BallotQuestionType convert(ch.ech.xmlns.ech_0155._3.TieBreakQuestionType tbqt);

	@Mapping(target = "ballotQuestionTitle", source = "tieBreakQuestionTitle")
	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	BallotQuestionType.BallotQuestionInfo convert(ch.ech.xmlns.ech_0155._3.TieBreakQuestionType.TieBreakQuestionInfo value);

	@Mapping(target = "standardQuestion", source = "questionInformation")
	@Mapping(target = "tieBreakQuestion", source = "tieBreakInformation")
	VariantBallotType convert(ch.ech.xmlns.ech_0155._3.BallotType.VariantBallot vb);

	@Mapping(target = "answerType", source = "answerType.answerType")
	@Mapping(target = "answer", ignore = true)
	StandardBallotType convert(ch.ech.xmlns.ech_0155._3.BallotType.StandardBallot sb);

	@Mapping(target = "ballotQuestionInfo", source = "ballotQuestionInfo")
	BallotQuestionType convert(ch.ech.xmlns.ech_0155._3.BallotQuestionType bqt);

	BallotQuestionType.BallotQuestionInfo convert(ch.ech.xmlns.ech_0155._3.BallotQuestionType.BallotQuestionInfo value);

	LanguageType convert(ch.ech.xmlns.ech_0155._3.LanguageType lang);

}