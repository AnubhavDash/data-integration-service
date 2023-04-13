/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.output;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.domain.xmlns.evotingconfig.RegisterType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.RegisterTypeWithVoterStream;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterType;

@Service
public class InvalidVoterService {

	public RegisterType buildInvalidRegister(final Stream<VoterType> voterTypes) {
		return new RegisterTypeWithVoterStream(voterTypes);
	}
}
