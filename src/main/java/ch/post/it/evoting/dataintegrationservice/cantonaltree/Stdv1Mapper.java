/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;

public class Stdv1Mapper {
	public static final Stdv1Mapper INSTANCE = new Stdv1Mapper();

	private static final Logger LOGGER = LoggerFactory.getLogger(Stdv1Mapper.class);

	private Stdv1Mapper() {
	}

	public Optional<CantonalTree> convert(final List<Stdv1DomainOfInfluence> dois, final List<Stdv1CountingCircle> ccs) {
		checkNotNull(dois);
		checkNotNull(ccs);

		if (dois.isEmpty() && ccs.isEmpty()) {
			return Optional.empty();
		} else {
			checkState(!dois.isEmpty(), "You must provide at least one domain of influence");
			checkState(!ccs.isEmpty(), "You must provide at least one counting circle");
			final CantonalTree result = new CantonalTree();

			for (final Stdv1DomainOfInfluence doi : dois) {
				if (result.hasDomainOfInfluence(doi.getCode())) {
					DomainOfInfluence doiResult = result.getDomainOfInfluence(
							doi.getCode());
					append(result, doiResult, doi);
					result.putDomainOfInfluence(doiResult);
				} else {
					final DomainOfInfluence doiResult = convert(result, doi);
					result.putDomainOfInfluence(doiResult);
				}
			}

			//from STDV1 format, the only DOI that has no description is FR-CH-1... Assigning it manually
			setFederalDescription(result, PropertyProvider.getPropertyValue("domainOfInfluence.CH-1.description"));

			for (final Stdv1CountingCircle cc : ccs) {
				if (cc.getOfsNumber() < Integer.parseInt(PropertyProvider.getPropertyValue("countingCircle.testControl.minValue"))) {
					DomainOfInfluence doi = result.getDomainOfInfluence(cc.getCode());
					doi = convert(doi, cc);
					result.putDomainOfInfluence(doi);
				} else {
					final CountingCircle specialCC = new CountingCircle(
							cc.getCantonCode(), "MU", String.valueOf(cc.getOfsNumber()), cc.getDescription(), Collections.emptyList(), cc.getCode(),
							cc.getDescription(), cc.getOfsNumber());
					result.putTestControlSupportCountingCircle(specialCC);
				}
			}

			return Optional.of(result);
		}
	}

	private void setFederalDescription(final CantonalTree result, final String description) {
		final Optional<DomainOfInfluence> federal = result.getAllDomainOfInfluences()
				.stream()
				.filter(doi -> doi.getType().equals("CH") && doi.getLocalId().equals("1")).findFirst();
		if (federal.isPresent()) {
			federal.get().setDescription(description);
		} else {
			LOGGER.warn("Unable to find the DomainOfInfluence FR-CH-1 to set its description");
		}
	}

	private CountingCircle convert(
			final DomainOfInfluence doi,
			final Stdv1CountingCircle cc) {
		final CountingCircle result = new CountingCircle(
				doi.getCantonAbbreviation(),
				doi.getType(),
				doi.getLocalId(),
				doi.getDescription(),
				doi.getDomainOfInfluenceIds(),
				cc.getCode(),
				cc.getDescription(),
				cc.getOfsNumber());
		for (final DomainOfInfluenceId doiId : doi.getDomainOfInfluenceIds()) {
			doiId.setRelatedDomainOfInfluence(result);
		}
		return result;
	}

	private void append(final CantonalTree cantonalTree, final DomainOfInfluence result, final Stdv1DomainOfInfluence doi) {
		final String[] split = splitCode(doi.getCode());
		result.setCantonAbbreviation(split[0]);
		result.setType(split[1]);
		result.setLocalId(split[2]);
		result.setDescription(doi.getDescription());

		if (result.getDomainOfInfluenceIds().stream().noneMatch(i -> i.getId().equals(doi.getId()))) {
			final DomainOfInfluenceId domainOfInfluenceId = append(cantonalTree, new DomainOfInfluenceId(), doi);
			domainOfInfluenceId.setRelatedDomainOfInfluence(result);
			result.getDomainOfInfluenceIds().add(domainOfInfluenceId);
		} else {
			final DomainOfInfluenceId domainOfInfluenceId = result.getDomainOfInfluenceIds().stream().filter(i -> i.getId().equals(doi.getId()))
					.findFirst()
					.orElseThrow();
			append(cantonalTree, domainOfInfluenceId, doi);
		}
	}

	private DomainOfInfluenceId append(final CantonalTree cantonalTree, final DomainOfInfluenceId domainOfInfluenceId,
			final Stdv1DomainOfInfluence doi) {
		domainOfInfluenceId.setId(doi.getId());
		domainOfInfluenceId.setParent(getOrCreateParentId(cantonalTree, doi.getParentId(), doi.getParentCode()));
		return domainOfInfluenceId;
	}

	DomainOfInfluence convert(final CantonalTree cantonalTree, final Stdv1DomainOfInfluence doi) {
		final DomainOfInfluence result = new DomainOfInfluence();
		append(cantonalTree, result, doi);
		return result;
	}

	private DomainOfInfluenceId getOrCreateParentId(final CantonalTree cantonalTree, final String parentId, final String parentCode) {
		if (cantonalTree.hasDomainOfInfluence(parentCode)) {
			final DomainOfInfluence doi = cantonalTree.getDomainOfInfluence(parentCode);
			if (doi.getDomainOfInfluenceIds().stream().noneMatch(i -> i.getId().equals(parentId))) {
				final DomainOfInfluenceId newDomainOfInfluenceId = new DomainOfInfluenceId();
				newDomainOfInfluenceId.setId(parentId);
				newDomainOfInfluenceId.setRelatedDomainOfInfluence(doi);
				doi.getDomainOfInfluenceIds().add(newDomainOfInfluenceId);
				return newDomainOfInfluenceId;
			} else {
				return doi.getDomainOfInfluenceIds().stream().filter(i -> i.getId().equals(parentId)).findFirst().orElseThrow();
			}
		} else {
			final DomainOfInfluence doi = new DomainOfInfluence();
			final String[] split = splitCode(parentCode);
			doi.setCantonAbbreviation(split[0]);
			doi.setType(split[1]);
			doi.setLocalId(split[2]);
			final DomainOfInfluenceId newDomainOfInfluenceId = new DomainOfInfluenceId();
			newDomainOfInfluenceId.setId(parentId);
			newDomainOfInfluenceId.setRelatedDomainOfInfluence(doi);
			doi.getDomainOfInfluenceIds().add(newDomainOfInfluenceId);
			cantonalTree.putDomainOfInfluence(doi);
			return newDomainOfInfluenceId;
		}
	}

	String[] splitCode(final String code) {
		return code.split("-");
	}
}
