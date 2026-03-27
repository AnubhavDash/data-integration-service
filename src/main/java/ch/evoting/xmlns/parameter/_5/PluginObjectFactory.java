/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.evoting.xmlns.parameter._5;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class PluginObjectFactory extends ObjectFactory {

	@Override
	public BirthYearAuthenticationKeyType createBirthYearAuthenticationKeyType() {
		return new BirthYearAuthenticationKeyTypeImpl();
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
	public ResidenceSplitterType createResidenceSplitterType() {
		return new ResidenceSplitterTypeImpl();
	}

	@Override
	public LanguageOfCorrespondanceSplitterType createLanguageOfCorrespondanceSplitterType() {
		return new LanguageOfCorrespondanceSplitterTypeImpl();
	}

	@Override
	public TestAuthorizationSplitterType createTestAuthorizationSplitterType() {
		return new TestAuthorizationSplitterTypeImpl();
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
	public VotePositionSetterType createVotePositionSetterType() {
		return new VotePositionSetterTypeImpl();
	}

	@Override
	public EGovTokenAuthenticationKeyType createEGovTokenAuthenticationKeyType() {
		return new EGovTokenAuthenticationKeyTypeImpl();
	}
}
