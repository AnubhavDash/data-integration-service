/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.register.RegisterService;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

@Component
public class RegisterPhaseComponent {

	private final RegisterService registerService;

	public RegisterPhaseComponent(final RegisterService registerService) {
		this.registerService = registerService;
	}

	public Function<VoterTypeWithRole, VoterTypeWithRole> assignFrankingArea() {
		return registerService::assignFrankingArea;
	}
}
