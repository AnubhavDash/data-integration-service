/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectoralBoardMembersType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectoralBoardType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.IncumbentTextInfoType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.IncumbentTextType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.TiebreakAnswerType;

@Mapper(imports = UUID.class)
public interface ParameterMapper {
	ParameterMapper INSTANCE = Mappers.getMapper(ParameterMapper.class);

	default List<StandardAnswerType> mapStandard(final List<ch.evoting.xmlns.parameter._5.StandardAnswerType> list, final String questionIdentifier) {
		return list.stream().map(answer -> map(answer, StringUtil.generateUUIDFromString(questionIdentifier + "-" + answer.getAnswerPosition())))
				.toList();
	}

	default List<TiebreakAnswerType> mapTiebreak(final List<ch.evoting.xmlns.parameter._5.TiebreakAnswerType> list, final String questionIdentifier) {
		return list.stream().map(answer -> map(answer, StringUtil.generateUUIDFromString(questionIdentifier + "-" + answer.getAnswerPosition())))
				.toList();
	}

	@Mapping(target = "answerPosition", source = "a.answerPosition")
	@Mapping(target = "answerIdentification", source = "answerIdentifier")
	@Mapping(target = "answerInfo", source = "a.answerInfo")
	StandardAnswerType map(ch.evoting.xmlns.parameter._5.StandardAnswerType a, String answerIdentifier);

	@Mapping(target = "answerPosition", source = "a.answerPosition")
	@Mapping(target = "answerIdentification", source = "answerIdentifier")
	@Mapping(target = "answerInfo", source = "a.answerInfo")
	TiebreakAnswerType map(ch.evoting.xmlns.parameter._5.TiebreakAnswerType a, String answerIdentifier);

	AnswerInformationType map(ch.evoting.xmlns.parameter._5.AnswerInformationType value);

	LanguageType map(ch.evoting.xmlns.parameter._5.LanguageType value);

	ElectoralBoardType map(ch.evoting.xmlns.parameter._5.ElectoralBoardType oea);

	ElectoralBoardMembersType map(ch.evoting.xmlns.parameter._5.ElectoralBoardMembersType emt);

	IncumbentTextType map(ch.evoting.xmlns.parameter._5.ContestType.IncumbentText value);

	@Mapping(target = "language", source = "language")
	@Mapping(target = "incumbentText", source = "text")
	IncumbentTextInfoType map(ch.evoting.xmlns.parameter._5.IncumbentTextInfo value);
}
