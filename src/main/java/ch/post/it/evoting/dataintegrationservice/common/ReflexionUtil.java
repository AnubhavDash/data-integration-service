/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ReflexionUtil {

	private ReflexionUtil() {
		//Intentionally left blank
	}

	public static <T> T getFieldValue(final Object src, final String path) {
		try {
			final List<String> objects = Arrays.asList(path.split("\\."));

			Object element = src;
			for (final String obj : objects.subList(0, objects.size() - 1)) {
				Field field = null;
				Class<?> clazz = element.getClass();
				final Class<?> originClass = clazz;
				checkState(originClass != null, "The original class must not be null");
				while (field == null && clazz != null) {
					try {
						field = clazz.getDeclaredField(obj);
						field.setAccessible(true);
					} catch (final NoSuchFieldException e) {
						clazz = clazz.getSuperclass();
					}
				}
				checkState(field != null, "The field could not be found in class. [field: %s, class: %s]", obj, originClass.getSimpleName());

				element = field.get(element);
				if (element == null) {
					throw new NullPointerException("object " + obj + " is null");
				}
			}

			final Field field = element.getClass().getDeclaredField(objects.get(objects.size() - 1));
			field.setAccessible(true);
			return (T) field.get(element);

		} catch (final IllegalAccessException | NoSuchFieldException e) {
			throw new IllegalArgumentException("Error during execution of VoterFieldModifier plugin :" + src, e);
		}
	}
}
