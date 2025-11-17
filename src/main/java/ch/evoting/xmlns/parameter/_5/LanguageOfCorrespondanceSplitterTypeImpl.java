/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import java.util.Map;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class LanguageOfCorrespondanceSplitterTypeImpl extends LanguageOfCorrespondanceSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final Map<String, Object> context) {
		return voterTypeWithRole.getPerson().getLanguageOfCorrespondance().name();
	}
}
