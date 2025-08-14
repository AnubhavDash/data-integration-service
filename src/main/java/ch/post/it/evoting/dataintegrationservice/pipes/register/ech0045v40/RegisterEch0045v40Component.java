/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v40;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._4.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.register.ECH0045v40Mapper;
import ch.post.it.evoting.dataintegrationservice.register.ECHv40FileRegisterLoader;

@Component
public class RegisterEch0045v40Component {

	private final ECHv40FileRegisterLoader ecHv40FileRegisterLoader;

	public RegisterEch0045v40Component(final ECHv40FileRegisterLoader ecHv40FileRegisterLoader) {
		this.ecHv40FileRegisterLoader = ecHv40FileRegisterLoader;
	}

	public Function<String, Iterable<VotingPersonType>> loadECH0045v40() {
		return ecHv40FileRegisterLoader::loadECH0045v4;
	}

	public Function<VotingPersonType, VoterTypeWithRole> fromECHv40Voter() {
		return ECH0045v40Mapper.INSTANCE::convert;
	}

	public Function<VoterTypeWithRole, VoterTypeWithRole> checkConsistency() {
		return ecHv40FileRegisterLoader::checkConsistency;
	}
}
