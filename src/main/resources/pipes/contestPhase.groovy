/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package pipes


import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ExtendedAuthenticationKeyPlugin
import ch.post.it.evoting.dataintegrationservice.pipes.ContestPhase
import ch.post.it.evoting.dataintegrationservice.pipes.ContestPhaseComponent

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.*
import static li.chee.reactive.plumber.Plumbing.*
import static reactor.core.publisher.Flux.zip

def contestPhaseComponent = getBean(ContestPhaseComponent.class)

def elections = pipe {
    from( merge(ContestPhase.contestEch015xv40.elections, ContestPhase.contestEch015xv42.elections) ) \
    collectList()
}

def votes = pipe {
    from( merge(ContestPhase.contestEch015xv40.votes, ContestPhase.contestEch015xv42.votes) ) \
    collectList()
}

def validatedContest = pipe {
    from( merge(ContestPhase.contestEch015xv40.contests, ContestPhase.contestEch015xv42.contests) ) \
    transformDeferred errorIfEmpty("No ech15x contests found ! Check if ech15x file is present.") \
    scan contestPhaseComponent.checkContestSame() \
    last() \
}

def contestParameters = pipe {
    from ContestPhase.parameterPhase.contestParameters
}

def extendedAuthKeyPlugins = pipe {
    from ContestPhase.parameterPhase.afterReadVoterPlugins \
    flatMapIterable {it} \
    filter { it instanceof ExtendedAuthenticationKeyPlugin} \
    cast(ExtendedAuthenticationKeyPlugin.class) \
    reduce(new LinkedList<String>(), contestPhaseComponent.addAll())
}

def contest = pipe {
    from zip(validatedContest, votes, elections, value(contestParameters)) \
    transformDeferred logAsInfo("Building the contest...") \
    map contestPhaseComponent.buildContest() \
    zipWith value(ContestPhase.parameterPhase.afterReadContestPlugins), apply() \
    zipWith value(contestParameters) \
    doOnNext contestPhaseComponent.checkContestConsistency() \
    map unzip() \
    zipWith extendedAuthKeyPlugins \
    map contestPhaseComponent.setExtendedAuthenticationKeyNames()
}

ContestPhase.exports.contest = contest
