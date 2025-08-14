/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._6.VotingPersonType;

class ECHv42FileRegisterLoaderTest {
	@Test
	void loadECH0045v6() {
		final Iterable<VotingPersonType> result = new ECHv42FileRegisterLoader().loadECH0045v42(
				"./src/test/resources/ECHv42FileRegisterLoaderTest/file1.ech0045v4_2.one.xml");

		final List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.getFirst().getPerson());
	}

}
