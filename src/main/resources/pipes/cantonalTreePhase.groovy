/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static pipes.Tools.*

def cantonalTreePhaseComponent = DataIntegrationServiceApplication.context.getBean(CantonalTreePhaseComponent.class)

def cantonalTree = pipe {
    from CantonalTreePhase.cantonalTreeStdv1.cantonalTree \
    transformDeferred logAsInfo("Building the cantonalTree...") \
    last() \
    doOnNext cantonalTreePhaseComponent.checkConsistency() \
    hide() \
}

CantonalTreePhase.exports.cantonalTree = cantonalTree
