/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

import pipes.register.ech0045v4.RegisterEch0045v4;
import reactor.core.publisher.Flux;

public abstract class RegisterPhase extends Tools {

	public static final Exports exports = new Exports();
	static RegisterEch0045v4.Exports registerEch0045v4 = RegisterEch0045v4.exports;
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
