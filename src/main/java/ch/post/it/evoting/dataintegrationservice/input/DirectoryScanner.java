/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.input;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DirectoryScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryScanner.class);
	private final String sourceDirectory;

	public DirectoryScanner(
			@Value("${directory.source}")
			final String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public Iterable<String> scan() {
		final Path path = Paths.get(sourceDirectory);
		LOGGER.info("Scanning directory ({}) for files to be handled", path.toAbsolutePath());
		try (final Stream<Path> paths = Files.list(path)) {
			return paths.filter(Files::isRegularFile)
					.filter(f -> !f.toFile().getName().toLowerCase(Locale.ENGLISH).endsWith(".xsd"))
					.map(Path::toString).toList();
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static Predicate<String> filterType(final String type) {
		return s -> Paths.get(s).getFileName().toString().toLowerCase(Locale.ENGLISH)
				.matches(String.format(".*%s.*", type.toLowerCase(Locale.ENGLISH)));
	}
}
