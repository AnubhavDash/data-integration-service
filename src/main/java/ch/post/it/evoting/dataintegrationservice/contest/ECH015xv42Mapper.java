/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet.toImmutableSet;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.function.Predicate.not;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

import ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
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
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.RoleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

@Mapper(imports = { UUID.class, LanguageType.class })
public interface ECH015xv42Mapper {
	ECH015xv42Mapper INSTANCE = Mappers.getMapper(ECH015xv42Mapper.class);

	Logger LOGGER = LoggerFactory.getLogger(ECH015xv42Mapper.class);

	ImmutableList<String> LANGUAGES = Arrays.stream(LanguageType.values()).map(LanguageType::value).collect(toImmutableList());

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

	@Mapping(target = "writeInsAllowed", ignore = true)
	@Mapping(target = "candidateAccumulation", ignore = true)
	ElectionType convert(ch.ech.xmlns.ech_0155._5.ElectionType et);

	ElectionDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType value);

	@Mapping(target = "electionDescriptionShort", expression = "java( mapElectionDescriptionShort(\"electionDescriptionShort\", value) )")
	ElectionDescriptionInformationType.ElectionDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType.ElectionDescriptionInfo value);

	ReferencedElectionInformationType convert(ch.ech.xmlns.ech_0155._5.ReferencedElectionInformationType value);

	@Mapping(target = "dwellingAddress", source = "dwellingAddress")
	@Mapping(target = "position", source = "sortID")
	@Mapping(target = "incumbent.incumbent", source = "incumbentYesNo")
	@Mapping(target = "referenceOnPosition", source = "candidateReference")
	@Mapping(target = "eligibility", constant = "EXPLICIT")
	@Mapping(target = "title", expression = "java( ct.getTitle() != null && ct.getTitle().length() > 0 ? ct.getTitle() : null )")
	@Mapping(target = "firstName", expression = "java( ct.getFirstName() != null && ct.getFirstName().length() > 0 ? ct.getFirstName() : ct.getCallName() )")
	CandidateType convert(ch.ech.xmlns.ech_0155._5.CandidateType ct);

	RoleInformationType convert(ch.ech.xmlns.ech_0155._5.RoleInformationType rit);

	@Mapping(target = "language", source = "roleInfo.language")
	@Mapping(target = "role", source = "roleInfo.role")
	RoleInformationType.RoleInfo convert(ch.ech.xmlns.ech_0155._5.RoleInformationType.RoleInfo roleInfo);

	PartyAffiliationformationType convert(ch.ech.xmlns.ech_0155._5.PartyAffiliationInformationType pat);

	@Mapping(target = "language", source = "partyAffiliationInfo.language")
	@Mapping(target = "partyAffiliationShort", source = "partyAffiliationInfo.partyAffiliationShort")
	@Mapping(target = "partyAffiliationLong", expression = "java( mapPartyAffiliationLong(partyAffiliationInfo) )")
	PartyAffiliationformationType.PartyAffiliationInfo convert(
			ch.ech.xmlns.ech_0155._5.PartyAffiliationInformationType.PartyAffiliationInfo partyAffiliationInfo);

	// This method is called from a MapStruct java expression
	default String mapPartyAffiliationLong(final ch.ech.xmlns.ech_0155._5.PartyAffiliationInformationType.PartyAffiliationInfo partyAffiliationInfo) {
		checkNotNull(partyAffiliationInfo);

		final String partyAffiliationLong = partyAffiliationInfo.getPartyAffiliationLong();
		if (partyAffiliationLong != null) {
			return partyAffiliationLong;
		}

		final String partyAffiliationShort = partyAffiliationInfo.getPartyAffiliationShort();
		LOGGER.warn("Field partyAffiliationLong is missing. Filling its value with the short one. [partyAffiliationShort: {}]",
				partyAffiliationShort);
		return partyAffiliationShort;
	}

	DwellingAddressType convert(ch.ech.xmlns.ech_0010._6.AddressInformationType address);

	CandidateType.Swiss map(ch.ech.xmlns.ech_0155._5.CandidateType.Swiss value);

	default CandidateTextInformationType convert(final ch.ech.xmlns.ech_0155._5.CandidateTextInformationType value) {
		final CandidateTextInformationType candidateTextInformationType = new CandidateTextInformationType();

		if (value == null) {
			candidateTextInformationType.setCandidateTextInfo(new ArrayList<>());
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

	default List<ListType> convert(final List<ch.ech.xmlns.ech_0155._5.ListType> list) {
		return list.stream().filter(not(ch.ech.xmlns.ech_0155._5.ListType::isIsEmptyList)).map(this::convert).toList();
	}

	@Mapping(target = "candidateListIdentification", ignore = true)
	CandidatePositionType convert(ch.ech.xmlns.ech_0155._5.CandidatePositionInformationType value);

	ListDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.ListDescriptionInformationType value);

	@Mapping(target = "language", source = "listDescriptionInfo.language")
	@Mapping(target = "listDescriptionShort", expression = "java( mapListDescriptionShort(listDescriptionInfo) )")
	@Mapping(target = "listDescription", source = "listDescriptionInfo.listDescription")
	ListDescriptionInformationType.ListDescriptionInfo convert(
			ch.ech.xmlns.ech_0155._5.ListDescriptionInformationType.ListDescriptionInfo listDescriptionInfo);

	// This method is called from a MapStruct java expression
	default String mapListDescriptionShort(final ch.ech.xmlns.ech_0155._5.ListDescriptionInformationType.ListDescriptionInfo listDescriptionInfo) {
		checkNotNull(listDescriptionInfo);

		final String listDescriptionShort = listDescriptionInfo.getListDescriptionShort();
		if (listDescriptionShort != null) {
			return listDescriptionShort;
		}

		final String listDescription = listDescriptionInfo.getListDescription();
		LOGGER.warn("Field listDescriptionShort is missing. Filling its value with a truncated value of the list description. [listDescription: {}]",
				listDescription);
		return listDescription.substring(0, Math.min(listDescription.length(), 20));
	}

	ListUnionType convert(ch.ech.xmlns.ech_0155._5.ListUnionType lu);

	ListUnionDescriptionType convert(ch.ech.xmlns.ech_0155._5.ListUnionDescriptionType ludt);

	ListUnionDescriptionType.ListUnionDescriptionInfo convert(ch.ech.xmlns.ech_0155._5.ListUnionDescriptionType.ListUnionDescriptionInfo value);

	@Mapping(target = "vote.voteIdentification", source = "v.vote.voteIdentification")
	@Mapping(target = "vote.domainOfInfluence", source = "v.vote.domainOfInfluence.domainOfInfluenceIdentification")
	@Mapping(target = "vote.voteDescription", source = "v.vote.voteDescription")
	@Mapping(target = "vote.ballot", source = "v.electronicBallot")
	VoteInformationType convert(ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation v);

	VoteDescriptionInformationType convert(ch.ech.xmlns.ech_0155._5.VoteDescriptionInformationType value);

	VoteDescriptionInformationType.VoteDescriptionInfo convert(ch.ech.xmlns.ech_0155._5.VoteDescriptionInformationType.VoteDescriptionInfo value);

	default ElectionGroupBallotType convert(final ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup e) {
		final ElectionGroupBallotType electionGroupBallotType = new ElectionGroupBallotType();

		electionGroupBallotType.setElectionGroupIdentification(e.getElectionGroupIdentification());
		electionGroupBallotType.setDomainOfInfluence(e.getDomainOfInfluence().getDomainOfInfluenceIdentification());
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
	@Mapping(target = "emptyList", expression = "java( mapEmptyList(electionInformation.getList(), electionInformation.getElection().getElectionIdentification()) )")
	@Mapping(target = "listUnion", source = "electionInformation.listUnion")
	ElectionInformationType convert(ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation electionInformation);

	default EmptyListType mapEmptyList(final List<ch.ech.xmlns.ech_0155._5.ListType> lists, final String electionIdentification) {
		// The eCH may not have an empty list in case of majority elections.
		if (lists.isEmpty()) {
			return null;
		}

		final ch.ech.xmlns.ech_0155._5.ListType emptyListSource = lists.stream()
				.filter(ch.ech.xmlns.ech_0155._5.ListType::isIsEmptyList)
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
		if (bt.getElectronicBallotGroup() != null) {
			ballotType.setBallotGroup(convert(bt.getElectronicBallotGroup(), bt));
		}
		ballotType.setStandardBallot(convert(bt.getStandardElectronicBallot(), bt));
		ballotType.setVariantBallot(convert(bt.getVariantElectronicBallot(), bt));

		return ballotType;
	}

	@Mapping(target = "ballotDescriptionInfo", source = "value.electronicBallotDescriptionInfo")
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
				.map(description -> StringUtils.left(description, 255))
				.orElseGet(() -> getBallotQuestionTitle(bdi.getLanguage(), ballotType)));

		return result;
	}

	@Mapping(target = "answerType", source = "answerInformation.answerType", defaultExpression = "java(new java.math.BigInteger(\"8\"))")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "electronicBallotQuestionNumber")
	@Mapping(target = "ballotQuestion", source = "electronicBallotQuestion")
	StandardQuestionType convert(ch.ech.xmlns.ech_0155._5.QuestionInformationType qit,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	@Mapping(target = "ballotQuestion", source = "tieBreakQuestion")
	@Mapping(target = "answerType", source = "tbit.answerInformation.answerType", defaultExpression = "java(new java.math.BigInteger(\"8\"))")
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

	@Mapping(target = "answerType", source = "answerInformation.answerType", defaultExpression = "java(new java.math.BigInteger(\"8\"))")
	@Mapping(target = "answer", source = "answerInformation.answerOptionIdentification")
	@Mapping(target = "questionNumber", source = "electronicBallotQuestionNumber")
	@Mapping(target = "ballotQuestion", source = "electronicBallotQuestion")
	StandardBallotType convert(ch.ech.xmlns.ech_0155._5.ElectronicBallotType.StandardElectronicBallot sb,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	@Mapping(target = "answerIdentification", source = "answerIdentification")
	@Mapping(target = "answerPosition", source = "answerSequenceNumber")
	@Mapping(target = "answerInfo", source = "answerTextInformation")
	@Mapping(target = "standardAnswerType", expression = "java( mapStandardAnswerType(answerOption.getAnswerTextInformation()) )")
	StandardAnswerType convertStandardAnswer(ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType answerOption);

	@Mapping(target = "answerIdentification", source = "answerIdentification")
	@Mapping(target = "answerPosition", source = "answerSequenceNumber")
	@Mapping(target = "answerInfo", source = "answerTextInformation")
	TiebreakAnswerType convertTiebreakAnswer(ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType answerOption);

	default String mapStandardAnswerType(final List<AnswerOptionIdentificationType.AnswerTextInformation> answerTextInformations) {

		final ImmutableSet<String> yes = ImmutableSet.of("Ja", "Oui", "Sì", "Gea");
		final ImmutableSet<String> no = ImmutableSet.of("Nein", "Non", "No", "Na");
		final ImmutableSet<String> empty = ImmutableSet.of("Leer", "Blanc", "Bianco", "Vid");

		final ImmutableSet<String> actual = answerTextInformations.stream()
				.map(AnswerOptionIdentificationType.AnswerTextInformation::getAnswerText)
				.collect(toImmutableSet());

		return switch (actual) {
			case final ImmutableSet<String> s when s.equals(yes) -> "YES";
			case final ImmutableSet<String> s when s.equals(no) -> "NO";
			case final ImmutableSet<String> s when s.equals(empty) -> "EMPTY";
			default -> "UNDEFINED";
		};
	}

	@Mapping(target = "ballotQuestionInfo", source = "electronicBallotQuestionInfo")
	BallotQuestionType convert(final ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType bqt,
			@Context
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType);

	default BallotQuestionType.BallotQuestionInfo convert(
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo bqi,
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
	@Mapping(target = "answerShort", source = "answerTextShort")
	AnswerInformationType convert(ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType.AnswerTextInformation answer);

	default LanguageType convert(final String value) {
		checkArgument(Arrays.stream(LanguageType.values()).anyMatch(lang -> lang.value().equals(value)),
				"Invalid language type: %s. Must be one of %s", value, LANGUAGES);
		return LanguageType.fromValue(value.toLowerCase(Locale.ENGLISH));
	}

	private ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType createEmptyBallotDescriptionInformationType(
			final ch.ech.xmlns.ech_0155._5.ElectronicBallotType ballotType) {
		LOGGER.info("Creation of an empty ballotDescriptionInformation tag from ballot questions for ballot {}.",
				ballotType.getElectronicBallotIdentification());

		final ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType ballotDescriptionInformationType = new ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType();

		final List<ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType.ElectronicBallotQuestionInfo> ballotQuestionInfoList = getBallotQuestionInfoList(
				ballotType);
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
			return ballotType.getVariantElectronicBallot().getQuestionInformation().getFirst().getElectronicBallotQuestion()
					.getElectronicBallotQuestionInfo();
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
			return StringUtils.left(ballotDescriptionInfo.getElectronicBallotDescriptionShort(), 100);
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
			return getBallotQuestionTitleFromInfo(language,
					ballotType.getStandardElectronicBallot().getElectronicBallotQuestion().getElectronicBallotQuestionInfo());
		} else if (ballotType.getVariantElectronicBallot() != null && ballotType.getVariantElectronicBallot().getQuestionInformation() != null) {
			return getBallotQuestionTitleFromInfo(language,
					ballotType.getVariantElectronicBallot().getQuestionInformation().getFirst().getElectronicBallotQuestion()
							.getElectronicBallotQuestionInfo());
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
