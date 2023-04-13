/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import java.util.LinkedList;
import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterDomainOfInfluence;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class DoiLocalIdMunicipalityIdSetterTypeImpl extends DoiLocalIdMunicipalityIdSetterType {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterTypeWithRole, final Optional<CantonalTree> cantonalTree) {

		final LinkedList<VoterDomainOfInfluenceInfo> resultDoiInfos = new LinkedList<>(voterTypeWithRole.getDomainOfInfluenceInfos());

		resultDoiInfos.stream()
				.filter(d -> d.getDomainOfInfluence().getType().equals(VoterDomainOfInfluence.VoterDomainOfInfluenceType.MU))
				.forEach(d -> d.getDomainOfInfluence()
						.setLocalId(String.format("%s", voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId())));

		voterTypeWithRole.setDomainOfInfluenceInfos(resultDoiInfos);

		return voterTypeWithRole;
	}
}
