/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.register.ech0045v4


import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v40.RegisterEch0045v40
import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v40.RegisterEch0045v40Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v40.RegisterEch0045v40.exports
import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.tube

def registerEch0045v40Component = getBean(RegisterEch0045v40Component.class)


def inputFilenamesECH0045v40 = tube {
    from RegisterEch0045v40.inputPhase.files \
    filter fileType("eCH-?0045_?v4(?!(_[1-9]))") \
    cache()
}

def voters = tube {
    from inputFilenamesECH0045v40 \
    flatMapIterable registerEch0045v40Component.loadECH0045v40() \
    map registerEch0045v40Component.fromECHv40Voter() \
    map registerEch0045v40Component.checkConsistency() \
}

exports.voters = voters
