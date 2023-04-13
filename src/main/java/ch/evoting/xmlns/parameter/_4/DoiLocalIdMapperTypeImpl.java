/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import java.util.LinkedList;
import java.util.Optional;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterDomainOfInfluence;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterTypeWithRole;

public class DoiLocalIdMapperTypeImpl extends DoiLocalIdMapperType {
	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterTypeWithRole, final Optional<CantonalTree> cantonalTree) {

		final LinkedList<VoterDomainOfInfluenceInfo> resultDoiInfos = new LinkedList<>(voterTypeWithRole.getDomainOfInfluenceInfos());

		resultDoiInfos.stream()
				.filter(d -> d.getDomainOfInfluence().getType().equals(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(type)) &&
						oldValue.contains(d.getDomainOfInfluence().getLocalId()))
				.forEach(e -> e.getDomainOfInfluence().setLocalId(newValue));

		voterTypeWithRole.setDomainOfInfluenceInfos(resultDoiInfos);

		return voterTypeWithRole;
	}
}
