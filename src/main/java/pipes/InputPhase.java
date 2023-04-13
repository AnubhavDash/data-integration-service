/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import reactor.core.publisher.Flux;

public abstract class InputPhase extends Tools {

	public static Exports exports = new Exports();

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

