/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.contest.ech015xv3

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static pipes.Tools.*

def contestEch015xv3Component = DataIntegrationServiceApplication.context.getBean(ContestEch015xv3Component.class)

def inputFilenames = tube {
    from ContestEch015xv3.inputPhase.files
}

def inputECH0157v3 = pipe {
    from inputFilenames \
    filter fileType("ech[-]?0157[_]?v3") \
    map contestEch015xv3Component.loadECH0157v3()
}

def elections = pipe {
    from inputECH0157v3 \
    flatMapIterable contestEch015xv3Component.elections() \
    map contestEch015xv3Component.fromECHv3Election()
}

def inputECH0157v3Contests = pipe {
    from inputECH0157v3 \
    map contestEch015xv3Component.electionContest()
}

def inputECH0159v3 = pipe {
    from inputFilenames \
    filter fileType("ech[-]?0159[_]?v3") \
    map contestEch015xv3Component.loadECH0159v3()
}

def votes = pipe {
    from inputECH0159v3 \
    flatMapIterable contestEch015xv3Component.votes() \
    map contestEch015xv3Component.fromECHv3Vote()
}

def inputECH0159v3Contests = pipe {
    from inputECH0159v3 \
    map contestEch015xv3Component.voteContest()
}

def contests = pipe {
    from merge(inputECH0157v3Contests, inputECH0159v3Contests) \
    map contestEch015xv3Component.fromECHv3Contest()
}

ContestEch015xv3.exports.contests = contests
ContestEch015xv3.exports.votes = votes
ContestEch015xv3.exports.elections = elections
