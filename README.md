# Running P1

Use the following command, adding in what driver file to run:
* The run.sh script will compile the  and materials filesmodeltoworld with the ejml library in the classpath
  * It will then run the Modeltoworld main with the given driverfile
* Before running the script it will rm all class files in the current directory and recompile
  * *I did this because sometimes the library wouldn't load properly, to run several driver files more effeciently comment out that line*

`
  ./run.sh 'driverfile.txt' 'outputfile.txt'
`

**Important Notes**

1. The ppm files may not match exactly because of the precision of floating point operations, and how vector normals are calculated. In future versions I intend to calculate vector normals by using a weighted average of the normals give in the .obj file.
2. The models, materials, and driver files must be in the SAME WORKING DIRECTORY as the ray tracer class.
3. If the run.sh script isn't working, you should be able to compile with these commands:

`
# Clean out class files in the current dir
rm *.class
# Remove the org dir and rebuild the libaries
rm -rf ./org
jar -xvf EJML.jar
# Compile with the libraries in the classpath
javac -cp "./EJMl.jar:." Raytracer.java
# Run the program
java Raytracer 'driverfile.txt' 'outputfile.ppm'
`
