/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.function.UnaryOperator;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class VoterWithRoleMapper implements UnaryOperator<VoterTypeWithRole> {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {
		return voterType;
	}
}
