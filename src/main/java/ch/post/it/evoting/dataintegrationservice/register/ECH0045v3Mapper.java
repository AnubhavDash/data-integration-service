/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static ch.post.it.evoting.dataintegrationservice.common.StringUtil.isNullOrEmpty;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import ch.ech.xmlns.ech_0046._2.EmailType;
import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectronicAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PhysicalAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.SexType;

@Mapper
public interface ECH0045v3Mapper {
	ECH0045v3Mapper INSTANCE = Mappers.getMapper(ECH0045v3Mapper.class);

	@SuppressWarnings("Duplicates")
	default VoterTypeWithRole convert(final ch.ech.xmlns.ech_0045._3.VotingPersonType voter) {
		final VoterTypeWithRole voterType = new VoterTypeWithRole();

		if (voter.getPerson().getSwissAbroad() != null) {
			appendAbroadVoter(voterType, voter);

			//ResidenceCountryId calculation
			String iso2code = voter.getPerson().getSwissAbroad().getResidenceCountry().getCountryIdISO2();
			if (isNullOrEmpty(iso2code)) {
				final Integer countryId = voter.getPerson().getSwissAbroad().getResidenceCountry().getCountryId();
				checkState(countryId != null, "voter '%s': neither iso2code, nor countryId is provided",
						voter.getPerson().getSwissAbroad().getSwissAbroadPerson().getPersonIdentification().getLocalPersonId().getPersonId());

				// conversion
				voterType.getPerson().setResidenceCountryId(getCountryIso2ByCountryId(countryId));
			} else {
				// direct setting
				if (iso2code.equalsIgnoreCase("XZ")) {
					iso2code = "XK";
				}
				voterType.getPerson().setResidenceCountryId(iso2code);
			}

		} else if (voter.getPerson().getSwiss() != null) {
			appendSwissVoter(voterType, voter);
		} else if (voter.getPerson().getForeigner() != null) {
			appendForeignerVoter(voterType, voter);
		} else {
			throw new IllegalStateException("Unimplemented personType");
		}

		if (voter.getDeliveryAddress() != null) {
			voterType.getPerson().setPhysicalAddress(convert(voter.getDeliveryAddress()));
		} else if (voter.getElectoralAddress() != null) {
			voterType.getPerson().setPhysicalAddress(convert(voter.getElectoralAddress()));
		} else {
			throw new IllegalStateException(String.format("Unable to retrieve an address for the voter (%s)", voterType.getVoterIdentification()));
		}

		voterType.setDomainOfInfluenceInfos(new LinkedList<>(voter.getDomaniOfInfluence().stream().map(doi ->
		{
			final VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();

			final VoterDomainOfInfluence voterDomainOfInfluence = new VoterDomainOfInfluence();
			voterDomainOfInfluence.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(doi.getDomainOfInfluenceType().toString()));
			voterDomainOfInfluence.setLocalId(doi.getLocalDomainOfInfluencetId());
			voterDomainOfInfluence.setName(doi.getDomainOfInfluenceName());
			voterDomainOfInfluence.setShortName(doi.getDomainOfInfluenceShortname());

			voterDomainOfInfluenceInfo.setDomainOfInfluence(voterDomainOfInfluence);
			return voterDomainOfInfluenceInfo;
		}).toList()));

