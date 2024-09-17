/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv5;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0155._5.ContestType;
import ch.ech.xmlns.ech_0157._5.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.contest.ECH015xv5Mapper;
import ch.post.it.evoting.dataintegrationservice.contest.ECHv5FileContestLoader;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.util.function.Tuple2;

@Component
public class ContestEch015xv5Component {

	private final ECHv5FileContestLoader ecHv5FileContestLoader;

	public ContestEch015xv5Component(final ECHv5FileContestLoader ecHv5FileContestLoader) {
		this.ecHv5FileContestLoader = ecHv5FileContestLoader;
	}

	Function<String, Tuple2<Iterable<EventInitialDelivery.ElectionGroup>, ContestType>> loadECH0157v5() {
		return ecHv5FileContestLoader::loadECH0157v5;
	}

	Function<String, Tuple2<Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation>, ContestType>> loadECH0159v5() {
		return ecHv5FileContestLoader::loadECH0159v5;
	}

	Function<Tuple2<Iterable<EventInitialDelivery.ElectionGroup>, ContestType>, Iterable<EventInitialDelivery.ElectionGroup>> elections() {
		return Tuple2::getT1;
	}

	Function<Tuple2<Iterable<EventInitialDelivery.ElectionGroup>, ContestType>, ContestType> electionContest() {
		return Tuple2::getT2;
	}

	Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>, Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>> votes() {
		return Tuple2::getT1;
	}

	Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>, ContestType> voteContest() {
		return Tuple2::getT2;
	}

	Function<EventInitialDelivery.ElectionGroup, ElectionGroupBallotType> fromECHv5Election() {
		return ECH015xv5Mapper.INSTANCE::convert;
	}

	Function<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation, VoteInformationType> fromECHv5Vote() {
		return ECH015xv5Mapper.INSTANCE::convert;
	}

	Function<ContestType, ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> fromECHv5Contest() {
		return ECH015xv5Mapper.INSTANCE::convert;
	}
}
