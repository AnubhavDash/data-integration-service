/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.domain;

import java.util.List;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType;

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
