/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static pipes.Tools.*

def registerPhaseComponent = DataIntegrationServiceApplication.context.getBean(RegisterPhaseComponent.class)

def voters = tube {
    from(RegisterPhase.registerEch0045v4.voters) \
    transformDeferred logAsInfo("Building the register...")
}

def votersWithPlugins = tube {
    from voters \
    map registerPhaseComponent.assignFrankingArea() \
    zipWith value(RegisterPhase.parameterPhase.afterReadVoterPlugins), apply()
}

def votersWithAuthPlugins = tube {
    from votersWithPlugins \
    zipWith value(RegisterPhase.parameterPhase.afterReadVoterAuthorizationPlugins), apply() \
}

RegisterPhase.exports.voters = votersWithAuthPlugins
