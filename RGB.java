
public class RGB {
    public double red;
    public double green;
    public double blue;

    public RGB (double R, double G, double B) {
        this.red = R;
        this.green = G;
        this.blue = B;
    }

    /**
     * Returns the pairwise products of the two color vectors
     */
    public RGB pairwiseProduct (RGB other) {
        double r,g,b;
        r = this.red * other.red;
        b = this.blue * other.blue;
        g = this.green * other.green;
        return new RGB (r,b,g);
    }

    /**
     * Returns the pairwise products of the two color vectors
     */
    public RGB scale (double s) {
        double r,g,b;
        r = this.red * s;
        b = this.blue * s;
        g = this.green * s;
        return new RGB (r,b,g);
    }

    public RGB add (RGB other) {
        double r,g,b;
        r = this.red + other.red;
        b = this.blue + other.blue;
        g = this.green + other.green;
        return new RGB (r,b,g);
    }

    public String toString () {
        return String.format ("Red %.2f, Green %.2f, Blue %.2f", red, green, blue);
    }
}