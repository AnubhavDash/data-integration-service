/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.FrankingAreaType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.PhysicalAddressType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;

@Service
public class RegisterService {

	private final Map<String, FrankingAreaType> frankingAreaCache = new HashMap<>();
	private final Map<String, String> diplomaticBagCache = new HashMap<>();
	private final Set<String> knownVoters = new HashSet<>();

	private final Environment environment;

	public RegisterService(final Environment environment) {
		this.environment = environment;
	}

	public void checkConsistency(final VoterTypeWithRole voterTypeWithRole) {
		checkState(!knownVoters.contains(voterTypeWithRole.getVoterIdentification()),
				"Duplicate voter identification found. [voterIdentification: %s]",
				voterTypeWithRole.getVoterIdentification());

		knownVoters.add(voterTypeWithRole.getVoterIdentification());

		checkState(voterTypeWithRole.getExtendedAuthenticationKeys() == null || voterTypeWithRole.getExtendedAuthenticationKeys()
						.getExtendedAuthenticationKey().stream().noneMatch(a -> StringUtil.isNullOrEmpty(a.getValue())),
				"Voter (%s) has an extended authentication key without value", voterTypeWithRole.getVoterIdentification());
	}

	public VoterTypeWithRole assignFrankingArea(final VoterTypeWithRole voterTypeWithRole) {
		if (voterTypeWithRole.getPerson().getPhysicalAddress() != null
				&& voterTypeWithRole.getPerson().getPhysicalAddress().getFrankingArea() == null) {
			FrankingAreaType frankingArea = FrankingAreaType.SWITZERLAND;
			if (voterTypeWithRole.getVoterType() == VoterTypeType.SWISSABROAD) {
				frankingArea = getFrankingAreaByCountryIso2(voterTypeWithRole.getPerson().getPhysicalAddress().getCountry());

				if (isContaining(voterTypeWithRole.getPerson().getPhysicalAddress(), "DFAE", "EDA", "CICR", "SWISSCOY")) {
					frankingArea = FrankingAreaType.SWITZERLAND;
				}
			}
			voterTypeWithRole.getPerson().getPhysicalAddress().setFrankingArea(frankingArea);

			voterTypeWithRole.setDiplomaticBag(getDiplomaticBagByCountryIso2(voterTypeWithRole.getPerson().getPhysicalAddress().getCountry()));
		}
		return voterTypeWithRole;
	}

	private boolean isContaining(final PhysicalAddressType physicalAddress, final String... label) {
		final String belowStreets = String.join(" ", physicalAddress.getBelowStreetLine());
		final String val = StringUtil.concat(physicalAddress.getStreet(), belowStreets, " ").toUpperCase();
		return Arrays.stream(label).map(l -> String.format(" %s", l)).anyMatch(val::contains);
	}

	private FrankingAreaType getFrankingAreaByCountryIso2(final String countryIso2) {
		return frankingAreaCache.computeIfAbsent(countryIso2, key -> {
			final String countryOFS = environment.getProperty(String.format("iso2code.%s", countryIso2.toUpperCase()));
			checkState(!StringUtil.isNullOrEmpty(countryOFS), "Cannot convert country ISO code to OFS code (%s)", countryIso2);
			return FrankingAreaType.fromValue(environment.getProperty(String.format("groupOFS.%s", countryOFS),
					environment.getProperty("groupOFS.default", String.class)));
		});
	}

	private String getDiplomaticBagByCountryIso2(final String countryIso2) {
		return diplomaticBagCache.computeIfAbsent(countryIso2,
				key -> environment.getProperty(String.format("diplomaticBag.%s", countryIso2), String.class));
	}
}
