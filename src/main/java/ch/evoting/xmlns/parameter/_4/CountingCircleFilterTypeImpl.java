/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.CountingCircleFilter;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;

public class CountingCircleFilterTypeImpl extends CountingCircleFilterType implements CountingCircleFilter {
	@Override
	public List<CountingCircle> apply(final VoterTypeWithRole voterTypeWithRole, final List<CountingCircle> countingCircles) {

		List<CountingCircle> result = new ArrayList<>(countingCircles);

		final boolean municipalityFilterSet = !getMunicipalityId().isEmpty();
		final boolean municipalityFiltered = getMunicipalityId().contains("" + voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId());
		final boolean swissAbroadFilterSet = isSwissAbroad() != null;
		final boolean swissAbroadFiltered =
				isSwissAbroad() != null && ((isSwissAbroad() && voterTypeWithRole.getVoterType().equals(VoterTypeType.SWISSABROAD)) || (
						!isSwissAbroad() && !voterTypeWithRole.getVoterType().equals(VoterTypeType.SWISSABROAD)));

		if ((municipalityFilterSet && municipalityFiltered && ((swissAbroadFilterSet && swissAbroadFiltered) || (!swissAbroadFilterSet
				&& !swissAbroadFiltered))) ||
				swissAbroadFilterSet && swissAbroadFiltered && ((municipalityFilterSet && municipalityFiltered) || (!municipalityFilterSet
						&& !municipalityFiltered))) {

			result = result.stream()
					.filter(countingCircle -> countingCircle.getCcId().equalsIgnoreCase(getCountingCircleId()))
					.collect(Collectors.toList());
		}

		return result;
	}
}
