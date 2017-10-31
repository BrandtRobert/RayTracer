#!/bin/bash
for i in $(ls -1 driver*.txt); do
    ppmname=$(echo $i | cut -d "." -f 1 | cut -b 7-)
    ./run.sh $i test$ppmname.ppm
done;