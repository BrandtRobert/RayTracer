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
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
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
		Point other = (Point) obj;
		if (x - other.x > 0.001) {
			return false;
		}
		if (y - other.y > 0.001) {
			return false;
		}
		if (z - other.z > 0.001) {
			return false;
		}
		return true;
	}
}