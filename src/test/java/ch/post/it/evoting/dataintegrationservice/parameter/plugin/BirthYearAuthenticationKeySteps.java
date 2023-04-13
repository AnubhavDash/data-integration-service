/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ch.evoting.xmlns.parameter._4.BirthYearAuthenticationKeyTypeImpl;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterWithRoleMapper;
import ch.post.it.evoting.domain.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BirthYearAuthenticationKeySteps {
	private final DatatypeFactory factory = DatatypeFactory.newInstance();
	private VoterWithRoleMapper plugin;
	private VoterType voter;

	public BirthYearAuthenticationKeySteps() throws DatatypeConfigurationException {
	}

	@Given("^The birth year authentication key plugin without parameters$")
	public void givenBirthYearAuthenticationPluginWithoutParameters() {
		plugin = new BirthYearAuthenticationKeyTypeImpl();
	}

	@And("^A voter born on (\\d+)-(\\d+)-(\\d+)$")
	public void andAVoterBorn(Integer year, Integer month, Integer day) {
		voter = new VoterTypeWithRole();
		PersonType person = new PersonType();
		person.setDateOfBirth(factory.newXMLGregorianCalendar(year, month, day, 0, 0, 0, 0, 0));
		voter.setPerson(person);
	}

	@When("^The plugin is applied to the voter$")
	public void whenPluginIsappliedToTheVoter() {
		voter = plugin.apply((VoterTypeWithRole) voter);
	}

	@Then("^One authentication key is generated$")
	public void thenOneAuthenticationKeyIsGenerated() {
		assertThat(voter.getExtendedAuthenticationKeys(), notNullValue());
		assertThat(voter.getExtendedAuthenticationKeys().getExtendedAuthenticationKey().size(), is(1));
	}

	@And("^The authentication key named \"([^\"]*)\" contains (\\d+)$")
	public void andAuthenticationKeyNamedContain(String key, Integer value) {
		assertThat(voter.getExtendedAuthenticationKeys().getExtendedAuthenticationKey().get(0).getName(),
				is(BirthYearAuthenticationKeyTypeImpl.KEY_NAME));
		assertThat(voter.getExtendedAuthenticationKeys().getExtendedAuthenticationKey().get(0).getValue(),
				is(value.toString()));
	}
}
