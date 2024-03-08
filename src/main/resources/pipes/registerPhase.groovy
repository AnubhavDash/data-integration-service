/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes


import ch.post.it.evoting.dataintegrationservice.pipes.RegisterPhase
import ch.post.it.evoting.dataintegrationservice.pipes.RegisterPhaseComponent

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.apply
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.logAsInfo
import static li.chee.reactive.plumber.Plumbing.*

def registerPhaseComponent = getBean(RegisterPhaseComponent.class)

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
