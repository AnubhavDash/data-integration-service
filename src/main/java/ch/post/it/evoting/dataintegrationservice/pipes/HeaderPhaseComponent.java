/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.output.HeaderService;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.HeaderType;

@Component
public class HeaderPhaseComponent {

	private final HeaderService headerService;

	public HeaderPhaseComponent(final HeaderService headerService) {
		this.headerService = headerService;
	}

	Function<Long, HeaderType> buildHeader() {
		return headerService::build;
	}
}
