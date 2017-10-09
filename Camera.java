import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

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
        this.near = - Math.abs(near);
        // head minus tail rule, this draws a vector from l to e
        viewPlaneNormal = new Vector (l, e);
        upVector = up;
        // Bnds should be 4 values
        if (bnd.length > 4) {
            System.err.println("Bounds provided contain too many values, taking the first 4");
            bnd = Arrays.copyOfRange(bnd, 0, 4);
        }
        left = bnd[0];
        bottom = bnd[1];
        right = bnd[2];
        top = bnd[3];
        Uv = up.crossProduct(viewPlaneNormal);
        Vv = viewPlaneNormal.crossProduct(Uv);
        Vector temp = Uv;
        Uv = Vv;
        Vv = temp;
        Uv.direction = Uv.normalized;
        Vv.direction = Vv.normalized;
    }

    /**
     * Renders a distance heat map for an object with width x height resolution
     */
    public Image generateImage (List<Face> faces, List<Sphere> spheres, int width, int height) {
        Image image = new Image(width, height);
        double [][] tValues = new double [height][width];
        // The t max and t min values for the heat map
        double min = Double.MAX_VALUE;
        double max = 0;
        // For each pixel cast a ray, and see what it hits
        for (int i = 0; i < image.height; i++) {
            for (int j = 0; j < image.width; j++) {
                Ray r = castRay (i, j, width, height);
                boolean rayCollided = false;
                double tTemp = 0;
                double closestObj = Double.MAX_VALUE;
                // Try to intersect all faces :/ bleh this is gonna take a long ass time .... 
                int faceCount = 1;
                for (Face f : faces) {
                    tTemp = intersectTriangle(f, r);
                    // If tDistance is not -1 the ray hit something
                    // We only want the face closest to the camera that the ray hits
                    if (tTemp >= 0) {
                        rayCollided = true;
                        closestObj = Math.min (tTemp, closestObj);
                    }
                    faceCount++;
                }
                // Try to intersect all spheres :/ I am going to be surprised if there's enough memory to render all this shit
                for (Sphere s : spheres) {
                    tTemp = intersectSphere(s, r);
                    // If tDistance is not -1 the ray hit something
                    // We only want the face closest to the camera that the ray hits
                    if (tTemp >= 0) {
                        rayCollided = true;
                        closestObj = Math.min (tTemp, closestObj);
                    }
                }
                // For each pixel collect the tValue
                // If no collision make the value -1
                tValues[i][j] = (rayCollided)? closestObj : -1;

                if (rayCollided) {
                    min = Math.min(min, closestObj);
                    max = Math.max(max, closestObj);
                }
            }
        }
System.out.printf("Max: %.2f Min: %.2f\n", max, min);
        return image.createHeatMap(tValues, max, min);
    }

    /**
     * Returns an ray cast from the eye point onto a pixel i, j in the image plane
     */
    private Ray castRay (double i, double j, double width, double height) {
        // Get the pixel value i,j on the image plane in world coordinates
        double x = (i / (width - 1)) * (right - left) + left;
        double y = (j / (height - 1)) * (top - bottom) + bottom;
        // Pixelpt = Eye + near * Wv (viewplaneNormal) + x * Uv + y * Vv
        Vector W = viewPlaneNormal.scale(near); // near * Wv
        Vector Uscaled = Uv.scale(x);           // x * Uv
        Vector Vscaled = Vv.scale(y);           // y * Vv
        Vector eyeVect = eyePoint.toVector();
        // A ray is a point and direction
        Point pixelPoint = eyeVect.add(W).add(Uscaled).add(Vscaled).toPoint();
        Vector direction = new Vector (eyePoint, pixelPoint);
        Ray newRay = new Ray (pixelPoint, direction);
        return newRay;
    }

    /**
     * Performs ray triangle intersection and returns the distance from the pixel to the triangle.
     */
    private double intersectTriangle (Face face, Ray ray) {
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
                    return t;
                }
            }
        }
    // We were unable to find an intersection
    // Return a negative 1 so the calling functions knows we didn't find anything
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
        double dSquared = (sphere.getRadius() * sphere.getRadius()) - (cSquared - vSquared);
        // If d == 0 the ray hits the center of the spehere, otherwise if it is greater than 0
        //  the ray hits some part of the sphere        
        if (dSquared >= 0) {
            double d = Math.sqrt(dSquared);
            // v - d is the distance from the ray origin to when it first intersects the sphere
            return v - d;
        }
        // If d < 0 then the ray doesn't intersect the sphere
        return -1;
    }

    public String toString() {
        String s = "";
        s += String.format("Eye: %s\n", eyePoint.toString());
        s += String.format("Lookat: %s\n", lookatPoint.toString());
        s += String.format("Up vector: %s\n", upVector.toString());
        s += String.format("VPN: %s\n", viewPlaneNormal.toString());
        s += String.format("Bounds: [Left %.1f, Bottom %.1f, Right %.1f, Top %.1f]\n", left, bottom, right, top);
        s += String.format("Near: %.1f\n", near);
        s += String.format("Uv: %s\n", Uv.toString());
        s += String.format("Vv: %s\n", Vv.toString());
        return s;
    }

}