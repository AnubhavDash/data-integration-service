/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class LanguageOfCorrespondanceSplitterTypeImpl extends LanguageOfCorrespondanceSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		return voterTypeWithRole.getPerson().getLanguageOfCorrespondance().name();
	}
}
