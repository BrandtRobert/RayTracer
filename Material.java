import org.ejml.simple.SimpleMatrix;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class Material {
    public RGB ambient;
    public RGB diffuse;
    public RGB specular;
    public RGB attenuation;
    public String name;
    public double phong;

    public Material (RGB ambient, RGB diffuse, RGB specular, RGB attn, double p, String n) {
        this.ambient  = ambient;
        this.diffuse  = diffuse;
        this.specular = specular;
        this.attenuation = attn;
        this.phong = p;
        this.name = n;        
    }

    public Material (RGB ambient, RGB diffuse, RGB specular) {
        this.ambient  = ambient;
        this.diffuse  = diffuse;
        this.specular = specular;
        // Just set a default phong constant
        this.phong = 16;
    }

    public static List<Material> fromFile (String filename) {
        try {
            RGB ambient = null; 
            RGB diffuse = null;
            RGB specular = null;
            RGB attenuation = new RGB(1, 1, 1);
            List<Material> matList = new ArrayList<Material>();
            String currMatName = null;
            double p = 0.0;            
            Scanner fReader = new Scanner (new File (filename));      
            while (fReader.hasNext()) {
                String line = fReader.nextLine();
                if (!line.startsWith("#*") || !line.matches("\\s")) {
                    String [] lineItems = line.split(" ");
                    if (lineItems[0].equalsIgnoreCase("newmtl")) {
                    	String materialName = lineItems[1];
                    	while (fReader.hasNext() && !fReader.hasNext("newmtl*")) {
                    		line = fReader.nextLine();
                    		lineItems = line.split(" ");
                    		double a, b, c;
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
                            } else if (lineItems[0].equalsIgnoreCase("Kr")) {
                                a = Double.parseDouble(lineItems[1]);
                                b = Double.parseDouble(lineItems[2]);
                                c = Double.parseDouble(lineItems[3]);
                                attenuation = new RGB (a,b,c);
                            } else {
                                // Just do nothing with this for now
                            }
                    	}
                    	matList.add(new Material(ambient, diffuse, specular, attenuation, p, materialName));
                    	attenuation = new RGB (1,1,1);
                    }
                }
            }
            fReader.close();
            return matList;
        } catch (Exception e) {
            System.err.println("Failure in generating material from file");
            return null;
        }
    }

    public String toString () {
        return String.format("Ambient: %s\nDiffuse: %s\nSpecular: %s", ambient, diffuse, specular);
    }
}