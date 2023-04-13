/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.Optional;
import java.util.function.BiFunction;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterAuthorizationMapper implements BiFunction<VoterTypeWithRole, Optional<CantonalTree>, VoterTypeWithRole> {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterTypeWithRole, final Optional<CantonalTree> cantonalTree) {
		return voterTypeWithRole;
	}
}
