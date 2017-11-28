
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
        g = this.green * other.green;        
        b = this.blue * other.blue;
        return new RGB (r,g,b);
    }

    /**
     * Returns the pairwise products of the two color vectors
     */
    public RGB scale (double s) {
        double r,g,b;
        r = this.red * s;
        g = this.green * s;
        b = this.blue * s;
        return new RGB (r,g,b);
    }

    public RGB add (RGB other) {
        double r,g,b;
        r = this.red + other.red;
        g = this.green + other.green;
        b = this.blue + other.blue;
        return new RGB (r,g,b);
    }

    public String toString () {
        return String.format ("Red %.2f, Green %.2f, Blue %.2f", red, green, blue);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(blue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(green);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(red);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RGB other = (RGB) obj;
		if (blue - other.blue > 0.001) {
			return false;
		}
		if (green - other.green > 0.001) {
			return false;
		}
		if (red - other.red  > 0.001) {
			return false;
		}
		return true;
	}
}