/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoterDomainOfInfluenceInfo {
	private VoterDomainOfInfluence domainOfInfluence;
	private VoterCountingCircle countingCircle;
}
