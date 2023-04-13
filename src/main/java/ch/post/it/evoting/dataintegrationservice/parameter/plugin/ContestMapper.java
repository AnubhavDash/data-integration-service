/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.function.UnaryOperator;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;

public class ContestMapper implements UnaryOperator<ContestType> {
	@Override
	public ContestType apply(final ContestType contestType) {
		return contestType;
	}
}
