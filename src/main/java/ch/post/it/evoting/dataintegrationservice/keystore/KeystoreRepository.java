/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeystoreRepository {

	private final String keystoreLocation;
	private final String keystorePasswordLocation;

	public KeystoreRepository(
			@Value("${direct.trust.keystore.location.canton}")
			final String keystoreLocation,
			@Value("${direct.trust.keystore.password.location.canton}")
			final String keystorePasswordLocation) {
		this.keystoreLocation = keystoreLocation;
		this.keystorePasswordLocation = keystorePasswordLocation;
	}

	public InputStream getCantonKeyStore() throws IOException {
		return Files.newInputStream(Paths.get(keystoreLocation));
	}

	public char[] getCantonKeystorePassword() throws IOException {
		return Files.readString(Paths.get(keystorePasswordLocation)).toCharArray();
	}
}