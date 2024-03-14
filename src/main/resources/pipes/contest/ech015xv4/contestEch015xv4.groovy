/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.contest.ech015xv4


import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv4.ContestEch015xv4
import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv4.ContestEch015xv4Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static li.chee.reactive.plumber.Plumbing.*
import static reactor.core.publisher.Flux.merge

def contestEch015xv4Component = getBean(ContestEch015xv4Component.class)

def inputFilenames = tube {
    from ContestEch015xv4.inputPhase.files
}

def inputECH0157v4 = pipe {
    from inputFilenames \
    filter fileType("ech[-]?0157[_]?v4") \
    map contestEch015xv4Component.loadECH0157v4()
}

def inputECH0159v4 = pipe {
    from inputFilenames \
    filter fileType("ech[-]?0159[_]?v4") \
    map contestEch015xv4Component.loadECH0159v4()
}


def elections = pipe {
    from inputECH0157v4 \
    flatMapIterable contestEch015xv4Component.elections() \
    map contestEch015xv4Component.fromECHv4Election()
}

def votes = pipe {
    from inputECH0159v4 \
    flatMapIterable contestEch015xv4Component.votes() \
    map contestEch015xv4Component.fromECHv4Vote()
}

def inputECH0157v4Contests = pipe {
    from inputECH0157v4 \
    map contestEch015xv4Component.electionContest()
}

def inputECH0159v4Contests = pipe {
    from inputECH0159v4 \
    map contestEch015xv4Component.voteContest()
}

def contests = pipe {
    from merge(inputECH0157v4Contests, inputECH0159v4Contests) \
    map contestEch015xv4Component.fromECHv4Contest()
}

ContestEch015xv4.exports.contests = contests
ContestEch015xv4.exports.votes = votes
ContestEch015xv4.exports.elections = elections
