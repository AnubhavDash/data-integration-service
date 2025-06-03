/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv40.ContestEch015xv40;
import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv42.ContestEch015xv42;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;

import reactor.core.publisher.Flux;

public abstract class ContestPhase extends Tools {

	public static final Exports exports = new Exports();
	public static final ContestEch015xv40.Exports contestEch015xv40 = ContestEch015xv40.exports;
	public static final ContestEch015xv42.Exports contestEch015xv42 = ContestEch015xv42.exports;
	public static final ParameterPhase.Exports parameterPhase = ParameterPhase.exports;

	public static class Exports {
		private Flux<ContestType> contest;

		public Flux<ContestType> getContest() {
			return contest;
		}

		public void setContest(final Flux<ContestType> contest) {
			this.contest = contest;
		}
	}
}
