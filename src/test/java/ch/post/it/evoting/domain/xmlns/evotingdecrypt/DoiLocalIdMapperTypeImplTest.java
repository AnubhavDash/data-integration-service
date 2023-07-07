/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingdecrypt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.evoting.xmlns.parameter._4.DoiLocalIdMapperType;
import ch.evoting.xmlns.parameter._4.DoiLocalIdMapperTypeImpl;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

class DoiLocalIdMapperTypeImplTest {

	final private String type = "CT";
	final private String oldValue = "2";
	final private String newValue = "1";
	private final VoterTypeWithRole voter = new VoterTypeWithRole();
	private DoiLocalIdMapperType plugin;

	@BeforeEach
	public void setUp() {
		plugin = new DoiLocalIdMapperTypeImpl()
				.withType(type)
				.withOldValue(oldValue)
				.withNewValue(newValue);

		final VoterDomainOfInfluence doi = new VoterDomainOfInfluence();
		doi.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type));
		doi.setLocalId(oldValue);

		final VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();
		voterDomainOfInfluenceInfo.setDomainOfInfluence(doi);

		voter.setDomainOfInfluenceInfos(Collections.singletonList(voterDomainOfInfluenceInfo));
	}

	@Test
	void apply() {
		final VoterDomainOfInfluence doi = voter.getDomainOfInfluenceInfos().stream().filter(d -> d.getDomainOfInfluence().getType().equals(
						VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) && d.getDomainOfInfluence().getLocalId().equals(oldValue))
				.findFirst().get().getDomainOfInfluence();
		assertEquals(doi.getLocalId(), oldValue);

		plugin.apply(voter);

		assertEquals(0, voter.getDomainOfInfluenceInfos().stream()
				.filter(d -> d.getDomainOfInfluence().getType().equals(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) &&
						d.getDomainOfInfluence().getLocalId().equals(oldValue)).count());

		assertEquals(1, voter.getDomainOfInfluenceInfos().stream()
				.filter(d -> d.getDomainOfInfluence().getType().equals(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) &&
						d.getDomainOfInfluence().getLocalId().equals(newValue)).count());
	}

}
