/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.contest.ech015xv3;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0155._3.ContestType;
import ch.ech.xmlns.ech_0157._3.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.contest.ECH015xv3Mapper;
import ch.post.it.evoting.dataintegrationservice.contest.ECHv3FileContestLoader;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.util.function.Tuple2;

@Component
public class ContestEch015xv3Component {

	private final ECHv3FileContestLoader ecHv3FileContestLoader;

	public ContestEch015xv3Component(final ECHv3FileContestLoader ecHv3FileContestLoader) {

		this.ecHv3FileContestLoader = ecHv3FileContestLoader;
	}

	Function<ContestType, ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> fromECHv3Contest() {
		return ECH015xv3Mapper.INSTANCE::convert;
	}

	Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation>, ContestType>, ContestType> voteContest() {
		return Tuple2::getT2;
	}

	Function<EventInitialDelivery.ElectionInformation, ElectionInformationType> fromECHv3Election() {
		return ECH015xv3Mapper.INSTANCE::convert;
	}

	Function<ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation, VoteInformationType> fromECHv3Vote() {
		return ECH015xv3Mapper.INSTANCE::convert;
	}

	Function<String, Tuple2<Iterable<ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation>, ContestType>> loadECH0159v3() {
		return ecHv3FileContestLoader::loadECH0159v3;
	}

	Function<Tuple2<Iterable<EventInitialDelivery.ElectionInformation>, ContestType>, Iterable<EventInitialDelivery.ElectionInformation>> elections() {
		return Tuple2::getT1;
	}

	Function<Tuple2<Iterable<EventInitialDelivery.ElectionInformation>, ContestType>, ContestType> electionContest() {
		return Tuple2::getT2;
	}

	Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation>, ContestType>, Iterable<ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation>> votes() {
		return Tuple2::getT1;
	}

	Function<String, Tuple2<Iterable<EventInitialDelivery.ElectionInformation>, ContestType>> loadECH0157v3() {
		return ecHv3FileContestLoader::loadECH0157v3;
	}
}
