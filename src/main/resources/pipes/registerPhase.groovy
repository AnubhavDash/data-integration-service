/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static pipes.Tools.*

def registerPhaseComponent = DataIntegrationServiceApplication.context.getBean(RegisterPhaseComponent.class)

def mergedVoters = tube {
    from merge(RegisterPhase.registerEch0045v3.voters, RegisterPhase.registerEch0045v4.voters) \
    transformDeferred logAsInfo("Building the register...")
}

def votersWithPlugins = tube {
    from mergedVoters \
    map registerPhaseComponent.assignFrankingArea() \
    zipWith value(RegisterPhase.parameterPhase.afterReadVoterPlugins), apply()
}

def votersWithAuthPlugins = tube {
    from zip(votersWithPlugins, value(RegisterPhase.cantonalTreePhase.cantonalTree)) \
    zipWith value(RegisterPhase.parameterPhase.afterReadVoterAuthorizationPlugins), applyTuple() \
}

RegisterPhase.exports.voters = votersWithAuthPlugins