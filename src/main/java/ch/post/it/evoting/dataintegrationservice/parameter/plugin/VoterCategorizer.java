/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.function.BiFunction;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class VoterCategorizer implements BiFunction<VoterTypeWithRole, String, String> {

	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		return "";
	}
}
