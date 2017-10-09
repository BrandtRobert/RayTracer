import java.io.PrintWriter;
import java.util.Arrays;
import java.io.File;
import org.ejml.simple.SimpleMatrix;

import javafx.beans.property.SimpleMapProperty;

public class Image {
    /**
     * Just an abstraction for an rgb triple
     */
    class Pixel {
        int r, g, b;
        Pixel (int r, int g, int b) {
            this.r = r;
            this.b = b;
            this.g = g;
        }

        public int [] pixelAsArray () {
            int [] a = {this.r, this.g, this.b};
            return a;
        }

        public String toString () {
            return r + " " + g + " " + b;
        }
    }
    // An image is a 2d array of pixels
    private Pixel [][] pixels;
    public int width, height;
    // Init an image with a given resolution
    public Image (int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Pixel[height][width];
    }

    /**
     * Uses the tValues to turn the image into a heat map
     * https://stackoverflow.com/questions/20792445/calculate-rgb-value-for-a-range-of-values-to-create-heat-map
     * ratio = 2 * (t - tmin) / (tmax - tmin) 
     * r = max(0, 255 * (1 - ratio)) 
     * b = max(0, 255 * (ratio - 1)) 
     * g = 255 - b - r 
     */
    public Image createHeatMap(double [][] tValues, double max, double min) {
        for (int i = 0; i < tValues.length; i++) {
            for (int j = 0; j < tValues[0].length; j++) {
                double t = tValues[i][j];
                int r = 255;
                int g = 255;
                int b = 255;
                if (t >= 0) {
                    double ratio = 2 * (t - min) / (max - min);
                    r = (int) Math.round(Math.max(0, 255*(1 - ratio)));
                    b = (int) Math.round(Math.max(0, 255*(ratio - 1)));
                    g = 255 - b - r;
                }
                int [] pixel = {r, g, b};
                this.setPixel(i, j, pixel);
            }
        }
        return this;
    }

    /**
     * Writes this image to an output file of a given name.
     * Format is ACSII PPM:
     * https://en.wikipedia.org/wiki/Netpbm_format#PPM_example
     */
    public void writeToFile (String filename) {
        try {        
            PrintWriter outFile = new PrintWriter (new File(filename));
            /**
             * The header looks like this:
             * P3
             * <width> <height> <max pixel value>
             */
            outFile.println("P3");
            outFile.printf("%d %d %d\n", this.width, this.height, 255);
            // Lazy way of avoiding the fact that everything is rotated right is to rotate it left
            // Output each pixel
            for (int i = this.height - 1; i >= 0; i--) {
                for (int j = 0; j < this.width; j++) {
                    outFile.print(this.pixels[i][j].toString() + " ");
                }
                // Print a new line after width number of pixels
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {
            System.err.println("Failed to output image file");
            System.err.println(e);
            return;
        }
    }

    /**
     * Sets a pixel at the specified index
     */
    public void setPixel(int i, int j, int[] pixel) {
        pixels[i][j] = new Pixel(pixel[0], pixel[1], pixel[2]);
    }
    /**
     * Retrieves a pixel from the array
     */
    public int [] getPixel (int i, int j) {
        return pixels[i][j].pixelAsArray();
    }
}