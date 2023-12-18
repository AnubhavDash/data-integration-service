/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.List;
import java.util.function.BiFunction;

import ch.post.it.evoting.dataintegrationservice.domain.VoterCountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public interface CountingCircleSetter extends BiFunction<VoterTypeWithRole, List<VoterCountingCircle>, VoterCountingCircle> {

	@Override
	VoterCountingCircle apply(VoterTypeWithRole voterTypeWithRole, List<VoterCountingCircle> countingCircles);
}
