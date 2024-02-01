/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import java.util.ArrayList;
import java.util.List;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.CountingCircleFilter;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;

public class CountingCircleFilterTypeImpl extends CountingCircleFilterType implements CountingCircleFilter {
	@Override
	public List<CountingCircle> apply(final VoterTypeWithRole voterTypeWithRole, final List<CountingCircle> countingCircles) {

		final boolean municipalityFilterSet = !getMunicipalityId().isEmpty();
		final boolean municipalityFiltered = getMunicipalityId().contains(
				String.valueOf(voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId()));
		final boolean swissAbroadFilterSet = isSwissAbroad() != null;
		final boolean swissAbroadFiltered =
				isSwissAbroad() != null && ((isSwissAbroad() && voterTypeWithRole.getVoterType().equals(VoterTypeType.SWISSABROAD)) || (
						!isSwissAbroad() && !voterTypeWithRole.getVoterType().equals(VoterTypeType.SWISSABROAD)));

		if ((municipalityFilterSet && municipalityFiltered && ((swissAbroadFilterSet && swissAbroadFiltered) || (!swissAbroadFilterSet
				&& !swissAbroadFiltered))) ||
				swissAbroadFilterSet && swissAbroadFiltered && ((municipalityFilterSet && municipalityFiltered) || (!municipalityFilterSet
						&& !municipalityFiltered))) {

			return new ArrayList<>(countingCircles.stream()
					.filter(countingCircle -> countingCircle.getCcId().equalsIgnoreCase(getCountingCircleId()))
					.toList());
		} else {
			return new ArrayList<>(countingCircles);
		}
	}
}
