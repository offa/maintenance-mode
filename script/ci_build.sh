#!/bin/sh

set -ex

mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
mvn verify -B
