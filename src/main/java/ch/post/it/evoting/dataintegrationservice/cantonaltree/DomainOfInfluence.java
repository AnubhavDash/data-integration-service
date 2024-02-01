/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainOfInfluence {
	private String cantonAbbreviation;
	private String type;
	private String localId;
	private String description;
	private List<DomainOfInfluenceId> domainOfInfluenceIds = new ArrayList<>();

	public String getCode() {
		return String.format("%s-%s-%s", getCantonAbbreviation(), getType(), getLocalId());
	}
}
