/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;

/**
 * Validates that the keystore contains the private key of this authority and the certificates of the other authorities.
 */
public final class KeyStoreValidator {

	private KeyStoreValidator() {
		// utility class
	}

	public static boolean validateKeyStore(final KeyStore keyStore, final Alias signingAlias) {
		final BooleanSupplier signerCertificatesPresent = () -> Arrays.stream(Alias.values())
				.allMatch(alias -> {
					try {
						return keyStore.getCertificate(alias.get()) != null;
					} catch (final KeyStoreException e) {
						return false;
					}
				});

		final BooleanSupplier signingKeyPresent = () -> Stream.of(signingAlias)
				.allMatch(alias -> {
					try {
						return keyStore.getKey(signingAlias.get(), "".toCharArray()) != null;
					} catch (final KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
						return false;
					}
				});

		return signerCertificatesPresent.getAsBoolean() && signingKeyPresent.getAsBoolean();
	}
}
