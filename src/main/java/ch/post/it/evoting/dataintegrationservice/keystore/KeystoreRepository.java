/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.evotinglibraries.domain.ConversionUtils;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;

@Repository
public class KeystoreRepository {

	private final String keystoreLocation;
	private final String keystorePasswordLocation;
	private final Alias alias;

	public KeystoreRepository(
			@Value("${direct-trust.keystore.location}")
			final String keystoreLocation,
			@Value("${direct-trust.password.location}")
			final String keystorePasswordLocation) {
		this.keystoreLocation = keystoreLocation;
		this.keystorePasswordLocation = keystorePasswordLocation;
		this.alias = Alias.CANTON;
	}

	/**
	 * @return the input stream containing the keystore.
	 * @throws IOException if an I/O error occurs during the creation of the input stream.
	 */
	public InputStream getCantonKeyStore() throws IOException {
		return Files.newInputStream(Paths.get(keystoreLocation));
	}

	/**
	 * @return a new {@code char} array containing the keystore password characters.
	 * @throws IOException if an I/O error occurs reading the keystore password.
	 */
	public char[] getCantonKeystorePassword() throws IOException {
		final ImmutableByteArray bytes = new ImmutableByteArray(Files.readAllBytes(Paths.get(keystorePasswordLocation)));
		return ConversionUtils.byteArrayToCharArray(bytes);
	}

	/**
	 * @return the keystore alias {@link Alias#CANTON}.
	 */
	public Alias getKeystoreAlias() {
		return alias;
	}
}