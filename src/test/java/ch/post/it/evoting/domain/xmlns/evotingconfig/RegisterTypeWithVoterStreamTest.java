/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class RegisterTypeWithVoterStreamTest {

	@Test
	void TestStreamAsList() {

		VoterType[] voters = new VoterType[] { new VoterType().withVoterIdentification("toto") };

		RegisterTypeWithVoterStream pojo = new RegisterTypeWithVoterStream(Arrays.stream(voters));

		Iterator<VoterType> it = pojo.getVoter().iterator();
		assertTrue(it.hasNext());

		VoterType voter = it.next();
		assertEquals("toto", voter.getVoterIdentification());

		assertFalse(it.hasNext());
	}
}