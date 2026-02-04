/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ch.ech.xmlns.ech_0007._6.SwissMunicipalityType;
import ch.ech.xmlns.ech_0008._3.CountryType;
import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.ech.xmlns.ech_0010._6.PersonMailAddressInfoType;
import ch.ech.xmlns.ech_0044._4.DatePartiallyKnownType;
import ch.ech.xmlns.ech_0044._4.NamedPersonIdType;
import ch.ech.xmlns.ech_0044._4.PersonIdentificationType;
import ch.ech.xmlns.ech_0045._6.ElectoralAddressType;
import ch.ech.xmlns.ech_0045._6.EmailType;
import ch.ech.xmlns.ech_0045._6.SwissAbroadType;
import ch.ech.xmlns.ech_0045._6.SwissDomesticType;
import ch.ech.xmlns.ech_0045._6.SwissPersonType;
import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;

@SpringBootTest
@ActiveProfiles("test")
class ECH0045V42MapperIT {

	private final ECH0045v42Mapper mapper = ECH0045v42Mapper.INSTANCE;

	private VotingPersonType createSwissVoter() throws Exception {
		final XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		gc.clear();
		gc.setYear(1999);
		return new VotingPersonType()
				.withPerson(new VotingPersonType.Person()
						.withSwiss(new SwissDomesticType()
								.withMunicipality(new SwissMunicipalityType()
										.withMunicipalityName("Fribourg")
										.withMunicipalityId(1))
								.withSwissDomesticPerson(new SwissPersonType()
										.withLanguageOfCorrespondance(ch.ech.xmlns.ech_0045._6.LanguageType.DE)
										.withPersonIdentification(new PersonIdentificationType()
												.withLocalPersonId(new NamedPersonIdType().withPersonId("1234"))
												.withOfficialName("Dupont")
												.withFirstName("Pierre")
												.withSex("1")
												.withDateOfBirth(new DatePartiallyKnownType()
														.withYear(gc)
												)
										)
								)
						)
				).withElectoralAddress(new ElectoralAddressType()
						.withPerson(new PersonMailAddressInfoType()
								.withFirstName("Pierre-Henri")
								.withLastName("Dupont-Duchêne")
						)
						.withAddressInformation(new AddressInformationType()
								.withStreet("Rue des lilas")
								.withHouseNumber("12a")
								.withDwellingNumber("222")
								.withPostOfficeBoxText("Case postale")
								.withPostOfficeBoxNumber(1002L)
								.withSwissZipCode(2072L)
								.withTown("St-Blaise")
								.withCountry(new ch.ech.xmlns.ech_0010._6.CountryType().withCountryIdISO2("ch"))
						)
				);
	}

	private VotingPersonType createAbroadVoter() throws Exception {
		final XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		gc.clear();
		gc.setYear(1999);
		return new VotingPersonType()
				.withPerson(new VotingPersonType.Person()
						.withSwissAbroad(new SwissAbroadType()
								.withResidenceCountry(new CountryType().withCountryIdISO2("FR"))
								.withMunicipality(new SwissMunicipalityType()
										.withMunicipalityName("Genève")
										.withMunicipalityId(2))
								.withSwissAbroadPerson(new SwissPersonType()
										.withLanguageOfCorrespondance(ch.ech.xmlns.ech_0045._6.LanguageType.IT)
										.withPersonIdentification(new PersonIdentificationType()
												.withLocalPersonId(new NamedPersonIdType().withPersonId("43210"))
												.withOfficialName("Durand")
												.withFirstName("Fabienne")
												.withSex("2")
												.withDateOfBirth(new DatePartiallyKnownType()
														.withYear(gc)
												)
										)
								)
						)
				)
				.withElectoralAddress(new ElectoralAddressType()
						.withPerson(new PersonMailAddressInfoType()
								.withTitle("Sa Sainteté")
								.withLastName("Durand-Duparis")
								.withFirstName("Fabienne Linda")
						)
						.withAddressInformation(new AddressInformationType()
								.withStreet("Rue des rosiers")
								.withForeignZipCode("4123ZX")
								.withTown("Whalala")
								.withCountry(new ch.ech.xmlns.ech_0010._6.CountryType().withCountryIdISO2("IN"))
						)
				)
				.withEmail(new EmailType()
						.withEmailAddress("fabienne.durand@provider.ch")
				);
	}

