/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv42;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0155._5.ContestType;
import ch.ech.xmlns.ech_0157._5.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.contest.ECH015xv42Mapper;
import ch.post.it.evoting.dataintegrationservice.contest.ECHv42FileContestLoader;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.util.function.Tuple2;

@Component
public class ContestEch015xv42Component {

	private final ECHv42FileContestLoader eCHv42FileContestLoader;

	public ContestEch015xv42Component(final ECHv42FileContestLoader eCHv42FileContestLoader) {
		this.eCHv42FileContestLoader = eCHv42FileContestLoader;
	}

	public Function<String, Tuple2<Iterable<EventInitialDelivery.ElectionGroup>, ContestType>> loadECH0157v42() {
		return eCHv42FileContestLoader::loadECH0157v42;
	}

	public Function<String, Tuple2<Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation>, ContestType>> loadECH0159v42() {
		return eCHv42FileContestLoader::loadECH0159v42;
	}

	public Function<Tuple2<Iterable<EventInitialDelivery.ElectionGroup>, ContestType>, Iterable<EventInitialDelivery.ElectionGroup>> elections() {
		return Tuple2::getT1;
	}

	public Function<Tuple2<Iterable<EventInitialDelivery.ElectionGroup>, ContestType>, ContestType> electionContest() {
		return Tuple2::getT2;
	}

	public Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation>, ContestType>, Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation>> votes() {
		return Tuple2::getT1;
	}

	public Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation>, ContestType>, ContestType> voteContest() {
		return Tuple2::getT2;
	}

	public Function<EventInitialDelivery.ElectionGroup, ElectionGroupBallotType> fromECHv42Election() {
		return ECH015xv42Mapper.INSTANCE::convert;
	}

	public Function<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation, VoteInformationType> fromECHv42Vote() {
		return ECH015xv42Mapper.INSTANCE::convert;
	}

	public Function<ContestType, ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> fromECHv42Contest() {
		return ECH015xv42Mapper.INSTANCE::convert;
	}
}
