/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.authorization.AuthorizationService;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.AuthorizationPlugin;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuple5;

@Component
public class AuthorizationPhaseComponent {

	private final AuthorizationService authorizationService;

	public AuthorizationPhaseComponent(final AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public Function<ContestType, List<String>> mapDomainOfInfluenceIds() {
		return authorizationService::mapDomainOfInfluenceIds;
	}

	public Function<Tuple5<VoterTypeWithRole, Tuple2<Optional<CantonalTree>, List<String>>, ContestType, List<AuthorizationPlugin>, ch.evoting.xmlns.parameter._4.ContestType>, AuthorizationType> buildAuthorization() {
		return tuple -> authorizationService.build(tuple.getT1(), tuple.getT2().getT1(), tuple.getT2().getT2(), tuple.getT3(), tuple.getT4(),
				tuple.getT5());
	}

	public Consumer<Tuple2<AuthorizationsType, List<String>>> checkAuthorizations() {
		return tuple -> authorizationService.checkAuthorizations(tuple.getT1(), tuple.getT2());
	}

	public BiFunction<AuthorizationsType, AuthorizationType, AuthorizationsType> appendAuthorization() {
		return authorizationService::appendAuthorization;
	}

	public Function<AuthorizationsType, AuthorizationsType> uniqueAuthorizationNames() {
		return authorizationService::uniqueAuthorizationNames;
	}

	public Function<Tuple2<AuthorizationsType, List<String>>, AuthorizationsType> getAuthorizationsFromTuple() {
		return Tuple2::getT1;
	}

	public Function<Tuple2<Optional<CantonalTree>, List<String>>, Tuple2<Optional<CantonalTree>, List<String>>> checkDomainOfInfluences() {
		return tuple -> {
			authorizationService.checkDomainOfInfluences(tuple.getT1(), tuple.getT2());
			return tuple;
		};
	}
}
