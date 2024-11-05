/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;

public class ResidenceSplitterTypeImpl extends ResidenceSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		if (splitSwissResidentByMunicipality != null && splitSwissResidentByMunicipality
				&& voterTypeWithRole.getVoterType() == VoterTypeType.SWISSRESIDENT) {
			return StringUtil.concat(VoterTypeType.SWISSRESIDENT.toString(), "" + voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId(),
					"_");
		}
		if (splitSwissAbroadByMunicipality != null && splitSwissAbroadByMunicipality
				&& voterTypeWithRole.getVoterType() == VoterTypeType.SWISSABROAD) {
			return StringUtil.concat(VoterTypeType.SWISSABROAD.toString(), "" + voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId(),
					"_");
		}
		return voterTypeWithRole.getVoterType().toString();
	}
}
