/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static li.chee.reactive.plumber.Plumbing.*
import static pipes.Tools.apply
import static pipes.Tools.logAsInfo

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

RegisterPhase.exports.voters = votersWithPlugins
