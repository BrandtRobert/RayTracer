#! /bin/bash
# Runs the project with cmd line arg $1
ls *.class | xargs rm
# javac -cp "./org/ejml/*:." Modeltoworld.java
javac -cp "./EJML.jar:." Modeltoworld.java
java Modeltoworld $1
