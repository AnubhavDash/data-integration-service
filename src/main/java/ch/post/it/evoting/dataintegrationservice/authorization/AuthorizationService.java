/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.authorization;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.domain.VoterCountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CountingCircleType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.DomainOfInfluenceType;

import reactor.util.function.Tuples;

@Service
public class AuthorizationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

	private static final int GRACE_PERIOD = Integer.parseInt(PropertyProvider.getPropertyValue("authorization.gracePeriod", "900"));

	private static final Integer MIN_TEST_CONTROL_VALUE = Integer.parseInt(PropertyProvider.getPropertyValue("countingCircle.testControl.minValue"));

	private static final boolean COUNTING_CIRCLE_ALL_TEST = Boolean.parseBoolean(PropertyProvider.getPropertyValue("countingCircle.allTest"));

	private HashMap<String, Integer> authorizationTypePriority;
	private final Comparator<AuthorizationObjectType> authorizationObjectTypeComparator = (o1, o2) -> {
		Integer prio1 = authorizationTypePriority.get(o1.getDomainOfInfluence().getDomainOfInfluenceType());
		Integer prio2 = authorizationTypePriority.get(o2.getDomainOfInfluence().getDomainOfInfluenceType());
		if (prio1 == null) {
			prio1 = Integer.MAX_VALUE;
		}
		if (prio2 == null) {
			prio2 = Integer.MAX_VALUE;
		}
		return prio1.compareTo(prio2);
	};
	private final Comparator<VoterDomainOfInfluenceInfo> voterDomainOfInfluenceInfoComparator = (doiInfo1, doiInfo2) -> {
		Integer prio1 = authorizationTypePriority.get(doiInfo1.getDomainOfInfluence().getType().name());
		Integer prio2 = authorizationTypePriority.get(doiInfo2.getDomainOfInfluence().getType().name());
		if (prio1 == null) {
			prio1 = Integer.MAX_VALUE;
		}
		if (prio2 == null) {
			prio2 = Integer.MAX_VALUE;
		}
		return prio1.compareTo(prio2);
	};

	public AuthorizationService() {
		initializeAuthorizationTypePriority();
	}

	public void initializeAuthorizationTypePriority() {
		authorizationTypePriority = new HashMap<>();
		authorizationTypePriority.put("CH", 0);
		authorizationTypePriority.put("CT", 1);
		authorizationTypePriority.put("BZ", 2);
		authorizationTypePriority.put("MU", 3);
		authorizationTypePriority.put("SC", 4);
		authorizationTypePriority.put("KI", 5);
		authorizationTypePriority.put("OG", 6);
		authorizationTypePriority.put("KO", 7);
		authorizationTypePriority.put("SK", 8);
		authorizationTypePriority.put("AN", 9);
	}

	public AuthorizationType build(final VoterTypeWithRole voter, final List<String> usedDOIids,
			final ContestType contest, final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {

		final AuthorizationType result = new AuthorizationType();

		//DOI and CC are given from the voter information (from ech-0045 v4)
		result.getAuthorizationObject().addAll(new LinkedList<>(voter.getDomainOfInfluenceInfos().stream()
				.filter(doiInfo -> usedDOIids.contains(doiInfo.getDomainOfInfluence().getLocalId())).map(doiInfo -> map(voter, doiInfo))
				.collect(Collectors.toMap(
						ao -> ao.getDomainOfInfluence().getDomainOfInfluenceIdentification() + "|" + ao.getCountingCircle()
								.getCountingCircleIdentification(),
						Function.identity(),
						(ao1, ao2) -> {
							LOGGER.warn("A voter with multiple identical domainOfInfluenceInfo found. Taking only the first one. [voterId: {}].",
									voter.getVoterIdentification());
							return ao1;
						}
				)).values()));

		result.setAuthorizationName(
				voter.getDomainOfInfluenceInfos().stream().filter(doiInfo -> usedDOIids.contains(doiInfo.getDomainOfInfluence().getLocalId()))
						.map(VoterDomainOfInfluenceInfo::getCountingCircle).map(VoterCountingCircle::getName).distinct()
						.reduce("", (cc1, cc2) -> StringUtil.concat(cc1, cc2, " | ")));

		result.setAuthorizationIdentification(StringUtil.generateUUIDFromString(
				voter.getDomainOfInfluenceInfos().stream().filter(doiInfo -> usedDOIids.contains(doiInfo.getDomainOfInfluence().getLocalId()))
						.sorted(voterDomainOfInfluenceInfoComparator)
						.map(doiInfo -> Tuples.of(doiInfo.getDomainOfInfluence().getLocalId(), doiInfo.getCountingCircle().getId()))
						.map(tuple -> StringUtil.concat(tuple.getT1(), tuple.getT2(), "|"))
						.reduce("", (s1, s2) -> StringUtil.concat(s1, s2, "+"))));

		result.setAuthorizationAlias(result.getAuthorizationIdentification());

		checkAuthorizationAliasLength(result);

		if (contestParameters.getEvotingTestBallotBoxesFromDate() != null
				&& contestParameters.getEvotingTestBallotBoxesFromDate().compare(contest.getEvotingFromDate()) > 0) {
			LOGGER.warn(
					"The test ballot boxes (evotingTestBallotBoxesFromDate ) open after the productive ballot boxes (evotingFromDate). Are you sure that this is the intended configuration?");
		}

		result.setAuthorizationTest(
				COUNTING_CIRCLE_ALL_TEST || voter.getDomainOfInfluenceInfos().stream().map(VoterDomainOfInfluenceInfo::getCountingCircle)
						.filter(cc -> StringUtil.isInteger(cc.getId())).anyMatch(cc -> Integer.parseInt(cc.getId()) >= MIN_TEST_CONTROL_VALUE));
		result.setAuthorizationFromDate(result.isAuthorizationTest() && contestParameters.getEvotingTestBallotBoxesFromDate() != null ?
				contestParameters.getEvotingTestBallotBoxesFromDate() :
				contest.getEvotingFromDate());
		result.setAuthorizationToDate(contest.getEvotingToDate());
		result.setAuthorizationGracePeriod(GRACE_PERIOD);

		return result;
	}

	private void checkAuthorizationAliasLength(final AuthorizationType result) {
		if (result.getAuthorizationAlias().length() > 100) {
			LOGGER.warn("Authorization alias ({}) is too long. Cutting it to get only 100 characters", result.getAuthorizationAlias());
			result.setAuthorizationAlias(result.getAuthorizationAlias().substring(0, 97) + "...");
		}
	}

	private AuthorizationObjectType map(final VoterTypeWithRole voter, final VoterDomainOfInfluenceInfo voterDomainOfInfluenceInfo) {
		checkNotNull(voter, "voter cannot be null");
		checkNotNull(voterDomainOfInfluenceInfo, "voterDomainOfInfluenceInfo cannot be null [voterId: %s].", voter.getVoterIdentification());
		checkNotNull(voterDomainOfInfluenceInfo.getDomainOfInfluence(), "voterDomainOfInfluence cannot be null [voterId: %s].",
				voter.getVoterIdentification());
		checkNotNull(voterDomainOfInfluenceInfo.getCountingCircle(), "voterCountingCircle cannot be null  [voterId: %s].",
				voter.getVoterIdentification());

		final AuthorizationObjectType ao = new AuthorizationObjectType();

		final DomainOfInfluenceType doi = new DomainOfInfluenceType();
		doi.setDomainOfInfluenceIdentification(voterDomainOfInfluenceInfo.getDomainOfInfluence().getLocalId());
		doi.setDomainOfInfluenceType(voterDomainOfInfluenceInfo.getDomainOfInfluence().getType().name());
		doi.setDomainOfInfluenceName(doi.getDomainOfInfluenceIdentification());

		final CountingCircleType cc = new CountingCircleType();
		cc.setCountingCircleIdentification(voterDomainOfInfluenceInfo.getCountingCircle().getId());
		cc.setCountingCircleName(voterDomainOfInfluenceInfo.getCountingCircle().getName());

		ao.setDomainOfInfluence(doi);
		ao.setCountingCircle(cc);

		return ao;
	}

	public AuthorizationsType appendAuthorization(final AuthorizationsType authorizations, final AuthorizationType authorization) {
		if (!authorization.getAuthorizationObject().isEmpty() && authorizations.getAuthorization().stream().noneMatch(same(authorization))) {
			authorization.getAuthorizationObject().sort(authorizationObjectTypeComparator);
			authorizations.getAuthorization().add(authorization);
		}
		return authorizations;
	}

	private Predicate<AuthorizationType> same(final AuthorizationType other) {
		return (AuthorizationType item) -> item != null && other != null && item.getAuthorizationIdentification() != null
				&& item.getAuthorizationIdentification().equals(other.getAuthorizationIdentification());
	}

	public List<String> mapDomainOfInfluenceIds(final ContestType contest) {
		final ArrayList<String> result = new ArrayList<>();

		contest.getElectionGroupBallot().forEach(e -> result.add(e.getDomainOfInfluence()));
		contest.getVoteInformation().forEach(v -> result.add(v.getVote().getDomainOfInfluence()));
		return result;
	}

	public void checkAuthorizations(final AuthorizationsType authorizations, final List<String> usedDOIIds) {
		authorizations.getAuthorization().stream()
				.flatMap(auths -> auths.getAuthorizationObject().stream())
				.map(AuthorizationObjectType::getCountingCircle)
				.collect(Collectors.groupingBy(
						CountingCircleType::getCountingCircleIdentification,
						Collectors.toSet()))
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().stream().map(CountingCircleType::getCountingCircleName).distinct().count() > 1)
				.forEach(entry -> {
					throw new IllegalStateException(
							String.format("Different counting circles are defined with the same id [countingCircleId: %s, countingCircleNames: [%s]]",
									entry.getKey(),
									String.join(", ", entry.getValue().stream().map(CountingCircleType::getCountingCircleName).toList())));
				});

		final List<String> doiInAuthorizations = authorizations.getAuthorization().stream().flatMap(a -> a.getAuthorizationObject().stream())
				.map(ao -> ao.getDomainOfInfluence().getDomainOfInfluenceIdentification()).toList();

		checkState(!doiInAuthorizations.isEmpty(), "No validVoters are defined !");

		for (final String doi : usedDOIIds) {
			if (!doiInAuthorizations.contains(doi)) {
				LOGGER.warn("The domainOfInfluence {} defined in contest is not linked to any voters from register", doi);
			}
		}
	}

	public VoterTypeWithRole assignAuthorization(final VoterTypeWithRole voter, final AuthorizationsType authorizations,
			final List<String> contestUsedDOIs, final ContestType contest,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {

		final AuthorizationType auth = build(voter, contestUsedDOIs, contest, contestParameters);

		//only for valid voters (which have an authorization with objects)
		if (!auth.getAuthorizationObject().isEmpty()) {
			final Optional<AuthorizationType> optionalFoundAuth = authorizations.getAuthorization().stream().filter(same(auth)).findFirst();
			checkState(optionalFoundAuth.isPresent(), "Unable to retrieve the authorization for voter [voterId: %s]", voter.getVoterIdentification());

			voter.setAuthorization(optionalFoundAuth.get().getAuthorizationIdentification());
			voter.setTestOrControlUser(optionalFoundAuth.get().isAuthorizationTest());
		}

		return voter;
	}

	public AuthorizationsType uniqueAuthorizationNames(final AuthorizationsType authorizationsType) {

		final Map<String, List<AuthorizationType>> authByName = authorizationsType.getAuthorization().stream()
				.collect(Collectors.groupingBy(AuthorizationType::getAuthorizationName));

		for (final List<AuthorizationType> values : authByName.values()) {
			if (values.size() > 1) {
				int cpt = 1;
				for (final AuthorizationType auth : values) {
					if (cpt > 1) {
						auth.setAuthorizationName(String.format("%s_%d", auth.getAuthorizationName(), cpt));
					}
					cpt++;
				}
			}
		}
		return authorizationsType;
	}

	public List<String> getUsedDois(final AuthorizationsType authorizations) {
		return authorizations.getAuthorization().stream()
				.flatMap(authorizationType -> authorizationType.getAuthorizationObject().stream())
				.map(authorizationObjectType -> authorizationObjectType.getDomainOfInfluence().getDomainOfInfluenceIdentification())
				.distinct()
				.toList();
	}

	public static void warnIfAllCountingCircleAsTestIsConfigured() {
		if (COUNTING_CIRCLE_ALL_TEST) {
			LOGGER.warn("As defined in the configuration, all counting circles will be treated as test ones");
		}
	}
}
