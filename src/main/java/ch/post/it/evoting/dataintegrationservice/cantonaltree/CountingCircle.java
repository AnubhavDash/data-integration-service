/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountingCircle extends DomainOfInfluence {
	private String ccId;
	private String ccName;
	private int municipalityId;

	public CountingCircle(final String cantonAbbreviation, final String type, final String localId, final String description,
			final List<DomainOfInfluenceId> domainOfInfluenceIds, final String ccId, final String ccName, final int municipalityId) {
		this.setCantonAbbreviation(cantonAbbreviation);
		this.setType(type);
		this.setLocalId(localId);
		this.setDescription(description);
		this.setDomainOfInfluenceIds(domainOfInfluenceIds);
		this.setCcId(ccId);
		this.setCcName(ccName);
		this.setMunicipalityId(municipalityId);
	}
}
