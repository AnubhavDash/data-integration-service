Feature: Ech015xv4_2 contest phase
  Scenario: 1 Building contest from ech015xv4_2 files: Contest and votations
    Given "src/test/resources/Ech015xv42ContestPhaseSteps/unittest-eCH-0159_v4_2-CH.xml" v4_2 file as input
    When executing the ech015xv4_2 contest phase pipeline
    And draining the ech015xv4_2 contest phase outputs
    Then it outputs 1 v4_2 contests
    And it outputs a list of v4_2 votations

  Scenario: Building contest from ech015xv4_2 files: Evoting period
    Given "src/test/resources/Ech015xv42ContestPhaseSteps/unittest-eCH-0159_v4_2-MU.xml" v4_2 file as input
    When executing the ech015xv4_2 contest phase pipeline
    And draining the ech015xv4_2 contest phase outputs
    Then it outputs 1 v4_2 contests
    And it outputs a list of v4_2 votations
    And the contest with identification "unittest_contest_v4_2_vote_01_mu" has evotingperiod set

  Scenario: Building contest from ech015xv4_2 files: Elections
    Given "src/test/resources/Ech015xv42ContestPhaseSteps/unittest-eCH-0157_v4_2_sr.xml" v4_2 file as input
    When executing the ech015xv4_2 contest phase pipeline
    And draining the ech015xv4_2 contest phase outputs
    Then it outputs 1 v4_2 contests
    And it outputs a list of v4_2 elections

  Scenario: Building contest from ech015xv4_2 files: Elections check DOI
    Given "src/test/resources/Ech015xv42ContestPhaseSteps/unittest-eCH-0157_v4_2_nr.xml" v4_2 file as input
    When executing the ech015xv4_2 contest phase pipeline
    And draining the ech015xv4_2 contest phase outputs
    Then it outputs 1 v4_2 contests
    And it outputs a v4_2 election with electionId "nr_st_gallen" has doi equals to "1033803945"

  Scenario: Building contest from ech015xv4_2 files: Elections check number of candidates
    Given "src/test/resources/Ech015xv42ContestPhaseSteps/unittest-eCH-0157_v4_2_nr.xml" v4_2 file as input
    When executing the ech015xv4_2 contest phase pipeline
    And draining the ech015xv4_2 contest phase outputs
    Then it outputs 1 v4_2 contests
    And it outputs a v4_2 election has 198 candidates

  Scenario: Building contest from ech015xv4_2 files: check no candidate text
    Given "src/test/resources/Ech015xv42ContestPhaseSteps/eCH-0157_v4_2.xml" v4_2 file as input
    When executing the ech015xv4_2 contest phase pipeline
    And draining the ech015xv4_2 contest phase outputs
    Then it outputs 1 v4_2 contests
    And it outputs a list of v4_2 elections
    And candidate v4_2 with callName "Keller Karin" has a candidateText
    And candidate v4_2 with callName "Keller Karin" has "No candidate text" as candidateTextInformation