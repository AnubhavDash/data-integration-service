/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v42;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.register.ECH0045v42Mapper;
import ch.post.it.evoting.dataintegrationservice.register.ECHv42FileRegisterLoader;

@Component
public class RegisterEch0045v42Component {

	private final ECHv42FileRegisterLoader eCHv42FileRegisterLoader;

	public RegisterEch0045v42Component(final ECHv42FileRegisterLoader eCHv42FileRegisterLoader) {
		this.eCHv42FileRegisterLoader = eCHv42FileRegisterLoader;
	}

	public Function<String, Iterable<VotingPersonType>> loadECH0045v42() {
		return eCHv42FileRegisterLoader::loadECH0045v42;
	}

	public Function<VotingPersonType, VoterTypeWithRole> fromECHv42Voter() {
		return ECH0045v42Mapper.INSTANCE::convert;
	}

	public Function<VoterTypeWithRole, VoterTypeWithRole> checkConsistency() {
		return eCHv42FileRegisterLoader::checkConsistency;
	}
}
