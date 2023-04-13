/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0155._3.ContestType;
import ch.ech.xmlns.ech_0157._3.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.contest.ECHv3FileContestLoader;

import reactor.util.function.Tuple2;

class ECHv3FileContestLoaderTest {

	final ECHv3FileContestLoader ecHv3FileContestLoader = new ECHv3FileContestLoader();
	@Test
	void loadECH0157v3() {
		Tuple2<Iterable<EventInitialDelivery.ElectionInformation>, ContestType> result = ecHv3FileContestLoader.loadECH0157v3(
				"./src/test/resources/ECHv3FileContestLoaderTest/eCH-0157-Export_M01_20161127_20161109170345_ech0157v3.xml");
		assertNotNull(result);
		assertNotNull(result.getT1());
		assertNotNull(result.getT2());
	}

	@Test
	void loadECH0159v3() {
		Tuple2<Iterable<ch.ech.xmlns.ech_0159._3.EventInitialDelivery.VoteInformation>, ContestType> result = ecHv3FileContestLoader.loadECH0159v3(
				"./src/test/resources/ECHv3FileContestLoaderTest/eCH-0159_ech0159v3.xml");
		assertNotNull(result);
		assertNotNull(result.getT1());
		assertNotNull(result.getT2());
	}

}