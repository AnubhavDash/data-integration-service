/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.input;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class DirectoryScannerTest {

	private final DirectoryScanner directoryScanner = new DirectoryScanner("src/test/resources");

	@Test
	void scan() {
		final List<String> filenames = new ArrayList<>();
		directoryScanner.scan().iterator().forEachRemaining(filenames::add);

		assertTrue(filenames.size() > 0);
		assertTrue(filenames.stream().anyMatch(e -> e.contains("param")));
	}

	@Test
	void filterType() {
		final Predicate<String> predicate = DirectoryScanner.filterType("test");
		assertTrue(predicate.test("blablatestblabla"));
		assertFalse(predicate.test("blablablabla"));
	}

}