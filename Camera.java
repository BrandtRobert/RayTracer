import java.util.Arrays;

/**
 * Defines a camera in 3d space
 * The components of a camera are:
 *  - an eye point (the camera's place in the world)
 *  - an up vector (which way is up)
 *  - a look at point (a point on the object on which it is focusing)
 *  - view plane normal vector (Wv), a vector from the look at point to the camera
 *      - yes, its facing backwards
 *  - bounds for the image plane
 *  - a value for how the far image plan is from the camera
 *  - Uv: a horizontal vector to scale the image plane
 *  - Vv: a vertical vector to scale the image plane
 */

public class Camera {
    Point eyePoint, lookatPoint;
    Vector upVector, viewPlaneNormal;
    // Image plan bounds (left, right, bottom, top)
    double right;
    double left;
    double bottom;
    double top;
    // How far the bounded image plane is from the camera
    double near;
    // The horizontal and vertical vectors
    Vector Uv, Vv;

    // Bnd contains integer values denoting the bounded image plane
    // In this order: left, bottom, right, top
    public Camera (Point e, Point l, Vector up, double [] bnd, double near) {
        eyePoint = e;
        lookatPoint = l;
        this.near = -Math.abs(near);
        // head minus tail rule, this draws a vector from l to e
        viewPlaneNormal(l, e);
        upVector = up;
        // Bnds should be 4 values
        if (bnd.length > 4) {
            System.err.println("Bounds provided contain too many values, taking the first 4");
            bnd = Arrays.copyOfRange(bnds, 0, 4);
        }
        left = bnd[0];
        bottom = bnd[1];
        right = bnd[2];
        top = bnd[3];
        Uv = up.crossProduct(viewPlaneNormal);
        Vv = viewPlaneNormal.crossProduct(Uv);
    }

    /**
     * Renders a distance heat map for an object with width x height resolution
     */
    public Image generateImage (ObjectModel renderObject, Sphere sphere, int width, int height) {
        Image image = new Image(width, height);
        double [][] tValues = new double [width][height];
        // The t max and t min values for the heat map
        double min = Double.MAX_VALUE;
        double max = 0;
        // For each pixel cast a ray
        for (int i = 0; i < image.height; i++) {
            for (int j = 0; j < image.height; j++) {
                Ray r = castRay (i, j);
                double tDistance = 0;
                // If there is a render object check for intersections on that
                if (renderObject != null) {
                    tDistance = intersectTriangle(renderObject, r);
                    tValues[i][j] = tDistance;
                    // Check the max and min if t is positive (we found an intersection)
                    if (tDistance > 0) {
                        min = Math.min(tDistance, min);
                        max = Math.max(tDistance, max);
                    }
                } else if (sphere != null) {
                    // Otherwise do intersections on a spehre
                    tDistance = intersectSphere(sphere, r);
                    tValues[i][j] = tDistance;
                    if (tDistance > 0) {
                        min = Math.min(tDistance, min);
                        max = Math.max(tDistance, max);
                    }
                } else {
                    // No object to render
                    return null;
                }
            }
        }
        return image.createHeatMap(tValues, max, min);
    }

    /**
     * Returns an ray cast from the eye point onto a pixel i, j in the image plane
     */
    private Ray castRay (double i, double j) {
        // Get the pixel value i,j on the image plane in world coordinates
        double x = ((i / width - 1) * (right - left)) + left;
        double y = ((i / height - 1) * (top - bottom)) + bottom;
        // Pixelpt = Eye + near * Wv (viewplaneNormal) + x * Uv + y * Vv
        Vector W = viewPlaneNormal.scale(near); // near * Wv
        Vector Uscaled = Uv.scale(x);           // x * Uv
        Vector Vscaled = Vv.scale(y);           // y * Vv
        Vector eyeVect = eyePoint.toVector();
        // A ray is a point and direction
        Point pixelPoint = eyeVect.add(W).add(Uscaled).add(Vscaled).toPoint();
        Vector direction = new Vector (eyePoint, pixelPoint);
        return new Ray (pixelPoint, direction);
    }

    /**
     * Performs ray triangle intersection and returns the distance from the pixel to the triangle.
     */
    private double intersectTriangle (ObjectModel object, Ray ray) {
        for (Face f : object.getFaces()) {
            // Before solving check to see if the triangle and ray are parallel
            Vector AC = new Vector (ray.A, ray.C);
            Vector AB = new Vector (ray.A, ray.B);
            // If the ray direction is orthogonal to the normal vector of the plane defined by vectors
            // AB and AC, then the ray is parallel to the triangle
            if (AC.crossProduct(AB).dotProduct(ray.getDirection()) != 0) {
                // Solve the matrix equation
                // Mx = Y
                SimpleMatrix Mm = generateM (f, ray.getDirection());
                SimpleMatrix Ym = generateY (f, ray.getOrigin());
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
                        return t;
                    }
                }
            }
        }
        // We were unable to find an intersection
        // Return a negative 1 so the calling functions knows we didn't find anything
        return 0;
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
    
    /**
     * Performs ray sphere intersection and returns the distance from the pixel to the sphere.
     * You can read more about this method in the following link.
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection
     */
    private double intersectSphere (Sphere sphere, Ray ray) {
        // Create a vector from the the eye point to the center of the sphere
        Vector rayOriginToCenter = new Vector (ray.getOrigin(), sphere.getCenter());
        // Determine the length of v by using the dot product
        double v = ray.getDirection().dotProduct(rayOriginToCenter);
        double vSquared = v * v;
        // The length of c is the magnitude of ray origin to the center of the sphere
        double cSquared = rayOriginToCenter.dotProduct(rayOriginToCenter);
        // d = sqrt (r^2 - (c^2 - v^2))
        double d = Math.sqrt((sphere.getRadius() * sphere.getRadius()) - (cSquared - vSquared));

        // If d == 0 the ray hits the center of the spehere, otherwise if it is greater than 0
        // The ray hits some part of the sphere
        if (d >= 0) {
            // v - d is the distance from the ray origin to when it first intersects the sphere
            return v - d;
        }
        // If d < 0 then the ray doesn't intersect the sphere
        return 0;
    }

}