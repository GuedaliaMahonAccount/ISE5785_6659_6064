package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Test suite for ImageWriter functionality.
 * Renders a yellow background with a red grid overlay and exports the image.
 */
public class ImageWriterTests {

    /**
     * Generates an 800x500 yellow image with a red grid every 50 pixels,
     * then writes the result to 'yellowsubmarine.png'.
     */
    @Test
    void testYellowImageWriter() {
        // Image dimensions
        int nx = 800;
        int ny = 500;
        // Grid line interval in pixels
        int interval = 50;
        // Define colors for background and grid lines
        Color YELLOW = new Color(java.awt.Color.YELLOW);
        Color RED    = new Color(java.awt.Color.RED);

        // Initialize the ImageWriter with resolution nx by ny
        ImageWriter imageWriter = new ImageWriter(nx, ny);

        // Fill the entire image with yellow
        for (int x = 0; x < nx; x++) {
            for (int y = 0; y < ny; y++) {
                imageWriter.writePixel(x, y, YELLOW);
            }
        }

        // Draw red grid lines at the specified interval
        for (int y = 0; y < ny; y++) {
            for (int x = 0; x < nx; x++) {
                if (y % interval == 0 || x % interval == 0) {
                    imageWriter.writePixel(x, y, RED);
                }
            }
        }

        // Export the image to file (appends .png by default)
        imageWriter.writeToImage("yellowsubmarine");
    }
}
