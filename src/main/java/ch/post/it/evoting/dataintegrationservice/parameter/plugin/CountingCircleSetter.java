/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.List;
import java.util.function.BiFunction;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public interface CountingCircleSetter extends BiFunction<VoterTypeWithRole, List<CountingCircle>, CountingCircle> {

	@Override
	CountingCircle apply(VoterTypeWithRole voterTypeWithRole, List<CountingCircle> countingCircles);
}
