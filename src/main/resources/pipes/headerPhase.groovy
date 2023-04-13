/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication
import ch.post.it.evoting.domain.xmlns.evotingconfig.VoterType
import org.apache.commons.lang3.StringUtils

import static pipes.Tools.*

def headerPhaseComponent = DataIntegrationServiceApplication.context.getBean(HeaderPhaseComponent.class)

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