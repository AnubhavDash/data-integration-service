Feature: Output Phase

  Background:

    # VALID voters
    Given a list of voters named "my voters"

    Given a voter named "voterA" in list "my voters"
    And voter named "voterA" has id "voter-id-A"
    And voter named "voterA" has authorization "01-00-aaa"

    Given a voter named "voterB" in list "my voters"
    And voter named "voterB" has id "voter-id-B"
    And voter named "voterB" has authorization "01-00-aaa"

    Given a voter named "voterC" in list "my voters"
    And voter named "voterC" has id "voter-id-C"
    And voter named "voterC" has authorization "01-00-aaa"

    # INVALID voters
    Given a list of invalid voters named "my invalid voters"

    Given an invalid voter named "invalid voterA" in list "my invalid voters"
    And invalid voter named "invalid voterA" has id "invalid-voter-id-A"

    Given an invalid voter named "invalid voterB" in list "my invalid voters"
    And invalid voter named "invalid voterB" has id "invalid-voter-id-B"

  Scenario: The background scenario is valid and no exceptions should occur when the output phase is executed
    When executing the output phase
    Then no exception is thrown
    And the configuration file "configuration.xml" should exist and contain 3 voters
    And the configuration file "configuration-anonymized.xml" should exist and contain 3 voters
    And the register file "invalidVoters.xml" should exist and contain 2 voters

  Scenario: Throw an exception when voter identification is not unique
    Given voter named "voterC" has id "voter-id-A"
    When executing the output phase and catching exceptions
    Then an exception with text "Duplicate voter identification found. [voterIdentification: voter-id-A]" should occur

  Scenario: Anonymization
    Given voter named "voterB" has first name, last name, date of birth "Pierre" "Dupont" "1991-05-22"
    When executing the output phase
    Then voter with id "voter-id-B" should have personal information in the file "configuration.xml"
    And voter with id "voter-id-B" should have no personal information in the file "configuration-anonymized.xml"

  Scenario: HTML Tags removed from the anonymized configuration
    Given the contest descriptions are cleared out
    And the contest has description "DE" "<B>Description</B> with HTML tags DE"
    And the contest has description "FR" "Description</B> with HTML tags FR"
    And the contest has description "IT" "Description<br> with HTML tags IT"
    And the contest has description "RM" "<B>Description<br><B> with HTML tags RM"
    When executing the output phase
    Then the configuration "configuration.xml" should have contest description "DE" "Description with HTML tags DE"
    And the configuration "configuration.xml" should have contest description "FR" "Description with HTML tags FR"
    And the configuration "configuration.xml" should have contest description "IT" "Description with HTML tags IT"
    And the configuration "configuration.xml" should have contest description "RM" "Description with HTML tags RM"
    And the configuration "configuration-anonymized.xml" should have contest description "DE" "Description with HTML tags DE"
    And the configuration "configuration-anonymized.xml" should have contest description "FR" "Description with HTML tags FR"
    And the configuration "configuration-anonymized.xml" should have contest description "IT" "Description with HTML tags IT"
    And the configuration "configuration-anonymized.xml" should have contest description "RM" "Description with HTML tags RM"




