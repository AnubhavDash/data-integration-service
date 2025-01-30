/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoterDomainOfInfluence {
	private VoterDomainOfInfluenceType type;
	private String localId;
	private String name;
	private String shortName;

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
