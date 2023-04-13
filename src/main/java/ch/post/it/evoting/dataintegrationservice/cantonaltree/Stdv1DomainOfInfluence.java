/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import lombok.Data;

@Data
public class Stdv1DomainOfInfluence {
	private String id;
	private String code;
	private String description;
	private String parentId;
	private String parentCode;
	private String cantonCode;
}
