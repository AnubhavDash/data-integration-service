/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getProperty;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ech.xmlns.ech_0045._6.ElectoralAddressType;
import ch.ech.xmlns.ech_0045._6.EmailType;
import ch.ech.xmlns.ech_0045._6.ForeignerPersonType;
import ch.ech.xmlns.ech_0045._6.LanguageType;
import ch.ech.xmlns.ech_0045._6.SwissPersonType;
import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterCountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectronicAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PhysicalAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.SexType;

@Mapper(uses = XsTokenMapper.class)
public interface ECH0045v42Mapper {
	Logger LOGGER = LoggerFactory.getLogger(ECH0045v42Mapper.class);
	ECH0045v42Mapper INSTANCE = Mappers.getMapper(ECH0045v42Mapper.class);

	@SuppressWarnings("Duplicates")
	default VoterTypeWithRole convert(final VotingPersonType voter) {
		final VoterTypeWithRole voterType = new VoterTypeWithRole();

		if (voter.getPerson().getSwissAbroad() != null) {
			appendAbroadVoter(voterType, voter, voter.getPerson().getSwissAbroad().getSwissAbroadPerson());

			//ResidenceCountryId calculation
			calculateResidenceCountry(voter, voterType);

		} else if (voter.getPerson().getSwiss() != null) {
			appendSwissVoter(voterType, voter, voter.getPerson().getSwiss().getSwissDomesticPerson());
		} else if (voter.getPerson().getForeigner() != null) {
			appendForeignerVoter(voterType, voter, voter.getPerson().getForeigner().getForeignerPerson());
		} else {
			throw new IllegalArgumentException("Unimplemented personType");
		}

		if (voter.getDeliveryAddress() != null) {
			voterType.getPerson().setPhysicalAddress(convert(voter.getDeliveryAddress()));
		} else if (voter.getElectoralAddress() != null) {
			voterType.getPerson().setPhysicalAddress(convert(voter.getElectoralAddress()));
		} else {
			throw new IllegalArgumentException(String.format("Unable to retrieve an address for the voter (%s)", voterType.getVoterIdentification()));
		}

		voterType.setDomainOfInfluenceInfos(new LinkedList<>(voter.getDomainOfInfluenceInfo().stream().map(domainOfInfluenceInfo ->
		{
			final VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();

			final VoterDomainOfInfluence voterDomainOfInfluence = new VoterDomainOfInfluence();
			voterDomainOfInfluence.setName(domainOfInfluenceInfo.getDomainOfInfluence().getDomainOfInfluenceName());
			voterDomainOfInfluence.setShortName(domainOfInfluenceInfo.getDomainOfInfluence().getDomainOfInfluenceShortname());
			voterDomainOfInfluence.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(
					domainOfInfluenceInfo.getDomainOfInfluence().getDomainOfInfluenceType().toString()));
			voterDomainOfInfluence.setLocalId(domainOfInfluenceInfo.getDomainOfInfluence().getDomainOfInfluenceIdentification());
			voterDomainOfInfluenceInfo.setDomainOfInfluence(voterDomainOfInfluence);

			if (domainOfInfluenceInfo.getCountingCircle() != null) {
				final VoterCountingCircle cc = new VoterCountingCircle();
				cc.setId(domainOfInfluenceInfo.getCountingCircle().getCountingCircleId());
				cc.setName(domainOfInfluenceInfo.getCountingCircle().getCountingCircleName());
				voterDomainOfInfluenceInfo.setCountingCircle(cc);
			}

			return voterDomainOfInfluenceInfo;
		}).toList()));

