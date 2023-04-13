Feature: Input Phase
  Scenario: Reading directory to discover files to be handled
    When executing the input phase pipeline
    And draining the input phase output
    Then it outputs a list containing multiple files
    And it outputs a file of type "ech0045v2"
    And it outputs a file of type "ech0157v3"
    And it outputs a file of type "ech0159v3"
    And it outputs a file of type "param"
