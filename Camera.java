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
    public Point eyePoint, lookatPoint;
    public Vector upVector, viewPlaneNormal;
    // Image plan bounds (left, right, bottom, top)
    public double right;
    public double left;
    public double bottom;
    public double top;
    // How far the bounded image plane is from the camera
    public double near;
    // The horizontal and vertical vectors
    public Vector Uv, Vv;
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