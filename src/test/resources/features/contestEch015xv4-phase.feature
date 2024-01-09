Feature: Ech015xv4 contest phase
  Scenario: 1 Building contest from ech015xv4 files: Contest and votations
    Given "src/test/resources/Ech015xv4ContestPhaseSteps/unittest-eCH-0159_v4-CH.xml" v4 file as input
    When executing the ech015xv4 contest phase pipeline
    And draining the ech015xv4 contest phase outputs
    Then it outputs 1 v4 contests
    And it outputs a list of v4 votations

  Scenario: Building contest from ech015xv4 files: Evoting period
    Given "src/test/resources/Ech015xv4ContestPhaseSteps/unittest-eCH-0159_v4-MU.xml" v4 file as input
    When executing the ech015xv4 contest phase pipeline
    And draining the ech015xv4 contest phase outputs
    Then it outputs 1 v4 contests
    And it outputs a list of v4 votations
    And contest with identification "unittest_contest_v4_vote_01_mu" has evotingperiod set

  Scenario: Building contest from ech015xv4 files: Elections
    Given "src/test/resources/Ech015xv4ContestPhaseSteps/unittest-eCH-0157_v4_sr.xml" v4 file as input
    When executing the ech015xv4 contest phase pipeline
    And draining the ech015xv4 contest phase outputs
    Then it outputs 1 v4 contests
    And it outputs a list of v4 elections

  Scenario: Building contest from ech015xv4 files: Elections check DOI
    Given "src/test/resources/Ech015xv4ContestPhaseSteps/unittest-eCH-0157_v4_nr.xml" v4 file as input
    When executing the ech015xv4 contest phase pipeline
    And draining the ech015xv4 contest phase outputs
    Then it outputs 1 v4 contests
    And it outputs an election with electionId "nr_st_gallen" has doi equals to "1033803945"

  Scenario: Building contest from ech015xv4 files: Elections check number of candidates
    Given "src/test/resources/Ech015xv4ContestPhaseSteps/unittest-eCH-0157_v4_nr.xml" v4 file as input
    When executing the ech015xv4 contest phase pipeline
    And draining the ech015xv4 contest phase outputs
    Then it outputs 1 v4 contests
    And it outputs election has 198 candidates

  Scenario: Building contest from ech015xv4 files: check no candidate text
    Given "src/test/resources/Ech015xv4ContestPhaseSteps/eCH-0157_v4.xml" v4 file as input
    When executing the ech015xv4 contest phase pipeline
    And draining the ech015xv4 contest phase outputs
    Then it outputs 1 v4 contests
    And it outputs a list of v4 elections
    And candidate v4 with callName "Keller Karin" has a candidateText
    And candidate v4 with callName "Keller Karin" has "No candidate text" as candidateTextInformation