/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingconfig;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TieBreakQuestionTypeWithReferences extends TieBreakQuestionType {
	private String referencedQuestion1;
	private String referencedQuestion2;
}
