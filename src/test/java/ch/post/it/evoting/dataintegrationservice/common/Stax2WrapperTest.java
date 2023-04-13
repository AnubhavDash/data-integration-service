/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._3.AuthorityType;
import ch.ech.xmlns.ech_0045._3.ObjectFactory;
import ch.ech.xmlns.ech_0045._3.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

class Stax2WrapperTest {

	@Test
	void testWithIteration() throws Exception {
		Stax2Wrapper wrapper = new Stax2Wrapper("./src/test/resources/Stax2WrapperTest/file1.ech0045v3.two.xml",
				this.getClass().getResource("./xsd/eCH-0045-3-0.xsd"), ObjectFactory.class);

		Iterator<VotingPersonType> it = wrapper.processByIteration("/voterDelivery/voterList/voter", VotingPersonType.class)
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
		AuthorityType[] reportingAuthority = new AuthorityType[1];

		Stax2Wrapper wrapper = new Stax2Wrapper("./src/test/resources/Stax2WrapperTest/file1.ech0045v3.two.xml",
				this.getClass().getResource("./xsd/eCH-0045-3-0.xsd"), ObjectFactory.class);
		wrapper.addElementListener("/voterDelivery/voterList/reportingAuthority", AuthorityType.class, (a) -> reportingAuthority[0] = a);

		wrapper.process();

		assertNotNull(reportingAuthority[0]);
	}
}
