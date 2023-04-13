/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static com.google.common.base.Preconditions.checkState;
import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.swisspush.supermachine.BeanScanner.from;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slf4j.LoggerFactory;

import ch.evoting.xmlns.parameter._4.AnswerInformationType;
import ch.evoting.xmlns.parameter._4.AnswerTranslationType;
import ch.evoting.xmlns.parameter._4.DefaultAnswerTranslationType;
import ch.evoting.xmlns.parameter._4.ElectionsType;
import ch.evoting.xmlns.parameter._4.StandardAnswerType;
import ch.evoting.xmlns.parameter._4.TiebreakAnswerType;
import ch.post.it.evoting.dataintegrationservice.common.DateUtil;
import ch.post.it.evoting.dataintegrationservice.common.XmlTransformer;
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
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.IncumbentType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ObjectFactory;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionTypeWithReferences;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteType;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import pipes.ContestPhase;
import pipes.ParameterPhase;
import pipes.contest.ech015xv3.ContestEch015xv3;
import pipes.contest.ech015xv4.ContestEch015xv4;
import reactor.core.publisher.Flux;

public class ContestPhaseSteps extends PhaseSteps {
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ContestPhaseSteps.class);
	private static final String DEFAULT_PARAMS = "my params";
	private static final String DEFAULT_CONTEST = "my contest";
	private static final String DEFAULT_VOTES = "my votes";
	private static final String DEFAULT_ELECTIONS = "my elections";
	private final MemoryAppender memoryAppender;
	private final Map<String, ContestType> contestMap = new HashMap<>();
	private final Map<String, ch.evoting.xmlns.parameter._4.ContestType> paramsMap = new HashMap<>();
	private final Map<String, List<ElectionInformationType>> electionsMap = new HashMap<>();
	private final Map<String, ElectionInformationType> electionMap = new HashMap<>();
	private final Map<String, List<VoteInformationType>> votesMap = new HashMap<>();
	private final Map<String, VoteInformationType> voteMap = new HashMap<>();
	private final Map<String, CandidateType> candidateMap = new HashMap<>();
	private final Map<String, ListType> listMap = new HashMap<>();
	private final Map<String, CandidatePositionType> candidatePositionMap = new HashMap<>();
	private final Map<String, BallotType> ballotMap = new HashMap<>();
	private final Map<String, StandardQuestionType> standardQuestionMap = new HashMap<>();
	private final Map<String, TieBreakQuestionTypeWithReferences> tiebreakQuestionMap = new HashMap<>();
	private final Map<String, AnswerTranslationType.Question> pQuestionMap = new HashMap<>();
	private final Map<String, DefaultAnswerTranslationType.AnswerType> pAnswerTypeMap = new HashMap<>();
	private final Map<String, StandardAnswerType> pStandardAnswerMap = new HashMap<>();
	private final Map<String, TiebreakAnswerType> pTiebreakAnswerMap = new HashMap<>();
	private final Map<String, ch.evoting.xmlns.parameter._4.ElectionType> pElectionMap = new HashMap<>();
	private final Map<String, ListUnionType> listUnionMap = new HashMap<>();
	private ContestType contest;
	private RuntimeException processException = null;

	public ContestPhaseSteps() {
		final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ch.post.it.evoting");
		logger.setLevel(Level.WARN);
		logger.setAdditive(true);
		memoryAppender = new MemoryAppender();
		memoryAppender.setContext(logger.getLoggerContext());
		memoryAppender.start();
		logger.addAppender(memoryAppender);
	}

	@Given("^a contest from eCHv3 with Id \"([^\"]*)\"$")
	public void given(final String contestId) {
		ContestEch015xv3.exports.setContests(Flux.just(new ContestType().withContestIdentification(contestId).withContestDescription(
				new ContestDescriptionInformationType().withContestDescriptionInfo(
						new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.DE)
								.withContestDescription("Desc DE")).withContestDescriptionInfo(
						new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.FR)
								.withContestDescription("Desc FR")).withContestDescriptionInfo(
						new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.IT)
								.withContestDescription("Desc IT")).withContestDescriptionInfo(
						new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.RM)
								.withContestDescription("Desc RM")))));
	}

	@And("^an election input eCHv3 with an Id \"([^\"]*)\"$")
	public void andElectionId(final String electionId) {
		ContestEch015xv3.exports.setElections(
				Flux.just(new ElectionInformationType().withElection(new ElectionType().withElectionIdentification(electionId)
						.withElectionDescription(new ElectionDescriptionInformationType().withElectionDescriptionInfo(
								new ElectionDescriptionInformationType.ElectionDescriptionInfo().withLanguage(LanguageType.DE)
										.withElectionDescription("Desc DE")).withElectionDescriptionInfo(
								new ElectionDescriptionInformationType.ElectionDescriptionInfo().withLanguage(LanguageType.FR)
										.withElectionDescription("Desc FR")).withElectionDescriptionInfo(
								new ElectionDescriptionInformationType.ElectionDescriptionInfo().withLanguage(LanguageType.IT)
										.withElectionDescription("Desc IT")).withElectionDescriptionInfo(
								new ElectionDescriptionInformationType.ElectionDescriptionInfo().withLanguage(LanguageType.RM)
										.withElectionDescription("Desc RM"))))));
	}

	@And("^a votation input eCHv3 with an Id \"([^\"]*)\"$")
	public void andVoteId(final String voteId) {
		ContestEch015xv3.exports.setVotes(Flux.just(new VoteInformationType().withVote(new VoteType().withVoteIdentification(voteId)
				.withVoteDescription(new VoteDescriptionInformationType().withVoteDescriptionInfo(
								new VoteDescriptionInformationType.VoteDescriptionInfo().withLanguage(LanguageType.DE).withVoteDescription("Desc DE"))
						.withVoteDescriptionInfo(
								new VoteDescriptionInformationType.VoteDescriptionInfo().withLanguage(LanguageType.FR).withVoteDescription("Desc FR"))
						.withVoteDescriptionInfo(
								new VoteDescriptionInformationType.VoteDescriptionInfo().withLanguage(LanguageType.IT).withVoteDescription("Desc IT"))
						.withVoteDescriptionInfo(new VoteDescriptionInformationType.VoteDescriptionInfo().withLanguage(LanguageType.RM)
								.withVoteDescription("Desc RM"))))));
	}

	@And("^a user parameter input$")
	public void andUserParameterInput() {
		ParameterPhase.exports.setContestParameters(Flux.just(
				new ch.evoting.xmlns.parameter._4.ContestType().withDefaultAnswerTranslations(new DefaultAnswerTranslationType())));

		ParameterPhase.exports.setAfterReadContestPlugins(Flux.just(Collections.emptyList()));
	}

	@When("^executing the contest phase pipeline$")
	public void when() {
		new Runtime().run(uri("contestPhase"));
	}

	@And("^draining the contest phase outputs$")
	public void andDraining() {
		drain(ContestPhase.exports.getContest().doOnNext(c -> contest = c));
	}

	@Then("^it outputs a contest with Id \"([^\"]*)\"$")
	public void then(final String contestId) {
		assertEquals(contest.getContestIdentification(), contestId);
	}

	@And("^it outputs a contest with electionId \"([^\"]*)\"$")
	public void andItOuputsAContestWithElectionId(final String electionId) {
		assertTrue(contest.getElectionInformation().stream().anyMatch(e -> e.getElection().getElectionIdentification().equalsIgnoreCase(electionId)));
	}

	@And("^it outputs a contest with votationId \"([^\"]*)\"$")
	public void andItOuputsAContestWithVotationId(final String voteId) {
		assertTrue(contest.getVoteInformation().stream().anyMatch(v -> v.getVote().getVoteIdentification().equals(voteId)));
	}

	//background
	//
	//
	//
	@Given("^user parameters named \"([^\"]*)\"$")
	public void givenUserParametersNamed(final String name) {
		paramsMap.put(name,
				new ch.evoting.xmlns.parameter._4.ContestType().withAnswerTranslations(new AnswerTranslationType().withQuestion(new ArrayList<>()))
						.withDefaultAnswerTranslations(new DefaultAnswerTranslationType()));
	}

	@And("^user parameters \"([^\"]*)\" has eVoting date from \"([^\"]*)\"$")
	public void andUserParametersHasEvotingDateFrom(final String name, final String dt) {
		paramsMap.get(name).setEvotingFromDate(DateUtil.parseToXMLGregorianCalendar(dt));
	}

	@And("^user parameters \"([^\"]*)\" has eVoting date to \"([^\"]*)\"$")
	public void andUserParametersHasEvotingDateTo(final String name, final String dt) {
		paramsMap.get(name).setEvotingToDate(DateUtil.parseToXMLGregorianCalendar(dt));
	}

	@And("^user parameters \"([^\"]*)\" has contest default language \"([^\"]*)\"$")
	public void andUserParametersHasContestDefaultLanguage(final String name, final String lang) {
		paramsMap.get(name).setDefaultLanguage(ch.evoting.xmlns.parameter._4.LanguageType.valueOf(lang));
	}

	@And("^user parameters \"([^\"]*)\" has election named \"([^\"]*)\"$")
	public void andUserParametersHasElectionNamed(final String name, final String electionName) {
		final ch.evoting.xmlns.parameter._4.ElectionType election = new ch.evoting.xmlns.parameter._4.ElectionType();
		pElectionMap.put(electionName, election);
		if (paramsMap.get(name).getElections() == null) {
			paramsMap.get(name).withElections(new ElectionsType().withElection(new ArrayList<>()));
		}
		paramsMap.get(name).getElections().getElection().add(election);
	}

	@And("^election \"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andElectionHasId(final String name, final String electionName, final String id) {
		pElectionMap.get(electionName).setElectionIdentification(id);
	}

	@And("^election \"([^\"]*)\".\"([^\"]*)\" has writeIns \"([^\"]*)\"$")
	public void andElectionHasWriteIns(final String name, final String electionName, final String writeIns) {
		pElectionMap.get(electionName).setWriteInsAllowed(Boolean.valueOf(writeIns));
	}

	@And("^election \"([^\"]*)\".\"([^\"]*)\" has accumulation (\\d+)$")
	public void andElectionHasAccumulation(final String name, final String electionName, final BigInteger accum) {
		pElectionMap.get(electionName).setCandidateAccumulation(accum);
	}

	@And("^election \"([^\"]*)\".\"([^\"]*)\" has minimalCandidateSelection (\\d+)$")
	public void andElectionHasMinimalCandidateSelection(final String name, final String electionName, final BigInteger minimalCandidate) {
		pElectionMap.get(electionName).setMinimalCandidateSelectionInList(minimalCandidate);
	}

	@And("^user parameters \"([^\"]*)\" has default answer type named \"([^\"]*)\"$")
	public void andUserParametersHasDefaultAnswerTypeNamed(final String name, final String answerType) {
		final DefaultAnswerTranslationType.AnswerType at = new DefaultAnswerTranslationType.AnswerType();

		paramsMap.get(name).getDefaultAnswerTranslations().getAnswerType().add(at);
		pAnswerTypeMap.put(name + "." + answerType, at);
	}

	@And("^default answer type \"([^\"]*)\".\"([^\"]*)\" has type (\\d+)$")
	public void andDefaultAnswerType(final String name, final String at, final Integer type) {
		pAnswerTypeMap.get(name + "." + at).setAnswerTypeIdentification(BigInteger.valueOf(type));
	}

	@And("^default answer type \"([^\"]*)\".\"([^\"]*)\" has standardAnswer named \"([^\"]*)\"$")
	public void andDefaultAnswer(final String name, final String at, final String answer) {
		final StandardAnswerType answerType = new StandardAnswerType();
		pAnswerTypeMap.get(name + "." + at).getStandardAnswer().add(answerType);
		pStandardAnswerMap.put(name + "." + at + "." + answer, answerType);
	}

	@And("^default answer type \"([^\"]*)\".\"([^\"]*)\" has tiebreakAnswer named \"([^\"]*)\"$")
	public void andDefaultAnswerType(final String name, final String at, final String answer) {
		final TiebreakAnswerType answerType = new TiebreakAnswerType();
		pAnswerTypeMap.get(name + "." + at).getTiebreakAnswer().add(answerType);
		pTiebreakAnswerMap.put(name + "." + at + "." + answer, answerType);
	}

	@And("^standardAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer position (\\d+)$")
	public void andStandardAnswer(final String name, final String at, final String answer, final Integer pos) {
		pStandardAnswerMap.get(name + "." + at + "." + answer).setAnswerPosition(BigInteger.valueOf(pos));
	}

	@And("^tiebreakAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer position (\\d+)$")
	public void andTiebreakAnswer(final String name, final String at, final String answer, final Integer pos) {
		pTiebreakAnswerMap.get(name + "." + at + "." + answer).setAnswerPosition(BigInteger.valueOf(pos));
	}

	@And("^tiebreakAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer questionReference \"([^\"]*)\"$")
	public void andTiebreakAnswer(final String name, final String at, final String answer, final String questionReference) {
		pTiebreakAnswerMap.get(name + "." + at + "." + answer).setStandardQuestionReference(questionReference);
	}

	@And("^standardAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer standardAnswerType = \"([^\"]*)\"$")
	public void andStandardAnswer(final String name, final String at, final String answer, final String answerType) {
		pStandardAnswerMap.get(name + "." + at + "." + answer).setStandardAnswerType(answerType);
	}

	@And("^tiebreakAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" is as blank defined$")
	public void andTiebreakAnswer(final String name, final String at, final String answer) {
		pTiebreakAnswerMap.get(name + "." + at + "." + answer).setStandardQuestionReference(null);
	}

	@And("^standardAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andStandardAnswer(final String name, final String at, final String answer, final String lang, final String info) {
		pStandardAnswerMap.get(name + "." + at + "." + answer).getAnswerInfo()
				.add(new AnswerInformationType().withLanguage(ch.evoting.xmlns.parameter._4.LanguageType.valueOf(lang)).withAnswer(info));
	}

	@And("^tiebreakAnswer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andTiebreakAnswer(final String name, final String at, final String answer, final String lang, final String info) {
		pTiebreakAnswerMap.get(name + "." + at + "." + answer).getAnswerInfo()
				.add(new AnswerInformationType().withLanguage(ch.evoting.xmlns.parameter._4.LanguageType.valueOf(lang)).withAnswer(info));
	}

	@Given("^the descriptions of the answer \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenDescriptionsOfAnswer(final String name, final String at, final String answer) {
		if (pStandardAnswerMap.get(name + "." + at + "." + answer) != null) {
			pStandardAnswerMap.get(name + "." + at + "." + answer).getAnswerInfo().clear();
		}
		if (pTiebreakAnswerMap.get(name + "." + at + "." + answer) != null) {
			pTiebreakAnswerMap.get(name + "." + at + "." + answer).getAnswerInfo().clear();
		}
	}

	@And("^user parameters \"([^\"]*)\" has question \"([^\"]*)\"$")
	public void andUserParametersHasQuestion(final String name, final String question) {
		final AnswerTranslationType.Question q = new AnswerTranslationType.Question();
		paramsMap.get(name).getAnswerTranslations().getQuestion().add(q);
		pQuestionMap.put(name + "." + question, q);
	}

	@And("^parameter question \"([^\"]*)\".\"([^\"]*)\" has questionIdentification \"([^\"]*)\"$")
	public void andParameterQuestionHasIdentification(final String name, final String question, final String id) {
		pQuestionMap.get(name + "." + question).setQuestionIdentification(id);
	}

	@And("^parameter question \"([^\"]*)\".\"([^\"]*)\" has standardAnswer named \"([^\"]*)\"$")
	public void andParameterQuestionHasStandardAnswerNamed(final String name, final String question, final String answer) {
		final StandardAnswerType at = new StandardAnswerType();
		pQuestionMap.get(name + "." + question).getStandardAnswer().add(at);
		pStandardAnswerMap.put(name + "." + question + "." + answer, at);
	}

	@And("^parameter question \"([^\"]*)\".\"([^\"]*)\" has tiebreakAnswer named \"([^\"]*)\"$")
	public void andParameterQuestionHasTiebreakAnswerNamed(final String name, final String question, final String answer) {
		final TiebreakAnswerType at = new TiebreakAnswerType();
		pQuestionMap.get(name + "." + question).getTiebreakAnswer().add(at);
		pTiebreakAnswerMap.put(name + "." + question + "." + answer, at);
	}

	//
	@Given("^a contest named \"([^\"]*)\"$")
	public void givenAContestNamed(final String name) {
		contestMap.put(name, new ContestType());
	}

	@And("^the contest \"([^\"]*)\" has Id=\"([^\"]*)\"$")
	public void andContestHasId(final String name, final String id) {
		contestMap.get(name).setContestIdentification(id);
	}

	@And("^the contest \"([^\"]*)\" has date \"([^\"]*)\"$")
	public void andContestHasDate(final String name, final String dt) {
		contestMap.get(name).setContestDate(DateUtil.parseToXMLGregorianCalendar(dt));
	}

	@And("^the contest \"([^\"]*)\" has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andContestHasDescription(final String name, final String lang, final String desc) {
		ContestDescriptionInformationType descInfo = contestMap.get(name).getContestDescription();
		if (descInfo == null) {
			descInfo = new ContestDescriptionInformationType();
		}
		descInfo.getContestDescriptionInfo()
				.add(new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.valueOf(lang))
						.withContestDescription(desc));
		contestMap.get(name).setContestDescription(descInfo);
	}

	@Given("^a list of elections named \"([^\"]*)\"$")
	public void givenAListOfElectionsNamed(final String elections) {
		electionsMap.put(elections, new ArrayList<>());
	}

	@And("^an election named \"([^\"]*)\" within \"([^\"]*)\"$")
	public void andElectionNamedWithin(final String election, final String elections) {
		final ElectionInformationType e = new ElectionInformationType().withElection(
						new ElectionType().withNumberOfMandates(BigInteger.valueOf(5L)).withTypeOfElection(BigInteger.valueOf(1L)))
				.withCandidate(new ArrayList<>());
		electionMap.put(elections + "." + election, e);
		electionsMap.get(elections).add(e);
	}

	@And("^the election \"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andTheElectionHasId(final String elections, final String election, final String id) {
		electionMap.get(elections + "." + election).getElection().setElectionIdentification(id);
	}

	@And("^the election \"([^\"]*)\".\"([^\"]*)\" has domain of influence \"([^\"]*)\"$")
	public void andElectionHasDOI(final String elections, final String election, final String doi) {
		electionMap.get(elections + "." + election).getElection().setDomainOfInfluence(doi);
	}

	@And("^the election \"([^\"]*)\".\"([^\"]*)\" has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andElectionHasDescription(final String elections, final String election, final String lang, final String desc) {
		ElectionDescriptionInformationType descInfo = electionMap.get(elections + "." + election).getElection().getElectionDescription();
		if (descInfo == null) {
			descInfo = new ElectionDescriptionInformationType();
		}
		descInfo.getElectionDescriptionInfo()
				.add(new ElectionDescriptionInformationType.ElectionDescriptionInfo().withLanguage(LanguageType.valueOf(lang))
						.withElectionDescription(desc));
		electionMap.get(elections + "." + election).getElection().setElectionDescription(descInfo);
	}

	@Given("^a list of votes named \"([^\"]*)\"$")
	public void givenAListOfVotesNamed(final String name) {
		votesMap.put(name, new ArrayList<>());
	}

	@And("^a vote named \"([^\"]*)\" within \"([^\"]*)\"$")
	public void andVoteNamed(final String vote, final String votes) {
		final VoteInformationType v = new VoteInformationType().withVote(
				new VoteType().withVoteDescription(new VoteDescriptionInformationType().withVoteDescriptionInfo(new ArrayList<>()))
						.withBallot(new ArrayList<>()));
		voteMap.put(votes + "." + vote, v);
		votesMap.get(votes).add(v);
	}

	@And("^the vote \"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andVoteHasId(final String votes, final String vote, final String id) {
		voteMap.get(votes + "." + vote).getVote().setVoteIdentification(id);
	}

	@And("^the vote \"([^\"]*)\".\"([^\"]*)\" has domain of influence \"([^\"]*)\"$")
	public void andVoteHasDOI(final String votes, final String vote, final String doi) {
		voteMap.get(votes + "." + vote).getVote().setDomainOfInfluence(doi);

	}

	@And("^the vote \"([^\"]*)\".\"([^\"]*)\" has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andVoteHasDescription(final String votes, final String vote, final String lang, final String desc) {
		VoteDescriptionInformationType descInfo = voteMap.get(votes + "." + vote).getVote().getVoteDescription();
		if (descInfo == null) {
			descInfo = new VoteDescriptionInformationType();
		}
		descInfo.getVoteDescriptionInfo()
				.add(new VoteDescriptionInformationType.VoteDescriptionInfo().withLanguage(LanguageType.valueOf(lang)).withVoteDescription(desc));
		voteMap.get(votes + "." + vote).getVote().setVoteDescription(descInfo);
	}

	@Given("^the vote \"([^\"]*)\".\"([^\"]*)\" has a \"([^\"]*)\" ballot \"([^\"]*)\"$")
	public void givenVoteHasBallot(final String votes, final String vote, final String type, final String name) {
		final BallotType ballot = new BallotType().withBallotPosition(BigInteger.valueOf(1L))
				.withBallotDescription(new BallotDescriptionInformationType().withBallotDescriptionInfo(new ArrayList<>()));
		if (type.equals("standard")) {
			ballot.setStandardBallot(new StandardBallotType().withBallotQuestion(new BallotQuestionType().withBallotQuestionInfo(new ArrayList<>()))
					.withAnswer(new ArrayList<>()));
		} else if (type.equals("variant")) {
			ballot.setVariantBallot(new VariantBallotType().withStandardQuestion(new ArrayList<>()));
		} else {
			throw new IllegalArgumentException("unknown " + type + " ballot type; use standard or variant");
		}
		voteMap.get(votes + "." + vote).getVote().getBallot().add(ballot);
		ballotMap.put(votes + "." + vote + "." + name, ballot);
	}

	@And("^the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andBallotHasId(final String votes, final String vote, final String ballot, final String id) {
		ballotMap.get(votes + "." + vote + "." + ballot).setBallotIdentification(id);
	}

	@And("^the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andBallotHasDescription(final String votes, final String vote, final String ballot, final String lang, final String desc) {
		ballotMap.get(votes + "." + vote + "." + ballot).getBallotDescription().getBallotDescriptionInfo()
				.add(new BallotDescriptionInformationType.BallotDescriptionInfo().withLanguage(LanguageType.valueOf(lang))
						.withBallotDescriptionLong(desc));
	}

	@And("^the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has variant ballot standard question \"([^\"]*)\"$")
	public void andBallotHasVariantBallotStandardQuestion(final String votes, final String vote, final String ballot, final String question) {
		final StandardQuestionType standardQuestion = new StandardQuestionType().withBallotQuestion(
				new BallotQuestionType().withBallotQuestionInfo(new ArrayList<>()));
		final VariantBallotType variantBallot = ballotMap.get(votes + "." + vote + "." + ballot).getVariantBallot();
		variantBallot.getStandardQuestion().add(standardQuestion);
		standardQuestionMap.put(votes + "." + vote + "." + ballot + "." + question, standardQuestion);
	}

	@And("^the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has variant ballot tiebreak question \"([^\"]*)\"$")
	public void andBallotHasVariantBallotTiebreakQuestion(final String votes, final String vote, final String ballot, final String question) {
		final TieBreakQuestionType tq = new TieBreakQuestionTypeWithReferences().withBallotQuestion(
				new BallotQuestionType().withBallotQuestionInfo(new ArrayList<>()));
		final VariantBallotType variantBallot = ballotMap.get(votes + "." + vote + "." + ballot).getVariantBallot();
		variantBallot.getTieBreakQuestion().add(tq);
		tiebreakQuestionMap.put(votes + "." + vote + "." + ballot + "." + question, (TieBreakQuestionTypeWithReferences) tq);
	}

	@And("^the standard question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andStandardQuestionHasId(final String votes, final String vote, final String ballot, final String question, final String id) {
		standardQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setQuestionIdentification(id);
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andTiebreakQuestionHasId(final String votes, final String vote, final String ballot, final String question, final String id) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setQuestionIdentification(id);
	}

	@And("^the standard question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has position (\\d+)$")
	public void andStandardQuestionHasPosition(final String votes, final String vote, final String ballot, final String question, final Integer pos) {
		standardQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setQuestionPosition(BigInteger.valueOf(pos));
	}

	@And("^the standard question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has position cleared out$")
	public void andStandardQuestionHasPositionClearedOut(final String votes, final String vote, final String ballot, final String question) {
		standardQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setQuestionPosition(null);
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has position (\\d+)$")
	public void andTiebreakQuestionHasPosition(final String votes, final String vote, final String ballot, final String question, final Integer pos) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setQuestionPosition(BigInteger.valueOf(pos));
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer type (\\d+)$")
	public void andTiebreakQuestionHasAnswerType(final String votes, final String vote, final String ballot, final String question, final BigInteger type) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setAnswerType(type);
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has referenceQuestion1 \"([^\"]*)\"$")
	public void andTiebreakQuestionHasReferenceQuestion1(final String votes, final String vote, final String ballot, final String question, final String refQ1) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setReferencedQuestion1(refQ1);
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has referenceQuestion2 \"([^\"]*)\"$")
	public void andTiebreakQuestionHasReferenceQuestion2(final String votes, final String vote, final String ballot, final String question, final String refQ2) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setReferencedQuestion2(refQ2);
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has position cleared out$")
	public void andTiebreakQuestionHasPositionClearedOut(final String votes, final String vote, final String ballot, final String question) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setQuestionPosition(null);
	}

	@And("^the standard question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer type (\\d+)$")
	public void andStandardQuestionHasAnswerType(final String votes, final String vote, final String ballot, final String question, final Integer type) {
		standardQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).setAnswerType(BigInteger.valueOf(type));
	}

	@And("^the standard question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has info \"([^\"]*)\" \"([^\"]*)\"$")
	public void andStandardQuestionHasInfo(final String votes, final String vote, final String ballot, final String question, final String lang, final String info) {
		final BallotQuestionType.BallotQuestionInfo questionInfo = new BallotQuestionType.BallotQuestionInfo().withLanguage(LanguageType.valueOf(lang))
				.withBallotQuestion(info);
		standardQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).getBallotQuestion().getBallotQuestionInfo().add(questionInfo);
	}

	@And("^the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has info \"([^\"]*)\" \"([^\"]*)\"$")
	public void andTiebreakQuestionHasInfo(final String votes, final String vote, final String ballot, final String question, final String lang, final String info) {
		final BallotQuestionType.BallotQuestionInfo questionInfo = new BallotQuestionType.BallotQuestionInfo().withLanguage(LanguageType.valueOf(lang))
				.withBallotQuestion(info);
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).getBallotQuestion().getBallotQuestionInfo().add(questionInfo);
	}

	@Given("^the descriptions of the tiebreak question \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void andDescriptionOfTiebreakQuestionAreClearedOut(final String votes, final String vote, final String ballot, final String question) {
		tiebreakQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).getBallotQuestion().getBallotQuestionInfo().clear();
	}

	@And("^the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has question id  \"([^\"]*)\"$")
	public void andBallotHasQuestionId(final String votes, final String vote, final String ballot, final String id) {
		ballotMap.get(votes + "." + vote + "." + ballot).getStandardBallot().setQuestionIdentification(id);
	}

	@And("^the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has answer type (\\d+)$")
	public void andBallotHasAnswerType(final String votes, final String vote, final String ballot, final Integer type) {
		ballotMap.get(votes + "." + vote + "." + ballot).getStandardBallot().setAnswerType(BigInteger.valueOf(type));
	}

	@And("^the ballot question of ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has info \"([^\"]*)\" \"([^\"]*)\"$")
	public void andBallotQuestionOfBallotHasInfo(final String votes, final String vote, final String ballot, final String lang, final String info) {
		ballotMap.get(votes + "." + vote + "." + ballot).getStandardBallot().getBallotQuestion().getBallotQuestionInfo()
				.add(new BallotQuestionType.BallotQuestionInfo().withLanguage(LanguageType.valueOf(lang)).withBallotQuestion(info));
	}

	@Given("^the descriptions of the ballot question of ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenDescriptionsOfBallotQuestion(final String votes, final String vote, final String ballot) {
		ballotMap.get(votes + "." + vote + "." + ballot).getStandardBallot().getBallotQuestion().getBallotQuestionInfo().clear();
	}

	//
	//
	// end background
	//
	@Given("^elections \"([^\"]*)\" are cleared out$")
	public void givenElectionsAreClearedOut(final String name) {
		electionsMap.put(name, new ArrayList<>());
	}

	@Given("^votes \"([^\"]*)\" are cleared out$")
	public void givenVotesAreClearedOut(final String name) {
		votesMap.put(name, new ArrayList<>());
	}

	@And("^the descriptions of the contest \"([^\"]*)\" are cleared out$")
	public void andDescriptionsOfContestAreClearedOut(final String name) {
		contestMap.get(name).getContestDescription().setContestDescriptionInfo(new ArrayList<>());
	}

	@Given("^the descriptions of the vote \"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenDescriptionsOfTheVoteAreClearedOut(final String votes, final String vote) {
		voteMap.get(votes + "." + vote).getVote().getVoteDescription().setVoteDescriptionInfo(new ArrayList<>());
	}

	@Given("^the descriptions of the ballot \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenDescriptionsOfBallotAreClearedOut(final String votes, final String vote, final String ballot) {
		ballotMap.get(votes + "." + vote + "." + ballot).getBallotDescription().setBallotDescriptionInfo(new ArrayList<>());
	}

	@Given("^the descriptions of the standard question  \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenDescriptionsOfStandardQuestionAreClearedOut(final String votes, final String vote, final String ballot, final String question) {
		standardQuestionMap.get(votes + "." + vote + "." + ballot + "." + question).getBallotQuestion().setBallotQuestionInfo(new ArrayList<>());
	}

	@And("^the descriptions of the election \"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void andDescriptionsOfElectionAreClearedOut(final String elections, final String election) {
		electionMap.get(elections + "." + election).getElection().getElectionDescription().setElectionDescriptionInfo(new ArrayList<>());
	}

	@And("^a candidate named \"([^\"]*)\" within \"([^\"]*)\".\"([^\"]*)\"$")
	public void andCandidateNamedWithinElection(final String name, final String elections, final String election) {
		final CandidateType candidate = new CandidateType().withDwellingAddress(
						new DwellingAddressType().withStreet("Rue de la gare").withHouseNumber("4").withSwissZipCode(1700L).withTown("Fribourg"))
				.withIncumbent(new IncumbentType().withIncumbent(true)).withSwiss(new CandidateType.Swiss()).withSex("1")
				.withDateOfBirth(DateUtil.parseToXMLGregorianCalendar("1965-04-29 00:00:00")).withCandidateText(new CandidateTextInformationType())
				.withOccupationalTitle(new OccupationalTitleInformationType());
		electionMap.get(elections + "." + election).getCandidate().add(candidate);
		candidateMap.put(elections + "." + election + "." + name, candidate);
	}

	@And("^candidate \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has text \"([^\"]*)\" \"([^\"]*)\"$")
	public void andCandidateHasText(final String elections, final String election, final String name, final String lang, final String text) {
		candidateMap.get(elections + "." + election + "." + name).getCandidateText().getCandidateTextInfo()
				.add(new CandidateTextInformationType.CandidateTextInfo().withLanguage(LanguageType.valueOf(lang)).withCandidateText(text));
	}

	@And("^candidate \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has occupational title \"([^\"]*)\" \"([^\"]*)\"$")
	public void andCandidateHasOccupationTitle
			(final String elections, final String election, final String name, final String lang, final String title) {
		candidateMap.get(elections + "." + election + "." + name).getOccupationalTitle().getOccupationalTitleInfo()
				.add(new OccupationalTitleInformationType.OccupationalTitleInfo().withLanguage(LanguageType.valueOf(lang))
						.withOccupationalTitle(title));
	}

	@And("^candidate \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andCandidateHasId(final String elections, final String election, final String name, final String id) {
		candidateMap.get(elections + "." + election + "." + name).setCandidateIdentification(id);
	}

	@And("^candidate \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has name \"([^\"]*)\" \"([^\"]*)\"$")
	public void andCandidateHasName(final String elections, final String election, final String cand, final String firstName, final String lastName) {
		candidateMap.get(elections + "." + election + "." + cand).withFirstName(firstName).withFamilyName(lastName)
				.withCallName(lastName + " " + firstName);
	}

	@And("^the text descriptions of the candidate \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void andTextDescriptionfOfCandidateAreClearedOut(final String elections, final String election, final String name) {
		candidateMap.get(elections + "." + election + "." + name).getCandidateText().setCandidateTextInfo(new ArrayList<>());
	}

	@Given("^the title descriptions of the candidate \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenTitleDescriptionsOfCandidateAreClearedOut(final String elections, final String election, final String name) {
		candidateMap.get(elections + "." + election + "." + name).getOccupationalTitle().setOccupationalTitleInfo(new ArrayList<>());
	}

	@Given("^a list named \"([^\"]*)\" within \"([^\"]*)\".\"([^\"]*)\"$")
	public void givenAListNamedWithin(final String name, final String elections, final String election) {
		final ListType list = new ListType().withListDescription(new ListDescriptionInformationType().withListDescriptionInfo(new ArrayList<>()));
		electionMap.get(elections + "." + election).getList().add(list);
		listMap.put(elections + "." + election + "." + name, list);
	}

	@And("^list \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andListHasId(final String elections, final String election, final String name, final String id) {
		listMap.get(elections + "." + election + "." + name).setListIdentification(id);
	}

	@And("^list \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has indentureNumber \"([^\"]*)\"$")
	public void andListHasIndentureNumber(final String elections, final String election, final String name, final String number) {
		listMap.get(elections + "." + election + "." + name).setListIndentureNumber(number);
	}

	@And("^list \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has listOrderOfPrecedence (\\d+)$")
	public void andListHasListOrderOfPrecedence(final String elections, final String election, final String name, final BigInteger order) {
		listMap.get(elections + "." + election + "." + name).setListOrderOfPrecedence(order);
	}

	@Given("^list \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has no listOrderOfPrecedence$")
	public void gibenListHasNoListOrderOfPrecedence(final String elections, final String election, final String name) {
		listMap.get(elections + "." + election + "." + name).setListOrderOfPrecedence(null);
	}

	@And("^list \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andListHasDescription(final String elections, final String election, final String name, final String lang, final String text) {
		listMap.get(elections + "." + election + "." + name).getListDescription().getListDescriptionInfo()
				.add(new ListDescriptionInformationType.ListDescriptionInfo().withLanguage(LanguageType.valueOf(lang)).withListDescription(text));
	}

	@Given("^the list descriptions of the list \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenListDescriptionAreClearedOut(final String elections, final String election, final String name) {
		listMap.get(elections + "." + election + "." + name).getListDescription().setListDescriptionInfo(new ArrayList<>());
	}

	@Given("^a position named \"([^\"]*)\" within \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\"$")
	public void givenAPositionNamed(final String position, final String elections, final String election, final String list) {
		final CandidatePositionType pos = new CandidatePositionType().withCandidateReferenceOnPosition("k12-13");
		pos.setCandidateTextOnPosition(new CandidateTextInformationType().withCandidateTextInfo(new ArrayList<>()));
		listMap.get(elections + "." + election + "." + list).getCandidatePosition().add(pos);
		candidatePositionMap.put(elections + "." + election + "." + list + "." + position, pos);
	}

	@And("^position \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andPositionHasId(final String elections, final String election, final String list, final String pos, final String id) {
		candidatePositionMap.get(elections + "." + election + "." + list + "." + pos).setCandidateIdentification(id);
	}

	@And("^position \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has position in list (\\d+)$")
	public void andPositionHasPositionOnList(final String elections, final String election, final String list, final String pos, final BigInteger posInList) {
		candidatePositionMap.get(elections + "." + election + "." + list + "." + pos).setPositionOnList(posInList);
	}

	@And("^position \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has text \"([^\"]*)\" \"([^\"]*)\"$")
	public void andPositionHasText(final String elections, final String election, final String list, final String pos, final String lang, final String text) {
		candidatePositionMap.get(elections + "." + election + "." + list + "." + pos).getCandidateTextOnPosition().getCandidateTextInfo()
				.add(new CandidateTextInformationType.CandidateTextInfo().withLanguage(LanguageType.valueOf(lang)).withCandidateText(text));
	}

	@Given("^the candidate text descriptions at position \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenCandidateTextDescriptionsAtPositionAreClearedOut(final String elections, final String election, final String list, final String pos) {
		candidatePositionMap.get(elections + "." + election + "." + list + "." + pos).getCandidateTextOnPosition()
				.setCandidateTextInfo(new ArrayList<>());
	}

	@When("^executing the contest phase$")
	public void whenExecutingTheContestPhase() {
		processException = null;
		ParameterPhase.exports.setContestParameters(Flux.just(paramsMap.get(DEFAULT_PARAMS)));
		ParameterPhase.exports.setAfterReadContestPlugins(Flux.just(Collections.emptyList()));
		ParameterPhase.exports.setAfterReadVoterPlugins(Flux.just(Collections.emptyList()));

		ContestEch015xv3.exports.setElections(Flux.fromIterable(electionsMap.get(DEFAULT_ELECTIONS)));
		ContestEch015xv3.exports.setVotes(Flux.fromIterable(votesMap.get(DEFAULT_VOTES)));
		ContestEch015xv4.exports.votes = Flux.empty();
		ContestEch015xv4.exports.elections = Flux.empty();
		ContestEch015xv3.exports.setContests(Flux.just(contestMap.get(DEFAULT_CONTEST)));
		ContestEch015xv4.exports.contests = Flux.empty();
		new Runtime().run(uri("contestPhase"));
		drain(ContestPhase.exports.getContest().doOnNext(c -> contest = c));
	}

	@And("^executing the contest phase and catching exceptions$")
	public void andExecutingTheContestPhaseAndCatchingExceptions() {
		processException = null;
		ParameterPhase.exports.setContestParameters(Flux.just(paramsMap.get(DEFAULT_PARAMS)));
		ParameterPhase.exports.setAfterReadContestPlugins(Flux.just(Collections.emptyList()));

		ContestEch015xv3.exports.setElections(Flux.fromIterable(electionsMap.get(DEFAULT_ELECTIONS)));
		ContestEch015xv3.exports.setVotes(Flux.fromIterable(votesMap.get(DEFAULT_VOTES)));
		ContestEch015xv4.exports.votes = Flux.empty();
		ContestEch015xv4.exports.elections = Flux.empty();
		ContestEch015xv3.exports.setContests(Flux.just(contestMap.get(DEFAULT_CONTEST)));
		ContestEch015xv4.exports.contests = Flux.empty();
		new Runtime().run(uri("contestPhase"));
		try {
			drain(ContestPhase.exports.getContest().doOnNext(c -> contest = c));
			fail();
		} catch (final RuntimeException e) {
			processException = e;
		}
	}

	@When("^executing the contest phase \\(catching exceptions\\) with parameters \"([^\"]*)\" and contest \"([^\"]*)\" and votes \"([^\"]*)\" and elections \"([^\"]*)\"$")
	public void whenExecutingContestPhaseAndCatchingExceptionsWithParameters(final String params, final String contestName, final String votesName,
			final String electionsName) {
		processException = null;
		ParameterPhase.exports.setContestParameters(Flux.just(paramsMap.get(params)));
		ParameterPhase.exports.setAfterReadContestPlugins(Flux.just(Collections.emptyList()));

		ContestEch015xv3.exports.setElections(Flux.fromIterable(electionsMap.get(electionsName)));
		ContestEch015xv3.exports.setVotes(Flux.fromIterable(votesMap.get(votesName)));
		ContestEch015xv4.exports.votes = Flux.empty();
		ContestEch015xv4.exports.elections = Flux.empty();
		ContestEch015xv3.exports.setContests(Flux.just(contestMap.get(contestName)));
		ContestEch015xv4.exports.contests = Flux.empty();
		new Runtime().run(uri("contestPhase"));
		try {
			drain(ContestPhase.exports.getContest().doOnNext(c -> contest = c));
			fail();
		} catch (final RuntimeException e) {
			processException = e;
		}
	}

	@When("^executing the contest phase with parameters \"([^\"]*)\" and contest \"([^\"]*)\" and votes \"([^\"]*)\" and elections \"([^\"]*)\"$")
	public void whenExecutingContestPhaseWithParameters(final String params, final String contestName, final String votesName, final String electionsName) {
		processException = null;
		ParameterPhase.exports.setContestParameters(Flux.just(paramsMap.get(params)));
		ParameterPhase.exports.setAfterReadContestPlugins(Flux.just(Collections.emptyList()));

		ContestEch015xv3.exports.setElections(Flux.fromIterable(electionsMap.get(electionsName)));
		ContestEch015xv3.exports.setVotes(Flux.fromIterable(votesMap.get(votesName)));
		ContestEch015xv4.exports.votes = Flux.empty();
		ContestEch015xv3.exports.setContests(Flux.just(contestMap.get(contestName)));
		ContestEch015xv4.exports.contests = Flux.empty();
		new Runtime().run(uri("contestPhase"));
		drain(ContestPhase.exports.getContest().doOnNext(c -> contest = c));
	}

	@Then("^it outputs a contest with id \"([^\"]*)\"$")
	public void thenItOuputsAContest(final String id) {
		assertEquals(contest.getContestIdentification(), id);
	}

	@Then("^it outputs an exception with text \"([^\"]*)\"$")
	public void thenItOutputsAnExceptionWithText(final String message) {
		assertTrue(processException.getMessage().endsWith(message));
	}

	@Then("^it outputs no exception$")
	public void thenItOutputsNoException() {
	}

	@And("^the contest xml is validated and displayed in the console logs$")
	public void andTheContestXMLIsValidatedAndDisplayedInTheConsoleLogs() {
		try {
			final URL schemaPath = ContestPhaseSteps.class.getResource("/ContestPhaseSteps/contest.xsd");
			checkState(schemaPath != null, "contest.xsd not found");

			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			XmlTransformer.toXml(outputStream, ObjectFactory.class, schemaPath, contest, new QName("contest"), null);
			final String result = outputStream.toString("UTF-8");
			LOGGER.warn(result);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Then("^the variant ballot standard question \"([^\"]*)\" should have position (\\d+)$")
	public void thenTheVariantBallotStandardQuestionShouldHavePosition(final String questionId, final Integer pos) {
		final StandardQuestionType question = from(contest).find(StandardQuestionType.class).filter(q -> q.getQuestionIdentification().equals(questionId))
				.stream().findFirst().get();
		assertEquals(question.getQuestionPosition(), BigInteger.valueOf(pos));
	}

	@Then("^the variant ballot tiebreak question \"([^\"]*)\" should have position (\\d+)$")
	public void thenTheVariantVallotTiebreakQuestionShouldHavePosition(final String questionId, final Integer pos) {
		final TieBreakQuestionType question = from(contest).find(TieBreakQuestionType.class).filter(q -> q.getQuestionIdentification().equals(questionId))
				.stream().findFirst().get();
		assertEquals(question.getQuestionPosition(), BigInteger.valueOf(pos));
	}

	@Then("^a log with text \"([^\"]*)\" must exist$")
	public void thenALogWithTextMustExist(final String message) {
		assertTrue(memoryAppender.list.stream().anyMatch(m -> m.getMessage().equals(message)));
	}

	@Given("^the elections parameters in \"([^\"]*)\" are cleared out$")
	public void givenTheElectionsParametersAreClearedOut(final String name) {
		pElectionMap.clear();
		paramsMap.get(name).setElections(null);
	}

	@Then("^all contest elections should have writeIns = false$")
	public void thenAllContestShouldHaveWritinsToFalse() {
		assertFalse(from(contest).find(ElectionType.class).stream().anyMatch(ElectionType::isWriteInsAllowed));
	}

	@Then("^election identified by \"([^\"]*)\" should have writeIns \"([^\"]*)\"$")
	public void thenElectionShouldHaveWriteIns(final String id, final String writeIns) {
		final ElectionType election = from(contest).find(ElectionType.class).filter(e -> e.getElectionIdentification().equals(id)).stream().findFirst()
				.get();
		assertEquals(election.isWriteInsAllowed(), (boolean) Boolean.valueOf(writeIns));
	}

	@Then("^election identified by \"([^\"]*)\" should have minimalCandidateSelection ([^\"]*)$")
	public void thenElectionShouldHaveMinimalCandidateSelection(final String id, final BigInteger minimalValue) {
		final ElectionType election = from(contest).find(ElectionType.class).filter(e -> e.getElectionIdentification().equals(id)).stream().findFirst()
				.get();
		assertEquals(election.getMinimalCandidateSelectionInList(), minimalValue);
	}

	@Then("^the list identified by \"([^\"]*)\" should contain (\\d+) occurrences of the candidateListIdentification \"([^\"]*)\"$")
	public void thenListShouldContainsOccurencesOfCandidateListIdentification(final String listId, final Integer accum, final String id) {
		assertEquals(accum.longValue(), Long.valueOf(
				from(contest).find(ListType.class).filter(list -> list.getListIdentification().equals(listId)).stream().findFirst().get()
						.getCandidatePosition().stream().filter(c -> c.getCandidateListIdentification().equals(id)).count()).longValue());
	}

	@Given("^a list-union named \"([^\"]*)\" within \"([^\"]*)\".\"([^\"]*)\"$")
	public void givenListUnionWithin(final String name, final String elections, final String election) {
		final ListUnionType union = new ListUnionType().withReferencedList(new ArrayList<>())
				.withListUnionDescription(new ListUnionDescriptionType().withListUnionDescriptionInfo(new ArrayList<>()));
		listUnionMap.put(elections + "." + election + "." + name, union);
		if (electionMap.get(elections + "." + election).getListUnion() == null) {
			electionMap.get(elections + "." + election).setListUnion(new ArrayList<>());
		}
		electionMap.get(elections + "." + election).getListUnion().add(union);
	}

	@And("^list-union  \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andListUntionHasId(final String elections, final String election, final String name, final String id) {
		listUnionMap.get(elections + "." + election + "." + name).setListUnionIdentification(id);
	}

	@And("^list-union  \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has type (\\d+)$")
	public void andListUnionHasType(final String elections, final String election, final String name, final BigInteger type) {
		listUnionMap.get(elections + "." + election + "." + name).setListUnionType(type);
	}

	@And("^list-union  \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void andListUnionHasDescription(final String elections, final String election, final String name, final String lang, final String desc) {
		listUnionMap.get(elections + "." + election + "." + name).getListUnionDescription().getListUnionDescriptionInfo()
				.add(new ListUnionDescriptionType.ListUnionDescriptionInfo().withLanguage(LanguageType.valueOf(lang)).withListUnionDescription(desc));
	}

	@And("^list-union  \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" has referenced list \"([^\"]*)\"$")
	public void andListUnionHasReferencedList(final String elections, final String election, final String name, final String reference) {
		listUnionMap.get(elections + "." + election + "." + name).getReferencedList().add(reference);
	}

	@Given("^the list-union descriptions for \"([^\"]*)\".\"([^\"]*)\".\"([^\"]*)\" are cleared out$")
	public void givenListUntionDescriptionAreClearedOut(final String elections, final String election, final String name) {
		listUnionMap.get(elections + "." + election + "." + name).getListUnionDescription().setListUnionDescriptionInfo(new ArrayList<>());
	}

	@And("^elections in parameters \"([^\"]*)\" are cleared out$")
	public void andElectionsInParametersAreClearedOut(final String params) {
		paramsMap.get(params).setElections(null);
	}

	@Then("^the list identified by \"([^\"]*)\" should have listOrderOfPrecedence (\\d+)$")
	public void thenListShouldHaveListOrderOfPrecedence(final String listId, final BigInteger order) {
		final ListType l = from(contest).find(ListType.class).stream().filter(list -> list.getListIdentification().equals(listId)).findFirst().get();
		assertEquals(l.getListOrderOfPrecedence(), order);
	}

	@And("^the contest default language is \"([^\"]*)\"$")
	public void andContestDefaultLanguageIs(final String lang) {
		assertEquals(LanguageType.DE, contest.getContestDefaultLanguage());
	}

	class MemoryAppender extends ListAppender<ILoggingEvent> {
		public void reset() {
			this.list.clear();
		}

		public boolean contains(final String string, final Level level) {
			return this.list.stream()
					.anyMatch(event -> event.toString().contains(string)
							&& event.getLevel().equals(level));
		}

		public int countEventsForLogger(final String loggerName) {
			return (int) this.list.stream()
					.filter(event -> event.getLoggerName().contains(loggerName))
					.count();
		}

		public List<ILoggingEvent> search(final String string) {
			return this.list.stream()
					.filter(event -> event.toString().contains(string))
					.toList();
		}

		public List<ILoggingEvent> search(final String string, final Level level) {
			return this.list.stream()
					.filter(event -> event.toString().contains(string)
							&& event.getLevel().equals(level))
					.toList();
		}

		public int getSize() {
			return this.list.size();
		}

		public List<ILoggingEvent> getLoggedEvents() {
			return Collections.unmodifiableList(this.list);
		}
	}
}







