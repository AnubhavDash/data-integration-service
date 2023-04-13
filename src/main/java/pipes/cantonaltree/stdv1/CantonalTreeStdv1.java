/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.cantonaltree.stdv1;

import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;

import pipes.InputPhase;
import pipes.Tools;
import reactor.core.publisher.Flux;

public abstract class CantonalTreeStdv1 extends Tools {
	public static final Exports exports = new Exports();
	static InputPhase.Exports inputPhase = InputPhase.exports;

	public static class Exports {
		private Flux<Optional<CantonalTree>> cantonalTree;

		public Flux<Optional<CantonalTree>> getCantonalTree() {
			return cantonalTree;
		}

		public void setCantonalTree(
				final Flux<Optional<CantonalTree>> cantonalTree) {
			this.cantonalTree = cantonalTree;
		}
	}
}
