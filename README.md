# Data Integration Service

## What is the Content of this Repository?

The Data Integration Service is a tool designed to process XML files describing the electoral roll and the configuration of election events.
The tool produces a single, unified XML file that the Swiss Post Voting system can use.
The XML files processed by the service follow the eCH-standard, an open E-Government standard used in Switzerland.

The standard defines a set of XML schemas that describe various types of data, such as the electoral roll and the configuration of election events.
One of the key features of the Data Integration Service is its ability to handle variations in the XML files it processes.
The XML files may differ slightly from one customer to another and come in different versions. Additionally, the service can enrich the data in the XML files with additional information.

We use the following eCH-standards (https://www.ech.ch):

* eCH-0045 for the electoral roll
* eCH-0157 for an election
* eCH-0159 for a vote (referendum-style).

The data integration service can process large amounts of data by optimizing memory usage via streams.

XML (eXtensible Markup Language) files are widely used as an interface between different systems due to their ability to facilitate easy content validation. The open eCH-standard also utilizes XML files as its primary format for data representation.

The use of XML files as an interface between different systems allows for the validation of the content of the files in a consistent and standardized manner. Suitable XSD files (XML schemas) are available at https://gitit.post.ch/projects/EVOTINGECOSYSTEM/repos/data-integration-service/browse/src/main/resources/xsd

The configuration file “param.xml” contains the following definitions for the SDM:
* voting period 
* admin board 
* electoral board 
* specific election definitions (candidate-accumulation, write-ins)
* extended authentication mode (birthyear / birthdate)

The properties file “application.properties” contains the following definitions:
* min number of days allowed between eVoting from and eVoting to
        * eVotingFromTo.min.delta.allowed=15
* grace period in seconds
        * authorization.gracePeriod=900
* circles of influence
        * domainOfInfluence.CH-1.description=Suisse / Schweiz
* counting circles
        * countingCircle.testControl.minValue=10000
* default answer type
        * votes.defaultAnswerType.standard = 2
        * votes.defaultAnswerType.tiebreak = 4

![Workflow](DIS.jpg)


## Usage
The data integration service works as a command-line tool:

```bash
java –jar [data-integration-service-xxxxx.jar] command "-Dbypass.checkConsistency.dates=[true|false] -Ddirect.trust.keystore.location.canton=cert\signing_keystore_canton.p12 -Ddirect.trust.keystore.password.location.canton=cert\signing_pw_canton.txt -Ddirectory.source=src"
```

In the folder src, the following files must be present:
* eCH-0045 XML file(s)
* eCH-0157/9 XML file(s)
* application.properties, defines some general parameters for the election event
* params.xml, configures the data integration service, by activating plugins, and provides additional election event parameters that are not present in the eCH-files


## Development

```bash
mvn clean install
```
