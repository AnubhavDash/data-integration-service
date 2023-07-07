/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.function.Function;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterAuthorizationMapper implements Function<VoterTypeWithRole, VoterTypeWithRole> {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterTypeWithRole) {
		return voterTypeWithRole;
	}
}
