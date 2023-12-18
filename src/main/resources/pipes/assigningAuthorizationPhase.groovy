/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType
import org.apache.commons.lang3.StringUtils

import static li.chee.reactive.plumber.Plumbing.*
import static pipes.Tools.logAsInfo
import static reactor.core.publisher.Flux.zip

def assigningAuthorizationPhaseComponent = DataIntegrationServiceApplication.context.getBean(AssigningAuthorizationPhaseComponent.class)

def voters = tube {
    from AssigningAuthorizationPhase.registerPhase.voters \
}

def authorizations = pipe {
    from AssigningAuthorizationPhase.authorizationPhase.authorizations \
}

def usedDOIs = pipe {
    from AssigningAuthorizationPhase.authorizationPhase.contestUsedDOIs
}

def contestParameters = pipe {
    from AssigningAuthorizationPhase.parameterPhase.contestParameters
}

def assignedVoters = tube {
    from zip(voters, value(authorizations), value(usedDOIs), value(AssigningAuthorizationPhase.contestPhase.contest), value(contestParameters)) \
    transformDeferred logAsInfo("Assigning authorization to voters...") \
    transformDeferred assigningAuthorizationPhaseComponent.warnIfAllCountingCircleAsTestIsConfigured() \
    map assigningAuthorizationPhaseComponent.assignAuthorization()
}

def allVoters = pipe {
    from assignedVoters
}

def validVoters = tube {
    from allVoters \
    filter {VoterType v -> !StringUtils.isEmpty(v.getAuthorization())}
}

def invalidVoters = tube {
    from allVoters \
    filter {VoterType v -> StringUtils.isEmpty(v.getAuthorization())}
}

def usedDoisFromAuthorizations = tube {
    from AssigningAuthorizationPhase.authorizationPhase.authorizations \
    map assigningAuthorizationPhaseComponent.usedDoisFromAuthorizations()
}

def filteredContest = tube {
    from zip(AssigningAuthorizationPhase.contestPhase.contest, usedDoisFromAuthorizations) \
    map assigningAuthorizationPhaseComponent.filterContest()
}

AssigningAuthorizationPhase.exports.assignedVoters = assignedVoters
AssigningAuthorizationPhase.exports.validVoters = validVoters
AssigningAuthorizationPhase.exports.invalidVoters = invalidVoters
AssigningAuthorizationPhase.exports.filteredContest = filteredContest
