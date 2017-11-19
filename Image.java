import java.io.PrintWriter;
import java.util.Arrays;
import java.io.File;
import org.ejml.simple.SimpleMatrix;

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
    }

    /**
     * Rotate the image into proper place and fill out the pixel values
     */
    public Image mapPixels(RGB [][] pValues) {
        // Yeah you have to reset these i don't even care anymore
        this.width = pValues[0].length;
        this.height = pValues.length;
        pixels = new Pixel[height][width];
        for (int i = 0; i < pValues.length; i++) {
            for (int j = 0; j < pValues[0].length; j++) {
                this.setPixel(i, j, pValues[i][j]);
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
            // for (int i = this.height - 1; i >= 0; i--) {
            for (int i = 0; i < this.height; i++) {
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
     * Sets a pixel at the specified index
     */
    public void setPixel(int i, int j, RGB pixel) {
        int r = (int) Math.round(Math.min(255, Math.max(0, pixel.red * 255)));
        int g = (int) Math.round(Math.min(255, Math.max(0, pixel.green * 255)));
        int b = (int) Math.round(Math.min(255, Math.max(0, pixel.blue * 255)));
        pixels[i][j] = new Pixel(r, g, b);
    }

    /**
     * Retrieves a pixel from the array
     */
    public int [] getPixel (int i, int j) {
        return pixels[i][j].pixelAsArray();
    }
}