/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stax2Wrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(Stax2Wrapper.class);
	private static final String XPATH_ROOT_ELEMENT = "/";
	private final XMLStreamReader2 reader;
	private final Unmarshaller unmarshaller;
	private final HashMap<String, Consumer<Object>> elements = new HashMap<>();
	private final HashMap<String, Class<?>> classes = new HashMap<>();
	private final LinkedList<String> actualParsingThroughTags = new LinkedList<>();
	private final String name;
	private Class<?> iteratorClass;
	private String iteratorPath;
	private Object iteratorObject;

	public Stax2Wrapper(final String xmlFileLocation, final URL xsdLocation, final Class<?> jaxbFactoryClass)
			throws XMLStreamException, JAXBException {
		this(xmlFileLocation, xsdLocation, jaxbFactoryClass, null);
	}

	public Stax2Wrapper(final InputStream input, final URL xsdLocation, final Class<?> jaxbFactoryClass,
			final Object specificUnmarshallerFactoryInstance)
			throws XMLStreamException, JAXBException {

		name = "inputStream";
		unmarshaller = JAXBContext.newInstance(jaxbFactoryClass).createUnmarshaller();
		if (specificUnmarshallerFactoryInstance != null) {
			unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", specificUnmarshallerFactoryInstance);
		}

		final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

		reader = Stax2ReaderAdapter.wrapIfNecessary(xmlInputFactory.createXMLStreamReader(input, "UTF-8"));
		if (xsdLocation != null) {
			final XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
			final XMLValidationSchema sv = sf.createSchema(xsdLocation);
			reader.validateAgainst(sv);
		}
	}

	public Stax2Wrapper(final String xmlFileLocation, final URL xsdLocation, final Class<?> jaxbFactoryClass,
			final Object specificUnmarshallerFactoryInstance)
			throws XMLStreamException, JAXBException {

		name = xmlFileLocation;
		unmarshaller = JAXBContext.newInstance(jaxbFactoryClass).createUnmarshaller();

		if (specificUnmarshallerFactoryInstance != null) {
			unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", specificUnmarshallerFactoryInstance);
		}

		final XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

		reader = factory.createXMLStreamReader(new File(xmlFileLocation));

		if (xsdLocation != null) {
			final XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
			final XMLValidationSchema sv = sf.createSchema(xsdLocation);
			reader.validateAgainst(sv);
		}
	}

	public <T> void addElementListener(final String path, final Class<T> clazz, final Consumer<T> consumer) {
		elements.put(path, (Consumer<Object>) consumer);
		classes.put(path, clazz);
	}

	public <T> Iterable<T> processByIteration(final String path, final Class<T> clazz) {
		iteratorPath = path;
		iteratorClass = clazz;

		return new MyIterable<>();
	}

	public void process() {
		checkState(iteratorPath == null, "Cannot process the file because an iterator has been configured. Process by iterating on it");
		process(null);
	}

	protected boolean process(final String stopProcessingPath) {
		try {
			while (reader.hasNext()) {
				if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
					actualParsingThroughTags.add(reader.getLocalName());

					final String actualPath = XPATH_ROOT_ELEMENT + String.join("/", actualParsingThroughTags);

					final boolean elementStep = elements.containsKey(actualPath);
					final boolean stopProcessingStep = actualPath.equals(stopProcessingPath);

					if (elementStep) {
						actualParsingThroughTags.removeLast();
						//When unmarshalling, we don't have to call the next() on reader because the unmarshaller already call it
						elements.get(actualPath).accept(unmarshaller.unmarshal(reader, classes.get(actualPath)).getValue());
					} else if (stopProcessingStep) {
						actualParsingThroughTags.removeLast();
						//When unmarshalling, we don't have to call the next() on reader because the unmarshaller already call it
						iteratorObject = unmarshaller.unmarshal(reader, iteratorClass).getValue();
						return true;
					} else {
						reader.next();
					}
				} else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
					if (actualParsingThroughTags.getLast().equals(reader.getLocalName())) {
						actualParsingThroughTags.removeLast();
					}
					reader.next();
				} else {
					reader.next();
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} finished {}", reader.toString().split("@")[1], name);
			}
			return false;
		} catch (final XMLStreamException | JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

	class MyIterable<T> implements Iterable<T> {
		final MyIterator<T> it = new MyIterator<>();

		@Override
		public Iterator<T> iterator() {
			return it;
		}
	}

	class MyIterator<T> implements Iterator<T> {

		private Boolean hasNext = null;

		public MyIterator() {
			//process until firstElement to be iterated
			final boolean containsNextElement = hasNext();
			if (!containsNextElement) {
				LOGGER.warn("There is no element to be extracted from XML file. Check your configuration");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} starting {}", reader.toString().split("@")[1], name);
			}
		}

		@Override
		public boolean hasNext() {
			if (hasNext == null) {
				hasNext = process(iteratorPath);
			}
			return hasNext;
		}

		@Override
		public T next() {
			try {
				if (hasNext()) {
					return (T) iteratorObject;
				}
				throw new NoSuchElementException();
			} finally {
				hasNext = null;
			}
		}
	}
}
