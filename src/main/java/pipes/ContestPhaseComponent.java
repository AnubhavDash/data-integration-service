/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.contest.ContestService;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ExtendedAuthenticationKeyPlugin;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;

@Component
public class ContestPhaseComponent {
	private final ContestService contestService;

	public ContestPhaseComponent(final ContestService contestService) {
		this.contestService = contestService;
	}

	public BiFunction<List<String>, ExtendedAuthenticationKeyPlugin, List<String>> addAll() {
		return (list, plugin) -> {
			for (final String keyName : plugin.getKeyNames()) {
				if (!list.contains(keyName)) {
					list.add(keyName);
				}
			}
			return list;
		};
	}

	public Function<Tuple2<ContestType, List<String>>, ContestType> setExtendedAuthenticationKeyNames() {
		return tuple -> contestService.setExtendedAuthenticationKeyNames(tuple.getT1(), tuple.getT2());
	}

	public Consumer<Tuple2<ContestType, ch.evoting.xmlns.parameter._4.ContestType>> checkContestConsistency() {
		return tuple -> contestService.checkConsistency(tuple.getT1(), tuple.getT2());
	}

	public BiFunction<ContestType, ContestType, ContestType> checkContestSame() {
		return contestService::checkSame;
	}

	public Function<Tuple4<ContestType, List<VoteInformationType>, List<ElectionGroupBallotType>, ch.evoting.xmlns.parameter._4.ContestType>, ContestType> buildContest() {
		return tuple -> contestService.build(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4());
	}
}
