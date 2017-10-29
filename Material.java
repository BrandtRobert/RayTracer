import org.ejml.simple.SimpleMatrix;
import java.util.Scanner;
import java.io.File;

public class Material {
    public RGB ambient;
    public RGB diffuse;
    public RGB specular;
    public double phong;

    public Material (RGB ambient, RGB diffuse, RGB specular, double p) {
        this.ambient  = ambient;
        this.diffuse  = diffuse;
        this.specular = specular;
        this.phong = p;
    }

    public Material (RGB ambient, RGB diffuse, RGB specular) {
        this.ambient  = ambient;
        this.diffuse  = diffuse;
        this.specular = specular;
        // Just set a default phong constant
        this.phong = 16;
    }

    public static Material fromFile (String filename) {
        try {
            RGB ambient = null; 
            RGB diffuse = null;
            RGB specular = null;
            double p = 0.0;            
            Scanner fReader = new Scanner (new File (filename));      
            while (fReader.hasNext()) {
                String line = fReader.nextLine();
                if (!line.startsWith("#*") || !line.matches("\\s")) {
                    String [] lineItems = line.split(" ");
                    double a;
                    double b;
                    double c;
                    if (lineItems[0].equalsIgnoreCase("Ka")) {
                        a = Double.parseDouble(lineItems[1]);
                        b = Double.parseDouble(lineItems[2]);
                        c = Double.parseDouble(lineItems[3]);
                        ambient = new RGB (a,b,c);
                    } else if (lineItems[0].equalsIgnoreCase("Kd")) {
                        a = Double.parseDouble(lineItems[1]);
                        b = Double.parseDouble(lineItems[2]);
                        c = Double.parseDouble(lineItems[3]);
                        diffuse = new RGB (a,b,c);
                    } else if (lineItems[0].equalsIgnoreCase("Ks")) {
                        a = Double.parseDouble(lineItems[1]);
                        b = Double.parseDouble(lineItems[2]);
                        c = Double.parseDouble(lineItems[3]);
                        specular = new RGB (a,b,c);
                    } else if (lineItems[0].equalsIgnoreCase("Ns")) {
                        p = Double.parseDouble(lineItems[1]);
                    } else {
                        // Just do nothing with this for now
                    }
                }
            }
            return new Material (ambient, diffuse, specular, p);        
        } catch (Exception e) {
            System.err.println("Failure in generating material from file");
            return null;
        }
    }

    public String toString () {
        return String.format("Ambient: %s\nDiffuse: %s\nSpecular: %s", ambient, diffuse, specular);
    }
}