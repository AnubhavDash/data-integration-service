/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.authorization;

import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.common.DateUtil;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluenceId;
import ch.post.it.evoting.dataintegrationservice.pipes.PhaseSteps;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoteType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterDomainOfInfluence;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import pipes.AuthorizationPhase;
import pipes.CantonalTreePhase;
import pipes.ContestPhase;
import pipes.ParameterPhase;
import pipes.RegisterPhase;
import reactor.core.publisher.Flux;

public class AuthorizationPhaseSteps extends PhaseSteps {

	private final ContestType contest = new ContestType()
			.withContestIdentification("111-222-abc")
			.withVoteInformation(new VoteInformationType().withVote(new VoteType().withDomainOfInfluence("4400-987333-65654")))
			.withContestDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00"))
			.withEvotingFromDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00"))
			.withEvotingToDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00"))
			.withContestDescription(new ContestDescriptionInformationType()
					.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
							.withLanguage(LanguageType.DE)
							.withContestDescription("Desc DE")
					)
					.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
							.withLanguage(LanguageType.FR)
							.withContestDescription("Desc FR")
					)
					.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
							.withLanguage(LanguageType.IT)
							.withContestDescription("Desc IT")
					)
					.withContestDescriptionInfo(new ContestDescriptionInformationType.ContestDescriptionInfo()
							.withLanguage(LanguageType.RM)
							.withContestDescription("Desc RM")
					)
			);
	private final ch.evoting.xmlns.parameter._4.ContestType contestParameters = new ch.evoting.xmlns.parameter._4.ContestType();
	private RuntimeException processException = null;
	private CantonalTree cantonalTree;
	private List<VoterTypeWithRole> voters;

	private void setDoiId(String code, String id, String parentId, String parentCode) {
		DomainOfInfluenceId parentDoiId = cantonalTree.getDomainOfInfluence(parentCode).getDomainOfInfluenceIds().stream()
				.filter(i -> i.getId().equals(parentId)).findFirst().get();
		DomainOfInfluenceId doiId = new DomainOfInfluenceId();
		doiId.setId(id);
		doiId.setParent(parentDoiId);
		doiId.setRelatedDomainOfInfluence(cantonalTree.getDomainOfInfluence(code));
		if (cantonalTree.getDomainOfInfluence(code).getDomainOfInfluenceIds() == null) {
			cantonalTree.getDomainOfInfluence(code).setDomainOfInfluenceIds(new ArrayList<>());
		}
		cantonalTree.getDomainOfInfluence(code).getDomainOfInfluenceIds().add(doiId);
	}

	@Given("^a cantonal tree$")
	public void givenCantonalTree() {
		cantonalTree = new CantonalTree();
	}

	@When("^executing the authorization phase$")
	public void whenExecutingTheAuthorizationPhase() {
		processException = null;
		ContestPhase.exports.setContest(Flux.just(contest));
		RegisterPhase.exports.setVoters(Flux.fromIterable(voters));
		ParameterPhase.exports.setAfterReadVoterAuthorizationPlugins(Flux.just(Collections.EMPTY_LIST));
		ParameterPhase.exports.setBuildAuthorizationPlugins(Flux.just(Collections.EMPTY_LIST));
		ParameterPhase.exports.setContestParameters(Flux.just(contestParameters));
		CantonalTreePhase.exports.setCantonalTree(Flux.just(Optional.of(cantonalTree)));
		new Runtime().run(uri("authorizationPhase"));
		drain(AuthorizationPhase.exports.getAuthorizations().doOnNext(auth -> {
				}),
				AuthorizationPhase.exports.getContestUsedDOIs().doOnNext(dois -> {
				}));
	}

	@When("^executing the authorization phase and catching exceptions$")
	public void whenExecutingTheAuthorizationPhaseAndCatchingExceptions() {
		processException = null;
		ContestPhase.exports.setContest(Flux.just(contest));
		RegisterPhase.exports.setVoters(Flux.fromIterable(voters));
		ParameterPhase.exports.setBuildAuthorizationPlugins(Flux.just(Collections.EMPTY_LIST));
		ParameterPhase.exports.setBuildAuthorizationPlugins(Flux.just(Collections.EMPTY_LIST));
		ParameterPhase.exports.setContestParameters(Flux.just(contestParameters));
		CantonalTreePhase.exports.setCantonalTree(Flux.just(Optional.of(cantonalTree)));
		new Runtime().run(uri("authorizationPhase"));
		try {
			drain(AuthorizationPhase.exports.getAuthorizations().doOnNext(auth -> {
					}),
					AuthorizationPhase.exports.getContestUsedDOIs().doOnNext(dois -> {
					}));
			fail();
		} catch (RuntimeException e) {
			processException = e;
		}
	}

	@Given("^aaa$")
	public void given() {
	}

	@And("^cantonal tree has domain of influence \"([^\"]*)\",\"([^\"]*)\",(\\d+)$")
	public void andCantonalTreeHasDOI(String arg0, String arg1, Integer arg2) {

	}

	@And("^cantonal tree has domain of influence \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
	public void andCantonalTreeHasDOI(String canton, String type, String localId) {
		DomainOfInfluence doi = new DomainOfInfluence();
		doi.setCantonAbbreviation(canton);
		doi.setType(type);
		doi.setLocalId(localId);
		cantonalTree.putDomainOfInfluence(doi);
	}

	@And("^cantonal tree has counting circle \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
	public void andCantonalTreeHasCountingCricle(String canton, String type, String localId) {
		CountingCircle cc = new CountingCircle(canton, type, localId, "dummy", new ArrayList<>(), "ccId", "ccName", 1111);
		cantonalTree.putDomainOfInfluence(cc);
	}

	@And("^domain of influence \"([^\"]*)\" has ID \"([^\"]*)\"$")
	public void andDomainOfInfluenceHasId(String code, String id) {
		DomainOfInfluenceId doiId = new DomainOfInfluenceId();
		doiId.setRelatedDomainOfInfluence(cantonalTree.getDomainOfInfluence(code));
		doiId.setId(id);
		if (cantonalTree.getDomainOfInfluence(code).getDomainOfInfluenceIds() == null) {
			cantonalTree.getDomainOfInfluence(code).setDomainOfInfluenceIds(new ArrayList<>());
		}
		cantonalTree.getDomainOfInfluence(code).getDomainOfInfluenceIds().add(doiId);
	}

	@And("^domain of influence \"([^\"]*)\" has ID \"([^\"]*)\" and parent ID \"([^\"]*)\" in \"([^\"]*)\"$")
	public void andDomainOfInfluenceHasIdAndParentId(String code, String id, String parentId, String parentCode) {
		setDoiId(code, id, parentId, parentCode);
	}

	@And("^counting circle \"([^\"]*)\" has ID \"([^\"]*)\" and parent ID \"([^\"]*)\" in \"([^\"]*)\"$")
	public void andCountingCircleHasIdAndParentId(String code, String id, String parentId, String parentCode) {
		setDoiId(code, id, parentId, parentCode);
	}

	@Given("^a list of valid voters$")
	public void givenAListOfValidVoters() {
		voters = new ArrayList<>();
	}

	@And("^the list of voters has a valid voter$")
	public void andListOfVotersHasAValidVoter() {
		VoterDomainOfInfluence voterDoi = new VoterDomainOfInfluence();
		voterDoi.setLocalId("1111");
		voterDoi.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.MU);
		VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();
		voterDomainOfInfluenceInfo.setDomainOfInfluence(voterDoi);

		VoterTypeWithRole voter = new VoterTypeWithRole();
		voter.setDomainOfInfluenceInfos(new ArrayList<>());
		voter.withPerson(new PersonType().withMunicipality(new PersonType.Municipality().withMunicipalityId(1111)));

		voter.getDomainOfInfluenceInfos().add(voterDomainOfInfluenceInfo);
		voters.add(voter);
	}

	@Given("^the list of valid voters is cleared out$")
	public void givenListOfValidVoterIsClearedOut() {
		voters.clear();
	}

	@Then("^an exception \"([^\"]*)\" should occur$")
	public void thenAnExceptionOccurs(String message) {
		assertTrue(processException.getMessage().endsWith(message));
	}
}


