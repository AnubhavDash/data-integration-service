/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.annotations.VisibleForTesting;

public class DataIntegrationServiceApplication {

	private static ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);

	public static void main(final String... args) {
		final DataIntegrationService setupService = context.getBean(DataIntegrationService.class);
		setupService.run();
	}

	public static ApplicationContext getContext() {
		return context;
	}

	@VisibleForTesting
	static void resetContext() {
		DataIntegrationServiceApplication.context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
	}

}
