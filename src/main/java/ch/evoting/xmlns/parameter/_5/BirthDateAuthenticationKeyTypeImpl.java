/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ExtendedAuthenticationKeyPlugin;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateUtils;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeyType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ExtendedAuthenticationKeysType;

public class BirthDateAuthenticationKeyTypeImpl extends BirthDateAuthenticationKeyType implements ExtendedAuthenticationKeyPlugin {

	private static final String KEY_NAME = "birthDate";

	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterType) {

		final ExtendedAuthenticationKeysType keys = voterType.getExtendedAuthenticationKeys() != null ?
				voterType.getExtendedAuthenticationKeys() : new ExtendedAuthenticationKeysType();

		if (keys.getExtendedAuthenticationKey().stream().noneMatch(k -> k.getName().equals(KEY_NAME))) {
			final ExtendedAuthenticationKeyType key = new ExtendedAuthenticationKeyType();
			key.setName(KEY_NAME);
			final LocalDate dateOfBirth = LocalDateUtils.fromXMLGregorianCalendar(voterType.getPerson().getDateOfBirth());
			key.setValue(dateOfBirth.format(LocalDateUtils.DATE_FORMATTER));
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
