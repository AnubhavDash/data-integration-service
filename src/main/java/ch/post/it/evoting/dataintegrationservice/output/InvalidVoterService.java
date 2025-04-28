/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.output;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.dataintegrationservice.domain.RegisterTypeWithVoterStream;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.RegisterType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType;

@Service
public class InvalidVoterService {

	public RegisterType buildInvalidRegister(final Stream<VoterType> voterTypes) {
		return new RegisterTypeWithVoterStream(voterTypes);
	}
}
