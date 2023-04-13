/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import com.google.common.base.Strings;

import ch.post.it.evoting.dataintegrationservice.common.PropertyProvider;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.FrankingAreaType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterTypeWithRole;

public class FrankingAreaSplitterTypeImpl extends FrankingAreaSplitterType {
	@Override
	public String apply(final VoterTypeWithRole voterTypeWithRole, final String previousSplit) {
		try {
			if (PropertyProvider.getPropertyValue("diplomaticBag.ENABLED", "false").equalsIgnoreCase("TRUE") && !Strings
					.isNullOrEmpty(voterTypeWithRole.getDiplomaticBag())) {
				return voterTypeWithRole.getDiplomaticBag();
			} else {
				return voterTypeWithRole.getPerson().getPhysicalAddress().getFrankingArea().toString();
			}
		} catch (final Exception e) {
			return FrankingAreaType.OTHER.toString();
		}
	}
}
