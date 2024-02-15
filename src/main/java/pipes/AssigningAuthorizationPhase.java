/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;

import reactor.core.publisher.Flux;

public abstract class AssigningAuthorizationPhase extends Tools {

	public static final Exports exports = new Exports();
	static RegisterPhase.Exports registerPhase = RegisterPhase.exports;
	static AuthorizationPhase.Exports authorizationPhase = AuthorizationPhase.exports;
	static ContestPhase.Exports contestPhase = ContestPhase.exports;
	static ParameterPhase.Exports parameterPhase = ParameterPhase.exports;

	public static class Exports {
		private Flux<VoterTypeWithRole> validVoters;
		private Flux<VoterTypeWithRole> invalidVoters;
		private Flux<VoterTypeWithRole> assignedVoters;
		private Flux<ContestType> filteredContest;

		public Flux<ContestType> getFilteredContest() {
			return filteredContest;
		}

		public void setFilteredContest(final Flux<ContestType> filteredContest) {
			this.filteredContest = filteredContest;
		}

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
