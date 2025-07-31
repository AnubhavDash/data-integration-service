/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoterDomainOfInfluenceInfo {
	private VoterDomainOfInfluence domainOfInfluence;
	private VoterCountingCircle countingCircle;
}
