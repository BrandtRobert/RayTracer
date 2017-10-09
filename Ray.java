import java.util.Arrays;

public class Ray {
    private Point origin;
    private Vector direction;

    public Ray (Point p, Vector d) {
        this.origin = p;
        this.direction = d;
        // Make the direction and normalization the same values to avoid mistakes
        // We are inforcing that the D vector in P + Dt must be unit length,
        // Having an endpoint for it is rather meaningless
        this.direction.direction = this.direction.normalized;
        this.direction.magnitude = 1;
    }

    /**
     * Scales along the ray starting at the origin in the direction of the rays vector.
     * Return a point that denotes the end of the ray.
     */
    public Point scale (double t) {
        double x, y, z;
        x = t * direction.normalized[0];
        y = t * direction.normalized[1];
        z = t * direction.normalized[2];
        return new Point(x, y, z);
    }

    public String toString () {
        return "Point: "     + origin.toString() + "\n" +
               "Direction: " + Arrays.toString(direction.normalized);
    }

    public Point getOrigin () {
        return origin;
    }

    public Vector getDirection () {
        return direction;
    }

}