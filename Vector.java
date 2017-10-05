import java.util.Arrays;

public class Vector {
  // The vectors direction
  public double [] direction;
  // magnitude of the non-unit length version of this vector
  public double magnitude;
  // The vector as unit length
  public double [] normalized;

  public Vector (double x, double y, double z) {
    double [] temp = {x, y, z};
    this.direction = temp;
    double tempSum = 0;
    for (int i = 0; i < direction.length; i++) {
      tempSum += Math.pow(direction[i], 2);
    }
    this.magnitude = Math.sqrt(tempSum);
    double [] temp2 = {x/magnitude, y/magnitude, z/magnitude};
    this.normalized = temp2;
  }

  /**
   * The cross product of two vectors
   * a = <x, y, z>
   * b = <x, y, z>
   * cx = aybz − azby
   * cy = azbx − axbz
   * cz = axby − aybx
   */
  public Vector crossProduct(Vector other) {
    double cx = this.direction[1] * other.direction[2] - this.direction[2] * other.direction[1];
    double cy = this.direction[2] * other.direction[0] - this.direction[0] * other.direction[2];
    double cz = this.direction[0] * other.direction[1] - this.direction[1] * other.direction[0];
    Vector v = new Vector(cx, cy, cz);
    return v;
  }

  public String toString() {
    String str = "Direction: " + Arrays.toString(direction) + "\n";
    str += "Unit Length: " + Arrays.toString(normalized) + "\n";
    str += "Magnitude: " + magnitude;
    return str;
  }
}