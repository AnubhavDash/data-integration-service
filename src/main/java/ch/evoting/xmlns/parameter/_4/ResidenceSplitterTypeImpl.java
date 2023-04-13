/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class ResidenceSplitterTypeImpl extends ResidenceSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		if (splitSwissResidentByMunicipality != null && splitSwissResidentByMunicipality
				&& voterTypeWithRole.getVoterType() == VoterTypeType.SWISSRESIDENT) {
			return StringUtil.concat(VoterTypeType.SWISSRESIDENT.toString(), "" + voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId(),
					"_");
		}
		return voterTypeWithRole.getVoterType().toString();
	}
}
