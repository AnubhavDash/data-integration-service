# Data Integration Service

## What is the Content of this Repository?

The Data Integration Service is a tool designed to process XML files describing the electoral roll and the configuration of election events.
The tool produces a single, unified XML file that the Swiss Post Voting system can use.
The XML files processed by the service follow the eCH-standard, an open E-Government standard used in Switzerland.

One of the key features of the Data Integration Service is its ability to handle variations in the XML files it processes.
The XML files may differ slightly from one customer to another and come in different versions. Additionally, the service can enrich the data in the XML files with additional information.

The [eCH-standard](https://www.ech.ch) is widely used in Switzerland as a common format for data exchange in the e-government domain.
The standard defines a set of XML schemas that describe various types of data, such as the electoral roll and the configuration of election events. The Data Integration Service can process XML files that conform to these schemas and produce a single, unified XML file that the Swiss Post Voting system can use.
We use the following eCH-standards:

* eCH-0045 for the electoral roll
* eCH-0157 for an election
* eCH-0159 for a vote (referendum-style).

The Data Integration Service is a powerful tool that allows for the seamless integration of data from various sources.
Moreover, the data integration service can process large amounts of data by optimizing memory usage via streams.
It can handle variations in the XML files it processes and enrich the data with additional information,
making it an essential tool for e-voting customers that need to process large amounts of data from various sources in the e-voting domain.

XML (eXtensible Markup Language) files are widely used as an interface between different systems due to their ability to facilitate easy content validation. The open eCH-standard also utilizes XML files as its primary format for data representation.

The use of XML files as an interface between different systems allows for the validation of the content of the files in a consistent and standardized manner. The structure of an XML file is defined by a schema, which specifies the elements and attributes that can be included in the file, as well as the relationships between these elements. By validating an XML file against its schema, it is possible to ensure that the file conforms to the expected structure and contains the required data.

The use of the eCH-standard further enhances the ability to validate the content of XML files, as it defines a set of schemas that are used to describe various types of data. By conforming to the eCH-standard, organizations can ensure that their data is in a format that can be easily understood and processed by other systems that also conform to the standard.

## Changelog

An overview of all major changes within the published releases is available [here.](CHANGELOG.md)

## Future Work

We plan for the following improvements to the Data Integration Service.

* Complete the obfuscation of all test data

## Usage

The data integration service works as a command-line tool:

```bash
java –jar [data-integration-service-xxxxx.jar] command
```

In the folder src, the following files must be present:

* eCH-0045 XML file(s)
* eCH-0157/9 XML file(s)
* Auxiliary CSV files (if eCH version 3 is used)
* params.xml, configures the data integration service, by activating plugins, and provides additional election event parameters that are not present in the eCH-files

## Development

```bash
mvn clean install
```
