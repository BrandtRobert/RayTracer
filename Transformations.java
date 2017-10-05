import org.ejml.simple.SimpleMatrix;

public class Transformations {
  /**
   * Rotates the object about the rotation axis by theta degrees
   * Make z the axis of rotation, rotate about z, restore the original axis
   *  1. Normalize the axis of rotation
   *    - write the axis of rotation as w = (wX, wY, wZ)
   *  2. Pick any axis M not parallel to W
   *    - pick the smallest term in w, set it to 1 and renormalize to create m
   *  3. Create U = M x W
   *  4. Pick an axis v perpendicular to to w & u
   *    - V = W x U
   *  5. Create the rotation matrix
   *    - | uX uY uZ 0 |
   *    - | vX vY vZ 0 |
   *    - | wX wY wZ 0 |
   *    - |  0  0  0 1 |
   */
  private static SimpleMatrix rotate3D(Vector Ww, double theta) {
    // Get vector M from the normalized vector rotation_axis
    Vector Mv = getMFromVector(Ww);
    Vector Uv = Mv.crossProduct(Ww);
    Vector Vv = Ww.crossProduct(Uv);
    SimpleMatrix Rm = Transformations.vectorsToMatrix(Uv, Vv, Ww);
    SimpleMatrix Zm = constructZrotation(theta);
    return Rm.transpose().mult(Zm).mult(Rm);
  }
  
  /**
   * zMatrix:
   * | cos(theta) -sin(theta) 0 0 |
   * | sin(theta) cos(theta)  0 0 |
   * |     0          0       1 0 |
   * |     0          0       0 1 |
   */
  private static SimpleMatrix constructZrotation(double theta) {
    double radians = Math.toRadians(theta);
    SimpleMatrix zMatrix = new SimpleMatrix(4,4);
    zMatrix.set(0,0, Math.cos(radians));
    zMatrix.set(0,1, Math.sin(radians) * -1);
    zMatrix.set(1,0, Math.sin(radians));
    zMatrix.set(1,1, Math.cos(radians));
    zMatrix.set(2,2,1);
    zMatrix.set(3,3,1);
    return zMatrix;
  }

  private static SimpleMatrix vectorsToMatrix(Vector u, Vector v, Vector w) {
    SimpleMatrix rotation = new SimpleMatrix(4, 4);
    for (int i = 0; i < 3; i++) {
      rotation.set(0, i, u.normalized[i]);
      rotation.set(1, i, v.normalized[i]);
      rotation.set(2, i, w.normalized[i]);
    }
    rotation.set(3,3,1);
    return rotation;
  }
  private static Vector getMFromVector(Vector rotation_axis) {
    double [] mArray = new double [3];
    // Copy the rotation_axis to avoid side effects
    System.arraycopy(rotation_axis.normalized, 0, mArray, 0, rotation_axis.normalized.length);
    double min = Math.abs(rotation_axis.normalized[0]);
    int min_index = 0;
    // Get the minimum value
    for (int i = 0; i < rotation_axis.normalized.length; i++) {
      if (Math.abs(rotation_axis.normalized[i]) < min) {
        min = rotation_axis.normalized[i];
        min_index = i;
      }
    }
    mArray[min_index] = 1;
    return new Vector(mArray[0], mArray[1], mArray[2]);
  }
  private static SimpleMatrix translate3D(Point t) {
    SimpleMatrix translationMatrix = SimpleMatrix.identity(4);
    translationMatrix.set(0, 3, t.x);
    translationMatrix.set(1, 3, t.y);
    translationMatrix.set(2, 3, t.z);
    return translationMatrix;
  }

  private static SimpleMatrix scale3D(double scalar) {
    SimpleMatrix scalingMatrix = SimpleMatrix.identity(4);
    scalingMatrix.set(0, 0, scalar);
    scalingMatrix.set(1, 1, scalar);
    scalingMatrix.set(2, 2, scalar);
    return scalingMatrix;
  }

  public static SimpleMatrix performTranslations
  (SimpleMatrix obj_matrix, Vector rotation_axis, double theta, double scalar, Point translate) 
  {
    SimpleMatrix Tm = translate3D(translate);
    SimpleMatrix Sm = scale3D(scalar);
    SimpleMatrix Rm = rotate3D(rotation_axis, theta);
    // Compose the transformations into a single matrix
    // Works on the composition property
    // A(BC) = (AB)C
    SimpleMatrix Mm = Tm.mult(Sm).mult(Rm);
    // In this case we want to rotate, scale, and then translate so we apply it like this
    // Sm * Tm * Rm * Vertices = (Sm*Tm*Rm) * Vertices
    return Mm.mult(obj_matrix);
  }
}