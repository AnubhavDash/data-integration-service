/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes;

import java.util.List;

import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType;

import reactor.core.publisher.Flux;

public abstract class AuthorizationPhase extends Tools {
	public static final Exports exports = new Exports();
	static RegisterPhase.Exports registerPhase = RegisterPhase.exports;
	static ContestPhase.Exports contestPhase = ContestPhase.exports;
	static ParameterPhase.Exports parameterPhase = ParameterPhase.exports;

	public static class Exports {
		private Flux<AuthorizationsType> authorizations;
		private Flux<List<String>> contestUsedDOIs;

		public Flux<List<String>> getContestUsedDOIs() {
			return contestUsedDOIs;
		}

		public void setContestUsedDOIs(final Flux<List<String>> contestUsedDOIs) {
			this.contestUsedDOIs = contestUsedDOIs;
		}

		public Flux<AuthorizationsType> getAuthorizations() {
			return authorizations;
		}

		public void setAuthorizations(final Flux<AuthorizationsType> authorizations) {
			this.authorizations = authorizations;
		}
	}
}
