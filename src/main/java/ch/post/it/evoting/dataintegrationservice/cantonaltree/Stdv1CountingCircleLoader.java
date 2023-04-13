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
public class Stdv1CountingCircleLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(Stdv1CountingCircleLoader.class);
	private static final Function<String[], Stdv1CountingCircle> mapLineToObject = strings -> {
		try {
			Stdv1CountingCircle result = new Stdv1CountingCircle();
			result.setCode(strings[0]);
			result.setDescription(strings[1]);
			result.setOfsNumber(Integer.parseInt(strings[2]));
			result.setCantonCode(strings[3]);

			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to map to object", e);
		}
	};

	public Iterable<Stdv1CountingCircle> loadCountingCircle(final String fileLocation) {
		try {
			LOGGER.debug("Loading stdv1CountingCircle : {}", fileLocation);
			final CsvReader<Stdv1CountingCircle> reader = new CsvReader<>(fileLocation, Charset.forName("Cp1252"), mapLineToObject);
			return reader.process();
		} catch (final IOException e) {
			throw new UncheckedIOException("Unable to access the file", e);
		}
	}

}
