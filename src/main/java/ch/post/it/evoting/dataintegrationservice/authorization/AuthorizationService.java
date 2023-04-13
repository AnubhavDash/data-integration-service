/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.authorization;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.CountingCircle;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.DomainOfInfluenceId;
import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;
import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.AuthorizationPlugin;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.CountingCircleFilter;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.CountingCircleSetter;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.CountingCircleType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.DomainOfInfluenceType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterCountingCircle;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterDomainOfInfluence;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class AuthorizationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

	private static final int GRACE_PERIOD = Integer.valueOf(PropertyProvider.getPropertyValue("authorization.gracePeriod", "900"));

	private static final Integer MIN_TEST_CONTROL_VALUE = Integer.parseInt(PropertyProvider.getPropertyValue("countingCircle.testControl.minValue"));

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

	public AuthorizationType build(final VoterTypeWithRole voter, final Optional<CantonalTree> cantonalTree, final List<String> usedDOIids,
			final ContestType contest,
			final List<AuthorizationPlugin> authorizationPlugins, final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {

		final AuthorizationType result = new AuthorizationType();

		if (voter.getDomainOfInfluenceInfos().stream().anyMatch(doiInfo -> doiInfo.getCountingCircle() == null)) {

			checkState(cantonalTree.isPresent(),
					"You should provide a cantonalTree when a user don't have all its counting circles defined [voterId: %s]",
					voter.getVoterIdentification());

			final List<CountingCircle> usedCountingCircles = new ArrayList<>();
			final List<Tuple2<DomainOfInfluence, CountingCircle>> authAlias = new ArrayList<>();

			List<CountingCircle> allCountingCircles = new LinkedList<>(cantonalTree.get().getAllCountingCircles());

			for (final VoterDomainOfInfluenceInfo voterdoiInfo : voter.getDomainOfInfluenceInfos()) {
				final List<CountingCircle> ccs;

				final CountingCircle filterCC = authorizationPlugins.stream().filter(CountingCircleSetter.class::isInstance)
						.map(CountingCircleSetter.class::cast).reduce((v, countingCircles) -> null, (bi1, bi2) -> (v, countingCircles) -> {
							final CountingCircle cc1 = bi1.apply(v, countingCircles);
							return cc1 != null ? cc1 : bi2.apply(v, countingCircles);
						}).apply(voter, allCountingCircles);

				if (voter.getAvailableCountingCircleIds() != null && !voter.getAvailableCountingCircleIds().isEmpty()) {
					ccs = allCountingCircles.stream()
							.filter(cc -> voter.getAvailableCountingCircleIds().contains(cc.getCcId().substring(cc.getCcId().indexOf("-") + 1)))
							.collect(Collectors.toList());
				} else {

					allCountingCircles = authorizationPlugins.stream().filter(CountingCircleFilter.class::isInstance)
							.map(CountingCircleFilter.class::cast).reduce((v, countingCircles) -> countingCircles,
									(bi1, bi2) -> (v, countingCircles) -> bi2.apply(v, bi1.apply(v, countingCircles)))
							.apply(voter, allCountingCircles);

					final boolean swissAbroadSpecificCCMode = contestParameters.getSwissAbroadCountingMode().getSpecificCountingCircle() != null;

					if (voter.getVoterType() == VoterTypeType.SWISSABROAD && swissAbroadSpecificCCMode) {
						final List<ch.evoting.xmlns.parameter._4.ContestType.SwissAbroadCountingMode.SpecificCountingCircle.Configuration> configs = contestParameters.getSwissAbroadCountingMode()
								.getSpecificCountingCircle().getConfiguration().stream().filter(c -> c.getVoteTypes().getVoteType().stream()
										.anyMatch(v -> v.value().equals(voterdoiInfo.getDomainOfInfluence().getType().name())))
								.filter(c -> c.getMunicipalities() == null || c.getMunicipalities().getMunicipalityId().stream()
										.anyMatch(m -> m.intValue() == voter.getPerson().getMunicipality().getMunicipalityId())).toList();

						checkState(configs.size() == 1,
								"Unable to define the CC to use for swissAbroad voter: [voterId: %s, voteType: %s, municipality: %s, possibleCCs: %s]",
								voter.getVoterIdentification(), voterdoiInfo.getDomainOfInfluence().getType(),
								voter.getPerson().getMunicipality().getMunicipalityId(), configs.stream()
										.map(ch.evoting.xmlns.parameter._4.ContestType.SwissAbroadCountingMode.SpecificCountingCircle.Configuration::getCountingCircleId)
										.toList());

						ccs = new LinkedList<>(
								allCountingCircles.stream().filter(c -> c.getCcId().equals(configs.get(0).getCountingCircleId())).toList());
					} else {
						ccs = new LinkedList<>(allCountingCircles.stream()
								.filter(c -> c.getMunicipalityId() == voter.getPerson().getMunicipality().getMunicipalityId()).toList());
						checkState(ccs.size() <= 1, "Many countingCircles found related to voter [voterId: %s]", voter.getVoterIdentification());
					}
				}

				checkState(!ccs.isEmpty(), "Unable to build the authorizations, because cannot find countingCircle related to voter [voterId: %s]",
						voter.getVoterIdentification());

				//sort counting circle by priority (smallest first KR->BZ->MU->CT-CH)
				ccs.sort((cc1, cc2) -> {
					Integer prio1 = authorizationTypePriority.get(cc1.getCcId().split("-")[1]);
					Integer prio2 = authorizationTypePriority.get(cc2.getCcId().split("-")[1]);
					if (prio1 == null) {
						prio1 = Integer.MAX_VALUE;
					}
					if (prio2 == null) {
						prio2 = Integer.MAX_VALUE;
					}
					return prio2.compareTo(prio1);
				});

				for (final CountingCircle cc : ccs) {
					for (final DomainOfInfluenceId id : cc.getDomainOfInfluenceIds()) {
						final DomainOfInfluenceId doiId = parseTree(id, voterdoiInfo.getDomainOfInfluence());
						if (doiId != null && usedDOIids.contains(doiId.getId()) && result.getAuthorizationObject().stream()
								.noneMatch(ao -> ao.getDomainOfInfluence().getDomainOfInfluenceIdentification().equals(doiId.getId()))) {
							final AuthorizationObjectType ao = new AuthorizationObjectType();

							final CountingCircle ccToLink = filterCC != null ? filterCC : cc;
							if (filterCC != null) {
								LOGGER.info("Changing the countingCircle with plugin for voter [voterId: {}] to {}}", voter.getVoterIdentification(),
										ccToLink.getCcId());
								checkState(filterCC.getMunicipalityId() >= MIN_TEST_CONTROL_VALUE,
										"Cannot change the countingCircle to a non test/control one [voterId: %s", voter.getVoterIdentification());
							}

							ao.setCountingCircle(new CountingCircleType().withCountingCircleIdentification(ccToLink.getCcId())
									.withCountingCircleName(ccToLink.getCcName()));
							ao.setDomainOfInfluence(new DomainOfInfluenceType().withDomainOfInfluenceIdentification(doiId.getId())
									.withDomainOfInfluenceName(String.format("%s-%s-%s", doiId.getRelatedDomainOfInfluence().getCantonAbbreviation(),
											doiId.getRelatedDomainOfInfluence().getType(), doiId.getRelatedDomainOfInfluence().getLocalId()))
									.withDomainOfInfluenceType(doiId.getRelatedDomainOfInfluence().getType()));

							usedCountingCircles.add(ccToLink);
							authAlias.add(Tuples.of(doiId.getRelatedDomainOfInfluence(), ccToLink));

							result.getAuthorizationObject().add(ao);
						}
					}
				}
			}

			result.setAuthorizationName(usedCountingCircles.stream().distinct().map(CountingCircle::getCcName)
					.reduce("", (cc1, cc2) -> StringUtil.concat(cc1, cc2, " | ")));

			result.setAuthorizationIdentification(StringUtil.generateUUIDFromString(authAlias.stream().sorted((o1, o2) -> {
						Integer prio1 = authorizationTypePriority.get(o1.getT1().getType());
						Integer prio2 = authorizationTypePriority.get(o2.getT2().getType());
						if (prio1 == null) {
							prio1 = Integer.MAX_VALUE;
						}
						if (prio2 == null) {
							prio2 = Integer.MAX_VALUE;
						}
						return prio1.compareTo(prio2);
					}).map(auth -> Tuples.of(auth.getT1().getCode(), auth.getT2().getCcId()))
					.map(tuple -> StringUtil.concat(tuple.getT1(), tuple.getT2(), "|")).reduce("", (s1, s2) -> StringUtil.concat(s1, s2, "+"))));

			result.setAuthorizationAlias(result.getAuthorizationIdentification());

			if (result.getAuthorizationAlias().length() > 100) {
				LOGGER.warn("Authorization alias ({}) is too long. Cutting it to get only 100 characters", result.getAuthorizationAlias());
				result.setAuthorizationAlias(result.getAuthorizationAlias().substring(0, 97) + "...");
			}

			result.setAuthorizationTest(usedCountingCircles.stream().anyMatch(cc -> cc.getMunicipalityId() >= MIN_TEST_CONTROL_VALUE));
			result.setAuthorizationFromDate(result.isAuthorizationTest() && contestParameters.getEvotingTestBallotBoxesFromDate() != null ?
					contestParameters.getEvotingTestBallotBoxesFromDate() :
					contest.getEvotingFromDate());
			result.setAuthorizationToDate(contest.getEvotingToDate());
			result.setAuthorizationGracePeriod(GRACE_PERIOD);
		} else {
			//DOI and CC are given from the voter information (from ech-0045 v4)
			result.getAuthorizationObject().addAll(voter.getDomainOfInfluenceInfos().stream()
					.filter(doiInfo -> usedDOIids.contains(doiInfo.getDomainOfInfluence().getLocalId())).map(doiInfo -> map(voter, doiInfo))
					.toList());

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

			if (result.getAuthorizationAlias().length() > 100) {
				LOGGER.warn("Authorization alias ({}) is too long. Cutting it to get only 100 characters", result.getAuthorizationAlias());
				result.setAuthorizationAlias(result.getAuthorizationAlias().substring(0, 97) + "...");
			}

			result.setAuthorizationTest(voter.getDomainOfInfluenceInfos().stream().map(VoterDomainOfInfluenceInfo::getCountingCircle)
					.filter(cc -> StringUtil.isInteger(cc.getId())).anyMatch(cc -> Integer.parseInt(cc.getId()) >= MIN_TEST_CONTROL_VALUE));
			result.setAuthorizationFromDate(result.isAuthorizationTest() && contestParameters.getEvotingTestBallotBoxesFromDate() != null ?
					contestParameters.getEvotingTestBallotBoxesFromDate() :
					contest.getEvotingFromDate());
			result.setAuthorizationToDate(contest.getEvotingToDate());
			result.setAuthorizationGracePeriod(GRACE_PERIOD);
		}

		return result;
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

	private DomainOfInfluenceId parseTree(final DomainOfInfluenceId doiId, final VoterDomainOfInfluence voterDomainOfInfluence) {
		if (doiId == null) {
			return null;
		}
		if (doiId.getRelatedDomainOfInfluence().getType().equals(voterDomainOfInfluence.getType().name()) && doiId.getRelatedDomainOfInfluence()
				.getLocalId().equals(voterDomainOfInfluence.getLocalId())) {
			return doiId;
		}
		return parseTree(doiId.getParent(), voterDomainOfInfluence);
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
			final Optional<CantonalTree> canton,
			final List<String> contestUsedDOIs, final ContestType contest, final List<AuthorizationPlugin> authorizationPlugins,
			final ch.evoting.xmlns.parameter._4.ContestType contestParameters) {

		final AuthorizationType auth = build(voter, canton, contestUsedDOIs, contest, authorizationPlugins, contestParameters);

		//only for valid voters (which have an authorization with objects)
		if (!auth.getAuthorizationObject().isEmpty()) {
			final Optional<AuthorizationType> optionalFoundAuth = authorizations.getAuthorization().stream().filter(same(auth)).findFirst();
			checkState(optionalFoundAuth.isPresent(), "Unable to retrieve the authorization for voter [voterId: %s]", voter.getVoterIdentification());

			voter.setAuthorization(optionalFoundAuth.get().getAuthorizationIdentification());
			voter.setTestOrControlUser(optionalFoundAuth.get().isAuthorizationTest());
		}

		return voter;
	}

	public void checkDomainOfInfluences(final Optional<CantonalTree> optCantonalTree, final List<String> usedDOIids) {
		if (optCantonalTree.isPresent()) {
			final CantonalTree cantonalTree = optCantonalTree.get();
			final List<String> cantonalDois = cantonalTree.getAllDomainOfInfluences().stream()
					.flatMap(d -> d.getDomainOfInfluenceIds().stream().map(DomainOfInfluenceId::getId)).toList();

			checkState(cantonalDois.containsAll(usedDOIids), "Some domain of influence defined in the contest are not defined in the cantonal tree.");
		}
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
}
