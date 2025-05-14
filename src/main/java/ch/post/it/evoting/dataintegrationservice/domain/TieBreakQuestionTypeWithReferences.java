/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TieBreakQuestionType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TieBreakQuestionTypeWithReferences extends TieBreakQuestionType {
	private String referencedQuestion1;
	private String referencedQuestion2;
}
