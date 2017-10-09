import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class DriverModel {

    List<Transformation> transformations;
    List<Sphere> spheres;
    Camera cameraModel;
    int resWidth, resHeight;

    private static class InvalidModelException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidModelException (String message) {
            super("Invalid Driver File Format: " + message);
        }
    }

    public DriverModel () {
        transformations = new ArrayList<Transformation>();
        spheres = new ArrayList<Sphere>();
    }

    public void loadDriverFile (String filename) throws Exception {
        File driverFile = new File(filename);
        if (driverFile.length() == 0) {
            throw new InvalidModelException("File empty!");
        }
        Scanner fileReader = new Scanner(driverFile);
        // Define values needed for the Camera Model
        Point eye, lookat;
        double right, left, bottom, top, near;
        Vector upVector;

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
                    Point center = new Point (x, y, z);
                    spheres.add(new Sphere(center, radius));
                } else if (linesItems[0].equals("eye")) {
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
                } else if (linesItems[0].equals("res")) {
                    resWidth = Integer.parseInt(lineItems[1]);
                    resHeight = Integer.parseInt(lineItems[2]);
                } else {
                    throw new InvalidModelException("Unable to recognize phrase '" + lineItems[0] + "'");
                }
            }
        }
        fileReader.close();
        double [] bnds = {left, bottom, right, top};
        this.camera = new Camera (eye, lookat, upVector, bnds, near);
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