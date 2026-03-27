/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import java.util.List;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.ech045voterextension.VoterExtensionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType;

public class VoterTypeWithRole extends VoterType {

	private List<VoterDomainOfInfluenceInfo> domainOfInfluenceInfos;

	private List<String> availableCountingCircleIds;

	private boolean isTestOrControlUser;

	private String diplomaticBag;

	private VoterExtensionType voterExtension;

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

	public VoterExtensionType getVoterExtension() {
		return voterExtension;
	}

	public void setVoterExtension(final VoterExtensionType voterExtension) {
		this.voterExtension = voterExtension;
	}
}
