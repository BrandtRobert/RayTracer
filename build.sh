#! /bin/bash
# Runs the project with cmd line arg $1
ls *.class | xargs rm
javac -cp "./EJML.jar:." Raytracer.java