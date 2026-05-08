/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ExtendedAuthenticationKeyPlugin;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeyType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeysType;

public class EGovTokenAuthenticationKeyTypeImpl extends EGovTokenAuthenticationKeyType implements ExtendedAuthenticationKeyPlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(EGovTokenAuthenticationKeyTypeImpl.class);
	private static final String KEY_NAME = "eGovToken";
	private static final String DUMMY_TOKEN = "00000000000000000000000000000000";
	private static final int TOKEN_LENGTH = 32;

	private final boolean addDummyTokenIfNotProvided = ApplicationContextProvider.getProperty("eGovToken.addDummyTokenIfNotProvided", Boolean.class,
			false);

	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {
		final ExtendedAuthenticationKeysType keys = voterType.getExtendedAuthenticationKeys() != null ?
				voterType.getExtendedAuthenticationKeys() : new ExtendedAuthenticationKeysType();

		if (keys.getExtendedAuthenticationKey().stream().noneMatch(k -> k.getName().equals(KEY_NAME))) {
			final ExtendedAuthenticationKeyType key = new ExtendedAuthenticationKeyType();
			key.setName(KEY_NAME);

			final String token = voterType.getVoterExtension() != null ? voterType.getVoterExtension().getEGovToken() : null;

			if (addDummyTokenIfNotProvided) {
				key.setValue(token != null && !token.isEmpty() ? token : DUMMY_TOKEN);
			} else {
				checkState(token != null && !token.isEmpty(), "EGovToken is not defined in eCH0045 for voter. [voterIdentification: %s]",
						voterType.getVoterIdentification());
				if (DUMMY_TOKEN.equalsIgnoreCase(token)) {
					LOGGER.warn("Input eCH0045 contains dummy eGovToken values. [voterIdentification: {}]", voterType.getVoterIdentification());
				}
				key.setValue(token);
			}

			checkState(key.getValue().length() == TOKEN_LENGTH,
					"EGovToken length is invalid. [voterIdentification: %s, expected length: %s, actual length: %s]", voterType.getVoterIdentification(),
					TOKEN_LENGTH,
					key.getValue().length());

			keys.getExtendedAuthenticationKey().add(key);
		}
		voterType.setExtendedAuthenticationKeys(keys);
		return voterType;
	}

	@Override
	public List<String> getKeyNames() {
		return Collections.singletonList(KEY_NAME);
	}
}
