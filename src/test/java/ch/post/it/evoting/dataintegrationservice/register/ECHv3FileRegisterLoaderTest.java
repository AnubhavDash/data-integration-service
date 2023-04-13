/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._3.VotingPersonType;

class ECHv3FileRegisterLoaderTest {

	@Test
	void loadECH0045v3() {
		final Iterable<VotingPersonType> result = new ECHv3FileRegisterLoader().loadECH0045v3(
				"./src/test/resources/ECHv3FileRegisterLoaderTest/file1.ech0045v3.one.xml");

		final List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.get(0).getPerson());
	}

}