	private void setBirthDate(final VotingPersonType voter, final int year, final Integer month, final Integer day) throws Exception {
		final XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		gc.clear();
		gc.setYear(year);
		if (month != null && day != null) {
			gc.setMonth(month);
			gc.setDay(day);
			if (voter.getPerson().getSwiss() != null) {
				voter.getPerson().getSwiss().getSwissDomesticPerson().getPersonIdentification().getDateOfBirth().setYearMonthDay(gc);
			} else {
				voter.getPerson().getSwissAbroad().getSwissAbroadPerson().getPersonIdentification().getDateOfBirth().setYearMonthDay(gc);
			}
		} else if (month != null) {
			gc.setMonth(month);
			if (voter.getPerson().getSwiss() != null) {
				voter.getPerson().getSwiss().getSwissDomesticPerson().getPersonIdentification().getDateOfBirth().setYearMonth(gc);
			} else {
				voter.getPerson().getSwissAbroad().getSwissAbroadPerson().getPersonIdentification().getDateOfBirth().setYearMonth(gc);
			}
		} else {
			if (voter.getPerson().getSwiss() != null) {
				voter.getPerson().getSwiss().getSwissDomesticPerson().getPersonIdentification().getDateOfBirth().setYear(gc);
			} else {
				voter.getPerson().getSwissAbroad().getSwissAbroadPerson().getPersonIdentification().getDateOfBirth().setYear(gc);
			}
		}
	}

	@Test
	void testBirthDate() throws Exception {
		final VotingPersonType echVoter = createSwissVoter();
		final VotingPersonType echAbroadVoter = createAbroadVoter();
		VoterTypeWithRole voter;

		setBirthDate(echVoter, 1999, null, null);
		voter = mapper.convert(echVoter);
		assertEquals("1999-01-01", voter.getPerson().getDateOfBirth().toXMLFormat());

		setBirthDate(echAbroadVoter, 2003, null, null);
		voter = mapper.convert(echAbroadVoter);
		assertEquals("2003-01-01", voter.getPerson().getDateOfBirth().toXMLFormat());

		setBirthDate(echVoter, 2004, 4, null);
		voter = mapper.convert(echVoter);
		assertEquals("2004-04-01", voter.getPerson().getDateOfBirth().toXMLFormat());

		setBirthDate(echAbroadVoter, 1965, 7, null);
		voter = mapper.convert(echAbroadVoter);
		assertEquals("1965-07-01", voter.getPerson().getDateOfBirth().toXMLFormat());

		setBirthDate(echVoter, 2001, 11, 25);
		voter = mapper.convert(echVoter);
		assertEquals("2001-11-25", voter.getPerson().getDateOfBirth().toXMLFormat());

		setBirthDate(echAbroadVoter, 1984, 12, 20);
		voter = mapper.convert(echAbroadVoter);
		assertEquals("1984-12-20", voter.getPerson().getDateOfBirth().toXMLFormat());
	}

