/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingconfig;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoterTypeWithRole extends VoterType {

	private List<VoterDomainOfInfluenceInfo> domainOfInfluenceInfos;

	private List<String> availableCountingCircleIds;

	private boolean isTestOrControlUser;

	private String diplomaticBag;
}
