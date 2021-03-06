import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;

public class Raytracer {

    private static final String objPath = "./drivers_models/";

    public static void main (String args[]) {
        String driverFname = null; 
        String outputFname = null;
        if (args.length == 2) {
            driverFname = args[0];
            outputFname = args[1];
        } else {
            System.err.println("Usage: Raytracer driver.txt driver.ppm");
            System.exit(1);
        }
        // Read the driver file and fill an array with all faces and spheres 
        DriverModel driver = new DriverModel(driverFname);
        ArrayList<Face> allObjectFaces = new ArrayList<Face>();
        // Perform each translation and add it to the list of total faces
        for (Transformation t : driver.transformations) {
            // Creating two objectmodels is ineffecienct will lead to memory issues w/ large objects 
            ObjectModel baseObj = new ObjectModel(objPath + t.object_name + ".obj");
            SimpleMatrix trans = Translator.performTranslations
                (baseObj.getVerticesMatrix(), t.rotation_axis, t.theta, t.scale, t.t_point);
            ObjectModel transObj = new ObjectModel(trans, baseObj);
            allObjectFaces.addAll(transObj.getFaces());
            System.out.printf("Placing object '%s'...\n", baseObj.getName());
        }
        System.out.println("Placing camera and rendering scene...");
        Scene scene = new Scene (driver.cameraModel, allObjectFaces, driver.spheres, driver.lights, driver.ambient);
        Image img = scene.generateImage(driver.resWidth, driver.resHeight, driver.recurDepth);
        System.out.printf("Writing scene to '%s'...\n", outputFname);
        img.writeToFile(outputFname);
        System.out.println("Done!");
    }
}
