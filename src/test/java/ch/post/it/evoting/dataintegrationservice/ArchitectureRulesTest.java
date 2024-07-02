/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "ch.post.it.evoting.cryptoprimitives")
public class ArchitectureRulesTest {

	@ArchTest
	static final ArchRule NO_CLASSES_SHOULD_CALL_TO_LOWER_CASE_WITHOUT_LOCALE = noClasses().should().callMethod(String.class, "toLowerCase");

	@ArchTest
	static final ArchRule NO_CLASSES_SHOULD_CALL_TO_UPPER_CASE_WITHOUT_LOCALE = noClasses().should().callMethod(String.class, "toUpperCase");
}
