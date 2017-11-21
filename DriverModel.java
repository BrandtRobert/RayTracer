import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class DriverModel {

    List<Transformation> transformations;
    List<Sphere> spheres;
    Light ambient;
    List<Light> lights;
    Camera cameraModel;
    int resWidth, resHeight, recurDepth;

    private static class InvalidModelException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidModelException (String message) {
            super("Invalid Driver File Format: " + message);
        }
    }

    public DriverModel (String fname) {
        transformations = new ArrayList<Transformation>();
        spheres = new ArrayList<Sphere>();
        lights = new ArrayList<Light>();
        try {
            loadDriverFile(fname);
        } catch (Exception e) {
            System.err.println ("Failed to load driver file");
            System.err.println (e);
        }
    }

    private void loadDriverFile (String filename) throws Exception {
        File driverFile = new File(filename);
        if (driverFile.length() == 0) {
            throw new InvalidModelException("File empty!");
        }
        Scanner fileReader = new Scanner(driverFile);
        // Define values needed for the Camera Model
        Point eye = null, lookat = null;
        double right = 0, left = 0, bottom = 0, top = 0, near = 0;
        Vector upVector = null;

        // For creating points
        double x, y, z;

        while(fileReader.hasNext()) {
            String line = fileReader.nextLine();
            // Lines starting # are comments
            if (!line.startsWith("#")) {
                // Split line into space delimited arraylist
                String [] lineItems = line.split(" ");
                if (lineItems[0].equals("model")) {
                    transformations.add((transformationFromLineItems(lineItems)));
                } else if (lineItems[0].equals("sphere")) {
                    x = Double.parseDouble(lineItems[1]);
                    y = Double.parseDouble(lineItems[2]);
                    z = Double.parseDouble(lineItems[3]);
                    double radius = Double.parseDouble(lineItems[4]);
                    // Ambient reflection
                    double ka_red = Double.parseDouble(lineItems[5]);
                    double ka_green = Double.parseDouble(lineItems[6]);
                    double ka_blue = Double.parseDouble(lineItems[7]);
                    RGB ka_reflect = new RGB (ka_red, ka_green, ka_blue);
                    // Diffuse reflection
                    double kd_red = Double.parseDouble(lineItems[8]);
                    double kd_green = Double.parseDouble(lineItems[9]);
                    double kd_blue = Double.parseDouble(lineItems[10]);
                    RGB kd_reflect = new RGB (kd_red, kd_green, kd_blue);
                    // Specular reflection
                    double ks_red = Double.parseDouble(lineItems[11]);
                    double ks_green = Double.parseDouble(lineItems[12]);
                    double ks_blue = Double.parseDouble(lineItems[13]);
                    RGB ks_reflect = new RGB (ks_red, ks_green, ks_blue);
                    // Attenuation coordinates
                    double kr_red = Double.parseDouble(lineItems[14]);
                    double kr_green = Double.parseDouble(lineItems[15]);
                    double kr_blue = Double.parseDouble(lineItems[16]);
                    RGB kr_coeff = new RGB (kr_red, kr_green, kr_blue);                    
                    Point center = new Point (x, y, z);
                    spheres.add(new Sphere(center, radius, ka_reflect, kd_reflect, ks_reflect, kr_coeff));
                } else if (lineItems[0].equals("eye")) {
                    x = Double.parseDouble(lineItems[1]);
                    y = Double.parseDouble(lineItems[2]);
                    z = Double.parseDouble(lineItems[3]);
                    eye = new Point (x, y, z);
                } else if (lineItems[0].equals("look")) {
                    x = Double.parseDouble(lineItems[1]);
                    y = Double.parseDouble(lineItems[2]);
                    z = Double.parseDouble(lineItems[3]);
                    lookat = new Point (x, y, z);
                } else if (lineItems[0].equals("up")) {
                    x = Double.parseDouble(lineItems[1]);
                    y = Double.parseDouble(lineItems[2]);
                    z = Double.parseDouble(lineItems[3]);
                    upVector =  new Vector (x, y, z);
                } else if (lineItems[0].equals("d")) {
                    near = Double.parseDouble(lineItems[1]);
                } else if (lineItems[0].equals("bounds")) {
                    // left, bottom, right, top
                    left = Double.parseDouble(lineItems[1]);
                    bottom = Double.parseDouble(lineItems[2]);
                    right = Double.parseDouble(lineItems[3]);
                    top = Double.parseDouble(lineItems[4]);
                } else if (lineItems[0].equals("res")) {
                    resWidth = Integer.parseInt(lineItems[1]);
                    resHeight = Integer.parseInt(lineItems[2]);
                } else if (lineItems[0].equals("ambient")) {
                    x = Double.parseDouble(lineItems[1]);
                    y = Double.parseDouble(lineItems[2]);
                    z = Double.parseDouble(lineItems[3]);
                    ambient = new Light (new RGB(x,y,z), null, 0);
                } else if (lineItems[0].equals("light")) {
                    x = Double.parseDouble(lineItems[1]);
                    y = Double.parseDouble(lineItems[2]);
                    z = Double.parseDouble(lineItems[3]);
                    double w = Double.parseDouble(lineItems[4]);
                    double r = Double.parseDouble(lineItems[5]);
                    double g = Double.parseDouble(lineItems[6]);
                    double b = Double.parseDouble(lineItems[7]);
                    Light l = new Light (new RGB(r,g,b), new Point(x,y,z), w);
                    lights.add(l);
                } else if (lineItems[0].equalsIgnoreCase("recursionLevel")) {
                    recurDepth = Integer.parseInt(lineItems[1]);
                } else {
                    throw new InvalidModelException("Unable to recognize phrase '" + lineItems[0] + "'");
                }
            }
        }
        fileReader.close();
        double [] bnds = {left, bottom, right, top};
        cameraModel = new Camera (eye, lookat, upVector, bnds, near);
    }

    private Transformation transformationFromLineItems (String [] lineItems) {
        // Read in model file
        double wX = Double.parseDouble(lineItems[1]);
        double wY = Double.parseDouble(lineItems[2]);
        double wZ = Double.parseDouble(lineItems[3]);
        double theta = Double.parseDouble(lineItems[4]);
        double scale = Double.parseDouble(lineItems[5]);
        double tX = Double.parseDouble(lineItems[6]);
        double tY = Double.parseDouble(lineItems[7]);
        double tZ = Double.parseDouble(lineItems[8]);
        // Remove the .obj extension
        String name = lineItems[9].substring(0, lineItems[9].indexOf("."));
        return new Transformation(wX,wY,wZ,theta,scale,tX,tY,tZ,name);
    }
}