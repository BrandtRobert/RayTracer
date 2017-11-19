import java.util.Arrays;
import org.ejml.simple.SimpleMatrix;

public class Ray {
    private Point origin;
    private Vector direction;
    private double closestDist;
    private double closestPt;
    private Object closestObj;

    public Ray (Point p, Vector d) {
        this.origin = p;
        this.direction = d;
        // Make the direction and normalization the same values to avoid mistakes
        // We are inforcing that the D vector in P + Dt must be unit length,
        // Having an endpoint for it is rather meaningless
        this.direction.direction = this.direction.normalized;
        this.direction.magnitude = 1;
        // Use these variables for ray tracing intersections 
        closestDist = Double.MAX_VALUE;
        closestObj = null;
    }

    /**
     * Scales along the ray starting at the origin in the direction of the rays vector.
     * Return a point that denotes the end of the ray.
     */
    public Point scale (double t) {
        double x, y, z;
        x = t * direction.normalized[0] + origin.x;
        y = t * direction.normalized[1] + origin.y;
        z = t * direction.normalized[2] + origin.z;
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

    public Point getClosestPoint () {
        return scale (closestDist);
    }

    public double getClosestDist () {
        return closestDist;
    }

    public Object getClosestObj () {
        return closestObj;
    }

    public double dotProduct (Ray other) {
        return this.getDirection().dotProduct(other.getDirection());
    }

    /**
     * Returns a copy of this Ray with the direction backwards
     */
    public Ray getReverse () {
        return new Ray (this.origin, this.direction.getReverse());
    }

    /**
     * Performs ray triangle intersection and returns the distance from the pixel to the triangle.
     */
    public double intersectTriangle (Face face) {
        Ray ray = this;
        // Before solving check to see if the triangle and ray are parallel
        Vector AC = new Vector (face.A, face.C);
        Vector AB = new Vector (face.A, face.B);
        // If the ray direction is orthogonal to the normal vector of the plane defined by vectors
        // AB and AC, then the ray is parallel to the triangle
        if (AC.crossProduct(AB).dotProduct(ray.getDirection()) != 0) {
            // Solve the matrix equation
            // Mx = Y
            SimpleMatrix Mm = generateM (face, ray.getDirection());
            SimpleMatrix Ym = generateY (face, ray.getOrigin());
            /**
             * The solution matrix is in the form
             * | B | - B is beta
             * | Y | - Y is gamma
             * | t | - t is the distance to the face plane
             */
            SimpleMatrix solution = Mm.invert().mult(Ym);
            double beta = solution.get(0,0);
            double gamma = solution.get(1,0);
            double t = solution.get(2,0);
            // If B & Y <= 0, t > 0, and beta + gamma <= 1
            // Then the ray intersects the triangle
            if (beta >= 0 && gamma >= 0 && t > 0) {
                if ((beta + gamma <= 1)) {
                    // If t is closer than any other obj set t to the value
                    if (t < ray.closestDist) {
                        ray.closestDist = t;
                        ray.closestObj = face;
                    }
                    return t;
                }
            }
        }
    // We were unable to find an intersection
    // Return a negative 1 so the calling functions knows we didn't find anything
    return -1;
    }
    
    /**
     * Performs ray sphere intersection and returns the distance from the pixel to the sphere.
     * You can read more about this method in the following link.
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection
     */
    public double intersectSphere (Sphere sphere) {
        Ray ray = this;
        // Create a vector from the the eye point to the center of the sphere
        Vector rayOriginToCenter = new Vector (ray.getOrigin(), sphere.getCenter());
        // Determine the length of v by using the dot product
        double v = ray.getDirection().dotProduct(rayOriginToCenter);
        double vSquared = v * v;
        // The length of c is the magnitude of ray origin to the center of the sphere
        double cSquared = rayOriginToCenter.dotProduct(rayOriginToCenter);
        // d = sqrt (r^2 - (c^2 - v^2))
        double dSquared = (sphere.getRadius() * sphere.getRadius()) - (cSquared - vSquared);
        // If d == 0 the ray hits the center of the spehere, otherwise if it is greater than 0
        //  the ray hits some part of the sphere        
        if (dSquared >= 0) {
            double d = Math.sqrt(dSquared);
            // v - d is the distance from the ray origin to when it first intersects the sphere
            double t = v - d;
            // If t is closer than any other obj set t to the value            
            if (t < ray.closestDist) {
                ray.closestDist = t;
                ray.closestObj = sphere;
            }
            return t;
        }
        // If d < 0 then the ray doesn't intersect the sphere
        return -1;
    }

    /**
     * Generates the matrix M for a triangle with points A, B, C
     * To solve the matrix equation Mx = Y
     * | ax - bx  ax - cx  dx | 
     * | ay - by  ay - cy  dy |
     * | az - bz  az - cz  dz |
     */
    private SimpleMatrix generateM (Face f, Vector d) {
        SimpleMatrix M = new SimpleMatrix(3,3);
        // First row
        M.set(0,0, (f.A.x - f.B.x));
        M.set(0,1, (f.A.x - f.C.x));
        M.set(0,2, d.normalized[0]);
        // Second row
        M.set(1,0, (f.A.y - f.B.y));
        M.set(1,1, (f.A.y - f.C.y));
        M.set(1,2, d.normalized[1]);
        // Third row
        M.set(2,0, (f.A.z - f.B.z));
        M.set(2,1, (f.A.z - f.C.z));
        M.set(2,2, d.normalized[2]);
        return M;
     }

     /**
      * Generates the Y matrix in the equation for ray triangle intersection.
      * | ax - lx |
      * | ay - ly |
      * | az - lz |
      */
     private SimpleMatrix generateY (Face f, Point l) {
        SimpleMatrix Y = new SimpleMatrix(3,1);
        Y.set(0,0, f.A.x - l.x);
        Y.set(1,0, f.A.y - l.y);
        Y.set(2,0, f.A.z - l.z);
        return Y;
     }

}