/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv40;

import ch.post.it.evoting.dataintegrationservice.pipes.InputPhase;
import ch.post.it.evoting.dataintegrationservice.pipes.Tools;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.core.publisher.Flux;

public abstract class ContestEch015xv40 extends Tools {

	public static final Exports exports = new Exports();
	static InputPhase.Exports inputPhase = InputPhase.exports;

	public static class Exports {
		private  Flux<ContestType> contests;
		private Flux<ElectionGroupBallotType> elections;
		private Flux<VoteInformationType> votes;

		public Flux<ContestType> getContests() {
			return contests;
		}

		public void setContests(final Flux<ContestType> contests) {
			this.contests = contests;
		}

		public Flux<ElectionGroupBallotType> getElections() {
			return elections;
		}

		public void setElections(final Flux<ElectionGroupBallotType> elections) {
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
