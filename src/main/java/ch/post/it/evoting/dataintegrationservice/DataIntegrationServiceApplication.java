/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import static ch.post.it.evoting.evotinglibraries.domain.JarHashLogger.getFingerprintLogMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DataIntegrationServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataIntegrationServiceApplication.class);


	private final DataIntegrationService dataIntegrationService;
	private final boolean applicationBootstrapEnabled;

	public DataIntegrationServiceApplication(
			final DataIntegrationService dataIntegrationService,
			@Value("${application.bootstrap.enabled}")
			final boolean applicationBootstrapEnabled) {
		this.dataIntegrationService = dataIntegrationService;
		this.applicationBootstrapEnabled = applicationBootstrapEnabled;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReadyEvent() {
		if (applicationBootstrapEnabled) {
			dataIntegrationService.run();
		}
	}

	public static void main(final String[] args) {
		SpringApplication.run(DataIntegrationServiceApplication.class, args);
		logJarHash();
	}

	private static void logJarHash() {
		final Path jarPath = new ApplicationHome(DataIntegrationServiceApplication.class).getSource().toPath();

		try {
			final String fingerprintLogMessage = getFingerprintLogMessage(jarPath);
			LOGGER.info(fingerprintLogMessage);
		} catch (final IOException | NoSuchAlgorithmException e) {
			LOGGER.error("Failed to compute JAR Fingerprint.", e);
		}
	}
}
