/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.function.BiFunction;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterCategorizer implements BiFunction<VoterTypeWithRole, String, String> {

	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		return "";
	}
}
