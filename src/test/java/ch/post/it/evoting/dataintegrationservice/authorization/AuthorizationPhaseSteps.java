/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.authorization;

import static li.chee.reactive.plumber.Plumbing.drain;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import ch.post.it.evoting.dataintegrationservice.common.DateUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.pipes.AuthorizationPhase;
import ch.post.it.evoting.dataintegrationservice.pipes.ContestPhase;
import ch.post.it.evoting.dataintegrationservice.pipes.ParameterPhase;
import ch.post.it.evoting.dataintegrationservice.pipes.PhaseSteps;
import ch.post.it.evoting.dataintegrationservice.pipes.RegisterPhase;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteType;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
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
	private Exception processException = null;
	private List<VoterTypeWithRole> voters;

	@When("^executing the authorization phase$")
	public void whenExecutingTheAuthorizationPhase() {
		processException = null;
		ContestPhase.exports.setContest(Flux.just(contest));
		RegisterPhase.exports.setVoters(Flux.fromIterable(voters));
		ParameterPhase.exports.setContestParameters(Flux.just(contestParameters));
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
		ParameterPhase.exports.setContestParameters(Flux.just(contestParameters));
		new Runtime().run(uri("authorizationPhase"));
		try {
			final Flux<AuthorizationsType> auths = AuthorizationPhase.exports.getAuthorizations().doOnNext(auth -> {
			});
			final Flux<List<String>> contestUsedDOIs = AuthorizationPhase.exports.getContestUsedDOIs().doOnNext(dois -> {
			});
			drain(auths, contestUsedDOIs);
			fail();
		} catch (final Exception e) {
			processException = e;
		}
	}

	@Given("^aaa$")
	public void given() {
	}

	@Given("^a list of valid voters$")
	public void givenAListOfValidVoters() {
		voters = new ArrayList<>();
	}

	@And("^the list of voters has a valid voter$")
	public void andListOfVotersHasAValidVoter() {
		final VoterDomainOfInfluence voterDoi = new VoterDomainOfInfluence();
		voterDoi.setLocalId("1111");
		voterDoi.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.MU);
		final VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();
		voterDomainOfInfluenceInfo.setDomainOfInfluence(voterDoi);

		final VoterTypeWithRole voter = new VoterTypeWithRole();
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
	public void thenAnExceptionOccurs(final String message) {
		assertTrue(processException.getMessage().endsWith(message));
	}
}


