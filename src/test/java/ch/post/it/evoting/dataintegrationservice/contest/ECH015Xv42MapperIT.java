/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.google.common.collect.MoreCollectors;

import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.ech.xmlns.ech_0155._5.CandidatePositionInformationType;
import ch.ech.xmlns.ech_0155._5.CandidateTextInformationType;
import ch.ech.xmlns.ech_0155._5.CandidateType;
import ch.ech.xmlns.ech_0155._5.ContestDescriptionInformationType;
import ch.ech.xmlns.ech_0155._5.ContestType;
import ch.ech.xmlns.ech_0155._5.DomainOfInfluenceType;
import ch.ech.xmlns.ech_0155._5.EVotingPeriodType;
import ch.ech.xmlns.ech_0155._5.ElectionDescriptionInformationType;
import ch.ech.xmlns.ech_0155._5.ElectionType;
import ch.ech.xmlns.ech_0155._5.ElectronicBallotDescriptionInformationType;
import ch.ech.xmlns.ech_0155._5.ElectronicBallotQuestionType;
import ch.ech.xmlns.ech_0155._5.ElectronicBallotType;
import ch.ech.xmlns.ech_0155._5.ListDescriptionInformationType;
import ch.ech.xmlns.ech_0155._5.ListType;
import ch.ech.xmlns.ech_0155._5.ListUnionType;
import ch.ech.xmlns.ech_0155._5.OccupationalTitleInformationType;
import ch.ech.xmlns.ech_0155._5.PartyAffiliationInformationType;
import ch.ech.xmlns.ech_0155._5.ReferencedElectionInformationType;
import ch.ech.xmlns.ech_0155._5.TieBreakQuestionType;
import ch.ech.xmlns.ech_0155._5.VoteDescriptionInformationType;
import ch.ech.xmlns.ech_0155._5.VoteType;
import ch.ech.xmlns.ech_0159._5.EventInitialDelivery;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateUtils;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType.ListDescriptionInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PartyAffiliationformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

@SpringBootTest
@ActiveProfiles("test")
class ECH015Xv42MapperIT {

	private final ECH015xv42Mapper mapper = ECH015xv42Mapper.INSTANCE;

	private ContestType createEchContestType() throws Exception {

		final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
		final XMLGregorianCalendar gc = datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
		gc.clear();
		gc.setDay(27);
		gc.setMonth(3);
		gc.setYear(1999);

		final GregorianCalendar tmpTill = gc.toGregorianCalendar();
		tmpTill.add(Calendar.DAY_OF_MONTH, 1);

		final ContestType echContest = new ContestType();
		echContest
				.withContestIdentification("A1-B2-1111111111111-aaa")
				.withContestDate(gc)
				.withContestDescription(createContestDescriptionInformationType()) // Opt
				.withEVotingPeriod(createEVotingPeriodType(datatypeFactory, gc));
		return echContest;
	}

