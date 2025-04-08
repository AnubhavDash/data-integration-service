/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

public class VotePositionSetterTypeImpl extends VotePositionSetterType {

	@Override
	public ContestType apply(final ContestType contestType) {
		this.getVote().forEach(tuple -> {
			final String voteIdentification = tuple.getVoteIdentification();
			final int position = tuple.getPosition() != null ? tuple.getPosition().intValueExact() : 0;

			contestType.getVoteInformation().stream().parallel()
					.map(VoteInformationType::getVote)
					.filter(v -> v.getVoteIdentification().equals(voteIdentification))
					.collect(MoreCollectors.toOptional())
					.orElseThrow(() -> new IllegalArgumentException(
							String.format("no vote found with given id. [voteIdentification: %s]", voteIdentification)))
					.setVotePosition(position);
		});

		return contestType;
	}
}
