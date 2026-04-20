/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.w3._2000._09.xmldsig_.ReferenceType;

import ch.post.it.evoting.evotinglibraries.domain.LocalDateTimeUtils;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

@SpringBootTest(properties = {
		"bypass.checkConsistency.dates=true",
		"formatOutput=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
abstract class ConfigurationAnonymizedReproducibleTestBase {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
	private static final String CONFIGURATION_ANONYMIZED = "configuration-anonymized.xml";
	private static final XmlFileRepository<Configuration> xmlFileRepository = new XmlFileRepository<>();
	protected static final String ELECTION_EVENTS = "src/test/resources/ConfigurationAnonymizedReproducibleTestBase/election-events";
	private static MockedStatic<LocalDateTimeUtils> localDateTimeUtilsMock;
	private static DatatypeFactory datatypeFactory;

	@TempDir
	protected static Path temporaryDirectory;

	@Autowired
	private DataIntegrationService dataIntegrationService;

	@BeforeAll
	static void setUp() throws DatatypeConfigurationException {
		localDateTimeUtilsMock = Mockito.mockStatic(LocalDateTimeUtils.class);
		datatypeFactory = DatatypeFactory.newInstance();
	}

	@AfterAll
	static void tearDown() {
		localDateTimeUtilsMock.close();
	}

	protected static void registerPgProperties(final DynamicPropertyRegistry registry, final String datasetName) {
		registry.add("directory.source", () -> String.format("%s/%s/D0", ELECTION_EVENTS, datasetName));
		registry.add("directory.target", temporaryDirectory::toString);
	}

	protected void sameConfigurationAnonymizedTest(final String datasetName) {
		final Configuration original = load(datasetName);

		final LocalDateTime fileDate = LocalDateTime.parse(original.getHeader().getFileDate().toXMLFormat(), DATE_TIME_FORMATTER);
		localDateTimeUtilsMock.when(LocalDateTimeUtils::now).thenReturn(fileDate);
		localDateTimeUtilsMock.when(() -> LocalDateTimeUtils.fromXMLGregorianCalendar(any())).thenAnswer(invocationOnMock -> {
			final XMLGregorianCalendar argument = invocationOnMock.getArgument(0);
			return LocalDateTime.parse(argument.toXMLFormat(), DATE_TIME_FORMATTER);
		});
		localDateTimeUtilsMock.when(() -> LocalDateTimeUtils.toXMLGregorianCalendar(any())).thenAnswer(invocationOnMock -> {
			final LocalDateTime argument = invocationOnMock.getArgument(0);
			return datatypeFactory.newXMLGregorianCalendar(argument.format(DATE_TIME_FORMATTER));
		});

		assertDoesNotThrow(() -> dataIntegrationService.run());

		final Configuration regenerated = xmlFileRepository.read(temporaryDirectory.resolve(CONFIGURATION_ANONYMIZED), XsdConstants.CANTON_CONFIG_XSD,
				Configuration.class);

		// Ignore fileDate field (use original fileDate in re-generated file).
		regenerated.getHeader().setFileDate(original.getHeader().getFileDate());

		assertTrue(sameConfigurationAnonymized(original, regenerated));
	}

	private static boolean sameConfigurationAnonymized(final Configuration expected, final Configuration actual) {
		final List<byte[]> expectedDigestValues = expected.getSignature().getSignedInfo().getReference().stream().map(ReferenceType::getDigestValue).toList();
		final List<byte[]> actualDigestValues = actual.getSignature().getSignedInfo().getReference().stream().map(ReferenceType::getDigestValue).toList();

		if (expectedDigestValues.size() != actualDigestValues.size()) {
			return false;
		}

		return IntStream.range(0, expectedDigestValues.size())
				.allMatch(i -> Arrays.equals(expectedDigestValues.get(i), actualDigestValues.get(i)));
	}

	private static Configuration load(final String datasetName) {
		return xmlFileRepository.read(Paths.get(ELECTION_EVENTS, datasetName, "D1/data-integration-service", CONFIGURATION_ANONYMIZED), XsdConstants.CANTON_CONFIG_XSD,
				Configuration.class);
	}

}
