# Changelog

## Release 2.8.3.2

Release 2.8.3.2 incorporates the following change:

* Update e-voting-libraries dependency to 1.4.3.2.

## Release 2.8.3.1

Release 2.8.3.1 incorporates the following change:

* Update e-voting-libraries dependency to 1.4.3.1.

## Release 2.8.3

Release 2.8.3 is a minor patch containing the following changes:

* Add two comma-separated displayCandidateLineStyle and displayListLineStyle without subListUnions.
* Prevent write-ins for proportional elections and accumulation for majoral elections.
* Use the ballotQuestionTitle as ballotDescriptionShort and ballotDescriptionLong (if ballotDescriptionShort and ballotDescriptionLong are missing).
* Updated dependencies and third-party libraries.

## Release 2.8.2

Release 2.8.2 is a minor patch containing the following changes:

* Added additional eCH XSD schemas to the packaging.
* Updated dependencies and third-party libraries.

## Release 2.8.1

Release 2.8.1 is a minor patch containing the following changes:

* Fix a bug with duplicate candidate identifiers.
* Fix a bug with uppercase language identifiers.
* Fix a bug with missing first name elements in the eCH-0157.xml.
* Improve the e-voting dates consistency checks.
* Ensure that certain election-related elements in the params file remain optional.
* Improve the packaging of the application.
* Migrated the application to SpringBoot.
* Updated dependencies and third-party libraries.

## Release 2.8.0

The Data Integration Service 2.8.0 contains the following changes

* Removed support of the legacy eCH-version 3.
* Removed deprecated plugins.
* Added support for primary and secondary elections.
* Added additional validations to prevent duplicate identifiers, inconsistent counting circles, and missing empty lists.
* Reduced the default time between election event start and date from 15 to 10.
* Ensured that the voter and candidate title contains at least one character.
* Made the list and candidate display configurable.
* Added a line to add election rules explanation.
* Updated the data integration service to evoting-config version 6.
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