		return voterType;
	}

	private void calculateResidenceCountry(final VotingPersonType voter, final VoterTypeWithRole voterType) {
		String iso2code = voter.getPerson().getSwissAbroad().getResidenceCountry().getCountryIdISO2();

		if (isNullOrEmpty(iso2code)) {
			final Integer countryId = voter.getPerson().getSwissAbroad().getResidenceCountry().getCountryId();
			checkState(countryId != null, "voter '%s': neither iso2code, nor countryId is provided",
					voter.getPerson().getSwissAbroad().getSwissAbroadPerson().getPersonIdentification().getLocalPersonId()
							.getPersonId());
			// conversion
			voterType.getPerson().setResidenceCountryId(getCountryIso2ByCountryId(countryId));
		} else {
			// direct setting
			if (iso2code.equalsIgnoreCase("XZ")) {
				iso2code = "XK";
			}
			voterType.getPerson().setResidenceCountryId(iso2code);
		}
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

	@Mapping(target = "person.officialName", source = "swissAbroadPerson.personIdentification.officialName")
	@Mapping(target = "person.firstName", source = "swissAbroadPerson.personIdentification.firstName")
	@Mapping(target = "person.sex", source = "swissAbroadPerson.personIdentification.sex")
	@Mapping(target = "person.dateOfBirth", source = "swissAbroadPerson.personIdentification.dateOfBirth")
	@Mapping(target = "person.languageOfCorrespondance", source = "swissAbroadPerson.languageOfCorrespondance")
	@Mapping(target = "person.residenceCountryId", ignore = true)
	@Mapping(target = "person.municipality", source = "v.person.swissAbroad.municipality")
	@Mapping(target = "voterIdentification", source = "swissAbroadPerson.personIdentification.localPersonId.personId", qualifiedBy = XsTokenMapper.XsTokenMapping.class)
	@Mapping(target = "sex", source = "swissAbroadPerson.personIdentification.sex")
	@Mapping(target = "authorization", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "person.electronicAddress", source = "v.email")
	@Mapping(target = "voterType", expression = "java( ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType.SWISSABROAD )")
	void appendAbroadVoter(
			@MappingTarget
			VoterTypeWithRole voter, VotingPersonType v, SwissPersonType swissAbroadPerson);

	@Mapping(target = "person.officialName", source = "swissDomesticPerson.personIdentification.officialName")
	@Mapping(target = "person.firstName", source = "swissDomesticPerson.personIdentification.firstName")
	@Mapping(target = "person.sex", source = "swissDomesticPerson.personIdentification.sex")
	@Mapping(target = "person.dateOfBirth", source = "swissDomesticPerson.personIdentification.dateOfBirth")
	@Mapping(target = "person.languageOfCorrespondance", source = "swissDomesticPerson.languageOfCorrespondance")
	@Mapping(target = "person.residenceCountryId", constant = "CH")
	@Mapping(target = "person.municipality", source = "v.person.swiss.municipality")
	@Mapping(target = "voterIdentification", source = "swissDomesticPerson.personIdentification.localPersonId.personId", qualifiedBy = XsTokenMapper.XsTokenMapping.class)
	@Mapping(target = "sex", source = "swissDomesticPerson.personIdentification.sex")
	@Mapping(target = "authorization", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "person.electronicAddress", source = "v.email")
	@Mapping(target = "voterType", expression = "java( ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType.SWISSRESIDENT )")
	void appendSwissVoter(
			@MappingTarget
			VoterTypeWithRole voter, VotingPersonType v, SwissPersonType swissDomesticPerson);

	@Mapping(target = "person.officialName", source = "foreignerPerson.personIdentification.officialName")
	@Mapping(target = "person.firstName", source = "foreignerPerson.personIdentification.firstName")
	@Mapping(target = "person.sex", source = "foreignerPerson.personIdentification.sex")
	@Mapping(target = "person.dateOfBirth", source = "foreignerPerson.personIdentification.dateOfBirth")
	@Mapping(target = "person.languageOfCorrespondance", source = "foreignerPerson.languageOfCorrespondance")
	@Mapping(target = "person.residenceCountryId", constant = "CH")
	@Mapping(target = "person.municipality", source = "v.person.foreigner.municipality")
	@Mapping(target = "voterIdentification", source = "foreignerPerson.personIdentification.localPersonId.personId", qualifiedBy = XsTokenMapper.XsTokenMapping.class)
	@Mapping(target = "sex", source = "foreignerPerson.personIdentification.sex")
	@Mapping(target = "authorization", ignore = true)
	@Mapping(target = "extendedAuthenticationKeys", ignore = true)
	@Mapping(target = "person.electronicAddress", source = "v.email")
	@Mapping(target = "voterType", expression = "java( ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType.FOREIGNER )")
	void appendForeignerVoter(
			@MappingTarget
			VoterTypeWithRole voter, VotingPersonType v, ForeignerPersonType foreignerPerson);

	@SuppressWarnings("Duplicates")
	default PhysicalAddressType convert(final ch.ech.xmlns.ech_0010._6.PersonMailAddressType echAddress) {
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

	default PhysicalAddressType convert(final ElectoralAddressType electoralAddress) {
		//choice swissZip or foreignZip or nothing
		final PhysicalAddressType address = new PhysicalAddressType();
		appendPhysicalAddress(address, electoralAddress);
		if (electoralAddress.getAddressInformation().getSwissZipCode() != null) {
			address.setZipCode(electoralAddress.getAddressInformation().getSwissZipCode().toString());
		} else if (!isNullOrEmpty(electoralAddress.getAddressInformation().getForeignZipCode())) {
			address.setZipCode(electoralAddress.getAddressInformation().getForeignZipCode());
		}

		//manage address line 1 and 2
		final String line1 = electoralAddress.getAddressInformation().getAddressLine1();
		final String line2 = electoralAddress.getAddressInformation().getAddressLine2();
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
		if (!isNullOrEmpty(electoralAddress.getAddressInformation().getLocality())) {
			address.setBelowTownLine(List.of(electoralAddress.getAddressInformation().getLocality()));
		}

		if ("XZ".equalsIgnoreCase(address.getCountry())) {
			address.setCountry("XK");
		}

		if (("US".equalsIgnoreCase(address.getCountry()) || "CA".equalsIgnoreCase(address.getCountry())) && !isNullOrEmpty(
				electoralAddress.getAddressInformation().getLocality())) {
			address.setTown(address.getTown() + " " + electoralAddress.getAddressInformation().getLocality());
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
	@Mapping(target = "country", source = "addressInformation.country.countryIdISO2")
	@Mapping(target = "countryNameShort", source = "addressInformation.country.countryNameShort")
	@Mapping(target = "belowTitleLine", ignore = true)
	@Mapping(target = "belowNameLine", ignore = true)
	@Mapping(target = "belowStreetLine", ignore = true)
	@Mapping(target = "belowPostOfficeBoxLine", ignore = true)
	@Mapping(target = "belowTownLine", ignore = true)
	@Mapping(target = "belowCountryLine", ignore = true)
	@Mapping(target = "frankingArea", ignore = true)
	void appendPhysicalAddress(
			@MappingTarget
			PhysicalAddressType address, ch.ech.xmlns.ech_0010._6.PersonMailAddressType echAddress);

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
	@Mapping(target = "country", source = "addressInformation.country.countryIdISO2")
	@Mapping(target = "countryNameShort", source = "addressInformation.country.countryNameShort")
	@Mapping(target = "belowTitleLine", ignore = true)
	@Mapping(target = "belowNameLine", ignore = true)
	@Mapping(target = "belowStreetLine", ignore = true)
	@Mapping(target = "belowPostOfficeBoxLine", ignore = true)
	@Mapping(target = "belowTownLine", ignore = true)
	@Mapping(target = "belowCountryLine", ignore = true)
	@Mapping(target = "frankingArea", ignore = true)
	void appendPhysicalAddress(
			@MappingTarget
			PhysicalAddressType address, ch.ech.xmlns.ech_0045._6.ElectoralAddressType echAddress);

	@Mapping(target = "electronicAddressType", constant = "1")
	@Mapping(target = "electronicAddressValue", source = "emailAddress")
	ElectronicAddressType convert(EmailType value);

	default List<ElectronicAddressType> convertToList(final EmailType email) {
		final ElectronicAddressType addressType = convert(email);
		return addressType == null ? Collections.emptyList() : Collections.singletonList(addressType);
	}

	LanguageType convert(LanguageType value);

	PersonType.Municipality convert(ch.ech.xmlns.ech_0007._6.SwissMunicipalityType value);

	@SuppressWarnings("Duplicates")
	default XMLGregorianCalendar getBirthDate(final ch.ech.xmlns.ech_0044._4.DatePartiallyKnownType partialBirthdate) {
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
		final String iso2code = getProperty(String.format("countryId.%s", countryId), String.class);
		checkState(!isNullOrEmpty(iso2code), "Cannot convert countryId to iso2code code (%s)", countryId);
		return iso2code;
	}

}

