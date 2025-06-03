/*
 * (c) Copyright 2025 Swiss Post Ltd.
 */
package pipes


import ch.post.it.evoting.dataintegrationservice.pipes.OutputPrePhase
import ch.post.it.evoting.dataintegrationservice.pipes.OutputPrePhaseComponent

import static ch.post.it.evoting.dataintegrationservice.ApplicationContextProvider.getBean
import static ch.post.it.evoting.dataintegrationservice.pipes.Tools.*
import static li.chee.reactive.plumber.Plumbing.*
import static reactor.core.publisher.Flux.*

def outputPrePhaseComponent = getBean(OutputPrePhaseComponent.class)

def configInfo = value pipe {
    from zip(OutputPrePhase.assigningAuthorizationPhase.filteredContest, OutputPrePhase.authorizationPhase.authorizations, OutputPrePhase.headerPhase.header)
}

def splitVoters = pipe {
    from OutputPrePhase.assigningAuthorizationPhase.validVoters \
    doOnNext outputPrePhaseComponent.checkRegisterConsistency() \
    zipWith(value(OutputPrePhase.parameterPhase.configSplitterPlugins)) \
    groupBy outputPrePhaseComponent.splitWithPlugins() \
}

def createSplitConfig = pipe each(splitVoters) {
    from it \
    map unzip()\
    transformDeferred outputPrePhaseComponent.collectStream() \
    zipWith configInfo, outputPrePhaseComponent.buildConfig() \
    transform fork() \
    doOnNext saveConfigXML(String.format("%s", key(it)))
}

def createConfig = pipe {
    from merge(createSplitConfig)
}

def contestWithoutHtml = pipe {
    from OutputPrePhase.assigningAuthorizationPhase.filteredContest \
    map outputPrePhaseComponent.cleanHtmlTags()
}

def anonymizedConfigInfo = value pipe {
    from zip(contestWithoutHtml, OutputPrePhase.authorizationPhase.authorizations, OutputPrePhase.headerPhase.header)
}

def anonymizedCreateConfig = pipe {
    from OutputPrePhase.assigningAuthorizationPhase.validVoters \
    map outputPrePhaseComponent.anonymize() \
    transformDeferred outputPrePhaseComponent.collectStream() \
    zipWith anonymizedConfigInfo, outputPrePhaseComponent.buildConfig() \
    transform fork() \
    doOnNext saveConfigXML("anonymized")
}

def invalidVoters = tube {
    from OutputPrePhase.assigningAuthorizationPhase.invalidVoters
}

def saveInvalidVoters = pipe {
    from OutputPrePhase.assigningAuthorizationPhase.invalidVoters \
    transform outputPrePhaseComponent.buildInvalidRegister() \
    filterWhen hasElements(invalidVoters) \
    transform fork() \
    doOnNext saveInvalidRegisterXML()
}

drain createConfig, anonymizedCreateConfig, saveInvalidVoters
