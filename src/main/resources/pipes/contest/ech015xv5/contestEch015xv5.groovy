/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package pipes.contest.ech015xv5


import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv5.ContestEch015xv5
import ch.post.it.evoting.dataintegrationservice.pipes.contest.ech015xv5.ContestEch015xv5Component

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.fileType
import static li.chee.reactive.plumber.Plumbing.*
import static reactor.core.publisher.Flux.merge

def contestEch015xv5Component = getBean(ContestEch015xv5Component.class)

def inputFilenames = tube {
    from ContestEch015xv5.inputPhase.files
}

def inputECH0157v5 = pipe {
    from inputFilenames \
    filter fileType("ech[-]?0157[_]?v5") \
    map contestEch015xv5Component.loadECH0157v5()
}

def inputECH0159v5 = pipe {
    from inputFilenames \
    filter fileType("ech[-]?0159[_]?v5") \
    map contestEch015xv5Component.loadECH0159v5()
}


def elections = pipe {
    from inputECH0157v5 \
    flatMapIterable contestEch015xv5Component.elections() \
    map contestEch015xv5Component.fromECHv5Election()
}

def votes = pipe {
    from inputECH0159v5 \
    flatMapIterable contestEch015xv5Component.votes() \
    map contestEch015xv5Component.fromECHv5Vote()
}

def inputECH0157v5Contests = pipe {
    from inputECH0157v5 \
    map contestEch015xv5Component.electionContest()
}

def inputECH0159v5Contests = pipe {
    from inputECH0159v5 \
    map contestEch015xv5Component.voteContest()
}

def contests = pipe {
    from merge(inputECH0157v5Contests, inputECH0159v5Contests) \
    map contestEch015xv5Component.fromECHv5Contest()
}

ContestEch015xv5.exports.contests = contests
ContestEch015xv5.exports.votes = votes
ContestEch015xv5.exports.elections = elections
