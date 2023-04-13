/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CantonalTree {
	private final HashMap<String, DomainOfInfluence> allElements = new HashMap<>();

	private final List<CountingCircle> testControlSupportCountingCircles = new ArrayList<>();
	private boolean upToDate = false;
	private Collection<CountingCircle> cacheCC;

	public DomainOfInfluence getDomainOfInfluence(final String code) {
		return allElements.get(code);
	}

	public boolean hasDomainOfInfluence(final String code) {
		return allElements.containsKey(code);
	}

	public void putDomainOfInfluence(final DomainOfInfluence doi) {
		upToDate = false;
		allElements.put(doi.getCode(), doi);
	}

	public Collection<DomainOfInfluence> getAllDomainOfInfluences() {
		return allElements.values();
	}

	public void putTestControlSupportCountingCircle(final CountingCircle testControlSupportCC) {
		upToDate = false;
		testControlSupportCountingCircles.add(testControlSupportCC);
	}

	public Collection<CountingCircle> getAllCountingCircles() {
		if (upToDate) {
			return cacheCC;
		} else {
			cacheCC = new LinkedList<>(allElements.values().stream()
					.filter(CountingCircle.class::isInstance)
					.map(CountingCircle.class::cast)
					.toList());
			cacheCC.addAll(testControlSupportCountingCircles);
			upToDate = true;
			return cacheCC;
		}
	}
}
