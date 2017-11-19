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
        Uv.direction = Uv.normalized;
        Vv.direction = Vv.normalized;
    }

    /**
     * Renders a distance heat map for an object with width x height resolution
     */
    public Image generateImage (List<Face> faces, List<Sphere> spheres, List<Light> lights, Light ambient, int width, int height) {
        Image img = new Image (width, height);
        RGB [][] pixelMap = new RGB [width][height];
        // For each pixel cast a ray, and see what it hits
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	Ray r = castRay (i, j , width, height);
                Vector surfaceNormal = r.rayTest(spheres, faces);
                // If we got a collision calculate the color
                if (surfaceNormal != null) {
                    // You can do this because the closeset object is either a sphere or face
                    // They both implement the getMaterial method
                    Material objMaterial = ((Colorable)r.getClosestObj()).getMaterial();
                    pixelMap [i][j] = colorPixel (r, objMaterial, surfaceNormal, lights, ambient);     
                } else {
                    // If no intersection color the pixel black
                    pixelMap [i][j] = new RGB(0,0,0);
                }
            }
        }
        return img.mapPixels(pixelMap);
    }

    /**
     * Returns an ray cast from the eye point onto a pixel i, j in the image plane
     */
    private Ray castRay (double i, double j, double width, double height) {
        // Get the pixel value i,j on the image plane in world coordinates
        double x = (i / (width - 1)) * (left - right) + right;        
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
     * For each light
     *  get the vector from the intersection to the light
     *  get the vector from the intersection to the camera
     *  if the the light is non-orthogonal to the surface normal:
     *      diffuse: mat_diffuse * light_brightness * surface_norm.dot(light_vect)
     *      specular:
     *          R = (2 * surface_norm.dot(light_vect)) * surface_norm - light_vect
     *          mat_spec * light_brightness * (camera_vect.dot(R))^phong_const
     */
    private RGB colorPixel (Ray ray, Material material, Vector surfaceNormal, List<Light> lights, Light ambient) {
        // Ambient
        // ambient * mat_ambient reflection
        RGB color = new RGB (0,0,0);
        if (ambient != null) {
            color = ambient.getColor().pairwiseProduct(material.ambient);
        }
        // To camera ray is the going back on the horse we rode in on
        Ray toOrig = ray.getReverse();
        // If the dot product between the camera vector and the surface normal is negative
        // Then the surface normal is backwards
        if (toOrig.getDirection().dotProduct(surfaceNormal) < 0) {
        	surfaceNormal = surfaceNormal.getReverse();
        } 	
        for (Light l : lights) {
            Vector toLightVect = new Vector (ray.getClosestPoint(), l.getPosition());
            Ray toLight = new Ray (ray.getClosestPoint(), toLightVect);
            double lDotNorm = toLight.getDirection().dotProduct(surfaceNormal);
            // Only calculate the illumination if the cos (theta) between the light vector and the surface normal
            // Is non-negative and non-zero, as a negative cos would denote the light as behind the surface
            if (lDotNorm > 0) {
            	// Calculate diffuse reflection
                color = color.add(material.diffuse.pairwiseProduct(l.getColor()).scale(lDotNorm));
                // Get the unit length vector for the reflection
                Vector spR = surfaceNormal.scale(2 * lDotNorm);
                spR = spR.subtract(toLight.getDirection());
                spR.makeUnitLength();
                // Check the angle between the camera vector and the reflection vector
                double origDotR = toOrig.getDirection().dotProduct(spR);
                // Only calculate phong reflection if the angle between the two is less than 90 degrees
                if (origDotR > 0) {
	                double cdPhong = Math.pow(origDotR, material.phong);
	                color = color.add(material.specular.pairwiseProduct(l.getColor()).scale(cdPhong));
                }
            }
        }
        return color;
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