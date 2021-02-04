#!/bin/sh

set -ex

mvn verify -Dmaven.javadoc.skip=true -V --no-transfer-progress
mvn pmd:pmd --no-transfer-progress
