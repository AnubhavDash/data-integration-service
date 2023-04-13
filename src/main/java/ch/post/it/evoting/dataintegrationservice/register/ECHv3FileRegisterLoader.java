/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static com.google.common.base.Preconditions.checkState;

import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._3.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

@Component
public class ECHv3FileRegisterLoader {
	private static final String ECH45_SCHEMA_PATH = "/xsd/eCH-0045-3-0.xsd";
	private static final Logger LOGGER = LoggerFactory.getLogger(ECHv3FileRegisterLoader.class);

	public Iterable<VotingPersonType> loadECH0045v3(final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0045v3 : {}", fileLocation);

			final URL schemaPath = ECHv3FileRegisterLoader.class.getResource(ECH45_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH45_SCHEMA_PATH);

			final Stax2Wrapper wrapper = new Stax2Wrapper(fileLocation,
					schemaPath,
					ch.ech.xmlns.ech_0045._3.ObjectFactory.class);
			return wrapper.processByIteration("/voterDelivery/voterList/voter", ch.ech.xmlns.ech_0045._3.VotingPersonType.class);
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}
}
