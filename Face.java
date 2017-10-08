/**
 * A face consists of three points that represent a planar triangle in 3d space
 */

public class Face {
    Point A, B, C;
    int [] vertexIndices;
    public Face (Point a, Point b, Point c, int [] i) {
        A = a;
        B = b;
        C = c;
        vertexIndices = i;
    }

    @Override
    public String toString() {
        return String.format("A: %s\nB: %s\nC: %s", A.toString(), B.toString(), C.toString());
    }
}