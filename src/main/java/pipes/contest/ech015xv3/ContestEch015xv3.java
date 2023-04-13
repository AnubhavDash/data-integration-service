/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.contest.ech015xv3;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import pipes.InputPhase;
import pipes.Tools;
import reactor.core.publisher.Flux;

public abstract class ContestEch015xv3 extends Tools {

	public static final Exports exports = new Exports();
	static InputPhase.Exports inputPhase = InputPhase.exports;

	public static class Exports {
		private Flux<ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> contests;
		private Flux<ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType> elections;
		private Flux<ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType> votes;

		public Flux<ContestType> getContests() {
			return contests;
		}

		public void setContests(final Flux<ContestType> contests) {
			this.contests = contests;
		}

		public Flux<ElectionInformationType> getElections() {
			return elections;
		}

		public void setElections(final Flux<ElectionInformationType> elections) {
			this.elections = elections;
		}

		public Flux<VoteInformationType> getVotes() {
			return votes;
		}

		public void setVotes(final Flux<VoteInformationType> votes) {
			this.votes = votes;
		}
	}
}
