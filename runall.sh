#!/bin/bash
for i in $(ls -1 ./drivers_models/driver*.txt); do
    ppmname=$(echo $i | cut -d "." -f 1 | cut -b 7-)
    ./run.sh ./drivers_models/$i test$ppmname.ppm
done;