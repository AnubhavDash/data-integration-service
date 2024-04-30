/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	@SuppressWarnings("java:S2696") // Safe usage as it is a Spring Bean.
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		context = applicationContext;
	}

	public static<T> T getProperty(final String key, final Class<T> targetType) {
		return context.getEnvironment().getProperty(key, targetType);
	}

	public static <T> T getBean(final Class<T> requiredType) {
		return context.getBean(requiredType);
	}
}