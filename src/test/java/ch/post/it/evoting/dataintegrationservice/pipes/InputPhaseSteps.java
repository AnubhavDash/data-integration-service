/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;

public class InputPhaseSteps extends PhaseSteps {

	private List<String> files;

	@When("^executing the input phase pipeline$")
	public void whenExecutingInputPhase() {
		new Runtime().run(uri("inputPhase"));
	}

	@And("^draining the input phase output$")
	public void andDrainingTheInput() {
		files = new ArrayList<>();
		drain(
				InputPhase.exports.getFiles().doOnNext(files::add)
		);
	}

	@Then("^it outputs a list containing multiple files$")
	public void thenItOutputsAListContainingMultipleFiles() {
		assertTrue(files.size() > 1);
	}

	@And("^it outputs a file of type \"([^\"]*)\"$")
	public void andItOutputsAFileOfType(final String type) {
		assertTrue(files.stream().anyMatch(f -> f.contains(type)));
	}
}
