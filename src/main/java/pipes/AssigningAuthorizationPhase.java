/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

import reactor.core.publisher.Flux;

public abstract class AssigningAuthorizationPhase extends Tools {

	public static final Exports exports = new Exports();
	static RegisterPhase.Exports registerPhase = RegisterPhase.exports;
	static AuthorizationPhase.Exports authorizationPhase = AuthorizationPhase.exports;
	static CantonalTreePhase.Exports cantonalTreePhase = CantonalTreePhase.exports;
	static ContestPhase.Exports contestPhase = ContestPhase.exports;
	static ParameterPhase.Exports parameterPhase = ParameterPhase.exports;

	public static class Exports {
		private Flux<VoterTypeWithRole> validVoters;
		private Flux<VoterTypeWithRole> invalidVoters;
		private Flux<VoterTypeWithRole> assignedVoters;

		public Flux<VoterTypeWithRole> getValidVoters() {
			return validVoters;
		}

		public void setValidVoters(final Flux<VoterTypeWithRole> validVoters) {
			this.validVoters = validVoters;
		}

		public Flux<VoterTypeWithRole> getInvalidVoters() {
			return invalidVoters;
		}

		public void setInvalidVoters(final Flux<VoterTypeWithRole> invalidVoters) {
			this.invalidVoters = invalidVoters;
		}

		public Flux<VoterTypeWithRole> getAssignedVoters() {
			return assignedVoters;
		}

		public void setAssignedVoters(final Flux<VoterTypeWithRole> assignedVoters) {
			this.assignedVoters = assignedVoters;
		}
	}
}
