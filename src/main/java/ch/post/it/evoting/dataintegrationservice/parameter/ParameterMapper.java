/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.domain.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ElectoralAuthorityMembersType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.ElectoralAuthorityType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.IncumbentTextInfoType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.IncumbentTextType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.PropertyType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.domain.xmlns.evotingconfig.UiPropertiesType;

@Mapper(imports = UUID.class)
public interface ParameterMapper {
	ParameterMapper INSTANCE = Mappers.getMapper(ParameterMapper.class);

	default List<StandardAnswerType> mapStandard(final List<ch.evoting.xmlns.parameter._4.StandardAnswerType> list, final String questionIdentifier) {
		return list.stream().map(answer -> map(answer, StringUtil.generateUUIDFromString(questionIdentifier + "-" + answer.getAnswerPosition())))
				.toList();
	}

	default List<TiebreakAnswerType> mapTiebreak(final List<ch.evoting.xmlns.parameter._4.TiebreakAnswerType> list, final String questionIdentifier) {
		return list.stream().map(answer -> map(answer, StringUtil.generateUUIDFromString(questionIdentifier + "-" + answer.getAnswerPosition())))
				.toList();
	}

	@Mapping(target = "answerPosition", source = "a.answerPosition")
	@Mapping(target = "answerIdentification", source = "answerIdentifier")
	@Mapping(target = "answerInfo", source = "a.answerInfo")
	StandardAnswerType map(ch.evoting.xmlns.parameter._4.StandardAnswerType a, String answerIdentifier);

	@Mapping(target = "answerPosition", source = "a.answerPosition")
	@Mapping(target = "answerIdentification", source = "answerIdentifier")
	@Mapping(target = "answerInfo", source = "a.answerInfo")
	TiebreakAnswerType map(ch.evoting.xmlns.parameter._4.TiebreakAnswerType a, String answerIdentifier);

	AnswerInformationType map(ch.evoting.xmlns.parameter._4.AnswerInformationType value);

	LanguageType map(ch.evoting.xmlns.parameter._4.LanguageType value);

	ElectoralAuthorityType map(ch.evoting.xmlns.parameter._4.ElectoralAuthorityType oea);

	ElectoralAuthorityMembersType map(ch.evoting.xmlns.parameter._4.ElectoralAuthorityMembersType emt);

	UiPropertiesType map(ch.evoting.xmlns.parameter._4.UiPropertiesType value);

	@Mapping(target = "key", source = "key")
	@Mapping(target = "value", source = "value")
	PropertyType map(ch.evoting.xmlns.parameter._4.PropertyType value);

	IncumbentTextType map(ch.evoting.xmlns.parameter._4.ContestType.IncumbentText value);

	@Mapping(target = "language", source = "language")
	@Mapping(target = "incumbentText", source = "text")
	IncumbentTextInfoType map(ch.evoting.xmlns.parameter._4.IncumbentTextInfo value);
}
