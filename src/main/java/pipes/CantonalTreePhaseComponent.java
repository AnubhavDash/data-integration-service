/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTreeService;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;

@Component
public class CantonalTreePhaseComponent {
	private final CantonalTreeService cantonalTreeService;

	public CantonalTreePhaseComponent(final CantonalTreeService cantonalTreeService) {
		this.cantonalTreeService = cantonalTreeService;
	}

	public Consumer<Optional<CantonalTree>> checkConsistency() {
		return cantonalTreeService::checkConsistency;
	}
}
