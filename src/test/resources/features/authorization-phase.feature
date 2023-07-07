Feature: Authorization Phase

  Background:

    Given a list of valid voters

    And the list of voters has a valid voter

 Scenario: throw an exception when no valid voters exist
   Given the list of valid voters is cleared out
   When executing the authorization phase and catching exceptions
   Then an exception "No validVoters are defined !" should occur
