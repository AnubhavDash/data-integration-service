/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static pipes.Tools.*

def inputPhaseComponent = DataIntegrationServiceApplication.context.getBean(InputPhaseComponent.class)

def files = tube {
    fromIterable inputPhaseComponent.scanner()
}

InputPhase.exports.files = files