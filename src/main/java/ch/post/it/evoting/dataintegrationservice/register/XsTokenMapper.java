/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Qualifier;

import ch.post.it.evoting.evotinglibraries.domain.validations.Validations;

public class XsTokenMapper {

	@XsTokenMapper.XsTokenMapping
	public static String mapXsToken(final String xsToken) {
		return Validations.validateXsToken(xsToken);
	}

	@Qualifier
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.CLASS)
	public @interface XsTokenMapping {
	}

}
