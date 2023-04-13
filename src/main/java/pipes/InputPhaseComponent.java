/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.input.DirectoryScanner;

@Component
public class InputPhaseComponent {

	private final DirectoryScanner directoryScanner;

	public InputPhaseComponent(final DirectoryScanner directoryScanner) {
		this.directoryScanner = directoryScanner;
	}

	public Iterable<String> scanner() {
		return directoryScanner.scan();
	}
}
