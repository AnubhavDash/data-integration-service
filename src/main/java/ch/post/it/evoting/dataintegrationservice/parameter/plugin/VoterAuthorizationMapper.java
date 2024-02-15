/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.function.UnaryOperator;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterAuthorizationMapper implements UnaryOperator<VoterTypeWithRole> {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterTypeWithRole) {
		return voterTypeWithRole;
	}
}
