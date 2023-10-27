/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.evoting.xmlns.parameter._4.ContestType;
import ch.evoting.xmlns.parameter._4.Parameter;
import ch.post.it.evoting.dataintegrationservice.parameter.ParameterLoader;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ContestMapper;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterCategorizer;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterWithRoleMapper;

@Component
public class ParameterPhaseComponent {

	private final ParameterLoader parameterLoader;

	public ParameterPhaseComponent(final ParameterLoader parameterLoader) {
		this.parameterLoader = parameterLoader;
	}

	public Function<Parameter, List<ContestMapper>> getAfterReadContestPlugins() {
		return p -> {
			if (p.getPlugins().getAfterReadContest() != null) {
				return p.getPlugins().getAfterReadContest().getPlugins().stream().map(ContestMapper.class::cast).toList();
			} else {
				return Collections.emptyList();
			}
		};
	}

	public Function<Parameter, List<VoterCategorizer>> getConfigSplitPlugins() {
		return p -> {
			if (p.getPlugins().getConfigSplitter() != null) {
				return p.getPlugins().getConfigSplitter().getPlugins();
			} else {
				return Collections.emptyList();
			}
		};
	}

	public Function<Parameter, ContestType> getContestParameters() {
		return Parameter::getContest;
	}

	public Function<String, Parameter> loadParameterV1() {
		return parameterLoader::loadParameter;
	}

	public Function<Parameter, List<VoterWithRoleMapper>> getAfterReadVoterPlugins() {
		return p -> {
			if (p.getPlugins().getAfterReadVoter() != null) {
				return p.getPlugins().getAfterReadVoter().getPlugins();
			} else {
				return Collections.emptyList();
			}
		};
	}

}
