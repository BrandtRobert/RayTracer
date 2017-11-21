import java.util.List;

public class Scene {
    private Camera camera;
    // Objects in the scene
    private List<Face> faces;
    private List<Sphere> spheres;
    // Lights in the scene
    private List<Light> lights;
    private Light ambient;

    public Scene (Camera c, List<Face> f, List<Sphere> s, List<Light> l, Light a) {
        camera = c;
        faces = f;
        spheres = s;
        lights = l;
        ambient = a;
    }

    /**
     * Renders a distance heat map for an object with width x height resolution
     */
    public Image generateImage (int width, int height, int depth) {
        Image img = new Image (width, height);
        RGB [][] pixelMap = new RGB [width][height];
        // For each pixel cast a ray, and see what it hits
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Ray r = castRay (i, j , width, height);
                pixelMap[i][j] = colorPixel(r, depth);
            }
        }
        return img.mapPixels(pixelMap);
    }

    /**
     * Returns an ray cast from the eye point onto a pixel i, j in the image plane
     */
    private Ray castRay (double i, double j, double width, double height) {
        // Get the pixel value i,j on the image plane in world coordinates
        double x = (i / (width - 1)) * (camera.right - camera.left) + camera.left;        
        double y = (j / (height - 1)) * (camera.top - camera.bottom) + camera.bottom;
        // Pixelpt = Eye + near * Wv (viewplaneNormal) + x * Uv + y * Vv
        Vector W = camera.viewPlaneNormal.scale(camera.near); // near * Wv
        Vector Uscaled = camera.Uv.scale(x);           // x * Uv
        Vector Vscaled = camera.Vv.scale(y);           // y * Vv
        Vector eyeVect = camera.eyePoint.toVector();
        // A ray is a point and direction
        Point pixelPoint = eyeVect.add(W).add(Uscaled).add(Vscaled).toPoint();
        Vector direction = new Vector (camera.eyePoint, pixelPoint);
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
    private RGB colorPixel (Ray ray, int depth) {
        RGB color = new RGB (0,0,0);
        // Check for collision on the ray
        Vector surfaceNormal = ray.rayTest(spheres, faces);
        // If we got a collision calculate the color
        if (surfaceNormal == null) {
            return color;
        }
        Material material = ((Colorable)ray.getClosestObj()).getMaterial();
        // Ambient
        // ambient * mat_ambient reflection
        if (ambient != null) {
            color = ambient.getColor().pairwiseProduct(material.ambient);
        }
        // To camera ray is the going back on the horse we rode in on
        Ray toOrig = ray.getReverse();
        if (toOrig.getDirection().dotProduct(surfaceNormal) < 0) {
            // If the dot product between the camera vector and the surface normal is negative
            // Then the surface normal is backwards
        	surfaceNormal = surfaceNormal.getReverse();
        }
      
        for (Light l : lights) {
            Vector toLightVect = new Vector (ray.getClosestPoint(), l.getPosition());
            Ray toLight = new Ray (ray.getClosestPoint(), toLightVect);
            /**
             * Shadows? Fire a ray from the light to the pt of intersection. (i.e. closestPt of the ray)
             * If there is some object that is in between then we know that the light is obstructed
             */  
            Ray lToSurface = new Ray (l.getPosition(), new Vector (l.getPosition(), ray.getClosestPoint()));
            lToSurface.rayTest(spheres, faces);
            if (!lToSurface.getClosestPoint().equals(ray.getClosestPoint())) {
            	continue;
            }
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
        // Ray trace
        RGB raytrace = null;
        if (depth > 0) {
            // (2 * np.dot(N, toC) * N) - toC            
            Vector vectorBounce = (surfaceNormal.scale(2 * surfaceNormal.dotProduct(toOrig.getDirection()))).subtract(toOrig.getDirection());
            vectorBounce.makeUnitLength();
            Ray rayBounce = new Ray (ray.getClosestPoint(), vectorBounce);
            raytrace = colorPixel(rayBounce, depth - 1);
            color = color.add(raytrace);
        }
        return color;
    }
}