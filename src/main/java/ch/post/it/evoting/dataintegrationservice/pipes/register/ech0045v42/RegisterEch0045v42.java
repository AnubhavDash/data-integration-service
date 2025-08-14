/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v42;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.pipes.InputPhase;
import ch.post.it.evoting.dataintegrationservice.pipes.Tools;

import reactor.core.publisher.Flux;

public abstract class RegisterEch0045v42 extends Tools {

	public static final Exports exports = new Exports();

	public static final InputPhase.Exports inputPhase = InputPhase.exports;

	public static class Exports {
		private Flux<VoterTypeWithRole> voters;

		public Flux<VoterTypeWithRole> getVoters() {
			return this.voters;
		}

		public void setVoters(final Flux<VoterTypeWithRole> voters) {
			this.voters = voters;
		}
	}
}
