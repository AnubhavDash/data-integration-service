/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jakarta.xml.bind.JAXBException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.StreamableSymmetricEncryptionDecryptionService;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.ech045voterextension.ObjectFactory;

@Component
public class ECHv42FileRegisterLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ECHv42FileRegisterLoader.class);

	private final StreamableSymmetricEncryptionDecryptionService streamableSymmetricEncryptionDecryptionService;
	private final String fileExtension;
	private final char[] encryptionPassword;

	@SuppressWarnings("java:S1075")
	private static final String ECH45_SCHEMA_PATH = "/xsd/eCH-0045-6-0.xsd";

	public ECHv42FileRegisterLoader(
			final StreamableSymmetricEncryptionDecryptionService streamableSymmetricEncryptionDecryptionService,
			@Value("${input.file-extension}")
			final String fileExtension,
			@Value("${input.encryption-password:}")
			final char[] encryptionPassword) {
		this.streamableSymmetricEncryptionDecryptionService = streamableSymmetricEncryptionDecryptionService;
		checkArgument(fileExtension != null && !fileExtension.isBlank(), "File extension must be provided.");
		this.fileExtension = fileExtension;
		this.encryptionPassword = encryptionPassword;
	}

	public Iterable<VotingPersonType> loadECH0045v42(final String fileLocation) {
		try {
			LOGGER.debug("Loading eCH0045v42 : {}", fileLocation);

			final URL schemaPath = ECHv42FileRegisterLoader.class.getResource(ECH45_SCHEMA_PATH);
			checkState(schemaPath != null, "Resource '%s' not found", ECH45_SCHEMA_PATH);

			final InputStream inputStream;
			if (fileLocation.endsWith(fileExtension)) {
				inputStream = streamableSymmetricEncryptionDecryptionService.getStreamPlaintext(new FileInputStream(fileLocation), encryptionPassword,
						ImmutableByteArray.EMPTY);
			} else {
				inputStream = new FileInputStream(fileLocation);
			}

			final Stax2Wrapper wrapper = new Stax2Wrapper(inputStream, schemaPath, ch.ech.xmlns.ech_0045._6.ObjectFactory.class,
					ObjectFactory.class);
			return wrapper.processByIteration("/voterDelivery/voterList/voter", VotingPersonType.class);
		} catch (final XMLStreamException | JAXBException | IOException e) {
			throw new IllegalArgumentException("Unable to load file", e);
		}
	}

	public VoterTypeWithRole checkConsistency(final VoterTypeWithRole voter) {
		checkState(voter.getDomainOfInfluenceInfos().stream().allMatch(doiInfo -> doiInfo.getCountingCircle() != null),
				"The voter must have all its counting circle defined [voterId: %s]", voter.getVoterIdentification());
		return voter;
	}
}
