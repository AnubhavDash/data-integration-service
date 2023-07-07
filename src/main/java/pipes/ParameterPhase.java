/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.List;

import ch.evoting.xmlns.parameter._4.ContestType;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ContestMapper;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterAuthorizationMapper;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterCategorizer;
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.VoterWithRoleMapper;

import reactor.core.publisher.Flux;

public abstract class ParameterPhase extends Tools {

	public static final Exports exports = new Exports();
	static InputPhase.Exports inputPhase = InputPhase.exports;

	public static class Exports {
		private Flux<List<VoterWithRoleMapper>> afterReadVoterPlugins;
		private Flux<List<VoterCategorizer>> configSplitterPlugins;
		private Flux<ContestType> contestParameters;
		private Flux<List<ContestMapper>> afterReadContestPlugins;
		private Flux<List<VoterAuthorizationMapper>> afterReadVoterAuthorizationPlugins;

		public Flux<List<VoterWithRoleMapper>> getAfterReadVoterPlugins() {
			return afterReadVoterPlugins;
		}

		public void setAfterReadVoterPlugins(
				final Flux<List<VoterWithRoleMapper>> afterReadVoterPlugins) {
			this.afterReadVoterPlugins = afterReadVoterPlugins;
		}

		public Flux<List<VoterCategorizer>> getConfigSplitterPlugins() {
			return configSplitterPlugins;
		}

		public void setConfigSplitterPlugins(
				final Flux<List<VoterCategorizer>> configSplitterPlugins) {
			this.configSplitterPlugins = configSplitterPlugins;
		}

		public Flux<ContestType> getContestParameters() {
			return contestParameters;
		}

		public void setContestParameters(final Flux<ContestType> contestParameters) {
			this.contestParameters = contestParameters;
		}

		public Flux<List<ContestMapper>> getAfterReadContestPlugins() {
			return afterReadContestPlugins;
		}

		public void setAfterReadContestPlugins(
				final Flux<List<ContestMapper>> afterReadContestPlugins) {
			this.afterReadContestPlugins = afterReadContestPlugins;
		}

		public Flux<List<VoterAuthorizationMapper>> getAfterReadVoterAuthorizationPlugins() {
			return afterReadVoterAuthorizationPlugins;
		}

		public void setAfterReadVoterAuthorizationPlugins(
				final Flux<List<VoterAuthorizationMapper>> afterReadVoterAuthorizationPlugins) {
			this.afterReadVoterAuthorizationPlugins = afterReadVoterAuthorizationPlugins;
		}

	}
}
