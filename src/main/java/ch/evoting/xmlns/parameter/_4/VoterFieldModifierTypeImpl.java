/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterFieldModifierTypeImpl extends VoterFieldModifierPluginType {

	private static final Logger LOGGER = LoggerFactory.getLogger(VoterFieldModifierTypeImpl.class);

	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {

		try {
			final String[] objects = getObject().split("\\.");
			Object element = voterType;
			for (final String obj : objects) {
				Field f = null;
				Class<?> clazz = element.getClass();
				while (f == null && clazz != null) {
					try {
						f = clazz.getDeclaredField(obj);
						f.setAccessible(true);
					} catch (final NoSuchFieldException e) {
						clazz = clazz.getSuperclass();
					}
				}
				if (f == null) {
					LOGGER.error("The field '{}' cannot be find", obj);
					throw new IllegalStateException(String.format("Field cannot be find. [field: %s]", obj));
				}

				final Object parent = element;
				element = f.get(element);
				if (element == null) {
					element = f.getType().getDeclaredConstructor().newInstance();
					f.set(parent, element);
				}
			}

			final Field field = element.getClass().getDeclaredField(getFieldName());
			field.setAccessible(true);
			Object value = null;

			if (!StringUtil.isNullOrEmpty(getFieldValue())) {
				if (field.getType().equals(int.class)) {
					value = Integer.parseInt(getFieldValue());
				} else if (field.getType().isAssignableFrom(String.class)) {
					value = getFieldValue();
				} else if (field.getType().isAssignableFrom(XMLGregorianCalendar.class)) {
					value = DatatypeFactory.newInstance().newXMLGregorianCalendar(getFieldValue());
				} else {
					final Method parseMethod = field.getType().getMethod("valueOf", String.class);
					value = parseMethod.invoke(field, getFieldValue());
				}
			}
			field.set(element, value);

		} catch (final IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException |
					   DatatypeConfigurationException e) {
			throw new IllegalStateException(String.format("Error during execution of VoterFieldModifier plugin. [object: %s]", getObject()), e);
		}
		return voterType;
	}
}
