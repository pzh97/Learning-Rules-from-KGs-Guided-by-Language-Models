#!bin/bash

for k in 5 10 20 50 100
do
    aqs=()
    for i in $(seq 0.0 0.1 1.0)
    do
        aq=$(mvn exec:java -Dexec.mainClass=de.mpii.util.Infer -Dexec.args="./data/wiki44k/ ./data/wiki44k/rules_noxyz/rules_$i.txt.sorted $k out" 2>&1 | grep -E "average_quality" | sed 's/INFO: #average_quality = //')
        aqs+=$aq
        aqs+=,
    done
    echo $aqs
done


























