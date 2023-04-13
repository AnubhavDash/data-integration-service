Feature: Birth Year Authentication
  Background:
    Given The birth year authentication key plugin without parameters
    And A voter born on 1979-04-13

  Scenario: Add an authentication key based on the birth year of the voter
    When The plugin is applied to the voter
    Then One authentication key is generated
      And The authentication key named "birthYear" contains 1979

  Scenario: Add twice in a row an authentication key based on the birth year of the voter
    When The plugin is applied to the voter
      And The plugin is applied to the voter
    Then One authentication key is generated
    And The authentication key named "birthYear" contains 1979

