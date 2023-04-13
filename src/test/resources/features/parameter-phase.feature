Feature: Parameter Phase
  Scenario: User parameters defining questions and plugins
    Given "file.param.xml" as input file
    When executing the parameter phase pipeline
    And draining the parameter phase outputs
    Then it outputs a contest with question "tiebreak-qid1"
    And it outputs a plugin of type "BirthYearAuthenticationKeyTypeImpl"
