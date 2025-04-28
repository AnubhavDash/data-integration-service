/*
 * (c) Copyright 2025 Swiss Post Ltd.
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

import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv42.ContestEch015xv42;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import reactor.core.publisher.Flux;

public class Ech015xv42ContestPhaseSteps extends PhaseSteps {

	private List<ContestType> contests;
	private List<ElectionGroupBallotType> elections;
	private List<VoteInformationType> votes;

	@Given("^\"([^\"]*)\" v4_2 file as input$")
	public void givenV4File(final String path) {
		InputPhase.exports.setFiles(Flux.just(path));
	}

	@When("^executing the ech015xv4_2 contest phase pipeline$")
	public void whenExecutingContestPhase() {
		new Runtime().run(uri("contest/ech015xv42/contestEch015xv42"));
	}

	@And("^draining the ech015xv4_2 contest phase outputs$")
	public void andDrainingContestPhase() {
		contests = new ArrayList<>();
		elections = new ArrayList<>();
		votes = new ArrayList<>();
		drain(
				ContestEch015xv42.exports.getContests().doOnNext(contests::add),
				ContestEch015xv42.exports.getElections().doOnNext(elections::add),
				ContestEch015xv42.exports.getVotes().doOnNext(votes::add)
		);
	}

	@Then("^it outputs (\\d+) v4_2 contests$")
	public void thenItOutputsV4Contests(final Integer number) {
		assertEquals((int) number, contests.size());
	}

	@And("^it outputs a list of v4_2 elections$")
	public void andItOutputsAListOfElections() {
		assertTrue(elections.size() > 0);
	}

	@And("^it outputs a list of v4_2 votations$")
	public void andItOutputsAListOfVotations() {
		assertTrue(votes.size() > 0);
	}

	@And("^the contest with identification \"([^\"]*)\" has evotingperiod set$")
	public void andContestHasEvotingPeriodSet(final String contestIdentification) {
		final Optional<ContestType> contest = contests.stream().filter(c -> c.getContestIdentification().equals(contestIdentification)).findFirst();
		assertTrue(contest.isPresent());
		assertNotNull(contest.get().getEvotingFromDate());
		assertNotNull(contest.get().getEvotingToDate());
	}

	@And("^it outputs a v4_2 contest contains election with electionId \"([^\"]*)\"$")
	public void andContestContainsElection(final String electionId) {
		assertTrue(
				contests.stream()
						.flatMap(c -> c.getElectionGroupBallot().stream())
						.flatMap(eg -> eg.getElectionInformation().stream())
						.anyMatch(e -> e.getElection().getElectionIdentification().equalsIgnoreCase(electionId)));
	}

	@And("^it outputs a v4_2 election with electionId \"([^\"]*)\" has doi equals to \"([^\"]*)\"$")
	public void andElectionHasDOI(final String electionId, final String doi) {
		assertTrue(
				elections.stream()
						.filter(eg -> eg.getElectionInformation().stream()
								.anyMatch(ei -> ei.getElection().getElectionIdentification().equals(electionId)))
						.anyMatch(eg -> eg.getDomainOfInfluence().equalsIgnoreCase(doi)));
	}

	@And("^it outputs a v4_2 election has (\\d*) candidates")
	public void andElectionsHasCandidates(final Integer numberOfCandidates) {
		assertEquals((int) numberOfCandidates,
				elections.stream().map(eg -> eg.getElectionInformation().get(0)).flatMap(e -> e.getCandidate().stream()).count());
	}

	@And("^candidate v4_2 with callName \"([^\"]*)\" has a candidateText")
	public void andCandidateHasCandidateText(final String candidateCallName) {
		final List<CandidateType> candidateList = elections.stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.map(ElectionInformationType::getCandidate)
				.flatMap(Collection::stream)
				.filter(c -> c.getCallName().equals(candidateCallName)).toList();
		if (candidateList.size() > 1) {
			fail(String.format("More that one candidate with callName  %s", candidateCallName));
		}
		assertNotNull(candidateList.get(0).getCandidateText().getCandidateTextInfo());
	}

	@And("^candidate v4_2 with callName \"([^\"]*)\" has \"([^\"]*)\" as candidateTextInformation")
	public void andCandidateHasCandidateTextInformation(final String candidateCallName, final String value) {
		final List<CandidateType> candidateList = elections.stream()
				.flatMap(eg -> eg.getElectionInformation().stream())
				.map(ElectionInformationType::getCandidate)
				.flatMap(Collection::stream)
				.filter(c -> c.getCallName().equals(candidateCallName)).toList();

		if (candidateList.size() > 1) {
			fail(String.format("More that one candidate with callName  %s", candidateCallName));
		}
		final List<CandidateTextInformationType.CandidateTextInfo> candidateTextList = candidateList.get(0).getCandidateText().getCandidateTextInfo();
		assertTrue(candidateTextList.stream().map(CandidateTextInformationType.CandidateTextInfo::getCandidateText).allMatch(s -> s.equals(value)));
	}
}
