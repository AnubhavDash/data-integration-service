/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

public class QuantitySplitterTypeImpl extends QuantitySplitterType {

	private final HashMap<String, AtomicInteger> counter = new HashMap<>();

	private int getCounterValue(final String key) {
		return counter.computeIfAbsent(key, k -> new AtomicInteger()).getAndIncrement();
	}

	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		return String.format("%s", getCounterValue(previousSplit) / maxPerFile);
	}
}
