import java.util.Arrays;
/**
 * A face consists of three points that represent a planar triangle in 3d space
 */

public class Face {
    Point A, B, C;
    int [] vertexIndices;
    Material material;

    public Face (Point a, Point b, Point c, int [] i) {
        A = a;
        B = b;
        C = c;
        vertexIndices = i;
    }

    public Face (Point a, Point b, Point c, int [] i, Material m) {
        this (a,b,c,i);
        this.material = m;
    }

    @Override
    public String toString() {
        return String.format("A: %s\nB: %s\nC: %s\nIndices: %s", A.toString(), B.toString(), 
                C.toString(), Arrays.toString(vertexIndices));
    }

    public Material getMaterial () {
        return material;
    }

    public int [] getIndices () {
        return vertexIndices;
    }
}