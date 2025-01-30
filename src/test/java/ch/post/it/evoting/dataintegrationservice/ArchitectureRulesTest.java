/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "ch.post.it.evoting.dataintegrationservice")
public class ArchitectureRulesTest {

	@ArchTest
	static final ArchRule NO_CLASSES_SHOULD_CALL_TO_LOWER_CASE_WITHOUT_LOCALE = noClasses().should().callMethod(String.class, "toLowerCase");

	@ArchTest
	static final ArchRule NO_CLASSES_SHOULD_CALL_TO_UPPER_CASE_WITHOUT_LOCALE = noClasses().should().callMethod(String.class, "toUpperCase");

	@ArchTest
	static final ArchRule NO_CLASSES_SHOULD_CALL_LOCAL_DATE_NOW = noClasses().should().callMethod(LocalDate.class, "now");

	@ArchTest
	static final ArchRule NO_CLASSES_SHOULD_CALL_LOCAL_DATE_TIME_NOW = noClasses().should().callMethod(LocalDateTime.class, "now");
}
