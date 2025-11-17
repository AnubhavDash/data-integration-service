/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

import static ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterCategorizer.CONTEXT_AUTHORIZATIONS;
import static ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterCategorizer.CONTEXT_PREVIOUS;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.common.StringUtil;
import ch.post.it.evoting.dataintegrationservice.contest.ContestService;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;
import ch.post.it.evoting.dataintegrationservice.output.ConfigService;
import ch.post.it.evoting.dataintegrationservice.output.InvalidVoterService;
import ch.post.it.evoting.dataintegrationservice.register.RegisterService;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.HeaderType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.RegisterType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

@Component
public class OutputPrePhaseComponent {

	private final ConfigService configService;
	private final ContestService contestService;
	private final RegisterService registerService;
	private final InvalidVoterService invalidVoterService;

	public OutputPrePhaseComponent(final ConfigService configService, final ContestService contestService, final RegisterService registerService,
			final InvalidVoterService invalidVoterService) {
		this.configService = configService;
		this.contestService = contestService;
		this.registerService = registerService;
		this.invalidVoterService = invalidVoterService;
	}

	public Function<VoterType, VoterType> anonymize() {
		return configService::anonymize;
	}

	public Function<ContestType, ContestType> sanitize() {
		return contestService::sanitize;
	}

	public Consumer<VoterTypeWithRole> checkRegisterConsistency() {
		return registerService::checkConsistency;
	}

	public Function<Flux<VoterType>, Flux<Stream<VoterType>>> collectStream() {
		return flux -> Flux.just(flux.toStream());
	}

	public Function<Flux<VoterType>, Mono<RegisterType>> buildInvalidRegister() {
		return flux -> Mono.just(invalidVoterService.buildInvalidRegister(flux.toStream()));
	}

	public BiFunction<Stream<VoterType>, Tuple3<ContestType, AuthorizationsType, HeaderType>, Configuration> buildConfig() {
		return (voters, tuple) -> configService.build(tuple.getT1(), tuple.getT2(), voters, tuple.getT3());
	}

	public Function<Tuple3<VoterTypeWithRole, List<BiFunction<VoterTypeWithRole, Map<String, Object>, String>>, AuthorizationsType>, String> splitWithPlugins() {
		return tuple -> tuple.getT2().stream().reduce((u, v) -> "", (f1, f2) -> (u, v) -> {
			final String resf1 = f1.apply(u, v);
			final Map<String, Object> map = Map.of(
					CONTEXT_PREVIOUS, resf1,
					CONTEXT_AUTHORIZATIONS, tuple.getT3()
			);
			final String resf2 = f2.apply(u, map);
			return StringUtil.concat(resf1, resf2, "-");
		}).apply(tuple.getT1(), Map.of());
	}
}
