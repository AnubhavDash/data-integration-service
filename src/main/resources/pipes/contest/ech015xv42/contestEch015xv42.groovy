/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.contest.ech015xv42


import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv42.ContestEch015xv42
import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv42.ContestEch015xv42Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static li.chee.reactive.plumber.Plumbing.*
import static reactor.core.publisher.Flux.merge

def contestEch015xv42Component = getBean(ContestEch015xv42Component.class)

def inputFilenames = tube {
    from ContestEch015xv42.inputPhase.files
}

def inputECH0157v42 = pipe {
    from inputFilenames \
    filter fileType("ech-?0157_?v4_2") \
    map contestEch015xv42Component.loadECH0157v42()
}

def inputECH0159v42 = pipe {
    from inputFilenames \
    filter fileType("ech-?0159_?v4_2") \
    map contestEch015xv42Component.loadECH0159v42()
}


def elections = pipe {
    from inputECH0157v42 \
    flatMapIterable contestEch015xv42Component.elections() \
    map contestEch015xv42Component.fromECHv42Election()
}

def votes = pipe {
    from inputECH0159v42 \
    flatMapIterable contestEch015xv42Component.votes() \
    map contestEch015xv42Component.fromECHv42Vote()
}

def inputECH0157v5Contests = pipe {
    from inputECH0157v42 \
    map contestEch015xv42Component.electionContest()
}

def inputECH0159v5Contests = pipe {
    from inputECH0159v42 \
    map contestEch015xv42Component.voteContest()
}

def contests = pipe {
    from merge(inputECH0157v5Contests, inputECH0159v5Contests) \
    map contestEch015xv42Component.fromECHv42Contest()
}

ContestEch015xv42.exports.contests = contests
ContestEch015xv42.exports.votes = votes
ContestEch015xv42.exports.elections = elections
