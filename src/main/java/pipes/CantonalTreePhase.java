/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;

import pipes.cantonaltree.stdv1.CantonalTreeStdv1;
import reactor.core.publisher.Flux;

public abstract class CantonalTreePhase extends Tools {
	public static final Exports exports = new Exports();
	static CantonalTreeStdv1.Exports cantonalTreeStdv1 = CantonalTreeStdv1.exports;

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
