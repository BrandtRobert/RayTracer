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
    normalize();
  }

  /**
   * Gets the magnitude and creates the normalized vector
   */
  public void normalize() {
    double tempSum = 0;
    // Get the magnitude and unit length vector
    for (int i = 0; i < direction.length; i++) {
      tempSum += Math.pow(direction[i], 2);
    }
    this.magnitude = Math.sqrt(tempSum);
    // If the magnitude is 0, i.e the vector is all zeros. Set the normalized vector to zeros
    if (this.magnitude == 0) {
      double [] zeroVect = {0, 0, 0};
      this.normalized = zeroVect;
    } else {
      double x = direction[0];
      double y = direction[1];
      double z = direction[2];
      double [] temp2 = {x/magnitude, y/magnitude, z/magnitude};
      this.normalized = temp2;
    }
  }

  /**
   * Creates a vector pointing from the origin to the given point
   */
  public Vector (Point head) {
    this (head.x, head.y, head.z);
  }

  /**
   * Creates a vector using the head - tail rule
   * From point tail to point head
   */
  public Vector (Point tail, Point head) {
    this (head.x - tail.x, head.y - tail.y, head.z - tail.z);
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

  /**
   * Returns the dot product of the two vectors
   */
  public double dotProduct(Vector other) {
    // Dot product for
    // <x1, y1, z1> <x2, y2, z2> is
    // x1 * x2 + y1 * y2 + z1 * z2
    return this.direction[0] * other.direction[0] + 
           this.direction[1] * other.direction[1] + 
           this.direction[2] * other.direction[2];
  }

  /**
   * Returns the result of adding the two vectors
   */
  public Vector add(Vector other) {
    double x = this.direction[0] + other.direction[0];
    double y = this.direction[1] + other.direction[1];
    double z = this.direction[2] + other.direction[2];
    return new Vector (x,y,z);
  }

  /**
   * Returns the result of subtracting the two vectors
   */
  public Vector subtract(Vector other) {
    double x = this.direction[0] - other.direction[0];
    double y = this.direction[1] - other.direction[1];
    double z = this.direction[2] - other.direction[2];
    return new Vector (x,y,z);
  }

  /**
   * Returns a result of a scaled vector
   */
  public Vector scale(double s) {
    double x = this.normalized[0] * s;
    double y = this.normalized[1] * s;
    double z = this.normalized[2] * s;
    return new Vector(x,y,z);
  }

  /**
   * Returns a point representation of the head of this vector
   */
  public Point toPoint() {
    double x = this.direction[0];
    double y = this.direction[1];
    double z = this.direction[2];
    return new Point(x,y,z);
  }

  public String toString() {
    String str = "Direction: " + Arrays.toString(direction) + "\n";
    str += "Unit Length: " + Arrays.toString(normalized) + "\n";
    str += "Magnitude: " + magnitude;
    return str;
  }

  public double getMagnitude () {
    return magnitude;
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (this.getClass() != other.getClass()) {
      return false;
    }
    Vector otherVect = (Vector) other;
    // if they have the same magnitude and direction the unit length vectors will be the same
    return Arrays.equals(this.direction, otherVect.direction) 
           && (this.magnitude == otherVect.magnitude);
  }

  public Vector getReverse () {
    double x,y,z;
    x = -1 * normalized[0];
    y = -1 * normalized[1];
    z = -1 * normalized[2];
    return new Vector (x,y,z);
  }
}