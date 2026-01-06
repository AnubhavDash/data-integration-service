/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static com.google.common.base.Preconditions.checkState;

import java.net.URL;

import jakarta.xml.bind.JAXBException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._4.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.ech045voterextension.ObjectFactory;

@Component
public class ECHv40FileRegisterLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ECHv40FileRegisterLoader.class);

	@SuppressWarnings("java:S1075")
	private static final String ECH45_SCHEMA_PATH = "/xsd/eCH-0045-4-0.xsd";

	public Iterable<VotingPersonType> loadECH0045v4(final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0045v4 : {}", fileLocation);

			final URL schemaPath = ECHv40FileRegisterLoader.class.getResource(ECH45_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH45_SCHEMA_PATH);

			final Stax2Wrapper wrapper = new Stax2Wrapper(fileLocation, schemaPath, ch.ech.xmlns.ech_0045._4.ObjectFactory.class,
					ObjectFactory.class);
			return wrapper.processByIteration("/voterDelivery/voterList/voter", ch.ech.xmlns.ech_0045._4.VotingPersonType.class);
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}

	public VoterTypeWithRole checkConsistency(final VoterTypeWithRole voter) {
		checkState(voter.getDomainOfInfluenceInfos().stream().allMatch(doiInfo -> doiInfo.getCountingCircle() != null),
				"The voter must have all its counting circle defined [voterId: %s]", voter.getVoterIdentification());
		return voter;
	}
}
