/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.output.HeaderService;
import ch.post.it.evoting.domain.xmlns.evotingconfig.HeaderType;

@Component
public class HeaderPhaseComponent {

	private final HeaderService headerService;

	public HeaderPhaseComponent(HeaderService headerService) {
		this.headerService = headerService;
	}

	Function<Long, HeaderType> buildHeader() {
		return headerService::build;
	}
}
