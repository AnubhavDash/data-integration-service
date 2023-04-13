/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication
import ch.post.it.evoting.dataintegrationservice.parameter.plugin.ExtendedAuthenticationKeyPlugin

import static pipes.Tools.*

def contestPhaseComponent = DataIntegrationServiceApplication.context.getBean(ContestPhaseComponent.class)

def elections = pipe {
    from merge( ContestPhase.contestEch015xv3.elections, ContestPhase.contestEch015xv4.elections ) \
    collectList()
}

def votes = pipe {
    from merge( ContestPhase.contestEch015xv3.votes, ContestPhase.contestEch015xv4.votes ) \
    collectList()
}

def validatedContest = pipe {
    from merge( ContestPhase.contestEch015xv3.contests, ContestPhase.contestEch015xv4.contests ) \
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