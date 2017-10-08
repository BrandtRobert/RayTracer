public class Point {
    public double x, y, z;
    public Point (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector toVector () {
        return new Vector(x,y,z);
    }
    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }
}