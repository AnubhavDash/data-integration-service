/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import java.util.Collections;
import java.util.List;

import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ExtendedAuthenticationKeyPlugin;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ExtendedAuthenticationKeysType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

public class BirthYearAuthenticationKeyTypeImpl extends BirthYearAuthenticationKeyType implements ExtendedAuthenticationKeyPlugin {

	public static final String KEY_NAME = "birthYear";

	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {

		final ExtendedAuthenticationKeysType keys = voterType.getExtendedAuthenticationKeys() != null ?
				voterType.getExtendedAuthenticationKeys() : new ExtendedAuthenticationKeysType();

		if (keys.getExtendedAuthenticationKey().stream().noneMatch(k -> k.getName().equals(KEY_NAME))) {
			final ch.post.it.evoting.domain.xmlns.evotingconfig.ExtendedAuthenticationKeyType key = new ch.post.it.evoting.domain.xmlns.evotingconfig.ExtendedAuthenticationKeyType();
			key.setName(KEY_NAME);
			key.setValue(String.format("%d", voterType.getPerson().getDateOfBirth().getYear()));
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
