#! /bin/bash
# Runs the project with cmd line arg $1
ls *.class | xargs rm
# javac -cp "./org/ejml/*:." Raytracer.java
javac -cp "./EJML.jar:." Raytracer.java
java Raytracer $1 $2
