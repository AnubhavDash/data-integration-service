/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

public class VoterDomainOfInfluence {
	private VoterDomainOfInfluenceType type;
	private String localId;
	private String name;
	private String shortName;

	public VoterDomainOfInfluenceType getType() {
		return type;
	}

	public void setType(final VoterDomainOfInfluenceType type) {
		this.type = type;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(final String localId) {
		this.localId = localId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(final String shortName) {
		this.shortName = shortName;
	}

	public enum VoterDomainOfInfluenceType {
		CH,
		CT,
		BZ,
		MU,
		SC,
		KI,
		OG,
		KO,
		SK,
		GT,
		AN
	}
}
