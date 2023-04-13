/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.register.ech0045v3;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._3.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.register.ECH0045v3Mapper;
import ch.post.it.evoting.dataintegrationservice.register.ECHv3FileRegisterLoader;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

@Component
public class RegisterEch0045v3Component {

	private final ECHv3FileRegisterLoader ecHv3FileRegisterLoader;

	public RegisterEch0045v3Component(final ECHv3FileRegisterLoader ecHv3FileRegisterLoader) {
		this.ecHv3FileRegisterLoader = ecHv3FileRegisterLoader;
	}

	public Function<String, Iterable<VotingPersonType>> loadECH0045v3() {
		return ecHv3FileRegisterLoader::loadECH0045v3;
	}

	public Function<VotingPersonType, VoterTypeWithRole> fromECHv3Voter() {
		return ECH0045v3Mapper.INSTANCE::convert;
	}
}
