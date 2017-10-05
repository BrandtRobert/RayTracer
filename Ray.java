import java.util.Arrays;

public class Ray {
    Point origin;
    Vector direction;

    public Ray (Point p, Vector d) {
        this.origin = p;
        this.direction = d;
    }

    /**
     * Scales along the ray starting at the origin in the direction of the rays vector.
     * Return a point that denotes the end of the ray.
     */
    public Point scale (double t) {
        double x, y, z;
        x = t * direction.normalize[0];
        y = t * direction.normalize[1];
        z = t * direction.normalize[2];
        return new Point(x, y, z);
    }

    public String toString () {
        return "Point: "     + origin.toString() + "\n" +
               "Direction: " + Arrays.toString(direction.normalized);
    }

}