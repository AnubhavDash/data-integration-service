/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

public class VoterDomainOfInfluenceInfo {
	private VoterDomainOfInfluence domainOfInfluence;
	private VoterCountingCircle countingCircle;

	public VoterDomainOfInfluence getDomainOfInfluence() {
		return domainOfInfluence;
	}

	public void setDomainOfInfluence(final VoterDomainOfInfluence domainOfInfluence) {
		this.domainOfInfluence = domainOfInfluence;
	}

	public VoterCountingCircle getCountingCircle() {
		return countingCircle;
	}

	public void setCountingCircle(final VoterCountingCircle countingCircle) {
		this.countingCircle = countingCircle;
	}
}
