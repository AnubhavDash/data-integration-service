/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication
import ch.post.it.evoting.domain.xmlns.evotingconfig.AuthorizationsType

import static li.chee.reactive.plumber.Plumbing.*
import static pipes.Tools.logAsInfo
import static reactor.core.publisher.Flux.zip

def authorizationPhaseComponent = DataIntegrationServiceApplication.context.getBean(AuthorizationPhaseComponent.class)


def cantonalTree = pipe {
	from AuthorizationPhase.cantonalTreePhase.cantonalTree
}

def voters = tube {
	from AuthorizationPhase.registerPhase.voters
}

def contestUsedDOIs = pipe {
	from AuthorizationPhase.contestPhase.contest  \
     map authorizationPhaseComponent.mapDomainOfInfluenceIds()  \

}

def cantonalTreeAndUsedDOIS = pipe {
	from zip(cantonalTree, contestUsedDOIs)  \
     map authorizationPhaseComponent.checkDomainOfInfluences()
}

def buildAuthorizationPlugins = pipe {
	from AuthorizationPhase.parameterPhase.buildAuthorizationPlugins
}

def contestParameters = pipe {
	from AuthorizationPhase.parameterPhase.contestParameters
}

def buildAuthorizations = pipe {
	from zip(voters, value(cantonalTreeAndUsedDOIS), value(AuthorizationPhase.contestPhase.contest), value(buildAuthorizationPlugins), value(contestParameters))  \
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