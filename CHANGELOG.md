# Changelog

## Release 2.9.3

Release 2.9.3 incorporates the following changes:

* Added additional display candidate line style.
* Improved the Rumantsch translation for incumbent candidates.
* Moved certain test data resources into test / resources

## Release 2.9.2.2

Release 2.9.2.2 is a minor maintenance patch correcting the following issue:

* Fixes a problem with inconsistent numberings in election groups and popular votes.

## Release 2.9.2.1

Release 2.9.2.1 incorporates the following changes:

* Added a validation that candidate accumulation does not exceed the number of mandates.
* Parametrized whether the incumbent text is displayed on the display candidate line.
* Added a testAuthorizationSplitter to split the output files for test voter by authorization.
* Minor code improvements.
* Updated dependencies and third-party libraries.

## Release 2.9.2

Release 2.9.2 incorporates the following changes:

* Improved the sanitization of the non-anonymized configuration file.
* Added additional display candidate line styles.
* Fixed a bug with overlapping vote and election group positions.
* Ensured correct treatment of the country code "NO".
* Added a default list order of precedence for the empty list.
* Fixed a bug with logging signature exceptions.
* Minor code improvements
* Updated dependencies and third-party libraries.

## Release 2.9.1

Release 2.9.1 incorporates the following changes:

* Improved a protection against XML external entity attacks.
* Added sanitization of markdown links
* Fixed a limitation when handling large number of municipalities.
* Fixed a problem when treating custom blank answers.
* Enforced a consistency check in the counting circle identifier to counting circle name mapping.
* Minor code improvements
* Updated dependencies and third-party libraries.

## Release 2.9.0

The Data Integration Service 2.9.0 contains the following changes:

* Added eCH-version 4.2 support.
* Improved input validation
    * Added business rules to handle missing fields in the eCH input files such as `electionDescription`, `electionGroupIdentification` and `electionGroupDescription`.
    * Imposed that the position fields are a monotonous sequence.
    * Improved identification of the hidden blank answer.
    * Prevent invalid configurations such as write-ins for proportional elections.
    * Imposed restrictions on the cardinality of certain fields such as that descriptions must be available in de, fr, it, and rm.
    * Imposed strict format of date-times.
    * Restricted identifier types.
* Updated the data integration service to evoting-config version 7.
    * Improved representation of empty lists, empty candidate positions and write-in positions.
    * Removed possibility to configure an administration board
    * Added possibility to set comma-separated candidate names in the displayCandidateLine
* Included the new XML signature for the evoting-config.
* General code quality improvements and minor bug fixes.
* Updated dependencies and third-party libraries.

## Release 2.8.5

Release 2.8.5 incorporates the following change:

* Improved the Italian and Rumantsch translations for 'Empty candidate positions.'
* Updated dependencies and third-party libraries.

## Release 2.8.4

Release 2.8.4 incorporates the following change:

* Minor code improvements
* Updated dependencies and third-party libraries.

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
