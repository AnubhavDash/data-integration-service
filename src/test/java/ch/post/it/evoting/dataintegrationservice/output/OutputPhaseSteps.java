/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;
import ch.post.it.evoting.dataintegrationservice.common.DateUtil;
import ch.post.it.evoting.dataintegrationservice.pipes.PhaseSteps;
import ch.post.it.evoting.domain.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.CountingCircleType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.DomainOfInfluenceType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.HeaderType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.RegisterType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.SexType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import li.chee.reactive.plumber.Runtime;
import pipes.AssigningAuthorizationPhase;
import pipes.AuthorizationPhase;
import pipes.ContestPhase;
import pipes.HeaderPhase;
import pipes.ParameterPhase;
import reactor.core.publisher.Flux;

public class OutputPhaseSteps extends PhaseSteps {

	private HeaderType header = new HeaderType().withFileDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 12:34:00"))
			.withVoterTotal(BigInteger.valueOf(3));

	private ContestType contest = new ContestType().withContestDefaultLanguage(LanguageType.DE).withContestIdentification("111-222-abc")
			.withContestDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00"))
			.withEvotingFromDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00"))
			.withEvotingToDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00")).withContestDescription(
					new ContestDescriptionInformationType().withContestDescriptionInfo(
							new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.DE)
									.withContestDescription("Desc DE")).withContestDescriptionInfo(
							new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.FR)
									.withContestDescription("Desc FR")).withContestDescriptionInfo(
							new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.IT)
									.withContestDescription("Desc IT")).withContestDescriptionInfo(
							new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.RM)
									.withContestDescription("Desc RM")));

	private AuthorizationsType authorization = new AuthorizationsType().withAuthorization(
			new AuthorizationType().withAuthorizationIdentification("auth-000-abc").withAuthorizationAlias("auth-000-abc-alias")
					.withAuthorizationName("auth-000-abc-name").withAuthorizationFromDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00"))
					.withAuthorizationToDate(DateUtil.parseToXMLGregorianCalendar("25-08-2077 00:00:00")).withAuthorizationTest(false)
					.withAuthorizationGracePeriod(BigInteger.valueOf(900L)).withAuthorizationObject(
							new AuthorizationObjectType().withCountingCircle(new CountingCircleType().withId("cc-000-abc").withName("cc-000-abc"))
									.withDomainOfInfluence(
											new DomainOfInfluenceType().withId("doi-000-abc").withCantonCode("CT").withType("CH").withLocalId("1234"))

					));
	private RuntimeException processException = null;
	private Map<String, List<VoterTypeWithRole>> votersMap = new HashMap<>();
	private Map<String, VoterTypeWithRole> voterMap = new HashMap<>();
	private Map<String, List<VoterTypeWithRole>> invalidVotersMap = new HashMap<>();
	private Map<String, VoterTypeWithRole> invalidVoterMap = new HashMap<>();

	private Object getXMLObjectFromFile(Class clazz, String fileName) {
		try {
			String targetDir = PropertyProvider.getPropertyValue("directory.target");
			File file = new File(targetDir + "/" + fileName);
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement root = jaxbUnmarshaller.unmarshal(new StreamSource(file), clazz);
			return root.getValue();
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Given("^a list of voters named \"([^\"]*)\"$")
	public void givenAListOvVoterNamed(String list) {
		votersMap.put(list, new ArrayList<>());
	}

	@Given("^a list of invalid voters named \"([^\"]*)\"$")
	public void givenAListOfInvalidVotersNamed(String list) {
		invalidVotersMap.put(list, new ArrayList<>());
	}

	@Given("^a voter named \"([^\"]*)\" in list \"([^\"]*)\"$")
	public void givenAVoterNamed(String name, String list) {
		VoterTypeWithRole voter = new VoterTypeWithRole();
		voter.setVoterType(VoterTypeType.SWISSRESIDENT);
		voter.setSex(SexType.FEMALE);
		votersMap.get(list).add(voter);
		voterMap.put(name, voter);
	}

	@Given("^an invalid voter named \"([^\"]*)\" in list \"([^\"]*)\"$")
	public void givenAnInvalidVoterNamed(String name, String list) {
		VoterTypeWithRole voter = new VoterTypeWithRole();
		invalidVotersMap.get(list).add(voter);
		invalidVoterMap.put(name, voter);
	}

	@And("^voter named \"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andVoterNameHasId(String name, String id) {
		voterMap.get(name).setVoterIdentification(id);
	}

	@And("^invalid voter named \"([^\"]*)\" has id \"([^\"]*)\"$")
	public void andInvalidVoterNamedHasId(String name, String id) {
		invalidVoterMap.get(name).setVoterIdentification(id);
	}

	@And("^voter named \"([^\"]*)\" has authorization \"([^\"]*)\"$")
	public void andVoterNamedHasAuthorization(String name, String auth) {
		voterMap.get(name).setAuthorization(auth);
	}

	@And("^voter named \"([^\"]*)\" has first name, last name, date of birth \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
	public void andVoterNamedHasDetails(String voter, String fname, String lname, String dt) {
		voterMap.get(voter).withPerson(new PersonType().withResidenceCountryId("CH").withSex("1").withLanguageOfCorrespondance(LanguageType.DE)
				.withMunicipality(new PersonType.Municipality().withMunicipalityId(1).withMunicipalityName("Bern")).withFirstName(fname)
				.withOfficialName(lname).withDateOfBirth(DateUtil.parseToXMLGregorianCalendar(dt + " 00:00:00")));
	}

	@When("^executing the output phase$")
	public void whenExecutingTheOutputPhase() {
		processException = null;
		HeaderPhase.exports.setHeader(Flux.just(header));
		ContestPhase.exports.setContest(Flux.just(contest));
		AuthorizationPhase.exports.setAuthorizations(Flux.just(authorization));
		AssigningAuthorizationPhase.exports.setValidVoters(Flux.fromIterable(votersMap.get("my voters")));
		AssigningAuthorizationPhase.exports.setInvalidVoters(Flux.fromIterable(invalidVotersMap.get("my invalid voters")));
		ParameterPhase.exports.setConfigSplitterPlugins(Flux.just(Collections.EMPTY_LIST));
		new Runtime().run(uri("outputPrePhase"));
	}

	@Then("^no exception is thrown$")
	public void thenNoExceptionIsThrown() {
	}

	@When("^executing the output phase and catching exceptions$")
	public void whenExecutionTheOutputPhaseAndCatchingExceptions() {
		try {
			processException = null;
			HeaderPhase.exports.setHeader(Flux.just(header));
			ContestPhase.exports.setContest(Flux.just(contest));
			AuthorizationPhase.exports.setAuthorizations(Flux.just(authorization));
			AssigningAuthorizationPhase.exports.setValidVoters(Flux.fromIterable(votersMap.get("my voters")));
			AssigningAuthorizationPhase.exports.setInvalidVoters(Flux.fromIterable(invalidVotersMap.get("my invalid voters")));
			ParameterPhase.exports.setConfigSplitterPlugins(Flux.just(Collections.EMPTY_LIST));
			new Runtime().run(uri("outputPrePhase"));
			fail();
		} catch (RuntimeException e) {
			processException = e;
		}
	}

	@Then("^an exception with text \"([^\"]*)\" should occur$")
	public void thenAnExceptionWithTestShouldOccur(String message) {
		assertTrue(processException.getMessage().endsWith(message));
	}

	@Then("^voter with id \"([^\"]*)\" should have personal information in the file \"([^\"]*)\"$")
	public void thenVoterWithIdShouldHaveInformation(String id, String fname) {
		Configuration configuration = (Configuration) getXMLObjectFromFile(Configuration.class, fname);
		VoterType voter = configuration.getRegister().getVoter().stream().filter(v -> v.getVoterIdentification().equals(id)).findFirst().get();
		assertNotNull(voter.getPerson());
	}

	@And("^voter with id \"([^\"]*)\" should have no personal information in the file \"([^\"]*)\"$")
	public void andVoterWithIdShouldHaveNoPersonalInformation(String id, String fname) {
		Configuration configuration = (Configuration) getXMLObjectFromFile(Configuration.class, fname);
		VoterType voter = configuration.getRegister().getVoter().stream().filter(v -> v.getVoterIdentification().equals(id)).findFirst().get();
		assertNull(voter.getPerson());
	}

	@And("^the configuration file \"([^\"]*)\" should exist and contain (\\d+) voters$")
	public void andConfigurationFileShouldExistAndContain(String fname, Long nb) {
		Configuration configuration = (Configuration) getXMLObjectFromFile(Configuration.class, fname);
		Long nbVoters = configuration.getRegister().getVoter().stream().count();
		assertEquals(nbVoters, nb);
	}

	@And("^the register file \"([^\"]*)\" should exist and contain (\\d+) voters$")
	public void andRegisterFileShouldExistAndContain(String fname, Long nb) {
		RegisterType register = (RegisterType) getXMLObjectFromFile(RegisterType.class, fname);
		Long nbVoters = register.getVoter().stream().count();
		assertEquals(nbVoters, nb);
	}

	@Given("^the contest has description \"([^\"]*)\" \"([^\"]*)\"$")
	public void givenContestHasDescription(String lang, String desc) {
		contest.getContestDescription().getContestDescriptionInfo()
				.add(new ContestDescriptionInformationType.ContestDescriptionInfo().withLanguage(LanguageType.valueOf(lang))
						.withContestDescription(desc));
	}

	@Given("^the contest descriptions are cleared out$")
	public void givenContestDescriptionsAreClearedOut() {
		contest.getContestDescription().setContestDescriptionInfo(new ArrayList<>());
	}

	@Then("^the configuration \"([^\"]*)\" should have contest description \"([^\"]*)\" \"([^\"]*)\"$")
	public void thenConfigurationShouldHaveContestDescription(String fname, String lang, String desc) {
		Configuration configuration = (Configuration) getXMLObjectFromFile(Configuration.class, fname);
		assertTrue(configuration.getContest().getContestDescription().getContestDescriptionInfo().stream()
				.anyMatch(d -> d.getLanguage().equals(LanguageType.valueOf(lang)) && d.getContestDescription().equals(desc)));
	}
}

