#!/usr/bin/env bash

ARGS=`echo "$@"`
mvn exec:java -Dexec.mainClass=de.mpii.embedding.HypernymClient -Dexec.args="$ARGS"
