/**
 * A sphere is denoted by a center point and a radius
 */
public class Sphere {
    private Point center;
    private double radius;

    public Sphere (Point c, double r ) {
        center = c;
        radius = r;
    }

    public Point getCenter () {
        return center;
    }
    
    public double getRadius () {
        return radius;
    }
 }