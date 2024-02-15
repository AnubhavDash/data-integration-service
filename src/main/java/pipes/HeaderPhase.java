/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.HeaderType;

import reactor.core.publisher.Flux;

public abstract class HeaderPhase extends Tools {
	public static final Exports exports = new Exports();
	static AssigningAuthorizationPhase.Exports assigningAuthorizationPhase = AssigningAuthorizationPhase.exports;

	public static class Exports {
		private Flux<HeaderType> header;

		public Flux<HeaderType> getHeader() {
			return header;
		}

		public void setHeader(final Flux<HeaderType> header) {
			this.header = header;
		}
	}
}
