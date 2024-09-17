/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.register.ech0045v5


import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v5.RegisterEch0045v5
import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v5.RegisterEch0045v5Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v5.RegisterEch0045v5.exports
import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.tube

def registerEch0045v5Component = getBean(RegisterEch0045v5Component.class)


def inputFilenamesECH0045v5 = tube {
    from RegisterEch0045v5.inputPhase.files \
    filter fileType("ech[-]?0045[_]?v5") \
    cache()
}

def voters = tube {
    from inputFilenamesECH0045v5 \
    flatMapIterable registerEch0045v5Component.loadECH0045v5() \
    map registerEch0045v5Component.fromECHv5Voter() \
    map registerEch0045v5Component.checkConsistency() \
}

exports.voters = voters
