#!bin/bash

for i in $(seq 0.0 0.1 1.0)
do
    bash run.sh -w ./data/wiki44k -em bert -ew $i -nw 4 # -xyz
    cp ./data/wiki44k/rules.txt.sorted ./data/wiki44k/rules_noxyz/rules_$i.txt.sorted
    # mvn exec:java -Dexec.mainClass=de.mpii.util.Infer -Dexec.args="./data/wiki44k/ ./data/wiki44k/rules/rules_$i.txt.sorted 5 out" 2>&1 | grep -E "average_quality"
done


