	@Test
	void testSwissVoter() throws Exception {
		final VotingPersonType echVoter = createSwissVoter();
		final VoterTypeWithRole voter = mapper.convert(echVoter);
		assertEquals("Dupont", voter.getPerson().getOfficialName());
		assertEquals("Pierre", voter.getPerson().getFirstName());
		assertEquals("1", voter.getPerson().getSex());
		assertEquals(VoterTypeType.SWISSRESIDENT, voter.getVoterType());
		assertEquals("1234", voter.getVoterIdentification());
		assertEquals(LanguageType.DE, voter.getPerson().getLanguageOfCorrespondance());
		assertEquals("CH", voter.getPerson().getResidenceCountryId());
		assertEquals(1, voter.getPerson().getMunicipality().getMunicipalityId());
		assertEquals("Fribourg", voter.getPerson().getMunicipality().getMunicipalityName());
		assertNull(voter.getPerson().getPhysicalAddress().getTitle());
		assertEquals("Pierre-Henri", voter.getPerson().getPhysicalAddress().getFirstName());
		assertEquals("Dupont-Duchêne", voter.getPerson().getPhysicalAddress().getLastName());
		assertEquals("Rue des lilas", voter.getPerson().getPhysicalAddress().getStreet());
		assertEquals("12a", voter.getPerson().getPhysicalAddress().getHouseNumber());
		assertEquals("222", voter.getPerson().getPhysicalAddress().getDwellingNumber());
		assertEquals("Case postale", voter.getPerson().getPhysicalAddress().getPostOfficeBoxText());
		assertEquals(Long.valueOf(1002L), voter.getPerson().getPhysicalAddress().getPostOfficeBoxNumber());
		assertEquals("2072", voter.getPerson().getPhysicalAddress().getZipCode());
		assertEquals("St-Blaise", voter.getPerson().getPhysicalAddress().getTown());
		assertEquals("ch", voter.getPerson().getPhysicalAddress().getCountry());
		assertEquals(0, voter.getPerson().getElectronicAddress().size());
		assertNull(voter.getPerson().getPhysicalAddress().getFrankingArea());
	}

	@Test
	void testAbroadVoter() throws Exception {
		final VotingPersonType echAbroadVoter = createAbroadVoter();
		final VoterTypeWithRole voter = mapper.convert(echAbroadVoter);
		assertEquals("Durand", voter.getPerson().getOfficialName());
		assertEquals("Fabienne", voter.getPerson().getFirstName());
		assertEquals("2", voter.getPerson().getSex());
		assertEquals(VoterTypeType.SWISSABROAD, voter.getVoterType());
		assertEquals("43210", voter.getVoterIdentification());
		assertEquals("FR", voter.getPerson().getResidenceCountryId());
		assertEquals(LanguageType.IT, voter.getPerson().getLanguageOfCorrespondance());
		assertEquals(2, voter.getPerson().getMunicipality().getMunicipalityId());
		assertEquals("Genève", voter.getPerson().getMunicipality().getMunicipalityName());
		assertEquals("Sa Sainteté", voter.getPerson().getPhysicalAddress().getTitle());
		assertEquals("Fabienne Linda", voter.getPerson().getPhysicalAddress().getFirstName());
		assertEquals("Durand-Duparis", voter.getPerson().getPhysicalAddress().getLastName());
		assertEquals("Rue des rosiers", voter.getPerson().getPhysicalAddress().getStreet());
		assertNull(voter.getPerson().getPhysicalAddress().getHouseNumber());
		assertNull(voter.getPerson().getPhysicalAddress().getDwellingNumber());
		assertNull(voter.getPerson().getPhysicalAddress().getPostOfficeBoxText());
		assertNull(voter.getPerson().getPhysicalAddress().getPostOfficeBoxNumber());
		assertEquals("4123ZX", voter.getPerson().getPhysicalAddress().getZipCode());
		assertEquals("Whalala", voter.getPerson().getPhysicalAddress().getTown());
		assertEquals("IN", voter.getPerson().getPhysicalAddress().getCountry());
		assertEquals(1, voter.getPerson().getElectronicAddress().get(0).getElectronicAddressType());
		assertEquals("fabienne.durand@provider.ch", voter.getPerson().getElectronicAddress().get(0).getElectronicAddressValue());
		assertNull(voter.getPerson().getPhysicalAddress().getFrankingArea());
	}

	@Test
	void testConvertCountryIdToIso2() throws Exception {
		final VotingPersonType echAbroadVoter = createAbroadVoter();
		final VoterTypeWithRole voter;
		echAbroadVoter.getPerson().getSwissAbroad().getResidenceCountry()
				.withCountryId(8207)
				.withCountryIdISO2(null);
		voter = mapper.convert(echAbroadVoter);
		assertEquals("DE", voter.getPerson().getResidenceCountryId());
	}
}
