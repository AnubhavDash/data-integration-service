/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.parameter.plugin.CountingCircleSetter;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

public class CountingCircleSetterTypeImpl extends CountingCircleSetterType implements CountingCircleSetter {
	@Override
	public CountingCircle apply(final VoterTypeWithRole voterTypeWithRole, final List<CountingCircle> countingCircles) {
		if (getVoterId().contains(voterTypeWithRole.getVoterIdentification())) {
			final Optional<CountingCircle> cc = countingCircles.stream().filter(e -> e.getCcId().equalsIgnoreCase(getCountingCircleId())).findFirst();
			checkState(cc.isPresent(), "Unable to find the defined counting circle. [countingCircleId: %s]", getCountingCircleId());
			return cc.get();
		} else {
			return null;
		}
	}
}
