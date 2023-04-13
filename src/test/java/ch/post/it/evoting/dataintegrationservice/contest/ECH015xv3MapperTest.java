/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.ech.xmlns.ech_0155._3.AnswerInformationType;
import ch.ech.xmlns.ech_0155._3.BallotDescriptionInformationType;
import ch.ech.xmlns.ech_0155._3.BallotQuestionType;
import ch.ech.xmlns.ech_0155._3.CandidatePositionInformationType;
import ch.ech.xmlns.ech_0155._3.CandidateTextInformationType;
import ch.ech.xmlns.ech_0155._3.CandidateType;
import ch.ech.xmlns.ech_0155._3.ContestDescriptionInformationType;
import ch.ech.xmlns.ech_0155._3.ElectionDescriptionInformationType;
import ch.ech.xmlns.ech_0155._3.ElectionType;
import ch.ech.xmlns.ech_0155._3.LanguageType;
import ch.ech.xmlns.ech_0155._3.ListDescriptionInformationType;
import ch.ech.xmlns.ech_0155._3.ListType;
import ch.ech.xmlns.ech_0155._3.ListUnionType;
import ch.ech.xmlns.ech_0155._3.OccupationalTitleInformationType;
import ch.ech.xmlns.ech_0155._3.QuestionInformationType;
import ch.ech.xmlns.ech_0155._3.ReferencedElectionInformationType;
import ch.ech.xmlns.ech_0155._3.TieBreakInformationType;
import ch.ech.xmlns.ech_0155._3.VoteDescriptionInformationType;
import ch.ech.xmlns.ech_0155._3.VoteType;
import ch.ech.xmlns.ech_0159._3.EventInitialDelivery;
import ch.evoting.xmlns.parameter._4.ContestType;
import ch.post.it.evoting.dataintegrationservice.common.DateUtil;
import ch.post.it.evoting.dataintegrationservice.parameter.ParameterMapper;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

class ECH015xv3MapperTest {

	private final ECH015xv3Mapper mapper = ECH015xv3Mapper.INSTANCE;

