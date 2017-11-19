import org.ejml.simple.SimpleMatrix;

public class Light {
    // Brightness for light
    private RGB lightcolor; // same as brightness, but in nonmatrix form
    private SimpleMatrix brightness;
    private Point position;
    private double w;

    public Light (RGB color, Point position, double w) {
        this.lightcolor = color;
        double [][] pmatr = {
            {color.red},
            {color.green},
            {color.blue}
        };
        brightness = new SimpleMatrix(pmatr);
        this.position = position;
        this.w = w;
    }

    public Point getPosition() {
        return position;
    }

    public SimpleMatrix getBrightness () {
        return brightness;
    }

    public RGB getColor () {
        return lightcolor;
    }

    public String toString () {
        return String.format("Color: (%s), Position: %s", lightcolor, position);
    }
}