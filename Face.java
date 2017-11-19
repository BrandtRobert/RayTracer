import java.util.Arrays;
/**
 * A face consists of three points that represent a planar triangle in 3d space
 */

public class Face implements Colorable {
    Point A, B, C;
    int [] vertexIndices;
    Material material;
    Vector normal;

    public Face (Point a, Point b, Point c, int [] i) {
        A = a;
        B = b;
        C = c;
        vertexIndices = i;
        initNormal();
    }

    public Face (Point a, Point b, Point c, int [] i, Material m) {
        this (a,b,c,i);
        this.material = m;
    }

    /**
     * Calculate the surface normal using the cross product of the edges
     */
    private void initNormal () {
        Vector AC = new Vector (this.A, this.C);
        Vector AB = new Vector (this.A, this.B);
        Vector surfaceNormal = AC.crossProduct(AB);
        surfaceNormal.direction = surfaceNormal.normalized;
        this.normal = surfaceNormal;
    }

    /**
     * Returns the surface normal for this face
     */
    public Vector getNormal () {
        return normal;
    }

    public Material getMaterial () {
        return material;
    }

    public int [] getIndices () {
        return vertexIndices;
    }

    @Override
    public String toString() {
        return String.format("A: %s\nB: %s\nC: %s\nIndices: %s", A.toString(), B.toString(), 
                C.toString(), Arrays.toString(vertexIndices));
    }
}