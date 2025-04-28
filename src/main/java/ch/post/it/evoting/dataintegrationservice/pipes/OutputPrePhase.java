/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.pipes;

public abstract class OutputPrePhase extends Tools {
	static AssigningAuthorizationPhase.Exports assigningAuthorizationPhase = AssigningAuthorizationPhase.exports;
	static AuthorizationPhase.Exports authorizationPhase = AuthorizationPhase.exports;
	static HeaderPhase.Exports headerPhase = HeaderPhase.exports;
	static ParameterPhase.Exports parameterPhase = ParameterPhase.exports;
}
