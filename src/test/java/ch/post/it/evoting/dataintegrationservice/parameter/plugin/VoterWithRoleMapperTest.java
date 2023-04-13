/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterWithRoleMapper;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

class VoterWithRoleMapperTest {

	@Test
	void apply() {
		//check that object is returned without any change (identity function)
		VoterWithRoleMapper mapper = new VoterWithRoleMapper();
		assertNull(mapper.apply(null));

		VoterTypeWithRole voter = mapper.apply(new VoterTypeWithRole());
		assertNotNull(voter);
		assertNull(voter.getAuthorization());
		assertNull(voter.getDomainOfInfluenceInfos());
		assertNull(voter.getExtendedAuthenticationKeys());
		assertNull(voter.getPerson());
		assertNull(voter.getVoterIdentification());
		assertNull(voter.getVoterType());
	}

}