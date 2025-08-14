/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.dataintegrationservice.domain.RegisterTypeWithVoterStream;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType;

class RegisterTypeWithVoterStreamTest {

	@Test
	void TestStreamAsList() {

		final VoterType[] voters = new VoterType[] { new VoterType().withVoterIdentification("toto") };

		final RegisterTypeWithVoterStream pojo = new RegisterTypeWithVoterStream(Arrays.stream(voters));

		final Iterator<VoterType> it = pojo.getVoter().iterator();
		assertTrue(it.hasNext());

		final VoterType voter = it.next();
		assertEquals("toto", voter.getVoterIdentification());

		assertFalse(it.hasNext());
	}
}