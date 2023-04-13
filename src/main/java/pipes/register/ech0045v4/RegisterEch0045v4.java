/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.register.ech0045v4;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

import pipes.InputPhase;
import pipes.Tools;
import reactor.core.publisher.Flux;

public abstract class RegisterEch0045v4 extends Tools {

	public static final Exports exports = new Exports();

	static InputPhase.Exports inputPhase = InputPhase.exports;

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
