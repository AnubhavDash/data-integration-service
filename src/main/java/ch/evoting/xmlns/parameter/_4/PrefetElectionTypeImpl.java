/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import static com.google.common.base.Preconditions.checkState;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;

public class PrefetElectionTypeImpl extends PrefetElectionType {
	@Override
	public ContestType apply(final ContestType contestType) {
		//add referenceOnPosition to candidate and then set position
		contestType.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.filter(e -> this.getElectionId().contains(e.getElection().getElectionIdentification()))
				.forEach(e ->
				{
					e.getCandidate().forEach(c ->
					{
						final Optional<ListType> list = e.getList().stream().filter(l -> !l.isListEmpty() && l.getCandidatePosition().stream()
								.anyMatch(cp -> cp.getCandidateIdentification().equals(c.getCandidateIdentification()))).findFirst();

						checkState(list.isPresent(), "Unable to find related list to candidate. [candidateIdentification: %s]",
								c.getCandidateIdentification());
						final Optional<CandidatePositionType> cp = list.get().getCandidatePosition().stream()
								.filter(p -> p.getCandidateIdentification().equals(c.getCandidateIdentification())).findFirst();

						checkState(cp.isPresent(), "Unable to find the candidate. [candidateIdentification: %s]", c.getCandidateIdentification());
						c.setReferenceOnPosition(cp.get().getCandidateReferenceOnPosition());
					});
					final int[] position = new int[] { 1 };
					e.getCandidate().stream().sorted(Comparator.comparing(CandidateType::getReferenceOnPosition))
							.forEach(c -> c.setPosition(BigInteger.valueOf(position[0]++)));
				});

		//remove the non-empty lists
		contestType.getElectionGroupBallot().stream().flatMap(electionGroupBallotType -> electionGroupBallotType.getElectionInformation().stream())
				.filter(e -> this.getElectionId().contains(e.getElection().getElectionIdentification()))
				.forEach(e -> e.setList(e.getList().stream()
						.filter(ListType::isListEmpty)
						.collect(Collectors.toList())));
		return contestType;
	}
}
