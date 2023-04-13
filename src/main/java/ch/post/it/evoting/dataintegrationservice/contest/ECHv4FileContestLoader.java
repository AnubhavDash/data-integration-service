/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static com.google.common.base.Preconditions.checkState;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0155._4.ContestType;
import ch.ech.xmlns.ech_0157._4.EventInitialDelivery;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
public class ECHv4FileContestLoader {

	private static final String ECH157_SCHEMA_PATH = "/xsd/eCH-0157-4-0.xsd";
	private static final String ECH159_SCHEMA_PATH = "/xsd/eCH-0159-4-0.xsd";
	private static final Logger LOGGER = LoggerFactory.getLogger(ECHv4FileContestLoader.class);

	public Tuple2<Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot>, ContestType> loadECH0157v4(
			final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0157v4 : {}", fileLocation);

			final URL schemaPath = ECHv4FileContestLoader.class.getResource(ECH157_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH157_SCHEMA_PATH);

			final ArrayList<ContestType> contests = new ArrayList<>();
			final Stax2Wrapper wrapper = new Stax2Wrapper(fileLocation,
					schemaPath,
					ch.ech.xmlns.ech_0157._4.ObjectFactory.class);
			wrapper.addElementListener("/delivery/initialDelivery/contest", ContestType.class, contests::add);
			final Iterable<ch.ech.xmlns.ech_0157._4.EventInitialDelivery.ElectionGroupBallot> itElections = wrapper.processByIteration(
					"/delivery/initialDelivery/electionGroupBallot", EventInitialDelivery.ElectionGroupBallot.class);

			return Tuples.of(itElections, contests.get(0));
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}

	public Tuple2<Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation>, ContestType> loadECH0159v4(
			final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0159v4 : {}", fileLocation);

			final URL schemaPath = ECHv4FileContestLoader.class.getResource(ECH159_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH159_SCHEMA_PATH);

			final ArrayList<ContestType> contests = new ArrayList<>();
			final Stax2Wrapper wrapper = new Stax2Wrapper(fileLocation,
					schemaPath,
					ch.ech.xmlns.ech_0159._4.ObjectFactory.class);
			wrapper.addElementListener("/delivery/initialDelivery/contest", ContestType.class, contests::add);
			final Iterable<ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation> votations = wrapper.processByIteration(
					"/delivery/initialDelivery/voteInformation", ch.ech.xmlns.ech_0159._4.EventInitialDelivery.VoteInformation.class);
			return Tuples.of(votations, contests.get(0));
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}
}
