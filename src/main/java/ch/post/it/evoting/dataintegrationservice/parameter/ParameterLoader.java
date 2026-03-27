/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jakarta.xml.bind.JAXBException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.evoting.xmlns.parameter._5.ObjectFactory;
import ch.evoting.xmlns.parameter._5.Parameter;
import ch.evoting.xmlns.parameter._5.PluginObjectFactory;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

@Component
public class ParameterLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterLoader.class);

	private static final String DEFAULT_PARAMETER_PATH = "xml/default.param.xml";
	@SuppressWarnings("java:S1075")
	private static final String PARAMETER_SCHEMA_PATH = "/xsd/evoting-parameter-5-0.xsd";

	public Parameter loadParameter(final String fileLocation) {
		return this.loadParameter(fileLocation, DEFAULT_PARAMETER_PATH);
	}

	public Parameter loadParameter(final String fileLocation, final String defaultParameterLocation) {
		checkNotNull(fileLocation);
		checkNotNull(defaultParameterLocation);

		// XSD
		final URL schemaPath = ParameterLoader.class.getResource(PARAMETER_SCHEMA_PATH);
		checkNotNull(schemaPath);

		LOGGER.info("Loading parameter : {}", fileLocation);

		// Default XML
		try (final InputStream defaultFile = ParameterLoader.class.getClassLoader().getResourceAsStream(defaultParameterLocation)) {

			final Parameter defaultParameterValue = new Stax2Wrapper(defaultFile, null, new Class<?>[] { ObjectFactory.class },
					new PluginObjectFactory())
					.processByIteration("/parameter", Parameter.class)
					.iterator().next();
			final Parameter inputParameterValue = new Stax2Wrapper(fileLocation, schemaPath, new Class<?>[] { ObjectFactory.class },
					new PluginObjectFactory())
					.processByIteration("/parameter", Parameter.class)
					.iterator().next();

			if (inputParameterValue.getContest().getDefaultAnswerTranslations() == null) {
				inputParameterValue.getContest().setDefaultAnswerTranslations(defaultParameterValue.getContest().getDefaultAnswerTranslations());
			}

			if (inputParameterValue.getContest().getEmptyPositionTextInfo() == null) {
				inputParameterValue.getContest().setEmptyPositionTextInfo(defaultParameterValue.getContest().getEmptyPositionTextInfo());
			}

			if (inputParameterValue.getContest().getReturnCodeWriteInDescriptionInfo() == null) {
				inputParameterValue.getContest()
						.setReturnCodeWriteInDescriptionInfo(defaultParameterValue.getContest().getReturnCodeWriteInDescriptionInfo());
			}

			if (inputParameterValue.getContest().getIncumbentText() == null) {
				inputParameterValue.getContest().setIncumbentText(defaultParameterValue.getContest().getIncumbentText());
			}

			if (inputParameterValue.getContest().getDefaultDisplayCandidateLineStyle() == null) {
				inputParameterValue.getContest()
						.setDefaultDisplayCandidateLineStyle(defaultParameterValue.getContest().getDefaultDisplayCandidateLineStyle());
			}

			if (inputParameterValue.getContest().getDefaultDisplayListLineStyle() == null) {
				inputParameterValue.getContest().setDefaultDisplayListLineStyle(defaultParameterValue.getContest().getDefaultDisplayListLineStyle());
			}

			return inputParameterValue;
		} catch (final XMLStreamException | JAXBException | IOException e) {
			throw new IllegalArgumentException("Unable to load Parameter", e);
		}
	}
}
