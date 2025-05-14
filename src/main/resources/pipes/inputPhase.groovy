/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes


import ch.post.it.evoting.dataintegrationservice.pipes.InputPhase
import ch.post.it.evoting.dataintegrationservice.pipes.InputPhaseComponent

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static li.chee.reactive.plumber.Plumbing.tube
import static reactor.core.publisher.Flux.fromIterable

def inputPhaseComponent = getBean(InputPhaseComponent.class)

def files = tube {
    fromIterable inputPhaseComponent.scanner()
}

InputPhase.exports.files = files
