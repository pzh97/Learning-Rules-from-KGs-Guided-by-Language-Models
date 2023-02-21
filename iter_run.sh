#!bin/bash

for i in $(seq 0.1 0.1 1.0)
do
    bash run.sh -w ./data/imdb -em bert -xyz -ew $i -nw 4
done


























