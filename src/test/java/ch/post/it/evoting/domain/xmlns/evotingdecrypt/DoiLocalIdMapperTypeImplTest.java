/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingdecrypt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.evoting.xmlns.parameter._4.DoiLocalIdMapperType;
import ch.evoting.xmlns.parameter._4.DoiLocalIdMapperTypeImpl;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterDomainOfInfluence;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

class DoiLocalIdMapperTypeImplTest {

	final private String type = "CT";
	final private String oldValue = "2";
	final private String newValue = "1";
	private final VoterTypeWithRole voter = new VoterTypeWithRole();
	private final CantonalTree cantonalTree = new CantonalTree();
	private DoiLocalIdMapperType plugin;

	@BeforeEach
	public void setUp() {
		plugin = new DoiLocalIdMapperTypeImpl()
				.withType(type)
				.withOldValue(oldValue)
				.withNewValue(newValue);

		VoterDomainOfInfluence doi = new VoterDomainOfInfluence();
		doi.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type));
		doi.setLocalId(oldValue);

		VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();
		voterDomainOfInfluenceInfo.setDomainOfInfluence(doi);

		voter.setDomainOfInfluenceInfos(Collections.singletonList(voterDomainOfInfluenceInfo));
	}

	@Test
	void apply() {
		VoterDomainOfInfluence doi = voter.getDomainOfInfluenceInfos().stream().filter(d -> d.getDomainOfInfluence().getType().equals(
						VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) && d.getDomainOfInfluence().getLocalId().equals(oldValue))
				.findFirst().get().getDomainOfInfluence();
		assertEquals(doi.getLocalId(), oldValue);

		plugin.apply(voter, Optional.of(cantonalTree));

		assertEquals(0, voter.getDomainOfInfluenceInfos().stream()
				.filter(d -> d.getDomainOfInfluence().getType().equals(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) &&
						d.getDomainOfInfluence().getLocalId().equals(oldValue)).count());

		assertEquals(1, voter.getDomainOfInfluenceInfos().stream()
				.filter(d -> d.getDomainOfInfluence().getType().equals(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) &&
						d.getDomainOfInfluence().getLocalId().equals(newValue)).count());
	}

}