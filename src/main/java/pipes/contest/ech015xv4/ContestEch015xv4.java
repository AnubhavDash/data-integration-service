/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.contest.ech015xv4;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import pipes.InputPhase;
import pipes.Tools;
import reactor.core.publisher.Flux;

public abstract class ContestEch015xv4 extends Tools {

	public static Exports exports = new Exports();
	static InputPhase.Exports inputPhase = InputPhase.exports;

	public static class Exports {
		public static Flux<ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> contests;
		public static Flux<ElectionInformationType> elections;
		public static Flux<VoteInformationType> votes;
	}
}
