/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package pipes


import ch.post.it.evoting.dataintegrationservice.pipes.HeaderPhase
import ch.post.it.evoting.dataintegrationservice.pipes.HeaderPhaseComponent
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoterType
import org.apache.commons.lang3.StringUtils

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.logAsInfo
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.logItemAsInfo
import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.pipe

def headerPhaseComponent = getBean(HeaderPhaseComponent.class)

def countVoters = pipe {
    from HeaderPhase.assigningAuthorizationPhase.assignedVoters \
    filter { VoterType v -> !StringUtils.isEmpty(v.getAuthorization())} \
    count() \
    doOnNext logItemAsInfo("Treating {} validVoters...")
}

def header = pipe {
    from countVoters \
    transformDeferred logAsInfo("Building the header...") \
    map headerPhaseComponent.buildHeader()
}

HeaderPhase.exports.header = header
