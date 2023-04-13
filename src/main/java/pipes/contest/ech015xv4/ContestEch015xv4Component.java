/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.contest.ech015xv4;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0155._4.ContestType;
import ch.ech.xmlns.ech_0157._4.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.contest.ECH015xv4Mapper;
import ch.post.it.evoting.dataintegrationservice.contest.ECHv4FileContestLoader;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.util.function.Tuple2;

@Component
public class ContestEch015xv4Component {

	private final ECHv4FileContestLoader ecHv4FileContestLoader;

	public ContestEch015xv4Component(final ECHv4FileContestLoader ecHv4FileContestLoader) {
		this.ecHv4FileContestLoader = ecHv4FileContestLoader;
	}
	 Function<String, Tuple2<Iterable<EventInitialDelivery.ElectionGroupBallot>, ContestType>> loadECH0157v4 (){
		 return ecHv4FileContestLoader::loadECH0157v4;
	 }
	 Function<String, Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>> loadECH0159v4 (){
		 return  ecHv4FileContestLoader::loadECH0159v4;
	 }
	 Function<Tuple2<Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>, ContestType>, Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>> elections (){
		 return  Tuple2::getT1;
	 }
	 Function<Tuple2<Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>, ContestType>, ContestType> electionContest (){
		 return Tuple2::getT2;
	 }
	 Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>, Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>> votes (){
		 return Tuple2::getT1;
	 }
	 Function<Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType>, ContestType> voteContest (){
		 return Tuple2::getT2;
	 }
	 Function<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot, ElectionInformationType> fromECHv4Election (){
		 return ECH015xv4Mapper.INSTANCE::convert;
	 }
	 Function<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation, VoteInformationType> fromECHv4Vote (){
		 return ECH015xv4Mapper.INSTANCE::convert;
	 }
	 Function<ContestType, ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType> fromECHv4Contest (){
		 return ECH015xv4Mapper.INSTANCE::convert;
	 }
}
