/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import ch.post.it.evoting.dataintegrationservice.common.ReflexionUtil;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterIdPrefixerTypeImpl extends VoterIdPrefixerType {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {
		if (!StringUtil.isNullOrEmpty(getPrefixReference())) {
			final String voterId = ReflexionUtil.getFieldValue(voterType, getPrefixReference()) + voterType.getVoterIdentification();
			voterType.setVoterIdentification(voterId);
		}
		if (!StringUtil.isNullOrEmpty(getPrefixValue())) {
			final String voterId = getPrefixValue() + voterType.getVoterIdentification();
			voterType.setVoterIdentification(voterId);
		}

		return voterType;
	}
}
