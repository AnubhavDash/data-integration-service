/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;

public class TieBreakQuestionTypeWithReferences extends TieBreakQuestionType {
	private String referencedQuestion1;
	private String referencedQuestion2;

	public String getReferencedQuestion1() {
		return referencedQuestion1;
	}

	public void setReferencedQuestion1(final String referencedQuestion1) {
		this.referencedQuestion1 = referencedQuestion1;
	}

	public String getReferencedQuestion2() {
		return referencedQuestion2;
	}

	public void setReferencedQuestion2(final String referencedQuestion2) {
		this.referencedQuestion2 = referencedQuestion2;
	}
}
