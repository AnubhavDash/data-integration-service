/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import java.util.Map;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;

public class TestAuthorizationSplitterTypeImpl extends TestAuthorizationSplitterType {

	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final Map<String, Object> context) {
		if (voterTypeWithRole.isTestOrControlUser()) {

			return ((AuthorizationsType) context.get(CONTEXT_AUTHORIZATIONS)).getAuthorization().stream()
					.filter(a -> a.getAuthorizationIdentification().equals(voterTypeWithRole.getAuthorization()))
					.findFirst()
					.map(AuthorizationType::getAuthorizationName)
					.orElse("");

		}
		return "";
	}
}
