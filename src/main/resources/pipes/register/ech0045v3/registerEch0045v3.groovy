/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.register.ech0045v3

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.tube
import static pipes.Tools.fileType

def registerEch0045v3Component = DataIntegrationServiceApplication.context.getBean(RegisterEch0045v3Component.class)

def inputFilenamesECH0045v3 = tube {
    from RegisterEch0045v3.inputPhase.files \
    filter fileType("ech[-]?0045[_]?v3") \
    cache()
}

def voters = tube {
    from inputFilenamesECH0045v3 \
    flatMapIterable registerEch0045v3Component.loadECH0045v3() \
    map registerEch0045v3Component.fromECHv3Voter() \
}

RegisterEch0045v3.exports.voters = voters
