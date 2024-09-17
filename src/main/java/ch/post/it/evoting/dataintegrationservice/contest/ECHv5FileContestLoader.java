/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.contest;

import static com.google.common.base.Preconditions.checkState;

import java.net.URL;
import java.util.ArrayList;

import jakarta.xml.bind.JAXBException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
public class ECHv5FileContestLoader {

	@SuppressWarnings("java:S1075")
	private static final String ECH157_SCHEMA_PATH = "/xsd/eCH-0157-4-0.xsd";
	@SuppressWarnings("java:S1075")
	private static final String ECH159_SCHEMA_PATH = "/xsd/eCH-0159-4-0.xsd";
	private static final Logger LOGGER = LoggerFactory.getLogger(ECHv5FileContestLoader.class);

	public Tuple2<Iterable<ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup>, ch.ech.xmlns.ech_0155._5.ContestType> loadECH0157v5(
			final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0157v5 : {}", fileLocation);

			final URL schemaPath = ECHv5FileContestLoader.class.getResource(ECH157_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH157_SCHEMA_PATH);

			final ArrayList<ch.ech.xmlns.ech_0155._5.ContestType> contests = new ArrayList<>();
			final Stax2Wrapper wrapper = new Stax2Wrapper(fileLocation,
					schemaPath,
					ch.ech.xmlns.ech_0157._5.ObjectFactory.class);
			wrapper.addElementListener("/delivery/initialDelivery/contest", ch.ech.xmlns.ech_0155._5.ContestType.class, contests::add);
			final Iterable<ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup> itElections = wrapper.processByIteration(
					"/delivery/initialDelivery/electionGroupBallot", ch.ech.xmlns.ech_0157._5.EventInitialDelivery.ElectionGroup.class);

			return Tuples.of(itElections, contests.get(0));
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}

	public Tuple2<Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation>, ch.ech.xmlns.ech_0155._5.ContestType> loadECH0159v5(
			final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0159v5 : {}", fileLocation);

			final URL schemaPath = ECHv5FileContestLoader.class.getResource(ECH159_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH159_SCHEMA_PATH);

			final ArrayList<ch.ech.xmlns.ech_0155._5.ContestType> contests = new ArrayList<>();
			final Stax2Wrapper wrapper = new Stax2Wrapper(fileLocation,
					schemaPath,
					ch.ech.xmlns.ech_0159._5.ObjectFactory.class);
			wrapper.addElementListener("/delivery/initialDelivery/contest", ch.ech.xmlns.ech_0155._5.ContestType.class, contests::add);
			final Iterable<ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation> votations = wrapper.processByIteration(
					"/delivery/initialDelivery/voteInformation", ch.ech.xmlns.ech_0159._5.EventInitialDelivery.VoteInformation.class);
			return Tuples.of(votations, contests.get(0));
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}
}