		return voterType;
	}

	default SexType convertSexCode(final String value) {
		if ("1".equalsIgnoreCase(value)) {
			return SexType.MALE;
		} else if ("2".equalsIgnoreCase(value)) {
			return SexType.FEMALE;
		} else {
			return SexType.UNDEFINED;
		}
	}

	@Mapping(target = "person.officialName", source = "person.swissAbroad.swissAbroadPerson.personIdentification.officialName")
	@Mapping(target = "person.firstName", source = "person.swissAbroad.swissAbroadPerson.personIdentification.firstName")
	@Mapping(target = "person.sex", source = "person.swissAbroad.swissAbroadPerson.personIdentification.sex")
	@Mapping(target = "person.dateOfBirth", expression = "java( getBirthDate(person.getSwissAbroad().getSwissAbroadPerson().getPersonIdentification().getDateOfBirth()) )")
	@Mapping(target = "person.languageOfCorrespondance", source = "person.swissAbroad.swissAbroadPerson.languageOfCorrespondance")
	@Mapping(target = "person.residenceCountryId", ignore = true)
	@Mapping(target = "person.municipality", source = "person.swissAbroad.municipality")
	@Mapping(target = "voterIdentification", source = "person.swissAbroad.swissAbroadPerson.personIdentification.localPersonId.personId")
	@Mapping(target = "sex", source = "person.swissAbroad.swissAbroadPerson.personIdentification.sex")
	@Mapping(target = "authorization", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "person.electronicAddress", source = "email")
	@Mapping(target = "voterType", expression = "java( ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType.SWISSABROAD )")
	void appendAbroadVoter(
			@MappingTarget
			VoterTypeWithRole voter, ch.ech.xmlns.ech_0045._3.VotingPersonType v);

	@Mapping(target = "person.officialName", source = "person.swiss.swissDomesticPerson.personIdentification.officialName")
	@Mapping(target = "person.firstName", source = "person.swiss.swissDomesticPerson.personIdentification.firstName")
	@Mapping(target = "person.sex", source = "person.swiss.swissDomesticPerson.personIdentification.sex")
	@Mapping(target = "person.dateOfBirth", expression = "java( getBirthDate(person.getSwiss().getSwissDomesticPerson().getPersonIdentification().getDateOfBirth()) )")
	@Mapping(target = "person.languageOfCorrespondance", source = "person.swiss.swissDomesticPerson.languageOfCorrespondance")
	@Mapping(target = "person.residenceCountryId", constant = "CH")
	@Mapping(target = "person.municipality", source = "person.swiss.municipality")
	@Mapping(target = "voterIdentification", source = "person.swiss.swissDomesticPerson.personIdentification.localPersonId.personId")
	@Mapping(target = "sex", source = "person.swiss.swissDomesticPerson.personIdentification.sex")
	@Mapping(target = "authorization", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "person.electronicAddress", source = "email")
	@Mapping(target = "voterType", expression = "java( ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType.SWISSRESIDENT )")
	void appendSwissVoter(
			@MappingTarget
			VoterTypeWithRole voter, ch.ech.xmlns.ech_0045._3.VotingPersonType v);

	@Mapping(target = "person.officialName", source = "person.foreigner.foreignerPerson.personIdentification.officialName")
	@Mapping(target = "person.firstName", source = "person.foreigner.foreignerPerson.personIdentification.firstName")
	@Mapping(target = "person.sex", source = "person.foreigner.foreignerPerson.personIdentification.sex")
	@Mapping(target = "person.dateOfBirth", expression = "java( getBirthDate(person.getForeigner().getForeignerPerson().getPersonIdentification().getDateOfBirth()) )")
	@Mapping(target = "person.languageOfCorrespondance", source = "person.foreigner.foreignerPerson.languageOfCorrespondance")
	@Mapping(target = "person.residenceCountryId", constant = "CH")
	@Mapping(target = "person.municipality", source = "person.foreigner.municipality")
	@Mapping(target = "voterIdentification", source = "person.foreigner.foreignerPerson.personIdentification.localPersonId.personId")
	@Mapping(target = "sex", source = "person.foreigner.foreignerPerson.personIdentification.sex")
	@Mapping(target = "authorization", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "person.electronicAddress", source = "email")
	@Mapping(target = "voterType", expression = "java( ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType.FOREIGNER )")
	void appendForeignerVoter(
			@MappingTarget
			VoterTypeWithRole voter, ch.ech.xmlns.ech_0045._3.VotingPersonType v);

	@SuppressWarnings("Duplicates")
	default PhysicalAddressType convert(final ch.ech.xmlns.ech_0010._5.PersonMailAddressType echAddress) {
		//choice swissZip or foreignZip or nothing
		final PhysicalAddressType address = new PhysicalAddressType();
		appendPhysicalAddress(address, echAddress);
		if (echAddress.getAddressInformation().getSwissZipCode() != null) {
			address.setZipCode(echAddress.getAddressInformation().getSwissZipCode().toString());
		} else if (!isNullOrEmpty(echAddress.getAddressInformation().getForeignZipCode())) {
			address.setZipCode(echAddress.getAddressInformation().getForeignZipCode());
		}

		//manage address line 1 and 2
		final String line1 = echAddress.getAddressInformation().getAddressLine1();
		final String line2 = echAddress.getAddressInformation().getAddressLine2();
		if (!isNullOrEmpty(line1) || !isNullOrEmpty(line2)) {
			address.setBelowNameLine(new ArrayList<>());
			if (!isNullOrEmpty(line1)) {
				address.getBelowNameLine().add(line1);
			}
			if (!isNullOrEmpty(line2)) {
				address.getBelowNameLine().add(line2);
			}
		}

		//manage locality to belowTown
		if (!isNullOrEmpty(echAddress.getAddressInformation().getLocality())) {
			address.setBelowTownLine(Collections.singletonList(echAddress.getAddressInformation().getLocality()));
		}

		if (address.getCountry().equalsIgnoreCase("XZ")) {
			address.setCountry("XK");
		}

		if ((address.getCountry().equalsIgnoreCase("US") || address.getCountry().equalsIgnoreCase("CA")) && !isNullOrEmpty(
				echAddress.getAddressInformation().getLocality())) {
			address.setTown(address.getTown() + " " + echAddress.getAddressInformation().getLocality());
		}

		return address;
	}

	@Mapping(target = "mrMrs", source = "person.mrMrs")
	@Mapping(target = "title", source = "person.title")
	@Mapping(target = "firstName", source = "person.firstName")
	@Mapping(target = "lastName", source = "person.lastName")
	@Mapping(target = "street", source = "addressInformation.street")
	@Mapping(target = "houseNumber", source = "addressInformation.houseNumber")
	@Mapping(target = "dwellingNumber", source = "addressInformation.dwellingNumber")
	@Mapping(target = "postOfficeBoxText", source = "addressInformation.postOfficeBoxText")
	@Mapping(target = "postOfficeBoxNumber", source = "addressInformation.postOfficeBoxNumber")
	@Mapping(target = "zipCode", ignore = true)
	@Mapping(target = "town", source = "addressInformation.town")
	@Mapping(target = "country", source = "addressInformation.country")
	@Mapping(target = "countryNameShort", constant = "--")
	@Mapping(target = "belowTitleLine", ignore = true)
	@Mapping(target = "belowNameLine", ignore = true)
	@Mapping(target = "belowStreetLine", ignore = true)
	@Mapping(target = "belowPostOfficeBoxLine", ignore = true)
	@Mapping(target = "belowTownLine", ignore = true)
	@Mapping(target = "belowCountryLine", ignore = true)
	@Mapping(target = "frankingArea", ignore = true)
	void appendPhysicalAddress(
			@MappingTarget
			PhysicalAddressType address, ch.ech.xmlns.ech_0010._5.PersonMailAddressType echAddress);

	@Mapping(target = "electronicAddressType", constant = "1")
	@Mapping(target = "electronicAddressValue", source = "emailAddress")
	ElectronicAddressType convert(ch.ech.xmlns.ech_0046._2.EmailType value);

	default List<ElectronicAddressType> convertToList(final EmailType email) {
		final ElectronicAddressType addressType = convert(email);
		return addressType == null ? Collections.emptyList() : Collections.singletonList(addressType);
	}

	LanguageType convert(ch.ech.xmlns.ech_0045._3.LanguageType value);

	PersonType.Municipality convert(ch.ech.xmlns.ech_0007._5.SwissMunicipalityType value);

	@SuppressWarnings("Duplicates")
	default XMLGregorianCalendar getBirthDate(final ch.ech.xmlns.ech_0044._3.DatePartiallyKnownType partialBirthdate) {
		if (partialBirthdate.getYearMonthDay() != null) {
			return partialBirthdate.getYearMonthDay();
		} else if (partialBirthdate.getYearMonth() != null) {
			final XMLGregorianCalendar calendar = partialBirthdate.getYearMonth();
			calendar.setDay(1);
			return calendar;
		} else {
			final XMLGregorianCalendar calendar = partialBirthdate.getYear();
			calendar.setDay(1);
			calendar.setMonth(1);
			return calendar;
		}
	}

	default String getCountryIso2ByCountryId(final Integer countryId) {
		final String iso2code = PropertyProvider.getPropertyValue(String.format("countryId.%s", countryId));
		checkState(!isNullOrEmpty(iso2code), "Cannot convert countryId to iso2code code. [countryId: %s]", countryId);
		return iso2code;
	}

}

