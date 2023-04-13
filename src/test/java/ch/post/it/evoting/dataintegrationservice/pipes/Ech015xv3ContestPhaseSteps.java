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

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import pipes.InputPhase;
import pipes.contest.ech015xv3.ContestEch015xv3;
import reactor.core.publisher.Flux;

public class Ech015xv3ContestPhaseSteps extends PhaseSteps {

	private List<ContestType> contests;
	private List<ElectionInformationType> elections;
	private List<VoteInformationType> votes;

	@Given("^\"([^\"]*)\" file as input$")
	public void givenFileAsInput(final String file) {
		InputPhase.exports.setFiles(Flux.just("src/test/resources/" + file));
	}

	@Given("^\"([^\"]*)\" and \"([^\"]*)\" files as input$")
	public void givenManyFilesAsInput(final String file1, final String file2) {
		InputPhase.exports.setFiles(Flux.just("src/test/resources/" + file1, "src/test/resources/" + file2));
	}

	@When("^executing the ech015xv3 contest phase pipeline$")
	public void whenExecuting015xv3Phase() {
		new Runtime().run(uri("contest/ech015xv3/contestEch015xv3"));
	}

	@And("^draining the ech015xv3 contest phase outouts$")
	public void andDraining015xv3ContestPhase() {
		contests = new ArrayList<>();
		elections = new ArrayList<>();
		votes = new ArrayList<>();
		drain(
				ContestEch015xv3.exports.getContests().doOnNext(contests::add),
				ContestEch015xv3.exports.getElections().doOnNext(elections::add),
				ContestEch015xv3.exports.getVotes().doOnNext(votes::add)
		);
	}

	@Then("^it outputs one contest$")
	public void thenItOutputsOneContest() {
		assertEquals(1, contests.size());
	}

	@Then("^it outputs two contests$")
	public void thenItOutputsTwoContests() {
		assertEquals(2, contests.size());
	}

	@And("^it outputs a list of elections$")
	public void andItOutputsAListOfElections() {
		assertTrue(elections.size() > 0);
	}

	@And("^it outputs a list of votations$")
	public void andItOutputsAListOfVotations() {
		assertTrue(votes.size() > 0);
	}

	@And("^candidate with callName \"([^\"]*)\" has a candidateText")
	public void andCandidateWithCallnameHasCandidateText(final String candidateCallName) {
		final List<CandidateType> candidateList = elections.stream()
				.map(ElectionInformationType::getCandidate)
				.flatMap(Collection::stream)
				.filter(c -> c.getCallName().equals(candidateCallName))
				.toList();
		if (candidateList.size() > 1) {
			fail(String.format("More that one candidate with callName  %s", candidateCallName));
		}
		assertNotNull(candidateList.get(0).getCandidateText().getCandidateTextInfo());
	}

	@And("^candidate with callName \"([^\"]*)\" has \"([^\"]*)\" as candidateTextInformation")
	public void andCandidateWithCallnameHasCandidateTextInformation(final String candidateCallName, final String value) {
		final List<CandidateType> candidateList = elections.stream()
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
