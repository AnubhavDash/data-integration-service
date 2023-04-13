/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.post.it.evoting.domain.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoteInformationType;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import pipes.InputPhase;
import pipes.contest.ech015xv4.ContestEch015xv4;
import reactor.core.publisher.Flux;

public class Ech015xv4ContestPhaseSteps extends PhaseSteps {

	private List<ContestType> contests;
	private List<ElectionInformationType> elections;
	private List<VoteInformationType> votes;

	@Given("^\"([^\"]*)\" v4 file as input$")
	public void givenV4File(String path) {
		InputPhase.exports.setFiles(Flux.just(path));
	}

	@When("^executing the ech015xv4 contest phase pipeline$")
	public void whenExecutingContestPhase() {
		new Runtime().run(uri("contest/ech015xv4/contestEch015xv4"));
	}

	@And("^draining the ech015xv4 contest phase outouts$")
	public void andDrainingContestPhase() {
		contests = new ArrayList<>();
		elections = new ArrayList<>();
		votes = new ArrayList<>();
		drain(
				ContestEch015xv4.exports.contests.doOnNext(contests::add),
				ContestEch015xv4.exports.elections.doOnNext(elections::add),
				ContestEch015xv4.exports.votes.doOnNext(votes::add)
		);
	}

	@Then("^it outputs (\\d+) v4 contests$")
	public void thenItOutputsV4Contests(Integer number) {
		assertEquals((int) number, contests.size());
	}

	@And("^it outputs a list of v4 elections$")
	public void andItOutputsAListOfElections() {
		assertTrue(elections.size() > 0);
	}

	@And("^it outputs a list of v4 votations$")
	public void andItOutputsAListOfVotations() {
		assertTrue(votes.size() > 0);
	}

	@And("^contest with identification \"([^\"]*)\" has evotingperiod set$")
	public void andContestHasEvotingPeriodSet(String contestIdentification) {
		Optional<ContestType> contest = contests.stream().filter(c -> c.getContestIdentification().equals(contestIdentification)).findFirst();
		assertTrue(contest.isPresent());
		assertNotNull(contest.get().getEvotingFromDate());
		assertNotNull(contest.get().getEvotingToDate());
	}

	@And("^it outputs a contest contains election with electionId \"([^\"]*)\"$")
	public void andContestContainsElection(String electionId) {
		assertTrue(
				contests.stream()
						.flatMap(c -> c.getElectionInformation().stream())
						.anyMatch(e -> e.getElection().getElectionIdentification().equalsIgnoreCase(electionId)));
	}

	@And("^it outputs an election with electionId \"([^\"]*)\" has doi equals to \"([^\"]*)\"$")
	public void andElectionHasDOI(String electionId, String doi) {
		assertTrue(
				elections.stream()
						.map(eit -> eit.getElection())
						.filter(e -> e.getElectionIdentification().equals(electionId))
						.anyMatch(e -> e.getDomainOfInfluence().equalsIgnoreCase(doi)));
	}

	@And("^it outputs election has (\\d*) candidates")
	public void andElectionsHasCandidates(Integer numberOfCandidates) {
		assertEquals((int) numberOfCandidates, elections.stream().flatMap(e -> e.getCandidate().stream()).count());
	}

	@And("^candidate v4 with callName \"([^\"]*)\" has a candidateText")
	public void andCandidateHasCandidateText(String candidateCallName) {
		List<CandidateType> candidateList = elections.stream()
				.map(ElectionInformationType::getCandidate)
				.flatMap(Collection::stream)
				.filter(c -> c.getCallName().equals(candidateCallName)).toList();
		if (candidateList.size() > 1) {
			fail(String.format("More that one candidate with callName  %s", candidateCallName));
		}
		assertNotNull(candidateList.get(0).getCandidateText().getCandidateTextInfo());
	}

	@And("^candidate v4 with callName \"([^\"]*)\" has \"([^\"]*)\" as candidateTextInformation")
	public void andCandidateHasCandidateTextInformation(String candidateCallName, String value) {
		List<CandidateType> candidateList = elections.stream()
				.map(ElectionInformationType::getCandidate)
				.flatMap(Collection::stream)
				.filter(c -> c.getCallName().equals(candidateCallName)).toList();

		if (candidateList.size() > 1) {
			fail(String.format("More that one candidate with callName  %s", candidateCallName));
		}
		List<CandidateTextInformationType.CandidateTextInfo> candidateTextList = candidateList.get(0).getCandidateText().getCandidateTextInfo();
		assertTrue(candidateTextList.stream().map(CandidateTextInformationType.CandidateTextInfo::getCandidateText).allMatch(s -> s.equals(value)));
	}
}
