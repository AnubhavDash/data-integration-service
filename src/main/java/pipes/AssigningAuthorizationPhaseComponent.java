/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.authorization.AuthorizationService;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.AuthorizationPlugin;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuple6;

@Component
public class AssigningAuthorizationPhaseComponent {

	private final AuthorizationService authorizationService;

	public AssigningAuthorizationPhaseComponent(final AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	Function<Tuple6<VoterTypeWithRole, AuthorizationsType, Optional<CantonalTree>, List<String>, ContestType, Tuple2<List<AuthorizationPlugin>, ch.evoting.xmlns.parameter._4.ContestType>>, VoterTypeWithRole> assignAuthorization() {
		return tuple -> authorizationService.assignAuthorization(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(),
				tuple.getT6().getT1(), tuple.getT6().getT2());
	}
}