/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv40;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0155._4.ContestType;
import ch.ech.xmlns.ech_0157._4.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.contest.ECH015xv40Mapper;
import ch.post.it.evoting.dataintegrationservice.contest.ECHv40FileContestLoader;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.util.function.Tuple2;

@Component
public class ContestEch015xv40Component {

	private final ECHv40FileContestLoader ecHv40FileContestLoader;

	public ContestEch015xv40Component(final ECHv40FileContestLoader ecHv40FileContestLoader) {
		this.ecHv40FileContestLoader = ecHv40FileContestLoader;
	}

	public Function<String, Tuple2<Iterable<EventInitialDelivery.ElectionGroupBallot>, ContestType>> loadECH0157v40() {
		return ecHv40FileContestLoader::loadECH0157v40;
	}

	public Function<String, Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>> loadECH0159v40() {
		return ecHv40FileContestLoader::loadECH0159v40;
	}

	public Function<Tuple2<Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>, ContestType>, Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>> elections() {
		return Tuple2::getT1;
	}

	public Function<Tuple2<Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>, ContestType>, ContestType> electionContest() {
		return Tuple2::getT2;
	}

	public Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>, Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>> votes() {
		return Tuple2::getT1;
	}

	public Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>, ContestType> voteContest() {
		return Tuple2::getT2;
	}

	public Function<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot, ElectionGroupBallotType> fromECHv40Election() {
		return ECH015xv40Mapper.INSTANCE::convert;
	}

	public Function<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation, VoteInformationType> fromECHv40Vote() {
		return ECH015xv40Mapper.INSTANCE::convert;
	}

	public Function<ContestType, ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> fromECHv40Contest() {
		return ECH015xv40Mapper.INSTANCE::convert;
	}
}
