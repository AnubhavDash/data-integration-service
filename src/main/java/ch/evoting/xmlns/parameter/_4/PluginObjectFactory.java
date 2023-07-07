/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._4;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class PluginObjectFactory extends ObjectFactory {

	@Override
	public BirthYearAuthenticationKeyType createBirthYearAuthenticationKeyType() {
		return new BirthYearAuthenticationKeyTypeImpl();
	}

	@Override
	public DoiLocalIdMapperType createDoiLocalIdMapperType() {
		return new DoiLocalIdMapperTypeImpl();
	}

	@Override
	public FrankingAreaSplitterType createFrankingAreaSplitterType() {
		return new FrankingAreaSplitterTypeImpl();
	}

	@Override
	public QuantitySplitterType createQuantitySplitterType() {
		return new QuantitySplitterTypeImpl();
	}

	@Override
	public PrefetElectionType createPrefetElectionType() {
		return new PrefetElectionTypeImpl();
	}

	@Override
	public DoiLocalIdMunicipalityIdSetterType createDoiLocalIdMunicipalityIdSetterType() {
		return new DoiLocalIdMunicipalityIdSetterTypeImpl();
	}

	@Override
	public ResidenceSplitterType createResidenceSplitterType() {
		return new ResidenceSplitterTypeImpl();
	}

	@Override
	public VoterFieldModifierPluginType createVoterFieldModifierPluginType() {
		return new VoterFieldModifierTypeImpl();
	}

	@Override
	public LanguageOfCorrespondanceSplitterType createLanguageOfCorrespondanceSplitterType() {
		return new LanguageOfCorrespondanceSplitterTypeImpl();
	}

	@Override
	public BirthDateAuthenticationKeyType createBirthDateAuthenticationKeyType() {
		return new BirthDateAuthenticationKeyTypeImpl();
	}

	@Override
	public TestAndControlSplitterType createTestAndControlSplitterType() {
		return new TestAndControlSplitterTypeImpl();
	}

	@Override
	public VoterIdPrefixerType createVoterIdPrefixerType() {
		return new VoterIdPrefixerTypeImpl();
	}

	@Override
	public VotePositionSetterType createVotePositionSetterType() {
		return new VotePositionSetterTypeImpl();
	}
}
