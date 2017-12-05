import org.ejml.simple.SimpleMatrix;

/**
 * A sphere is denoted by a center point and a radius
 */
public class Sphere implements Colorable {
    private Point center;
    private double radius;
    private Material material;
    private SimpleMatrix attenuation_coeff;

    public Sphere (Point c, double r) {
        center = c;
        radius = r;
    }

    // Use the triple structure of a point to hold the RGB values (kind of a hack)
    public Sphere (Point c, double r, RGB ambient, RGB diffuse, RGB specular, RGB attenuation) {
        center = c;
        radius = r;
        material = new Material (ambient, diffuse, specular, attenuation, 16, "sphere");
    }

    /**
     * Get the surface normal for a given point the sphere
     */
    public Vector getNormal (Point surfacePt) {
        Point c = getCenter();
        Vector surfaceNormal = new Vector (c, surfacePt);
        surfaceNormal.direction = surfaceNormal.normalized;
        return surfaceNormal;
    }

    public Point getCenter () {
        return center;
    }
    
    public double getRadius () {
        return radius;
    }

    public Material getMaterial () {
        return material;
    }

    public String toString () {
        return String.format("Center: %s\nRadius: %.2f\nMaterial:\n%s\nAttenuation: %s", center, radius, material, attenuation_coeff);
    }
 }