	private ContestDescriptionInformationType createContestDescriptionInformationType() {
		return new ContestDescriptionInformationType()
				.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
						.withLanguage("de")
						.withContestDescription("Votation fédérale (DE)")
				)
				.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
						.withLanguage("fr")
						.withContestDescription("Votation fédérale (FR)")
				)
				.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
						.withLanguage("it")
						.withContestDescription("Votation fédérale (IT)")
				);
	}

	private EVotingPeriodType createEVotingPeriodType(final DatatypeFactory dateFactory, final XMLGregorianCalendar refDate) {
		final GregorianCalendar tmpTill = refDate.toGregorianCalendar();
		tmpTill.add(Calendar.DAY_OF_MONTH, 1);
		final XMLGregorianCalendar dateTill = dateFactory.newXMLGregorianCalendar(tmpTill);

		return new EVotingPeriodType() // Opt
				.withEVotingPeriodFrom(refDate)
				.withEVotingPeriodTill(dateTill);
	}

	private VoteType createEchVoteType() {
		return new VoteType()
				.withVoteIdentification("1234-aaaa-nnnn-0000")
				.withDomainOfInfluence(new DomainOfInfluenceType()
						.withDomainOfInfluenceIdentification("DoiForVote")
						.withDomainOfInfluenceName("DoiName")
						.withDomainOfInfluenceShortname("DoiShortname"))
				.withVoteDescription(createEchVoteDescriptionInformationType());
	}

	private VoteDescriptionInformationType createEchVoteDescriptionInformationType() {
		return new VoteDescriptionInformationType()
				.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo()
						.withLanguage("de")
						.withVoteDescription("L'énergie solaire (DE)")
				)
				.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo()
						.withLanguage("fr")
						.withVoteDescription("L'énergie solaire (FR)")
				)
				.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo()
						.withLanguage("it")
						.withVoteDescription("L'énergie solaire (IT)")
				);
	}

	private ElectronicBallotDescriptionInformationType createEchElectronicBallotDescriptionInformationType() {
		return new ElectronicBallotDescriptionInformationType()
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("de")
						.withElectronicBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (DE)")
						.withElectronicBallotDescriptionShort("Nouvelle loi ES (DE)")
				)
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("fr")
						.withElectronicBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (FR)")
				)
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("it")
						.withElectronicBallotDescriptionShort("Nouvelle loi ES (IT)")
				);
	}

	private ElectronicBallotDescriptionInformationType createEchElectronicBallotDescriptionInformationTypeWithMissingDescriptions() {
		return new ElectronicBallotDescriptionInformationType()
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("de")
						.withElectronicBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (DE)")
						.withElectronicBallotDescriptionShort("Nouvelle loi ES (DE)")
				)
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("fr")
						.withElectronicBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (FR)")
				)
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("it")
						.withElectronicBallotDescriptionShort("Nouvelle loi ES (IT)")
				)
				.withElectronicBallotDescriptionInfo(new ElectronicBallotDescriptionInformationType.ElectronicBallotDescriptionInfo()
						.withLanguage("rm")
				);
	}

	private ElectronicBallotQuestionType createEchElectronicBallotQuestionType() {
		return new ElectronicBallotQuestionType()
				.withElectronicBallotQuestionInfo(new ElectronicBallotQuestionType.ElectronicBallotQuestionInfo()
						.withLanguage("de")
						.withElectronicBallotQuestionTitle("Votre avis sur la loi ES (DE)")
						.withElectronicBallotQuestion("Acceptez-vous la loi ES ? (DE)")
				)
				.withElectronicBallotQuestionInfo(new ElectronicBallotQuestionType.ElectronicBallotQuestionInfo()
						.withLanguage("it")
						.withElectronicBallotQuestionTitle("Votre avis sur la loi ES (IT)")
						.withElectronicBallotQuestion("Acceptez-vous la loi ES ? (IT)")
				)
				.withElectronicBallotQuestionInfo(new ElectronicBallotQuestionType.ElectronicBallotQuestionInfo()
						.withLanguage("rm")
						.withElectronicBallotQuestionTitle("Votre avis sur la loi ES (RM)")
						.withElectronicBallotQuestion("Acceptez-vous la loi ES ? (RM)")
				);
	}

	private ElectronicBallotQuestionType createEchElectronicBallotQuestionTypeWithMissingQuestionInfo() {
		return new ElectronicBallotQuestionType();
	}

	private ElectronicBallotType.VariantElectronicBallot.QuestionInformation createEchQuestionInformation() {
		return new ElectronicBallotType.VariantElectronicBallot.QuestionInformation()
				.withQuestionIdentification("QUE-000-43511932")
				.withQuestionPosition(BigInteger.ONE)
				.withElectronicBallotQuestion(createEchElectronicBallotQuestionType());
	}

	private ElectronicBallotType.VariantElectronicBallot.TieBreakInformation createEchTieBreakInformation() {
		return new ElectronicBallotType.VariantElectronicBallot.TieBreakInformation()
				.withQuestionIdentification("QUE-000-41500009")
				.withQuestionPosition(BigInteger.valueOf(3))
				.withTieBreakQuestion(new TieBreakQuestionType()
						.withTieBreakQuestionInfo(new TieBreakQuestionType.TieBreakQuestionInfo()
								.withLanguage("de")
								.withTieBreakQuestionTitle("En cas de rejet des 2 variantes (DE)")
								.withTieBreakQuestion("Laquelle choisissez-vous ? (DE)")
						)
						.withTieBreakQuestionInfo(new TieBreakQuestionType.TieBreakQuestionInfo()
								.withLanguage("fr")
								.withTieBreakQuestionTitle("En cas de rejet des 2 variantes (FR)")
								.withTieBreakQuestion("Laquelle choisissez-vous ? (FR)")
						)
						.withTieBreakQuestionInfo(new TieBreakQuestionType.TieBreakQuestionInfo()
								.withLanguage("rm")
								.withTieBreakQuestionTitle("En cas de rejet des 2 variantes (RM)")
								.withTieBreakQuestion("Laquelle choisissez-vous ? (RM)")
						)
				);
	}

	private ElectronicBallotType createEchElectronicBallot() {
		return new ElectronicBallotType()
				.withElectronicBallotIdentification("BAL-000-432556")
				.withElectronicBallotPosition(BigInteger.ONE)
				.withElectronicBallotDescription(createEchElectronicBallotDescriptionInformationType())
				.withStandardElectronicBallot(createEchStandardElectronicBallot());
	}

	private ElectronicBallotType createEchElectronicBallotWithMissingQuestionInfo() {
		return new ElectronicBallotType()
				.withElectronicBallotIdentification("BAL-000-432556")
				.withElectronicBallotPosition(BigInteger.ONE)
				.withElectronicBallotDescription(createEchElectronicBallotDescriptionInformationTypeWithMissingDescriptions())
				.withStandardElectronicBallot(createEchStandardElectronicBallotWithMissingQuestionInfo());
	}

	private ElectronicBallotType.StandardElectronicBallot createEchStandardElectronicBallot() {
		return new ElectronicBallotType.StandardElectronicBallot()
				.withQuestionIdentification("QUE-000-43532532")
				.withElectronicBallotQuestion(createEchElectronicBallotQuestionType());
	}

	private ElectronicBallotType.StandardElectronicBallot createEchStandardElectronicBallotWithMissingQuestionInfo() {
		return new ElectronicBallotType.StandardElectronicBallot()
				.withQuestionIdentification("QUE-000-43532532")
				.withElectronicBallotQuestion(createEchElectronicBallotQuestionTypeWithMissingQuestionInfo());
	}

	private ElectronicBallotType.VariantElectronicBallot createEchVariantElectronicBallot() {
		return new ElectronicBallotType.VariantElectronicBallot()
				.withQuestionInformation(createEchQuestionInformation())
				.withQuestionInformation(createEchQuestionInformation())
				.withTieBreakInformation(createEchTieBreakInformation())
				.withTieBreakInformation(createEchTieBreakInformation());
	}

	private EventInitialDelivery.VoteInformation createEchVoteInformation() {
		final EventInitialDelivery.VoteInformation echVote = new EventInitialDelivery.VoteInformation();
		echVote
				.withVote(createEchVoteType())
				.withElectronicBallot(new ElectronicBallotType()
						.withElectronicBallotIdentification("BAL-000-432556")
						.withElectronicBallotPosition(BigInteger.ONE)
						.withElectronicBallotDescription(createEchElectronicBallotDescriptionInformationType())
						.withStandardElectronicBallot(createEchStandardElectronicBallot())

				)
				.withElectronicBallot(new ElectronicBallotType()
						.withElectronicBallotIdentification("BAL-000-A325156")
						.withElectronicBallotPosition(BigInteger.TWO)
						.withElectronicBallotDescription(createEchElectronicBallotDescriptionInformationType())
						.withVariantElectronicBallot(createEchVariantElectronicBallot())
				);
		return echVote;
	}

	private ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation createEchElectionInformation() {
		final ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation echElection = new ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation();
		echElection
				.withElection(new ElectionType()
						.withElectionIdentification("elec-1")
						.withTypeOfElection(BigInteger.ONE)
						.withElectionDescription(new ElectionDescriptionInformationType()
								.withElectionDescriptionInfo(new ElectionDescriptionInformationType.ElectionDescriptionInfo()
										.withLanguage("de")
										.withElectionDescription("desc DE"))
								.withElectionDescriptionInfo(new ElectionDescriptionInformationType.ElectionDescriptionInfo()
										.withLanguage("fr")
										.withElectionDescription("desc FR")
										.withElectionDescriptionShort("desc FR short")))
						.withNumberOfMandates(BigInteger.valueOf(16))
						//                      writeIns : built in contestBuilder
						//                      candidateAccumulation : built in contestBuilder
						.withReferencedElection(new ReferencedElectionInformationType()
								.withReferencedElection("ref1")
								.withElectionRelation(BigInteger.ONE))
						.withReferencedElection(new ReferencedElectionInformationType()
								.withReferencedElection("ref2")
								.withElectionRelation(BigInteger.TWO)))
				.withCandidate(new CandidateType()
								.withCandidateIdentification("candidate1")
								.withMrMrs("1")
								.withTitle("22")
								.withFamilyName("Dupond")
								.withFirstName("Pierre")
								.withCandidateText(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("de")
												.withCandidateText("Candidate 1 text")))
								.withDateOfBirth(LocalDateUtils.toXMLGregorianCalendar(LocalDateUtils.parse("1999-04-29")))
								.withSex("1")
								.withIncumbentYesNo(false)
								.withDwellingAddress(new AddressInformationType()
										.withStreet("Rue de la Gare")
										.withHouseNumber("12 bis")
										.withSwissZipCode(1700L)
										.withTown("Fribourg"))
								.withSwiss(new CandidateType.Swiss()
										.withOrigin("Vuadens")
										.withOrigin("Treyvaux"))
								.withOccupationalTitle(new OccupationalTitleInformationType()
										.withOccupationalTitleInfo(new OccupationalTitleInformationType.OccupationalTitleInfo()
												.withLanguage("de")
												.withOccupationalTitle("Occupation candidate 1"))
										.withOccupationalTitleInfo(new OccupationalTitleInformationType.OccupationalTitleInfo()
												.withLanguage("it")
												.withOccupationalTitle("Occupation candidate 1 IT")))
						//                      position : built in contestBuilder
						//                      referenceOnPosition : built in contestBuilder
				)
				.withList(new ListType()
						.withListIdentification("list1-id")
						.withListIndentureNumber("1a")
						.withListDescription(new ListDescriptionInformationType()
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage("de")
										.withListDescriptionShort("list1 short desc")
										.withListDescription("list1 desc"))
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage("rm")
										.withListDescriptionShort("list1 short desc RM")
										.withListDescription("list1 desc RM")))
						.withListOrderOfPrecedence(BigInteger.ONE)
						.withCandidatePosition(new CandidatePositionInformationType()
								//                              candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.ONE)
								.withCandidateReferenceOnPosition("01.01")
								.withCandidateIdentification("candidate1-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("de")
												.withCandidateText("Candidate 1 list 1 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("it")
												.withCandidateText("Candidate 1 list 1 text IT"))))
						.withCandidatePosition(new CandidatePositionInformationType()
								//                               candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.TWO)
								.withCandidateReferenceOnPosition("01.02")
								.withCandidateIdentification("candidate2-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("de")
												.withCandidateText("Candidate 2 list 1 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("it")
												.withCandidateText("Candidate 2 list 1 text IT")))))
				.withList(new ListType()
						.withListIdentification("list2-id")
						.withListIndentureNumber("2a")
						.withListDescription(new ListDescriptionInformationType()
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage("de")
										.withListDescriptionShort("list2 short desc")
										.withListDescription("list2 desc"))
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage("rm")
										.withListDescriptionShort("list2 short desc RM")
										.withListDescription("list2 desc RM")))
						.withListOrderOfPrecedence(BigInteger.TWO)
						.withCandidatePosition(new CandidatePositionInformationType()
								//                                      candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.TWO)
								.withCandidateReferenceOnPosition("02.02")
								.withCandidateIdentification("candidate2-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("de")
												.withCandidateText("Candidate 2 list 2 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("it")
												.withCandidateText("Candidate 2 list 2 text IT"))))
						.withCandidatePosition(new CandidatePositionInformationType()
								//                                      candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.ONE)
								.withCandidateReferenceOnPosition("02.01")
								.withCandidateIdentification("candidate1-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("de")
												.withCandidateText("Candidate 1 list 2 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage("it")
												.withCandidateText("Candidate 1 list 2 text IT")))))
				.withList(new ListType()
						.withListIdentification("99")
						.withListIndentureNumber("99")
						.withListDescription(new ListDescriptionInformationType()
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage("de")
										.withListDescriptionShort("list99 short desc")
										.withListDescription("list99 desc"))
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage("rm")
										.withListDescriptionShort("list99 short desc RM")
										.withListDescription("list99 desc RM")))
						.withIsEmptyList(true)
						.withListOrderOfPrecedence(BigInteger.valueOf(99))
						.withTotalPositionsOnList(BigInteger.ZERO)
						.withEmptyListPositions(BigInteger.TWO))
				.withListUnion(new ListUnionType()
						.withListUnionIdentification("LLL-aaa-1"))
				.withListUnion(new ListUnionType()
						.withListUnionIdentification("LLL-aaa-2"));
		return echElection;
	}

	private ListDescriptionInformationType.ListDescriptionInfo createListDescriptionInfo(
			final boolean listDescriptionShortProvided) {

		final ListDescriptionInformationType.ListDescriptionInfo listDescriptionInfo = new ListDescriptionInformationType.ListDescriptionInfo()
				.withLanguage("de")
				.withListDescription("list1 long");

		if (!listDescriptionShortProvided) {
			return listDescriptionInfo;
		}

		return listDescriptionInfo.withListDescriptionShort("list1 short");
	}

	private PartyAffiliationInformationType.PartyAffiliationInfo createPartyAffiliationInfo(final boolean partyAffiliationShortProvided) {
		final PartyAffiliationInformationType.PartyAffiliationInfo partyAffiliationInfo = new PartyAffiliationInformationType.PartyAffiliationInfo()
				.withLanguage("de")
				.withPartyAffiliationShort("partei1 short");

		if (!partyAffiliationShortProvided) {
			return partyAffiliationInfo;
		}

		return partyAffiliationInfo.withPartyAffiliationLong("partei1 long");
	}

	@Test
	void testContestType() throws Exception {

		final ContestType echContest = createEchContestType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType contest = mapper.convert(echContest);
		assertEquals(echContest.getContestIdentification(), contest.getContestIdentification());
		assertEquals(echContest.getContestDate().toXMLFormat(), contest.getContestDate().toXMLFormat());
	}

	@Test
	void testContestDescriptionInformationType() throws Exception {
		final ContestType echContest = createEchContestType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType contest = mapper.convert(echContest);
		final Set<String> echDescriptions = echContest.getContestDescription().getContestDescriptionInfo()
				.stream()
				.map(d -> d.getLanguage() + "-" + d.getContestDescription())
				.collect(Collectors.toSet());
		final Set<String> descriptions = contest.getContestDescription().getContestDescriptionInfo()
				.stream()
				.map(d -> d.getLanguage().value() + "-" + d.getContestDescription())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
	}

	@Test
	void testElectronicBallotDescriptionInformationType() {
		final ElectronicBallotDescriptionInformationType source = createEchElectronicBallotDescriptionInformationType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotDescriptionInformationType destination = mapper.convert(source,
				createEchElectronicBallot());

		assertEquals(source.getElectronicBallotDescriptionInfo().size(), destination.getBallotDescriptionInfo().size());
	}

	@Test
	void testElectronicBallotDescriptionInfo() {
		final ElectronicBallotDescriptionInformationType source = createEchElectronicBallotDescriptionInformationType();
		final ElectronicBallotType echBallot = createEchElectronicBallot();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotDescriptionInformationType destination = mapper.convert(source,
				echBallot);

		final Set<String> echDescriptions = source.getElectronicBallotDescriptionInfo()
				.stream()
				.map(d -> {
					final String language = d.getLanguage();
					final Optional<ElectronicBallotQuestionType.ElectronicBallotQuestionInfo> ballotQuestion = echBallot.getStandardElectronicBallot()
							.getElectronicBallotQuestion()
							.getElectronicBallotQuestionInfo().stream()
							.filter(bdi -> bdi.getLanguage().equals(language))
							.findAny();

					String ballotDescriptionShort = "";
					if (d.getElectronicBallotDescriptionShort() != null) {
						ballotDescriptionShort = d.getElectronicBallotDescriptionShort();
					} else if (d.getElectronicBallotDescriptionLong() != null) {
						ballotDescriptionShort = d.getElectronicBallotDescriptionLong();
					} else if (ballotQuestion.isPresent()) {
						ballotDescriptionShort = ballotQuestion.get().getElectronicBallotQuestionTitle();
					}

					String ballotDescriptionLong = "";
					if (d.getElectronicBallotDescriptionLong() != null) {
						ballotDescriptionLong = d.getElectronicBallotDescriptionLong();
					} else if (d.getElectronicBallotDescriptionShort() != null) {
						ballotDescriptionLong = d.getElectronicBallotDescriptionShort();
					} else if (ballotQuestion.isPresent()) {
						ballotDescriptionLong = ballotQuestion.get().getElectronicBallotQuestionTitle();
					}

					return String.format("%s - %s - %s", d.getLanguage(), ballotDescriptionShort, ballotDescriptionLong);
				})
				.collect(Collectors.toSet());
		final Set<String> descriptions = destination.getBallotDescriptionInfo()
				.stream()
				.map(d -> String.format("%s - %s - %s", d.getLanguage().value(), d.getBallotDescriptionShort(), d.getBallotDescriptionLong()))
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
	}

	@Test
	void testElectronicBallotDescriptionInfoWithMissingDescriptions() {
		final ElectronicBallotDescriptionInformationType source = createEchElectronicBallotDescriptionInformationTypeWithMissingDescriptions();
		final ElectronicBallotType echBallotWithMissingQuestionInfo = createEchElectronicBallotWithMissingQuestionInfo();
		final IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> mapper.convert(source, echBallotWithMissingQuestionInfo));
		assertEquals("Ballot question info is not available in expected language. [language: rm]", exception.getMessage());
	}

	@Test
	void testElectronicBallotQuestionType() {
		final ElectronicBallotQuestionType source = createEchElectronicBallotQuestionType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType destination = mapper.convert(source, null);

		assertEquals(source.getElectronicBallotQuestionInfo().size(), destination.getBallotQuestionInfo().size());
	}

	@Test
	void testElectronicBallotQuestionType_BallotQuestionInfo() {
		final ElectronicBallotQuestionType source = createEchElectronicBallotQuestionType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType destination = mapper.convert(source, null);

		final Set<String> echDescriptions = source.getElectronicBallotQuestionInfo()
				.stream()
				.map(d -> String.format("%s - %s - %s", d.getLanguage(), d.getElectronicBallotQuestionTitle(), d.getElectronicBallotQuestion()))
				.collect(Collectors.toSet());
		final Set<String> descriptions = destination.getBallotQuestionInfo()
				.stream()
				.map(d -> String.format("%s - %s - %s", d.getLanguage().value(), d.getBallotQuestionTitle(), d.getBallotQuestion()))
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
	}

	@Test
	void testVoteInformation() {
		final EventInitialDelivery.VoteInformation source = createEchVoteInformation();
		final VoteInformationType destination = mapper.convert(source);

		assertEquals(source.getVote() != null, destination.getVote() != null);
		assertEquals(source.getElectronicBallot().size(), destination.getVote().getBallot().size());
	}

	@Test
	void testVoteDescriptionInfo() {
		final EventInitialDelivery.VoteInformation source = createEchVoteInformation();
		final VoteInformationType destination = mapper.convert(source);

		final Set<String> echDescriptions = source.getVote().getVoteDescription().getVoteDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage() + "-" + v.getVoteDescription())
				.collect(Collectors.toSet());
		final Set<String> descriptions = destination.getVote().getVoteDescription().getVoteDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getVoteDescription())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
	}

	@Test
	void testElectionInformation() {

		final ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.ElectionInformation source = createEchElectionInformation();
		final ElectionInformationType destination = mapper.convert(source);

		final ElectionType electionSource = source.getElection();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType electionDestination = destination.getElection();

		assertEquals(electionSource != null, electionDestination != null);
		assertEquals(source.getList().size() - 1, destination.getList().size());

		final ListType sourceEmptyList = source.getList().stream().filter(ListType::isIsEmptyList).collect(MoreCollectors.onlyElement());
		final EmptyListType destinationEmptyList = destination.getEmptyList();
		assertEquals(sourceEmptyList.getListIdentification(), destinationEmptyList.getListIdentification());

		if (null != electionSource && null != electionDestination) {
			assertEquals(electionSource.getElectionIdentification(), electionDestination.getElectionIdentification());
			assertEquals(electionSource.getNumberOfMandates().intValue(), electionDestination.getNumberOfMandates());
			assertEquals(electionSource.getReferencedElection().size(), electionDestination.getReferencedElection().size());
		}
	}

	@Test
	void testListDescriptionShortMissing() {
		final ListDescriptionInformationType.ListDescriptionInfo source = createListDescriptionInfo(false);

		final ListDescriptionInfo destination = mapper.convert(source);

		assertEquals(source.getLanguage(), destination.getLanguage().value());
		assertEquals(source.getListDescription(), destination.getListDescription());
		assertEquals(source.getListDescription(), destination.getListDescriptionShort());
	}

	@Test
	void testListDescriptionShortProvided() {
		final ListDescriptionInformationType.ListDescriptionInfo source = createListDescriptionInfo(true);

		final ListDescriptionInfo destination = mapper.convert(source);

		assertEquals(source.getLanguage(), destination.getLanguage().value());
		assertEquals(source.getListDescription(), destination.getListDescription());
		assertEquals(source.getListDescriptionShort(), destination.getListDescriptionShort());
	}

	@Test
	void testPartyAffiliationLongMissing() {
		final PartyAffiliationInformationType.PartyAffiliationInfo partyAffiliationInfo = createPartyAffiliationInfo(false);

		final PartyAffiliationformationType.PartyAffiliationInfo destination = mapper.convert(partyAffiliationInfo);

		assertEquals(partyAffiliationInfo.getLanguage(), destination.getLanguage().value());
		assertEquals(partyAffiliationInfo.getPartyAffiliationShort(), destination.getPartyAffiliationShort());
		assertEquals(partyAffiliationInfo.getPartyAffiliationShort(), destination.getPartyAffiliationLong());
	}

	@Test
	void testPartyAffiliationLongProvided() {
		final PartyAffiliationInformationType.PartyAffiliationInfo partyAffiliationInfo = createPartyAffiliationInfo(true);

		final PartyAffiliationformationType.PartyAffiliationInfo destination = mapper.convert(partyAffiliationInfo);

		assertEquals(partyAffiliationInfo.getLanguage(), destination.getLanguage().value());
		assertEquals(partyAffiliationInfo.getPartyAffiliationShort(), destination.getPartyAffiliationShort());
		assertEquals(partyAffiliationInfo.getPartyAffiliationLong(), destination.getPartyAffiliationLong());
	}
}
