/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class LanguageOfCorrespondanceSplitterTypeImpl extends LanguageOfCorrespondanceSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		return voterTypeWithRole.getPerson().getLanguageOfCorrespondance().name();
	}
}
