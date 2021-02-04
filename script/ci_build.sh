#!/bin/sh

set -ex

mvn verify --batch-mode -Dmaven.javadoc.skip=true -V --no-transfer-progress
mvn pmd:pmd --batch-mode --no-transfer-progress
