#!/usr/bin/env bash
#
# (c) Copyright 2023 Swiss Post Ltd.
#

BASEDIR=$(dirname "$0")

mvn install:install-file -Dfile="$BASEDIR"/crypto-primitives-1.2.0.0.jar -DpomFile="$BASEDIR"/crypto-primitives-1.2.0.0.pom
mvn install:install-file -Dfile="$BASEDIR"/crypto-primitives-domain-1.2.0.0.jar -DpomFile="$BASEDIR"/crypto-primitives-domain-1.2.0.0.pom
mvn install:install-file -Dfile="$BASEDIR"/domain-1.2.2.0.jar -DpomFile="$BASEDIR"/domain-1.2.2.0.pom
mvn install:install-file -Dfile="$BASEDIR"/evoting-dependencies-1.2.2.0.pom -DpomFile="$BASEDIR"/evoting-dependencies-1.2.2.0.pom -Dpackaging=pom
mvn install:install-file -Dfile="$BASEDIR"/evoting-1.2.2.0.pom -DpomFile="$BASEDIR"/evoting-1.2.2.0.pom -Dpackaging=pom
mvn install:install-file -Dfile="$BASEDIR"/cryptolib-1.2.2.0.jar -DpomFile="$BASEDIR"/cryptolib-1.2.2.0.pom
