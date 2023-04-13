/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CantonalTreeService {
	public void checkConsistency(final Optional<CantonalTree> optCantonalTree) {
		if (optCantonalTree.isPresent()) {
			final CantonalTree cantonalTree = optCantonalTree.get();

			checkState(!cantonalTree.getAllDomainOfInfluences().isEmpty(), "CantonalTree not consistent : DomainOfInfluences are not defined");

			checkState(!cantonalTree.getAllCountingCircles().isEmpty(), "CantonalTree not consistent : CountingCircles are not defined");

			checkState(cantonalTree.getAllDomainOfInfluences().stream().noneMatch(d -> !(d instanceof CountingCircle) && d.getType().equals("MU")),
					"CantonalTree not consistent : At least one municipality is not defined as a countingCircle");

			checkState(cantonalTree.getAllDomainOfInfluences().stream().filter(d -> d.getType().equals("CT")).count() == 1,
					"CantonalTree not consistent : Zero or many domainOfInfluence with Type CT defined");

			checkState(cantonalTree.getAllDomainOfInfluences().stream().filter(d -> d.getType().equals("CH")).count() == 1,
					"CantonalTree not consistent : Zero or many domainOfInfluence with Type CH defined");
		}
	}
}
