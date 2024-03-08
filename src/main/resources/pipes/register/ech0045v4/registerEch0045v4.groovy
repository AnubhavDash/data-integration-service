/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.register.ech0045v4


import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v4.RegisterEch0045v4
import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v4.RegisterEch0045v4Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v4.RegisterEch0045v4.exports
import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.tube

def registerEch0045v4Component = getBean(RegisterEch0045v4Component.class)


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
