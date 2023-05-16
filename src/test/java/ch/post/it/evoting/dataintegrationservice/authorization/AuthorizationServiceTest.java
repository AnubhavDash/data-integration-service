/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CountingCircleType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.DomainOfInfluenceType;

class AuthorizationServiceTest {

	AuthorizationService authorizationService = new AuthorizationService();

	@Test
	void appendAuthorization() {
		AuthorizationsType authorizations = new AuthorizationsType();
		final AuthorizationType auth = new AuthorizationType()
				.withAuthorizationIdentification("id")
				.withAuthorizationAlias("1234").withAuthorizationName("Toto").withAuthorizationObject(
						new AuthorizationObjectType()
								.withDomainOfInfluence(new DomainOfInfluenceType().withDomainOfInfluenceIdentification("doi"))
								.withCountingCircle(new CountingCircleType().withCountingCircleIdentification("cc"))
				);

		authorizations = authorizationService.appendAuthorization(authorizations, auth);
		authorizations = authorizationService.appendAuthorization(authorizations, auth);
		assertEquals(1, authorizations.getAuthorization().size());
	}

}