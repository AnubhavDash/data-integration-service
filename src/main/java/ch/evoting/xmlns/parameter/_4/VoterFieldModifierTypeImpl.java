/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.post.it.evoting.dataintegrationservice.common.ReflexionUtil;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class VoterFieldModifierTypeImpl extends VoterFieldModifierPluginType {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {

		try {
			final String[] objects = getObject().split("\\.");
			Object element = voterType;

			for (final String obj : objects) {
				Field f = ReflexionUtil.getField(element, obj);
				element = ReflexionUtil.getOrCreateFieldValue(f, element);
			}

			final Field field = ReflexionUtil.getField(element, getFieldName());
			checkState(field != null, "Field not found. [fieldName: %s]", getFieldName());
			ReflexionUtil.setFieldValue(field, element, getFieldValueTyped(field));

		} catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException |
					   DatatypeConfigurationException e) {
			throw new IllegalStateException(String.format("Error during execution of VoterFieldModifier plugin. [object: %s]", getObject()), e);
		}
		return voterType;
	}

	private Object getFieldValueTyped(final Field field)
			throws DatatypeConfigurationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		if (!StringUtil.isNullOrEmpty(getFieldValue())) {
			if (field.getType().equals(int.class)) {
				return Integer.parseInt(getFieldValue());
			} else if (field.getType().isAssignableFrom(String.class)) {
				return getFieldValue();
			} else if (field.getType().isAssignableFrom(XMLGregorianCalendar.class)) {
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(getFieldValue());
			} else {
				final Method parseMethod = field.getType().getMethod("valueOf", String.class);
				return parseMethod.invoke(field, getFieldValue());
			}
		} else {
			return null;
		}
	}
}
