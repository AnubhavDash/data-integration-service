/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import ch.post.it.evoting.domain.xmlns.evotingconfig.ContestType;

import pipes.contest.ech015xv3.ContestEch015xv3;
import pipes.contest.ech015xv4.ContestEch015xv4;
import reactor.core.publisher.Flux;

public abstract class ContestPhase extends Tools {

	public static final Exports exports = new Exports();
	static ContestEch015xv3.Exports contestEch015xv3 = ContestEch015xv3.exports;
	static ContestEch015xv4.Exports contestEch015xv4 = ContestEch015xv4.exports;
	static ParameterPhase.Exports parameterPhase = ParameterPhase.exports;

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
