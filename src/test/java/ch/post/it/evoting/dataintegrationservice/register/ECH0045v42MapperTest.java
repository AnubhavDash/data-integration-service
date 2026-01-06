/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.ech.xmlns.ech_0010._6.CountryType;
import ch.ech.xmlns.ech_0010._6.PersonMailAddressInfoType;
import ch.ech.xmlns.ech_0010._6.PersonMailAddressType;
import ch.ech.xmlns.ech_0044._4.DatePartiallyKnownType;
import ch.ech.xmlns.ech_0044._4.NamedPersonIdType;
import ch.ech.xmlns.ech_0044._4.PersonIdentificationType;
import ch.ech.xmlns.ech_0045._6.SwissDomesticType;
import ch.ech.xmlns.ech_0045._6.SwissPersonType;
import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateUtils;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.ech045voterextension.UnstructuredPhysicalAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.ech045voterextension.VoterExtensionType;

class ECH0045v42MapperTest {

	private final ECH0045v42Mapper mapper = ECH0045v42Mapper.INSTANCE;

	@Test
	void convertVoterWithExtensionAssignUnstructuredAddress() {
		final VotingPersonType votingPersonType = new VotingPersonType()
				.withPerson(new VotingPersonType.Person()
						.withSwiss(new SwissDomesticType().withSwissDomesticPerson(
								new SwissPersonType()
										.withPersonIdentification(new PersonIdentificationType()
												.withLocalPersonId(new NamedPersonIdType().withPersonId("test1234"))
												.withOfficialName("test")
												.withFirstName("test")
												.withDateOfBirth(new DatePartiallyKnownType().withYear(
														LocalDateUtils.toXMLGregorianCalendar(LocalDateUtils.parse("2000-01-01"))))
										)
										.withExtension(new VoterExtensionType().withUnstructuredPhysicalAddress(
												new UnstructuredPhysicalAddressType()
														.withAddressLine1("unstructured line1")
														.withAddressLine2("unstructured line2")
														.withAddressLine3("unstructured line3")
														.withCountryIso2("CH"))))))
				.withDeliveryAddress(
						new PersonMailAddressType()
								.withPerson(new PersonMailAddressInfoType()
										.withLastName("nope")
										.withFirstName("nope"))
								.withAddressInformation(new AddressInformationType()
										.withStreet("nope")
										.withAddressLine1("nope")
										.withCountry(new CountryType().withCountryIdISO2("FR"))
								));

		final VoterTypeWithRole disVoter = mapper.convert(votingPersonType);

		assertNull(disVoter.getPerson().getPhysicalAddress().getStructuredPhysicalAddress());
		assertNotNull(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress());

		assertFalse(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine1().contains("nope"));
		assertFalse(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine2().contains("nope"));
		assertFalse(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine3().contains("nope"));
		assertNull(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine4());
		assertNull(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine5());
		assertNull(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine6());
		assertNull(disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getAddressLine7());

		assertEquals("CH", disVoter.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getCountryIdISO2());
	}
}
