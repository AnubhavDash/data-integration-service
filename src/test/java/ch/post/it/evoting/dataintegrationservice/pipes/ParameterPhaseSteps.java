/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import ch.evoting.xmlns.parameter._4.ContestType;
import ch.evoting.xmlns.parameter._4.Parameter;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterWithRoleMapper;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import reactor.core.publisher.Flux;

public class ParameterPhaseSteps extends PhaseSteps {

	private List<VoterWithRoleMapper> plugins;
	private ContestType contest;

	@Given("^\"([^\"]*)\" as input file")
	public void givenInputFile(final String fileName) {
		InputPhase.exports.setFiles(Flux.just("src/test/resources/ParameterPhaseSteps/" + fileName));
	}

	@When("^executing the parameter phase pipeline$")
	public void whenExecutingParameterPhase() {
		new Runtime().run(uri("parameterPhase"));
	}

	@And("^draining the parameter phase outputs$")
	public void andDrainingTheParameterPhase() {
		drain(
				ParameterPhase.exports.getAfterReadVoterPlugins().doOnNext(p -> plugins = p),
				ParameterPhase.exports.getContestParameters().doOnNext(c -> contest = c));
	}

	@Then("^it outputs a contest with question \"([^\"]*)\"$")
	public void thenItOutputsAContestWithQuestion(final String questionId) {
		assertTrue(marshall(new Parameter().withContest(contest)).contains(questionId));
	}

	@And("^it outputs a plugin of type \"([^\"]*)\"$")
	public void andItOutputsAPluginType(final String type) {
		assertTrue(plugins.stream().anyMatch(p -> p.getClass().getSimpleName().equals(type)));
	}
}
