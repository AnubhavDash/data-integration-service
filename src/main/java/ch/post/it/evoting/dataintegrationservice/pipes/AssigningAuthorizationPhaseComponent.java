/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.authorization.AuthorizationService;
import ch.post.it.evoting.dataintegrationservice.contest.ContestService;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple5;

@Component
public class AssigningAuthorizationPhaseComponent {

	private final AuthorizationService authorizationService;
	private final ContestService contestService;

	public AssigningAuthorizationPhaseComponent(final AuthorizationService authorizationService, final ContestService contestService) {
		this.authorizationService = authorizationService;
		this.contestService = contestService;
	}

	Function<Tuple5<VoterTypeWithRole, AuthorizationsType, List<String>, ContestType, ch.evoting.xmlns.parameter._4.ContestType>, VoterTypeWithRole> assignAuthorization() {
		return tuple -> authorizationService.assignAuthorization(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());
	}

	Function<Tuple2<ContestType, List<String>>, ContestType> filterContest() {
		return tuple -> contestService.filterContest(tuple.getT1(), tuple.getT2());
	}

	Function<AuthorizationsType, List<String>> usedDoisFromAuthorizations() {
		return authorizationService::getUsedDois;
	}

	public static <T> Function<Flux<T>, Flux<T>> warnIfAllCountingCircleAsTestIsConfigured() {
		AuthorizationService.warnIfAllCountingCircleAsTestIsConfigured();
		return flux -> flux;
	}

}
