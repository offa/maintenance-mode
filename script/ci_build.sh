#!/bin/sh

set -ex

mvn install -DskipTests=true -Dmaven.javadoc.skip=true -V --no-transfer-progress
mvn verify --no-transfer-progress
