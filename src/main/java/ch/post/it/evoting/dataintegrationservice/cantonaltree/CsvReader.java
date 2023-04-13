/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

public class CsvReader<T> {
	private final Function<String[], T> mapper;
	private final Stream<String> stream;
	private final boolean hasHeader;

	public CsvReader(final String filename, final Charset charset, final Function<String[], T> mapper) throws IOException {
		this(filename, charset, true, mapper);
	}

	public CsvReader(final String filename, final Charset charset, final boolean hasHeader, final Function<String[], T> mapper) throws IOException {
		this.mapper = mapper;
		stream = Files.lines(Paths.get(filename), charset);
		this.hasHeader = hasHeader;
	}

	public Iterable<T> process() {
		//skip the first line (header)
		//split with ; character
		//map to object with given Function
		return () -> stream.skip(hasHeader ? 1 : 0).map(l -> l.split(";")).map(mapper).iterator();
	}
}
