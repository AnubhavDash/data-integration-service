/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluenceId;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterDomainOfInfluence;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class DistrictAuthorizationAppenderTypeImpl extends DistrictAuthorizationAppenderType {

	private static final Logger LOGGER = LoggerFactory.getLogger(DistrictAuthorizationAppenderTypeImpl.class);

	@Override
	public VoterTypeWithRole apply(final VoterTypeWithRole voterTypeWithRole, final Optional<CantonalTree> cantonalTree) {

		if (voterTypeWithRole.getDomainOfInfluenceInfos().stream().allMatch(doiInfo -> doiInfo.getCountingCircle() != null)) {
			LOGGER.warn("This plugin does not work for voter defined in ech0045 version 4 and after format. Just bypassing voter. [voterId: {}]",
					voterTypeWithRole.getVoterIdentification());
		} else {
			checkState(cantonalTree.isPresent(), "a cantonal tree must be set to use this plugin");

			if (voterTypeWithRole.getDomainOfInfluenceInfos().stream().anyMatch(d -> d.getDomainOfInfluence().getType().equals(
					VoterDomainOfInfluence.VoterDomainOfInfluenceType.CT))) {

				final int municipalityId = voterTypeWithRole.getPerson().getMunicipality().getMunicipalityId();

				final Optional<DomainOfInfluence> muDoi = cantonalTree.get().getAllDomainOfInfluences().stream()
						.filter(d -> d.getCode().contains(String.format("MU-%s", municipalityId))).findFirst();

				checkState(muDoi.isPresent(), "Unable to find the DomainOfInfluence of the municipality of voter [voterId: %s]",
						voterTypeWithRole.getVoterIdentification());

				final LinkedList<VoterDomainOfInfluenceInfo> resultDoiInfos = new LinkedList<>(voterTypeWithRole.getDomainOfInfluenceInfos());

				for (final DomainOfInfluence bz : findBz(muDoi.get())) {
					final VoterDomainOfInfluence newDoi = new VoterDomainOfInfluence();
					newDoi.setType(VoterDomainOfInfluence.VoterDomainOfInfluenceType.valueOf(bz.getType()));
					newDoi.setLocalId(bz.getLocalId());

					final VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo = new VoterDomainOfInfluenceInfo();
					voterDomainOfInfluenceInfo.setDomainOfInfluence(newDoi);
					resultDoiInfos.add(voterDomainOfInfluenceInfo);
				}

				voterTypeWithRole.setDomainOfInfluenceInfos(resultDoiInfos);
			}
		}
		return voterTypeWithRole;
	}

	private List<DomainOfInfluence> findBz(final DomainOfInfluence muDoi) {
		final List<DomainOfInfluence> result = new ArrayList<>();
		for (final DomainOfInfluenceId doid : muDoi.getDomainOfInfluenceIds()) {
			if (doid.getParent() != null) {
				if (doid.getParent().getRelatedDomainOfInfluence().getType().equals("BZ")) {
					result.add(doid.getParent().getRelatedDomainOfInfluence());
				} else {
					result.addAll(findBz(doid.getParent().getRelatedDomainOfInfluence()));
				}
			}
		}

		return result.stream()
				.distinct()
				.collect(Collectors.toList());
	}
}
