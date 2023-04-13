Feature: Ech015xv3 contest phase
  Scenario: Building contest from ech015xv3 files
    Given "Ech015xv3ContestPhaseSteps/eCH-0157-Export_M01_20161127_20161109170345_ech0157v3.xml" and "Ech015xv3ContestPhaseSteps/eCH-0159_ech0159v3.xml" files as input
    When executing the ech015xv3 contest phase pipeline
    And draining the ech015xv3 contest phase outouts
    Then it outputs two contests
    And it outputs a list of elections
    And it outputs a list of votations

  Scenario: Building contest from ech015xv3 files: check no candidate text
    Given "Ech015xv3ContestPhaseSteps/eCH-0157_v3.xml" file as input
    When executing the ech015xv3 contest phase pipeline
    And draining the ech015xv3 contest phase outouts
    Then it outputs one contest
    And it outputs a list of elections
    And candidate with callName "Mendoza Inayah" has a candidateText
    And candidate with callName "Mendoza Inayah" has "No candidate text" as candidateTextInformation