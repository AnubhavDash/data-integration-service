/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.junit.jupiter.api.Test;

import ch.evoting.xmlns.parameter._5.BirthYearAuthenticationKeyTypeImpl;
import ch.evoting.xmlns.parameter._5.ObjectFactory;
import ch.evoting.xmlns.parameter._5.Parameter;
import ch.evoting.xmlns.parameter._5.PluginObjectFactory;
import ch.post.it.evoting.dataintegrationservice.common.Stax2Wrapper;

class ParameterLoaderTest {

	private final ParameterLoader parameterLoader = new ParameterLoader();

	@Test
	void loadParameter() {
		final Parameter param = parameterLoader.loadParameter("./src/test/resources/ParameterLoaderTest/file.param.xml");
		assertTrue(param.getPlugins().getAfterReadVoter().getPlugins().stream()
				.anyMatch(p -> p.getClass().equals(BirthYearAuthenticationKeyTypeImpl.class)));
	}

	@Test
	void mergeTest() throws Exception {
		final Parameter actual = new ParameterLoader().loadParameter("src/test/resources/ParameterLoaderTest/mergeTest/file.param.xml",
				"ParameterLoaderTest/mergeTest/default.param.xml");

		final Parameter expected = new Stax2Wrapper("src/test/resources/ParameterLoaderTest/mergeTest/ParameterLoaderTest_merge.xml",
				null,
				ObjectFactory.class,
				new PluginObjectFactory())
				.processByIteration("/parameter", Parameter.class).iterator().next();

		assertEquals(convertToXml(expected), convertToXml(actual));
	}

	public static String convertToXml(final Object object) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(object.getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter writer = new StringWriter();
		marshaller.marshal(object, writer);
		return writer.toString();
	}
}
