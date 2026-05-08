/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jakarta.xml.bind.JAXBException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.ech045voterextension.ObjectFactory;

@Component
public class ECHv42FileRegisterLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ECHv42FileRegisterLoader.class);

	private static final String ECH45_SCHEMA_PATH = "/xsd/eCH-0045-6-0.xsd";

	/**
	 * Loads the eCH-0045 file and checks that it is compatible with version 4.2 of the eCH standard.
	 *
	 * @param fileLocation the location of the eCH-0045 file to be loaded.
	 * @return an iterable of the voting person type.
	 */
	public Iterable<VotingPersonType> loadECH0045v42(final String fileLocation) {
		checkNotNull(fileLocation);

		try {
			LOGGER.debug("Loading eCH0045v42... [location: {}]", fileLocation);

			final URL schemaPath = ECHv42FileRegisterLoader.class.getResource(ECH45_SCHEMA_PATH);
			checkState(schemaPath != null, "Schema not found. [path: %s]", ECH45_SCHEMA_PATH);

			final InputStream inputStream = new FileInputStream(fileLocation);
			final Stax2Wrapper wrapper = new Stax2Wrapper(inputStream, schemaPath, ch.ech.xmlns.ech_0045._6.ObjectFactory.class, ObjectFactory.class);

			return wrapper.processByIteration("/voterDelivery/voterList/voter", VotingPersonType.class);
		} catch (final XMLStreamException | JAXBException | IOException e) {
			throw new IllegalArgumentException(String.format("Unable to load eCH-0045 file. [location: %s]", fileLocation), e);
		}
	}

	public VoterTypeWithRole checkConsistency(final VoterTypeWithRole voter) {
		checkNotNull(voter);

		checkState(voter.getDomainOfInfluenceInfos().stream()
						.allMatch(doiInfo -> doiInfo.getCountingCircle() != null),
				"The voter must have all its counting circles defined. [voterId: %s]", voter.getVoterIdentification());

		return voter;
	}

}
