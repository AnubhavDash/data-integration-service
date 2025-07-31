/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v4;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._4.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.register.ECH0045v4Mapper;
import ch.post.it.evoting.dataintegrationservice.register.ECHv4FileRegisterLoader;

@Component
public class RegisterEch0045v4Component {

	private final ECHv4FileRegisterLoader ecHv4FileRegisterLoader;

	public RegisterEch0045v4Component(final ECHv4FileRegisterLoader ecHv4FileRegisterLoader) {
		this.ecHv4FileRegisterLoader = ecHv4FileRegisterLoader;
	}

	public Function<String, Iterable<VotingPersonType>> loadECH0045v4() {
		return ecHv4FileRegisterLoader::loadECH0045v4;
	}

	public Function<VotingPersonType, VoterTypeWithRole> fromECHv4Voter() {
		return ECH0045v4Mapper.INSTANCE::convert;
	}

	public Function<VoterTypeWithRole, VoterTypeWithRole> checkConsistency() {
		return ecHv4FileRegisterLoader::checkConsistency;
	}
}
