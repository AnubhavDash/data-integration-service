/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v5;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._4.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.register.ECH0045v5Mapper;
import ch.post.it.evoting.dataintegrationservice.register.ECHv5FileRegisterLoader;

@Component
public class RegisterEch0045v5Component {

	private final ECHv5FileRegisterLoader ecHv5FileRegisterLoader;

	public RegisterEch0045v5Component(final ECHv5FileRegisterLoader ecHv5FileRegisterLoader) {
		this.ecHv5FileRegisterLoader = ecHv5FileRegisterLoader;
	}

	public Function<String, Iterable<VotingPersonType>> loadECH0045v5() {
		return ecHv5FileRegisterLoader::loadECH0045v5;
	}

	public Function<VotingPersonType, VoterTypeWithRole> fromECHv5Voter() {
		return ECH0045v5Mapper.INSTANCE::convert;
	}

	public Function<VoterTypeWithRole, VoterTypeWithRole> checkConsistency() {
		return ecHv5FileRegisterLoader::checkConsistency;
	}
}
