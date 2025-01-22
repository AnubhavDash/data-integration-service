/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static ch.ech.xmlns.ech_0155._4.PartyAffiliationformationType.*;
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
import ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.BallotQuestionType;
import ch.ech.xmlns.ech_0155._4.BallotType;
import ch.ech.xmlns.ech_0155._4.CandidatePositionInformationType;
import ch.ech.xmlns.ech_0155._4.CandidateTextInformationType;
import ch.ech.xmlns.ech_0155._4.CandidateType;
import ch.ech.xmlns.ech_0155._4.ContestDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.ContestType;
import ch.ech.xmlns.ech_0155._4.EVotingPeriodType;
import ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.ElectionType;
import ch.ech.xmlns.ech_0155._4.ListDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.ListType;
import ch.ech.xmlns.ech_0155._4.ListUnionType;
import ch.ech.xmlns.ech_0155._4.OccupationalTitleInformationType;
import ch.ech.xmlns.ech_0155._4.QuestionInformationType;
import ch.ech.xmlns.ech_0155._4.ReferencedElectionInformationType;
import ch.ech.xmlns.ech_0155._4.TieBreakInformationType;
import ch.ech.xmlns.ech_0155._4.TieBreakQuestionType;
import ch.ech.xmlns.ech_0155._4.VoteDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.VoteType;
import ch.ech.xmlns.ech_0159._4.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.common.DateUtil;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType.ListDescriptionInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PartyAffiliationformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

@SpringBootTest
@ActiveProfiles("test")
class ECH015Xv40MapperIT {

	private final ECH015xv40Mapper mapper = ECH015xv40Mapper.INSTANCE;

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
				.withDomainOfInfluenceIdentification("DoiForVote")
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

