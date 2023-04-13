/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static pipes.Tools.*

def parameterPhaseComponent = DataIntegrationServiceApplication.context.getBean(ParameterPhaseComponent.class)

def loadParameterFile = pipe {
    from ParameterPhase.inputPhase.files \
    filter fileType("param") \
    transformDeferred logAsInfo("Loading the parameters...") \
    map parameterPhaseComponent.loadParameterV1() \
    transformDeferred errorIfEmpty("No parameter file found !")
}

def afterReadVoterPlugins = pipe {
    from loadParameterFile \
    map parameterPhaseComponent.getAfterReadVoterPlugins() \
}

def contestParameters = pipe {
    from loadParameterFile \
    map parameterPhaseComponent.getContestParameters() \
}

def configSplitPlugins = pipe {
    from loadParameterFile \
    map parameterPhaseComponent.getConfigSplitPlugins() \
}

def afterReadContestPlugins = pipe {
    from loadParameterFile \
    map parameterPhaseComponent.getAfterReadContestPlugins() \
}

def afterReadVoterAuthorizationPlugins = pipe {
    from loadParameterFile \
    map parameterPhaseComponent.getAfterReadVoterAuthorizationPlugins()
}

def buildAuthorizationPlugins = pipe {
    from loadParameterFile \
    map parameterPhaseComponent.getBuildAuthorizationPlugins()
}

ParameterPhase.exports.afterReadVoterPlugins = afterReadVoterPlugins
ParameterPhase.exports.afterReadContestPlugins = afterReadContestPlugins
ParameterPhase.exports.afterReadVoterAuthorizationPlugins = afterReadVoterAuthorizationPlugins
ParameterPhase.exports.configSplitterPlugins = configSplitPlugins
ParameterPhase.exports.contestParameters = contestParameters
ParameterPhase.exports.buildAuthorizationPlugins = buildAuthorizationPlugins
