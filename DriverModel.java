
public class DriverModel {
    String object_name;    
    Vector rotation_axis;  
    Point t_point;
    double theta;
    double scale;
    public DriverModel(double wX, double wY, double wZ, double rotate, double scale, double tX, double tY, double tZ, String name) {
        this.object_name = name;
        this.rotation_axis = new Vector(wX, wY, wZ);
        this.t_point = new Point(tX, tY, tZ);
        this.theta = rotate;
        this.scale = scale;
    }
}