	private BallotDescriptionInformationType createEchBallotDescriptionInformationType() {
		return new BallotDescriptionInformationType()
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("de")
						.withBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (DE)")
						.withBallotDescriptionShort("Nouvelle loi ES (DE)")
				)
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("fr")
						.withBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (FR)")
				)
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("it")
						.withBallotDescriptionShort("Nouvelle loi ES (IT)")
				);
	}

	private BallotDescriptionInformationType createEchBallotDescriptionInformationTypeWithMissingDescriptions() {
		return new BallotDescriptionInformationType()
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("de")
						.withBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (DE)")
						.withBallotDescriptionShort("Nouvelle loi ES (DE)")
				)
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("fr")
						.withBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (FR)")
				)
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("it")
						.withBallotDescriptionShort("Nouvelle loi ES (IT)")
				)
				.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
						.withLanguage("rm")
				);
	}

	private BallotQuestionType createEchBallotQuestionType() {
		return new BallotQuestionType()
				.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
						.withLanguage("de")
						.withBallotQuestionTitle("Votre avis sur la loi ES (DE)")
						.withBallotQuestion("Acceptez-vous la loi ES ? (DE)")
				)
				.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
						.withLanguage("it")
						.withBallotQuestionTitle("Votre avis sur la loi ES (IT)")
						.withBallotQuestion("Acceptez-vous la loi ES ? (IT)")
				)
				.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
						.withLanguage("rm")
						.withBallotQuestionTitle("Votre avis sur la loi ES (RM)")
						.withBallotQuestion("Acceptez-vous la loi ES ? (RM)")
				);
	}

	private BallotQuestionType createEchBallotQuestionTypeWithMissingQuestionInfo() {
		return new BallotQuestionType();
	}

	private QuestionInformationType createEchQuestionInformationType() {
		return new QuestionInformationType()
				.withQuestionIdentification("QUE-000-43511932")
				.withQuestionPosition(BigInteger.ONE)
				.withBallotQuestion(createEchBallotQuestionType());
	}

	private TieBreakInformationType createEchTieBreakInformationType() {
		return new TieBreakInformationType()
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

	private BallotType createEchBallot() {
		return new BallotType()
				.withBallotIdentification("BAL-000-432556")
				.withBallotPosition(BigInteger.ONE)
				.withBallotDescription(createEchBallotDescriptionInformationType())
				.withStandardBallot(createEchStandardBallot());
	}

	private BallotType createEchBallotWithMissingQuestionInfo() {
		return new BallotType()
				.withBallotIdentification("BAL-000-432556")
				.withBallotPosition(BigInteger.ONE)
				.withBallotDescription(createEchBallotDescriptionInformationTypeWithMissingDescriptions())
				.withStandardBallot(createEchStandardBallotWithMissingQuestionInfo());
	}

	private BallotType.StandardBallot createEchStandardBallot() {
		return new BallotType.StandardBallot()
				.withQuestionIdentification("QUE-000-43532532")
				.withBallotQuestion(createEchBallotQuestionType());
	}

	private BallotType.StandardBallot createEchStandardBallotWithMissingQuestionInfo() {
		return new BallotType.StandardBallot()
				.withQuestionIdentification("QUE-000-43532532")
				.withBallotQuestion(createEchBallotQuestionTypeWithMissingQuestionInfo());
	}

	private BallotType.VariantBallot createEchVariantBallot() {
		return new BallotType.VariantBallot()
				.withQuestionInformation(createEchQuestionInformationType())
				.withQuestionInformation(createEchQuestionInformationType())
				.withTieBreakInformation(createEchTieBreakInformationType())
				.withTieBreakInformation(createEchTieBreakInformationType());
	}

	private EventInitialDelivery.VoteInformation createEchVoteInformation() {
		final EventInitialDelivery.VoteInformation echVote = new EventInitialDelivery.VoteInformation();
		echVote
				.withVote(createEchVoteType())
				.withBallot(new BallotType()
						.withBallotIdentification("BAL-000-432556")
						.withBallotPosition(BigInteger.ONE)
						.withBallotDescription(createEchBallotDescriptionInformationType())
						.withStandardBallot(createEchStandardBallot())

				)
				.withBallot(new BallotType()
						.withBallotIdentification("BAL-000-A325156")
						.withBallotPosition(BigInteger.TWO)
						.withBallotDescription(createEchBallotDescriptionInformationType())
						.withVariantBallot(createEchVariantBallot())
				);
		return echVote;
	}

	private ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation createEchElectionInformation() {
		final ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation echElection = new ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation();
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
								.withDateOfBirth(DateUtil.parseToXMLGregorianCalendar("1999-04-29 00:00:00"))
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

	private PartyAffiliationInfo createPartyAffiliationInfo(final boolean partyAffiliationShortProvided) {
		final PartyAffiliationInfo partyAffiliationInfo = new PartyAffiliationInfo()
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
	void testBallotDescriptionInformationType() {
		final BallotDescriptionInformationType source = createEchBallotDescriptionInformationType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotDescriptionInformationType destination = mapper.convert(source,
				createEchBallot());

		assertEquals(source.getBallotDescriptionInfo().size(), destination.getBallotDescriptionInfo().size());
	}

	@Test
	void testBallotDescriptionInfo() {
		final BallotDescriptionInformationType source = createEchBallotDescriptionInformationType();
		final BallotType echBallot = createEchBallot();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotDescriptionInformationType destination = mapper.convert(source,
				echBallot);

		final Set<String> echDescriptions = source.getBallotDescriptionInfo()
				.stream()
				.map(d -> {
					final String language = d.getLanguage();
					final Optional<BallotQuestionType.BallotQuestionInfo> ballotQuestion = echBallot.getStandardBallot().getBallotQuestion()
							.getBallotQuestionInfo().stream()
							.filter(bdi -> bdi.getLanguage().equals(language))
							.findAny();

					String ballotDescriptionShort = "";
					if (d.getBallotDescriptionShort() != null) {
						ballotDescriptionShort = d.getBallotDescriptionShort();
					} else if (d.getBallotDescriptionLong() != null) {
						ballotDescriptionShort = d.getBallotDescriptionLong();
					} else if (ballotQuestion.isPresent()) {
						ballotDescriptionShort = ballotQuestion.get().getBallotQuestionTitle();
					}

					String ballotDescriptionLong = "";
					if (d.getBallotDescriptionLong() != null) {
						ballotDescriptionLong = d.getBallotDescriptionLong();
					} else if (d.getBallotDescriptionShort() != null) {
						ballotDescriptionLong = d.getBallotDescriptionShort();
					} else if (ballotQuestion.isPresent()) {
						ballotDescriptionLong = ballotQuestion.get().getBallotQuestionTitle();
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
	void testBallotDescriptionInfoWithMissingDescriptions() {
		final BallotDescriptionInformationType source = createEchBallotDescriptionInformationTypeWithMissingDescriptions();
		final BallotType echBallotWithMissingQuestionInfo = createEchBallotWithMissingQuestionInfo();
		final IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> mapper.convert(source, echBallotWithMissingQuestionInfo));
		assertEquals("Ballot question info is not available in expected language. [language: rm]", exception.getMessage());
	}

	@Test
	void testBallotQuestionType() {
		final BallotQuestionType source = createEchBallotQuestionType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType destination = mapper.convert(source, null);

		assertEquals(source.getBallotQuestionInfo().size(), destination.getBallotQuestionInfo().size());
	}

	@Test
	void testBallotQuestionType_BallotQuestionInfo() {
		final BallotQuestionType source = createEchBallotQuestionType();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType destination = mapper.convert(source, null);

		final Set<String> echDescriptions = source.getBallotQuestionInfo()
				.stream()
				.map(d -> String.format("%s - %s - %s", d.getLanguage(), d.getBallotQuestionTitle(), d.getBallotQuestion()))
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
		assertEquals(source.getBallot().size(), destination.getVote().getBallot().size());
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

		final ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot.ElectionInformation source = createEchElectionInformation();
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
		final PartyAffiliationInfo partyAffiliationInfo = createPartyAffiliationInfo(false);

		final PartyAffiliationformationType.PartyAffiliationInfo destination = mapper.convert(partyAffiliationInfo);

		assertEquals(partyAffiliationInfo.getLanguage(), destination.getLanguage().value());
		assertEquals(partyAffiliationInfo.getPartyAffiliationShort(), destination.getPartyAffiliationShort());
		assertEquals(partyAffiliationInfo.getPartyAffiliationShort(), destination.getPartyAffiliationLong());
	}

	@Test
	void testPartyAffiliationLongProvided() {
		final PartyAffiliationInfo partyAffiliationInfo = createPartyAffiliationInfo(true);

		final PartyAffiliationformationType.PartyAffiliationInfo destination = mapper.convert(partyAffiliationInfo);

		assertEquals(partyAffiliationInfo.getLanguage(), destination.getLanguage().value());
		assertEquals(partyAffiliationInfo.getPartyAffiliationShort(), destination.getPartyAffiliationShort());
		assertEquals(partyAffiliationInfo.getPartyAffiliationLong(), destination.getPartyAffiliationLong());
	}
}
