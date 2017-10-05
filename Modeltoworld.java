import java.io.File;
import java.util.*;

public class Modeltoworld {
  private static Map<String, Integer> obj_names = new HashMap<String, Integer>();
  private static String outputDir = "";
  /**
   * Takes the object and determines if there are duplicates
   * Returns the name/path for the output file
   */
  private static String getOutputFileName (String obj) {
    String exten = "";
    if (!obj_names.containsKey(obj)) {
      obj_names.put(obj, 0);
      exten = "_mw00.obj";
    } else {
      // The key is already in the map 
      int count = obj_names.get(obj) + 1;
      obj_names.put(obj, count);
      if (count < 10) {
        exten = "_mw0" + count + ".obj";
      } else {
        exten = "_mw" + count + ".obj";
      }
    }
    return outputDir + "/" + obj + exten;
  }

  private static void createDriverDir (String dirname) {
    // Get the directory name for the output files
    // Create the directory if it doesn't exsit
    Modeltoworld.outputDir = dirname.substring(0, dirname.indexOf("."));
    File dir = new File(Modeltoworld.outputDir);
    if (!dir.exists()) {
      dir.mkdir();
    }
  }
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: java Driver <driverfile>");
      System.exit(1);
    }
    // Files may come in the form ./test_driver/driver06.txt
    // But we want to create driver dirs at this level, so splice of the beginning of the path
    String [] pathArray = args[0].split("/");
    String dirname = pathArray[pathArray.length-1];
    // Create the directory for driver files if it doesn't exist already
    Modeltoworld.createDriverDir(dirname);
    // Attempt loading the driver file
    ArrayList<DriverModel> transformations = null;
    try {
      transformations = DriverFileLoader.loadDriverFile(args[0]);
    } catch (Exception e) {
      System.err.println("Failure to load driver file");
      System.err.println(e);
      System.exit(-1);
    }
    for (DriverModel d : transformations) {
      String outfilename = Modeltoworld.getOutputFileName(d.object_name);
      // Grab the object our of models/<obj_name>.obj
      ObjectModel currentObj = new ObjectModel("models/" + d.object_name + ".obj");
      currentObj.setVerticesMatrix(
        Transformations.performTranslations(currentObj.getVerticesMatrix(), d.rotation_axis, d.theta, d.scale, d.t_point)
      );
      System.out.println("Writing... " + outfilename);
      try {
        currentObj.writeToFile(outfilename);        
      } catch (Exception e) {
        System.err.printf("Unable to write obj: %s to file\n", d.object_name);
        System.err.println(e);
      }
    }
    System.out.println("done");
  }
}