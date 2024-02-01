Feature: Authorization Phase

  Background:

    Given a list of valid voters

    And the list of voters has a valid voter

    Given a cantonal tree

    And cantonal tree has domain of influence "FR","CH","1"
    And domain of influence "FR-CH-1" has ID "1131-432543-44556"
    And domain of influence "FR-CH-1" has ID "3333-324255-11334"

    And cantonal tree has domain of influence "FR","CT","1"
    And domain of influence "FR-CT-1" has ID "4478-547654-77544" and parent ID "1131-432543-44556" in "FR-CH-1"
    And domain of influence "FR-CT-1" has ID "6587-546666-25711" and parent ID "3333-324255-11334" in "FR-CH-1"

    And cantonal tree has counting circle "FR","MU","1111"
    And counting circle "FR-MU-1111" has ID "1166-888657-12300" and parent ID "4478-547654-77544" in "FR-CT-1"
    And counting circle "FR-MU-1111" has ID "4400-987333-65654" and parent ID "6587-546666-25711" in "FR-CT-1"

 Scenario: throw an exception when no valid voters exist
   Given the list of valid voters is cleared out
   When executing the authorization phase and catching exceptions
   Then an exception "No validVoters are defined !" should occur
