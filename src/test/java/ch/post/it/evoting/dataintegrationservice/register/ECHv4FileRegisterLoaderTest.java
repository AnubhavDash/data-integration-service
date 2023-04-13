/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._4.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.register.ECHv4FileRegisterLoader;

class ECHv4FileRegisterLoaderTest {
	@Test
	void loadECH0045v4() {
		Iterable<VotingPersonType> result = new ECHv4FileRegisterLoader().loadECH0045v4(
				"./src/test/resources/ECHv4FileRegisterLoaderTest/file1.ech0045v4.one.xml");

		List<VotingPersonType> voters = new ArrayList<>();
		result.iterator().forEachRemaining(voters::add);

		assertEquals(1, voters.size());
		assertNotNull(voters.get(0).getPerson());
	}

}
