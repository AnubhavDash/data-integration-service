# Changelog

## Release 2.7.3

Release 2.7.3 is a minor maintenance patch containing the following changes:

* Ensure that the candidate and voter title contains at least one character.
* Updated dependencies and third-party libraries.

## Release 2.7.2

Release 2.7.2 is a minor maintenance patch containing the following changes:

* Enforce the presence of the referenceOnPosition field for each candidate.
* Add a check that each election contains an empty list and no duplicate list identifiers.
* Prevent configurations where two different counting circles have the same ID.
* Updated dependencies and third-party libraries.

## Release 2.7.1

Release 2.7.1 is a minor maintenance patch containing the following changes:

* Reuse the electionIdentification as electionGroupIdentification if the electionGroup has no identification and contains a single election.
* Provide a warning if the evotingTestBallotBoxesFromDate is after the evotingFromDate.
* Provide a splitter for generating multiple files per ballot box.
* Replace incumbent text "Bisher" with "bisher".
* Updated dependencies and third-party libraries.

## Release 2.7.0

The Data Integration Service 2.7.0 contains the following changes

* Adapted plugin to set the electionGroupPosition and votePosition.
* Added possibility to set all authorizations to test.
* Remove elections and votes with no associated voters from the produced election event configuration file.
* Improved the log messages.
* Improved code quality.
* Updated dependencies and third-party libraries.

---

## Release 2.6.0

The Data Integration Service 2.6.0 contains the following changes

* Updated to evoting-config version 5.0.
* Extracted XML deserialization to evoting-libraries.
* Removed the postVotePhaseType plugins.
* Added the adminBoard to the evoting-params.
* Removed the uiProperties from evoting-params.
* Removed the electoralAuthorityThreshold from evoting-params.
* Fixed minor code smells.
* Updated dependencies and third-party libraries.

---

## Release 2.5.1

The Data Integration Service 2.5.1 contains the following changes

* Updated dependencies and third-party libraries.
* Removed a case-sensitive path that blocked the build on Linux.
* Prepared the source code for publication.
