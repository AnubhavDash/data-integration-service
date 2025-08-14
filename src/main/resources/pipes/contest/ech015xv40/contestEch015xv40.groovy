/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package pipes.contest.ech015xv40


import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv40.ContestEch015xv40
import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv40.ContestEch015xv40Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static li.chee.reactive.plumber.Plumbing.*
import static reactor.core.publisher.Flux.merge

def contestEch015xv40Component = getBean(ContestEch015xv40Component.class)

def inputFilenames = tube {
    from ContestEch015xv40.inputPhase.files
}

def inputECH0157v4 = pipe {
    from inputFilenames \
    filter fileType("eCH-?0157_?v4(?!(_[1-9]))") \
    map contestEch015xv40Component.loadECH0157v40()
}

def inputECH0159v4 = pipe {
    from inputFilenames \
    filter fileType("eCH-?0159_?v4(?!(_[1-9]))") \
    map contestEch015xv40Component.loadECH0159v40()
}


def elections = pipe {
    from inputECH0157v4 \
    flatMapIterable contestEch015xv40Component.elections() \
    map contestEch015xv40Component.fromECHv40Election()
}

def votes = pipe {
    from inputECH0159v4 \
    flatMapIterable contestEch015xv40Component.votes() \
    map contestEch015xv40Component.fromECHv40Vote()
}

def inputECH0157v4Contests = pipe {
    from inputECH0157v4 \
    map contestEch015xv40Component.electionContest()
}

def inputECH0159v4Contests = pipe {
    from inputECH0159v4 \
    map contestEch015xv40Component.voteContest()
}

def contests = pipe {
    from merge(inputECH0157v4Contests, inputECH0159v4Contests) \
    map contestEch015xv40Component.fromECHv40Contest()
}

ContestEch015xv40.exports.contests = contests
ContestEch015xv40.exports.votes = votes
ContestEch015xv40.exports.elections = elections