	private ch.ech.xmlns.ech_0155._3.ContestType createEchContest() throws Exception {
		final XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		gc.clear();
		gc.setDay(27);
		gc.setMonth(3);
		gc.setYear(1999);
		final ch.ech.xmlns.ech_0155._3.ContestType echContest = new ch.ech.xmlns.ech_0155._3.ContestType();
		echContest
				.withContestIdentification("A1-B2-1111111111111-aaa")
				.withContestDate(gc)
				.withContestDescription(new ContestDescriptionInformationType()
						.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
								.withLanguage(LanguageType.DE)
								.withContestDescription("Votation fédérale (DE)")
						)
						.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
								.withLanguage(LanguageType.FR)
								.withContestDescription("Votation fédérale (FR)")
						)
						.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
								.withLanguage(LanguageType.IT)
								.withContestDescription("Votation fédérale (IT)")
						)
				);
		return echContest;
	}

	private EventInitialDelivery.VoteInformation createEchVote() throws Exception {
		final EventInitialDelivery.VoteInformation echVote = new EventInitialDelivery.VoteInformation();
		echVote
				.withVote(new VoteType()
						.withVoteIdentification("1234-aaaa-nnnn-0000")
						.withVoteDescription(new VoteDescriptionInformationType()
								.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo()
										.withLanguage(LanguageType.DE)
										.withVoteDescription("L'énergie solaire (DE)")
								)
								.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo()
										.withLanguage(LanguageType.FR)
										.withVoteDescription("L'énergie solaire (FR)")
								)
								.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo()
										.withLanguage(LanguageType.IT)
										.withVoteDescription("L'énergie solaire (IT")
								)
						)
				)
				.withBallot(new ch.ech.xmlns.ech_0155._3.BallotType()
						.withBallotIdentification("BAL-000-432556")
						.withBallotPosition(BigInteger.valueOf(1))
						.withBallotDescription(new BallotDescriptionInformationType()
								.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
										.withLanguage(LanguageType.DE)
										.withBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (DE)")
										.withBallotDescriptionShort("Nouvelle loi ES (DE)")
								)
								.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
										.withLanguage(LanguageType.FR)
										.withBallotDescriptionLong("Nouvelle loi sur l'énergie solaire (FR")
								)
								.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
										.withLanguage(LanguageType.IT)
										.withBallotDescriptionShort("Nouvelle loi ES (IT)")
								)
						)
						.withStandardBallot(new ch.ech.xmlns.ech_0155._3.BallotType.StandardBallot()
								.withQuestionIdentification("QUE-000-43532532")
								.withAnswerType(new AnswerInformationType()
										.withAnswerType(BigInteger.valueOf(2))
								)
								.withBallotQuestion(new BallotQuestionType()
										.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
												.withLanguage(LanguageType.DE)
												.withBallotQuestionTitle("Votre avis sur la loi ES (DE)")
												.withBallotQuestion("Acceptez-vous la loi ES ? (DE)")
										)
										.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
												.withLanguage(LanguageType.IT)
												.withBallotQuestionTitle("Votre avis sur la loi ES (IT)")
												.withBallotQuestion("Acceptez-vous la loi ES ? (IT)")
										)
								)
						)

				)
				.withBallot(new ch.ech.xmlns.ech_0155._3.BallotType()
						.withBallotIdentification("BAL-000-A325156")
						.withBallotPosition(BigInteger.valueOf(2))
						.withBallotDescription(new BallotDescriptionInformationType()
								.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
										.withLanguage(LanguageType.DE)
										.withBallotDescriptionLong("Variantes sur l'énergie solaire (DE)")
										.withBallotDescriptionShort("Variantes ES (DE)")
								)
								.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
										.withLanguage(LanguageType.FR)
										.withBallotDescriptionLong("Variantes sur l'énergie solaire (FR")
								)
								.withBallotDescriptionInfo(new BallotDescriptionInformationType.BallotDescriptionInfo()
										.withLanguage(LanguageType.IT)
										.withBallotDescriptionShort("Variantes ES (IT)")
								)
						)
						.withVariantBallot(new ch.ech.xmlns.ech_0155._3.BallotType.VariantBallot()
								.withQuestionInformation(new QuestionInformationType()
										.withQuestionIdentification("QUE-000-43511932")
										.withQuestionPosition(BigInteger.valueOf(1))
										.withAnswerType(new AnswerInformationType()
												.withAnswerType(BigInteger.valueOf(2))
										)
										.withBallotQuestion(new BallotQuestionType()
												.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
														.withLanguage(LanguageType.DE)
														.withBallotQuestionTitle("Votre avis sur la loi ES variante 1 (DE)")
														.withBallotQuestion("Acceptez-vous la loi ES  variante 1 ? (DE)")
												)
												.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
														.withLanguage(LanguageType.FR)
														.withBallotQuestionTitle("Votre avis sur la loi ES variante 1 (FR)")
														.withBallotQuestion("Acceptez-vous la loi ES  variante 1 ? (FR)")
												)
										)
								)
								.withQuestionInformation(new QuestionInformationType()
										.withQuestionIdentification("QUE-000-41511939")
										.withQuestionPosition(BigInteger.valueOf(2))
										.withAnswerType(new AnswerInformationType()
												.withAnswerType(BigInteger.valueOf(1))
										)
										.withBallotQuestion(new BallotQuestionType()
												.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
														.withLanguage(LanguageType.DE)
														.withBallotQuestionTitle("Votre avis sur la loi ES variante 2 (DE)")
														.withBallotQuestion("Acceptez-vous la loi ES  variante 2 ? (DE)")
												)
												.withBallotQuestionInfo(new BallotQuestionType.BallotQuestionInfo()
														.withLanguage(LanguageType.FR)
														.withBallotQuestionTitle("Votre avis sur la loi ES variante 2 (FR)")
														.withBallotQuestion("Acceptez-vous la loi ES  variante 2 ? (FR)")
												)
										)
								)
								.withTieBreakInformation(new TieBreakInformationType()
										.withQuestionIdentification("QUE-000-41500009")
										.withQuestionPosition(BigInteger.valueOf(3))
										.withTieBreakQuestion(new ch.ech.xmlns.ech_0155._3.TieBreakQuestionType()
												.withTieBreakQuestionInfo(new ch.ech.xmlns.ech_0155._3.TieBreakQuestionType.TieBreakQuestionInfo()
														.withLanguage(LanguageType.DE)
														.withTieBreakQuestionTitle("En cas de rejet des 2 variantes (DE)")
														.withTieBreakQuestion("Laquelle choisissez-vous ? (DE)")
												)
												.withTieBreakQuestionInfo(new ch.ech.xmlns.ech_0155._3.TieBreakQuestionType.TieBreakQuestionInfo()
														.withLanguage(LanguageType.FR)
														.withTieBreakQuestionTitle("En cas de rejet des 2 variantes (FR)")
														.withTieBreakQuestion("Laquelle choisissez-vous ? (FR)")
												)
										)
								)
								.withTieBreakInformation(new TieBreakInformationType()
										.withAnswerType(new AnswerInformationType()
												.withAnswerType(BigInteger.valueOf(1L)))
										.withQuestionIdentification("QUE-000-41500010")
										.withQuestionPosition(BigInteger.valueOf(4))
										.withTieBreakQuestion(new ch.ech.xmlns.ech_0155._3.TieBreakQuestionType()
												.withTieBreakQuestionInfo(new ch.ech.xmlns.ech_0155._3.TieBreakQuestionType.TieBreakQuestionInfo()
														.withLanguage(LanguageType.DE)
														.withTieBreakQuestionTitle("Et finalement (DE)")
														.withTieBreakQuestion("En cas de doute ? (DE)")
												)
										)
								)
						)
				);
		return echVote;
	}

	private ch.ech.xmlns.ech_0157._3.EventInitialDelivery.ElectionInformation createEchElection() throws Exception {
		final ch.ech.xmlns.ech_0157._3.EventInitialDelivery.ElectionInformation echElection = new ch.ech.xmlns.ech_0157._3.EventInitialDelivery.ElectionInformation();
		echElection
				.withElection(new ElectionType()
						.withElectionIdentification("elec-1")
						.withDomainOfInfluence("doi-1")
						.withTypeOfElection(BigInteger.valueOf(1))
						.withElectionDescription(new ElectionDescriptionInformationType()
								.withElectionDescriptionInfo(new ElectionDescriptionInformationType.ElectionDescriptionInfo()
										.withLanguage(LanguageType.DE)
										.withElectionDescription("desc DE"))
								.withElectionDescriptionInfo(new ElectionDescriptionInformationType.ElectionDescriptionInfo()
										.withLanguage(LanguageType.FR)
										.withElectionDescription("desc FR")
										.withElectionDescriptionShort("desc FR short")))
						.withNumberOfMandates(BigInteger.valueOf(16))
						//                      writeIns : built in contestBuilder
						//                      candidateAccumulation : built in contestBuilder
						.withReferencedElection(new ReferencedElectionInformationType()
								.withReferencedElection("ref1")
								.withElectionRelation(BigInteger.valueOf(1)))
						.withReferencedElection(new ReferencedElectionInformationType()
								.withReferencedElection("ref2")
								.withElectionRelation(BigInteger.valueOf(2))))
				.withCandidate(new CandidateType()
								.withCandidateIdentification("candidate1")
								.withMrMrs("1")
								.withTitle("22")
								.withFamilyName("Dupond")
								.withFirstName("Pierre")
								.withCandidateText(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.DE)
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
												.withLanguage(LanguageType.DE)
												.withOccupationalTitle("Occupation candidate 1"))
										.withOccupationalTitleInfo(new OccupationalTitleInformationType.OccupationalTitleInfo()
												.withLanguage(LanguageType.IT)
												.withOccupationalTitle("Occupation candidate 1 IT")))
						//                      position : built in contestBuilder
						//                      referenceOnPosition : built in contestBuilder
				)
				.withList(new ListType()
						.withListIdentification("list1-id")
						.withListIndentureNumber("1a")
						.withListDescription(new ListDescriptionInformationType()
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage(LanguageType.DE)
										.withListDescriptionShort("list1 short desc")
										.withListDescription("list1 desc"))
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage(LanguageType.RM)
										.withListDescriptionShort("list1 short desc RM")
										.withListDescription("list1 desc RM")))
						.withListOrderOfPrecedence(BigInteger.valueOf(1L))
						.withCandidatePosition(new CandidatePositionInformationType()
								//                              candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.valueOf(1L))
								.withCandidateReferenceOnPosition("01.01")
								.withCandidateIdentification("candidate1-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.DE)
												.withCandidateText("Candidate 1 list 1 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.IT)
												.withCandidateText("Candidate 1 list 1 text IT"))))
						.withCandidatePosition(new CandidatePositionInformationType()
								//                               candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.valueOf(2L))
								.withCandidateReferenceOnPosition("01.02")
								.withCandidateIdentification("candidate2-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.DE)
												.withCandidateText("Candidate 2 list 1 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.IT)
												.withCandidateText("Candidate 2 list 1 text IT")))))
				.withList(new ListType()
						.withListIdentification("list2-id")
						.withListIndentureNumber("2a")
						.withListDescription(new ListDescriptionInformationType()
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage(LanguageType.DE)
										.withListDescriptionShort("list2 short desc")
										.withListDescription("list2 desc"))
								.withListDescriptionInfo(new ListDescriptionInformationType.ListDescriptionInfo()
										.withLanguage(LanguageType.RM)
										.withListDescriptionShort("list2 short desc RM")
										.withListDescription("list2 desc RM")))
						.withListOrderOfPrecedence(BigInteger.valueOf(2L))
						.withCandidatePosition(new CandidatePositionInformationType()
								//                                      candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.valueOf(2L))
								.withCandidateReferenceOnPosition("02.02")
								.withCandidateIdentification("candidate2-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.DE)
												.withCandidateText("Candidate 2 list 2 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.IT)
												.withCandidateText("Candidate 2 list 2 text IT"))))
						.withCandidatePosition(new CandidatePositionInformationType()
								//                                      candidateListIdentification : built in contestBuilder
								.withPositionOnList(BigInteger.valueOf(1L))
								.withCandidateReferenceOnPosition("02.01")
								.withCandidateIdentification("candidate1-id")
								.withCandidateTextOnPosition(new CandidateTextInformationType()
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.DE)
												.withCandidateText("Candidate 1 list 2 text"))
										.withCandidateTextInfo(new CandidateTextInformationType.CandidateTextInfo()
												.withLanguage(LanguageType.IT)
												.withCandidateText("Candidate 1 list 2 text IT")))))
				.withListUnion(new ListUnionType()
						.withListUnionIdentification("LLL-aaa-1"))
				.withListUnion(new ListUnionType()
						.withListUnionIdentification("LLL-aaa-2"));
		return echElection;
	}

	@Test
	void testElection() throws Exception {
		final ch.ech.xmlns.ech_0157._3.EventInitialDelivery.ElectionInformation echElection = createEchElection();
		final ElectionGroupBallotType electionGroup = mapper.convert(echElection);
		final ElectionInformationType election = electionGroup.getElectionInformation().get(0);
		assertEquals("elec-1", election.getElection().getElectionIdentification());
		assertEquals("doi-1", electionGroup.getDomainOfInfluence());
		assertEquals(1L, election.getElection().getTypeOfElection().longValue());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getElection().getElectionDescription().getElectionDescriptionInfo().get(0).getLanguage());
		assertEquals("desc DE", election.getElection().getElectionDescription().getElectionDescriptionInfo().get(0).getElectionDescription());
		assertNull(election.getElection().getElectionDescription().getElectionDescriptionInfo().get(0).getElectionDescriptionShort());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.FR,
				election.getElection().getElectionDescription().getElectionDescriptionInfo().get(1).getLanguage());
		assertEquals("desc FR", election.getElection().getElectionDescription().getElectionDescriptionInfo().get(1).getElectionDescription());
		assertEquals("desc FR short",
				election.getElection().getElectionDescription().getElectionDescriptionInfo().get(1).getElectionDescriptionShort());
		assertEquals("ref1", election.getElection().getReferencedElection().get(0).getReferencedElection());
		assertEquals(1L, election.getElection().getReferencedElection().get(0).getElectionRelation().longValue());
		assertEquals("ref2", election.getElection().getReferencedElection().get(1).getReferencedElection());
		assertEquals(2L, election.getElection().getReferencedElection().get(1).getElectionRelation().longValue());
		assertEquals("candidate1", election.getCandidate().get(0).getCandidateIdentification());
		assertEquals("1", election.getCandidate().get(0).getMrMrs());
		assertEquals("22", election.getCandidate().get(0).getTitle());
		assertEquals("Dupond", election.getCandidate().get(0).getFamilyName());
		assertEquals("Pierre", election.getCandidate().get(0).getFirstName());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getCandidate().get(0).getCandidateText().getCandidateTextInfo().get(0).getLanguage());
		assertEquals("Candidate 1 text", election.getCandidate().get(0).getCandidateText().getCandidateTextInfo().get(0).getCandidateText());
		assertEquals(DateUtil.parseToXMLGregorianCalendar("1999-04-29 00:00:00"), election.getCandidate().get(0).getDateOfBirth());
		assertEquals("1", election.getCandidate().get(0).getSex());
		assertFalse(election.getCandidate().get(0).getIncumbent().isIncumbent());
		assertNull(election.getCandidate().get(0).getIncumbent().getIncumbentText());
		assertEquals("Rue de la Gare", election.getCandidate().get(0).getDwellingAddress().getStreet());
		assertEquals("12 bis", election.getCandidate().get(0).getDwellingAddress().getHouseNumber());
		assertEquals((Long) 1700L, election.getCandidate().get(0).getDwellingAddress().getSwissZipCode());
		assertEquals("Fribourg", election.getCandidate().get(0).getDwellingAddress().getTown());
		assertEquals("Vuadens", election.getCandidate().get(0).getSwiss().getOrigin().get(0));
		assertEquals("Treyvaux", election.getCandidate().get(0).getSwiss().getOrigin().get(1));
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getCandidate().get(0).getOccupationalTitle().getOccupationalTitleInfo().get(0).getLanguage());
		assertEquals("Occupation candidate 1",
				election.getCandidate().get(0).getOccupationalTitle().getOccupationalTitleInfo().get(0).getOccupationalTitle());
		assertEquals("list1-id", election.getList().get(0).getListIdentification());
		assertEquals("1a", election.getList().get(0).getListIndentureNumber());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getList().get(0).getListDescription().getListDescriptionInfo().get(0).getLanguage());
		assertEquals("list1 desc", election.getList().get(0).getListDescription().getListDescriptionInfo().get(0).getListDescription());
		assertEquals("list1 short desc", election.getList().get(0).getListDescription().getListDescriptionInfo().get(0).getListDescriptionShort());
		assertEquals(1, election.getList().get(0).getListOrderOfPrecedence());
		assertNull(election.getList().get(0).getCandidatePosition().get(0).getCandidateListIdentification());
		assertEquals(1, election.getList().get(0).getCandidatePosition().get(0).getPositionOnList());
		assertEquals("01.01", election.getList().get(0).getCandidatePosition().get(0).getCandidateReferenceOnPosition());
		assertEquals("candidate1-id", election.getList().get(0).getCandidatePosition().get(0).getCandidateIdentification());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getList().get(0).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(0).getLanguage());
		assertEquals("Candidate 1 list 1 text",
				election.getList().get(0).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(0)
						.getCandidateText());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.IT,
				election.getList().get(0).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(1).getLanguage());
		assertEquals("Candidate 1 list 1 text IT",
				election.getList().get(0).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(1)
						.getCandidateText());
		assertNull(election.getList().get(0).getCandidatePosition().get(1).getCandidateListIdentification());
		assertEquals(2, election.getList().get(0).getCandidatePosition().get(1).getPositionOnList());
		assertEquals("01.02", election.getList().get(0).getCandidatePosition().get(1).getCandidateReferenceOnPosition());
		assertEquals("candidate2-id", election.getList().get(0).getCandidatePosition().get(1).getCandidateIdentification());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getList().get(0).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(0).getLanguage());
		assertEquals("Candidate 2 list 1 text",
				election.getList().get(0).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(0)
						.getCandidateText());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.IT,
				election.getList().get(0).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(1).getLanguage());
		assertEquals("Candidate 2 list 1 text IT",
				election.getList().get(0).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(1)
						.getCandidateText());
		assertEquals("list2-id", election.getList().get(1).getListIdentification());
		assertEquals("2a", election.getList().get(1).getListIndentureNumber());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getList().get(1).getListDescription().getListDescriptionInfo().get(0).getLanguage());
		assertEquals("list2 desc", election.getList().get(1).getListDescription().getListDescriptionInfo().get(0).getListDescription());
		assertEquals("list2 short desc", election.getList().get(1).getListDescription().getListDescriptionInfo().get(0).getListDescriptionShort());
		assertEquals(2, election.getList().get(1).getListOrderOfPrecedence());
		assertNull(election.getList().get(1).getCandidatePosition().get(0).getCandidateListIdentification());
		assertEquals(2, election.getList().get(1).getCandidatePosition().get(0).getPositionOnList());
		assertEquals("02.02", election.getList().get(1).getCandidatePosition().get(0).getCandidateReferenceOnPosition());
		assertEquals("candidate2-id", election.getList().get(1).getCandidatePosition().get(0).getCandidateIdentification());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getList().get(1).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(0).getLanguage());
		assertEquals("Candidate 2 list 2 text",
				election.getList().get(1).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(0)
						.getCandidateText());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.IT,
				election.getList().get(1).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(1).getLanguage());
		assertEquals("Candidate 2 list 2 text IT",
				election.getList().get(1).getCandidatePosition().get(0).getCandidateTextOnPosition().getCandidateTextInfo().get(1)
						.getCandidateText());
		assertNull(election.getList().get(1).getCandidatePosition().get(1).getCandidateListIdentification());
		assertEquals(1, election.getList().get(1).getCandidatePosition().get(1).getPositionOnList());
		assertEquals("02.01", election.getList().get(1).getCandidatePosition().get(1).getCandidateReferenceOnPosition());
		assertEquals("candidate1-id", election.getList().get(1).getCandidatePosition().get(1).getCandidateIdentification());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.DE,
				election.getList().get(1).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(0).getLanguage());
		assertEquals("Candidate 1 list 2 text",
				election.getList().get(1).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(0)
						.getCandidateText());
		assertEquals(ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType.IT,
				election.getList().get(1).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(1).getLanguage());
		assertEquals("Candidate 1 list 2 text IT",
				election.getList().get(1).getCandidatePosition().get(1).getCandidateTextOnPosition().getCandidateTextInfo().get(1)
						.getCandidateText());
		assertEquals("LLL-aaa-1", election.getListUnion().get(0).getListUnionIdentification());
		assertEquals("LLL-aaa-2", election.getListUnion().get(1).getListUnionIdentification());
	}

	@Test
	void testContest() throws Exception {
		final ch.ech.xmlns.ech_0155._3.ContestType echContest = createEchContest();
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType contest = mapper.convert(echContest);
		final Set<String> echDescriptions = echContest.getContestDescription().getContestDescriptionInfo()
				.stream()
				.map(d -> d.getLanguage().value() + "-" + d.getContestDescription())
				.collect(Collectors.toSet());
		final Set<String> descriptions = contest.getContestDescription().getContestDescriptionInfo()
				.stream()
				.map(d -> d.getLanguage().value() + "-" + d.getContestDescription())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
		assertEquals("A1-B2-1111111111111-aaa", contest.getContestIdentification());
		assertEquals("1999-03-27", contest.getContestDate().toXMLFormat());
	}

	@Test
	void testVote() throws Exception {
		final EventInitialDelivery.VoteInformation echVote = createEchVote();
		final VoteInformationType vote = mapper.convert(echVote);
		assertEquals("1234-aaaa-nnnn-0000", vote.getVote().getVoteIdentification());
		Set<String> echDescriptions = echVote.getVote().getVoteDescription().getVoteDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getVoteDescription())
				.collect(Collectors.toSet());
		Set<String> descriptions = vote.getVote().getVoteDescription().getVoteDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getVoteDescription())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);

		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType ballot1 = vote.getVote().getBallot().stream()
				.filter(b -> b.getBallotPosition() == 1).findFirst().get();
		final ch.ech.xmlns.ech_0155._3.BallotType echBallot1 = echVote.getBallot().stream().filter(b -> b.getBallotPosition().intValue() == 1)
				.findFirst()
				.get();
		assertEquals("BAL-000-432556", ballot1.getBallotIdentification());
		echDescriptions = echBallot1.getBallotDescription().getBallotDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotDescriptionLong() + "-" + v.getBallotDescriptionShort())
				.collect(Collectors.toSet());
		descriptions = ballot1.getBallotDescription().getBallotDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotDescriptionLong() + "-" + v.getBallotDescriptionShort())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
		assertEquals("QUE-000-43532532", ballot1.getStandardBallot().getQuestionIdentification());
		assertEquals(BigInteger.TWO, ballot1.getStandardBallot().getAnswerType());
		echDescriptions = echBallot1.getStandardBallot().getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		descriptions = ballot1.getStandardBallot().getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);

		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType ballot2 = vote.getVote().getBallot().stream()
				.filter(b -> b.getBallotPosition() == 2).findFirst().get();
		final ch.ech.xmlns.ech_0155._3.BallotType echBallot2 = echVote.getBallot().stream().filter(b -> b.getBallotPosition().intValue() == 2)
				.findFirst()
				.get();
		assertEquals("BAL-000-A325156", ballot2.getBallotIdentification());
		echDescriptions = echBallot2.getBallotDescription().getBallotDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotDescriptionLong() + "-" + v.getBallotDescriptionShort())
				.collect(Collectors.toSet());
		descriptions = ballot2.getBallotDescription().getBallotDescriptionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotDescriptionLong() + "-" + v.getBallotDescriptionShort())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);

		final StandardQuestionType variantQuestion1 = ballot2.getVariantBallot().getStandardQuestion().stream()
				.filter(v -> v.getQuestionPosition() == 1).findFirst().get();
		final QuestionInformationType echVariantQuestion1 = echBallot2.getVariantBallot().getQuestionInformation().stream()
				.filter(v -> v.getQuestionPosition().intValue() == 1).findFirst().get();
		assertEquals("QUE-000-43511932", variantQuestion1.getQuestionIdentification());
		assertEquals(1, variantQuestion1.getQuestionPosition());
		assertNull(variantQuestion1.getQuestionNumber());
		assertEquals(BigInteger.TWO, variantQuestion1.getAnswerType());
		echDescriptions = variantQuestion1.getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		descriptions = echVariantQuestion1.getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);

		final StandardQuestionType variantQuestion2 = ballot2.getVariantBallot().getStandardQuestion().stream()
				.filter(v -> v.getQuestionPosition() == 2).findFirst().get();
		final QuestionInformationType echVariantQuestion2 = echBallot2.getVariantBallot().getQuestionInformation().stream()
				.filter(v -> v.getQuestionPosition().intValue() == 2).findFirst().get();
		assertEquals("QUE-000-41511939", variantQuestion2.getQuestionIdentification());
		assertEquals(2, variantQuestion2.getQuestionPosition());
		assertNull(variantQuestion2.getQuestionNumber());
		assertEquals(BigInteger.ONE, variantQuestion2.getAnswerType());
		echDescriptions = variantQuestion2.getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		descriptions = echVariantQuestion2.getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);

		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType tiebreakQuestion1 = ballot2.getVariantBallot()
				.getTieBreakQuestion()
				.stream().filter(v -> v.getQuestionPosition() == 3).findFirst().get();
		final TieBreakInformationType echTiebreakQuestion1 = echBallot2.getVariantBallot().getTieBreakInformation().stream()
				.filter(v -> v.getQuestionPosition().intValue() == 3).findFirst().get();
		assertNull(tiebreakQuestion1.getAnswerType());
		assertEquals("QUE-000-41500009", tiebreakQuestion1.getQuestionIdentification());
		assertEquals(3, tiebreakQuestion1.getQuestionPosition());
		assertNull(tiebreakQuestion1.getQuestionNumber());
		echDescriptions = tiebreakQuestion1.getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		descriptions = echTiebreakQuestion1.getTieBreakQuestion().getTieBreakQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getTieBreakQuestionTitle() + "-" + v.getTieBreakQuestion())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);

		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType tiebreakQuestion2 = ballot2.getVariantBallot()
				.getTieBreakQuestion()
				.stream().filter(v -> v.getQuestionPosition() == 4).findFirst().get();
		final TieBreakInformationType echTiebreakQuestion2 = echBallot2.getVariantBallot().getTieBreakInformation().stream()
				.filter(v -> v.getQuestionPosition().intValue() == 4).findFirst().get();
		assertEquals(BigInteger.ONE, tiebreakQuestion2.getAnswerType());
		assertEquals("QUE-000-41500010", tiebreakQuestion2.getQuestionIdentification());
		assertEquals(4, tiebreakQuestion2.getQuestionPosition());
		assertNull(tiebreakQuestion2.getQuestionNumber());
		echDescriptions = tiebreakQuestion2.getBallotQuestion().getBallotQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getBallotQuestionTitle() + "-" + v.getBallotQuestion())
				.collect(Collectors.toSet());
		descriptions = echTiebreakQuestion2.getTieBreakQuestion().getTieBreakQuestionInfo()
				.stream()
				.map(v -> v.getLanguage().value() + "-" + v.getTieBreakQuestionTitle() + "-" + v.getTieBreakQuestion())
				.collect(Collectors.toSet());
		assertEquals(echDescriptions, descriptions);
	}

	@Test
	void testIncumbentIsTrue() throws Exception {

		final ContestType contestParameters = new ContestType();

		final ch.ech.xmlns.ech_0157._3.EventInitialDelivery.ElectionInformation echElection = createEchElection();
		echElection.getCandidate().get(0).withIncumbentYesNo(true);
		final ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType election = mapper.convert(echElection)
				.getElectionInformation()
				.get(0);

		assertTrue(election.getCandidate().get(0).getIncumbent().isIncumbent());
		assertEquals(election.getCandidate().get(0).getIncumbent().getIncumbentText(),
				ParameterMapper.INSTANCE.map(contestParameters.getIncumbentText()));
	}
}