/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._4.AuthorityType;
import ch.ech.xmlns.ech_0045._4.ObjectFactory;
import ch.ech.xmlns.ech_0045._4.VotingPersonType;

class Stax2WrapperTest {

	@Test
	void testWithIteration() throws Exception {
		final Stax2Wrapper wrapper = new Stax2Wrapper("./src/test/resources/Stax2WrapperTest/file1.ech0045v4.two.xml",
				this.getClass().getResource("./xsd/eCH-0045-4-0.xsd"), ObjectFactory.class);

		final Iterator<VotingPersonType> it = wrapper.processByIteration("/voterDelivery/voterList/voter", VotingPersonType.class)
				.iterator();

		int cpt = 0;
		while (it.hasNext()) {
			cpt++;
			it.next();
		}
		assertEquals(2, cpt);
	}

	@Test
	void testWithoutIteration() throws Exception {
		final AuthorityType[] reportingAuthority = new AuthorityType[1];

		final Stax2Wrapper wrapper = new Stax2Wrapper("./src/test/resources/Stax2WrapperTest/file1.ech0045v4.two.xml",
				this.getClass().getResource("./xsd/eCH-0045-4-0.xsd"), ObjectFactory.class);
		wrapper.addElementListener("/voterDelivery/voterList/reportingAuthority", AuthorityType.class, a -> reportingAuthority[0] = a);

		wrapper.process();

		assertNotNull(reportingAuthority[0]);
	}
}
