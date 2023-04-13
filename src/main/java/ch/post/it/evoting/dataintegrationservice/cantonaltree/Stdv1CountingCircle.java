/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import lombok.Data;

@Data
public class Stdv1CountingCircle {
	private String code;
	private String description;
	private int ofsNumber;
	private String cantonCode;
}
