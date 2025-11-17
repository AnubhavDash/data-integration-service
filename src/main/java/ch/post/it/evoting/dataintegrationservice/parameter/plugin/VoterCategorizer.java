/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.Map;
import java.util.function.BiFunction;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterCategorizer implements BiFunction<VoterTypeWithRole, Map<String, Object>, String> {

	public static final String CONTEXT_PREVIOUS = "previous";
	public static final String CONTEXT_AUTHORIZATIONS= "authorizations";

	public String apply(final VoterTypeWithRole voterTypeWithRole, final Map<String, Object> context) {
		return "";
	}
}
