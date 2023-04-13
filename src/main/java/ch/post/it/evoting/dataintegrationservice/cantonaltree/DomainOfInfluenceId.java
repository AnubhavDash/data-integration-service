/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import lombok.Data;

@Data
public class DomainOfInfluenceId {
	private String id;
	private DomainOfInfluenceId parent;
	private DomainOfInfluence relatedDomainOfInfluence;
}