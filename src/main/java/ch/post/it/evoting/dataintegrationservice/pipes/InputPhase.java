/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import reactor.core.publisher.Flux;

public abstract class InputPhase extends Tools {

	public static final Exports exports = new Exports();

	public static class Exports {
		private Flux<String> files;

		public Flux<String> getFiles() {
			return files;
		}

		public void setFiles(final Flux<String> files) {
			this.files = files;
		}
	}
}

