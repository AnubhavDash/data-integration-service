/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluence;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import pipes.CantonalTreePhase;
import pipes.cantonaltree.stdv1.CantonalTreeStdv1;
import reactor.core.publisher.Flux;

public class CantonalTreePhaseSteps extends PhaseSteps {
	private CantonalTree cantonalTree;

	@Given("^a Stdv1CantonalTree$")
	public void given() {
		final CantonalTree ct = new CantonalTree();

		DomainOfInfluence doi = new DomainOfInfluence();
		doi.setCantonAbbreviation("NE");
		doi.setType("CH");
		doi.setLocalId("1");
		ct.putDomainOfInfluence(doi);

		doi = new DomainOfInfluence();
		doi.setCantonAbbreviation("NE");
		doi.setType("CT");
		doi.setLocalId("1");
		ct.putDomainOfInfluence(doi);

		ct.putDomainOfInfluence(
				new CountingCircle(doi.getCantonAbbreviation(), doi.getType(), doi.getLocalId(), doi.getDescription(), doi.getDomainOfInfluenceIds(),
						"9832", "ccName", 9832));
		CantonalTreeStdv1.exports.setCantonalTree(Flux.just(Optional.of(ct)));
	}

	@When("^executing the cantonalTree phase$")
	public void when() {
		new Runtime().run(uri("cantonalTreePhase"));
	}

	@And("^draining the cantonalTree outputs$")
	public void and() {
		drain(CantonalTreePhase.exports.getCantonalTree().doOnNext(c -> cantonalTree = c.get()));
	}

	@Then("^it outputs a cantonalTree containing the same value as the input element$")
	public void then() {
		assertNotNull(cantonalTree);
		assertEquals(2, cantonalTree.getAllDomainOfInfluences().size());
		assertEquals(1, cantonalTree.getAllCountingCircles().size());

		DomainOfInfluence doi = cantonalTree.getAllDomainOfInfluences().stream().filter(d -> d.getType().equals("CT")).findFirst().get();
		assertEquals("NE", doi.getCantonAbbreviation());
		assertEquals("CT", doi.getType());
		assertEquals("1", doi.getLocalId());

		doi = cantonalTree.getAllDomainOfInfluences().stream().filter(d -> d.getType().equals("CH")).findFirst().get();
		assertEquals("NE", doi.getCantonAbbreviation());
		assertEquals("CH", doi.getType());
		assertEquals("1", doi.getLocalId());

		final CountingCircle cc = cantonalTree.getAllCountingCircles().stream().findFirst().get();
		assertEquals("NE", cc.getCantonAbbreviation());
		assertEquals("9832", cc.getCcId());
		assertEquals("ccName", cc.getCcName());
		assertEquals(9832, cc.getMunicipalityId());
	}
}