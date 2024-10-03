/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v40.RegisterEch0045v40;
import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v42.RegisterEch0045v42;

import reactor.core.publisher.Flux;

public abstract class RegisterPhase extends Tools {

	public static final Exports exports = new Exports();
	static RegisterEch0045v40.Exports registerEch0045v4 = RegisterEch0045v40.exports;
	static RegisterEch0045v42.Exports registerEch0045v42 = RegisterEch0045v42.exports;
	static ParameterPhase.Exports parameterPhase = ParameterPhase.exports;

	public static class Exports {
		private Flux<VoterTypeWithRole> voters;

		public Flux<VoterTypeWithRole> getVoters() {
			return voters;
		}

		public void setVoters(final Flux<VoterTypeWithRole> voters) {
			this.voters = voters;
		}
	}
}
