Feature: CantonalTree Phase
  Scenario: Merging all cantonalTree formats
    Given a Stdv1CantonalTree
    When executing the cantonalTree phase
    And draining the cantonalTree outputs
    Then it outputs a cantonalTree containing the same value as the input element
