#!/bin/sh

set -ex

mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V --no-transfer-progress
mvn verify -B --no-transfer-progress
