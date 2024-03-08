/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DataIntegrationServiceApplication {

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
	}

}
