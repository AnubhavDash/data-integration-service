/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.register.ech0045v42


import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v42.RegisterEch0045v42
import ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v42.RegisterEch0045v42Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static ch.post.it.evoting.dataintegrationservice.pipes.register.ech0045v42.RegisterEch0045v42.exports
import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.tube

def registerEch0045v42Component = getBean(RegisterEch0045v42Component.class)


def inputFilenamesECH0045v42 = tube {
    from RegisterEch0045v42.inputPhase.files \
    filter fileType("ech-?0045_?v4_2") \
    cache()
}

def voters = tube {
    from inputFilenamesECH0045v42 \
    flatMapIterable registerEch0045v42Component.loadECH0045v42() \
    map registerEch0045v42Component.fromECHv42Voter() \
    map registerEch0045v42Component.checkConsistency() \
}

exports.voters = voters
