/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import java.util.List;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType;

public class VoterTypeWithRole extends VoterType {

	private List<VoterDomainOfInfluenceInfo> domainOfInfluenceInfos;

	private List<String> availableCountingCircleIds;

	private boolean isTestOrControlUser;

	private String diplomaticBag;

	public List<VoterDomainOfInfluenceInfo> getDomainOfInfluenceInfos() {
		return domainOfInfluenceInfos;
	}

	public void setDomainOfInfluenceInfos(final List<VoterDomainOfInfluenceInfo> domainOfInfluenceInfos) {
		this.domainOfInfluenceInfos = domainOfInfluenceInfos;
	}

	public List<String> getAvailableCountingCircleIds() {
		return availableCountingCircleIds;
	}

	public void setAvailableCountingCircleIds(final List<String> availableCountingCircleIds) {
		this.availableCountingCircleIds = availableCountingCircleIds;
	}

	public boolean isTestOrControlUser() {
		return isTestOrControlUser;
	}

	public void setTestOrControlUser(final boolean testOrControlUser) {
		isTestOrControlUser = testOrControlUser;
	}

	public String getDiplomaticBag() {
		return diplomaticBag;
	}

	public void setDiplomaticBag(final String diplomaticBag) {
		this.diplomaticBag = diplomaticBag;
	}
}
