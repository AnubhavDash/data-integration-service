/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("java:S3011")
public class ReflexionUtil {

	private ReflexionUtil() {
		//Intentionally left blank
	}

	public static <T> T getFieldValue(final Object src, final String path) {
		try {
			final List<String> objects = Arrays.asList(path.split("\\."));

			Object element = src;
			for (final String obj : objects.subList(0, objects.size() - 1)) {
				checkState(element.getClass() != null, "The element class must not be null");
				final Field field = getField(element, obj);

				checkState(field != null, "The field could not be found in class. [field: %s, class: %s]", obj, element.getClass().getSimpleName());
				element = field.get(element);
				if (element == null) {
					throw new NullPointerException(String.format("object %s is null", obj));
				}
			}

			final Field field = element.getClass().getDeclaredField(objects.get(objects.size() - 1));
			field.setAccessible(true);
			return (T) field.get(element);

		} catch (final IllegalAccessException | NoSuchFieldException e) {
			throw new IllegalArgumentException("Error during execution of VoterFieldModifier plugin :" + src, e);
		}
	}

	public static Object getOrCreateFieldValue(final Field f, final Object element)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
		Object result = f.get(element);
		if (result == null) {
			result = f.getType().getDeclaredConstructor().newInstance();
			f.set(element, result);
		}
		return result;
	}

	public static Field getField(final Object element, final String fieldName) {
		Field result = null;
		Class<?> clazz = element.getClass();
		while (result == null && clazz != null) {
			try {
				result = clazz.getDeclaredField(fieldName);
				result.setAccessible(true);
			} catch (final NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return result;
	}

	public static void setFieldValue(final Field field, final Object element, final Object value) {
		try {
			field.setAccessible(true);
			field.set(element, value);
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException("Error setting value on field", e);
		}
	}
}
