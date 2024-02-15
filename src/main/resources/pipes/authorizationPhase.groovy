/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationsType

import static li.chee.reactive.plumber.Plumbing.*
import static pipes.Tools.logAsInfo
import static reactor.core.publisher.Flux.zip

def authorizationPhaseComponent = DataIntegrationServiceApplication.context.getBean(AuthorizationPhaseComponent.class)

def voters = tube {
	from AuthorizationPhase.registerPhase.voters
}

def contestUsedDOIs = pipe {
	from AuthorizationPhase.contestPhase.contest  \
     map authorizationPhaseComponent.mapDomainOfInfluenceIds()  \

}

def contestParameters = pipe {
	from AuthorizationPhase.parameterPhase.contestParameters
}

def buildAuthorizations = pipe {
	from zip(voters, value(contestUsedDOIs), value(AuthorizationPhase.contestPhase.contest), value(contestParameters))  \
     transformDeferred logAsInfo("Building the authorizations...")  \
     map authorizationPhaseComponent.buildAuthorization()  \
     reduce(new AuthorizationsType(), authorizationPhaseComponent.appendAuthorization())  \
     map authorizationPhaseComponent.uniqueAuthorizationNames()
}

def authorizations = pipe {
	from zip(buildAuthorizations, value(contestUsedDOIs))  \
     doOnNext authorizationPhaseComponent.checkAuthorizations()  \
     hide()  \
     map authorizationPhaseComponent.getAuthorizationsFromTuple()
}

AuthorizationPhase.exports.authorizations = authorizations
AuthorizationPhase.exports.contestUsedDOIs = contestUsedDOIs
