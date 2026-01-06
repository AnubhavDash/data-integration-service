/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getProperty;

import java.util.Map;

import com.google.common.base.Strings;

import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.FrankingAreaType;

public class FrankingAreaSplitterTypeImpl extends FrankingAreaSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final Map<String, Object> context) {
		final boolean diplomaticBagEnabled = getProperty("diplomaticBag.enabled", Boolean.class);
		try {
			if (diplomaticBagEnabled && !Strings.isNullOrEmpty(voterTypeWithRole.getDiplomaticBag())) {
				return voterTypeWithRole.getDiplomaticBag();
			} else {
				return voterTypeWithRole.getPerson().getPhysicalAddress().getStructuredPhysicalAddress()!=null ?
						voterTypeWithRole.getPerson().getPhysicalAddress().getStructuredPhysicalAddress().getFrankingArea().toString() :
						voterTypeWithRole.getPerson().getPhysicalAddress().getUnstructuredPhysicalAddress().getFrankingArea().toString();
			}
		} catch (final Exception _) {
			return FrankingAreaType.OTHER.toString();
		}
	}
}
