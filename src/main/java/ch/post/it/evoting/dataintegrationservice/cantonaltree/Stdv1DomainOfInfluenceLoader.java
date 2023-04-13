/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Stdv1DomainOfInfluenceLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(Stdv1DomainOfInfluenceLoader.class);

	private static final Function<String[], Stdv1DomainOfInfluence> mapLineToObject = strings -> {
		try {
			Stdv1DomainOfInfluence result = new Stdv1DomainOfInfluence();
			result.setId(strings[0]);
			result.setCode(strings[1]);
			result.setDescription(strings[2]);
			result.setParentId(strings[3]);
			result.setParentCode(strings[4]);
			result.setCantonCode(strings[5]);

			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to map to object", e);
		}
	};

	public Iterable<Stdv1DomainOfInfluence> loadDomainOfInfluence(final String fileLocation) {
		try {
			LOGGER.debug("Loading stdv1CountingCircle : {}", fileLocation);
			final CsvReader<Stdv1DomainOfInfluence> reader = new CsvReader<>(fileLocation, Charset.forName("Cp1252"), mapLineToObject);
			return reader.process();
		} catch (final IOException e) {
			throw new UncheckedIOException("Unable to access the file", e);
		}
	}
}
