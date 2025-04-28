/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.register.RegisterService;

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
