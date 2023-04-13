/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.register.ech0045v4

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.tube
import static pipes.Tools.fileType
import static pipes.register.ech0045v4.RegisterEch0045v4.exports

def registerEch0045v4Component = DataIntegrationServiceApplication.context.getBean(RegisterEch0045v4Component.class)


def inputFilenamesECH0045v4 = tube {
    from RegisterEch0045v4.inputPhase.files \
    filter fileType("ech[-]?0045[_]?v4") \
    cache()
}

def voters = tube {
    from inputFilenamesECH0045v4 \
    flatMapIterable registerEch0045v4Component.loadECH0045v4() \
    map registerEch0045v4Component.fromECHv4Voter() \
    map registerEch0045v4Component.checkConsistency() \
}

exports.voters = voters
