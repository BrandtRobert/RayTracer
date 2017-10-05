import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

public class DriverFileLoader {

    private static class InvalidModelException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidModelException (String message) {
            super("Invalid Driver File Format: " + message);
        }
    }

    public static ArrayList<DriverModel> loadDriverFile (String filename) throws Exception {
        File driverFile = new File(filename);
        if (driverFile.length() == 0) {
            throw new InvalidModelException("File empty!");
        }
        Scanner fileReader = new Scanner(driverFile);
        // List for holding all the models in the driver file
        ArrayList<DriverModel> modelList = new ArrayList<DriverModel>();

        while(fileReader.hasNext()) {
            String line = fileReader.nextLine();
            // Lines starting # are comments
            if (!line.startsWith("#")) {
                // Split line into space delimited arraylist
                String [] lineItems = line.split(" ");
                if (!lineItems[0].equals("model")) {
                    fileReader.close();
                    throw new InvalidModelException("lines must begin with 'model'");
                }
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
                // Create new model and push it to the list
                modelList.add(new DriverModel(wX,wY,wZ,theta,scale,tX,tY,tZ,name));
            }
        }
        fileReader.close();
        return modelList;
    }
}