package renderer;

import static java.awt.Color.YELLOW;
import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;
import primitives.Color;
import renderer.ImageWriter;

/**
 * Test for ImageWriter class
 */
public class ImageWriterTest {
    @Test
    public void testWriteGrid() {
        // Create an ImageWriter instance with a resolution of 800x500
        ImageWriter imageWriter = new ImageWriter(800, 500);

        // Set the background and grid colors
        Color backgroundColor = new Color(BLUE);
        Color gridColor = new Color(YELLOW);

        // Fill the entire image with the background color
        for (int y = 0; y < 500; y++) {
            for (int x = 0; x < 800; x++) {
                imageWriter.writePixel(x, y, backgroundColor);
            }
        }

        // Add a 16x10 grid
        int gridSizeX = 50;
        int gridSizeY = 50;
        for (int y = 0; y < 500; y++) {
            for (int x = 0; x < 800; x++) {
                if (x % gridSizeX == 0 || y % gridSizeY == 0) {
                    imageWriter.writePixel(x, y, gridColor);
                }
            }
        }

        // Save the image to a file
        imageWriter.writeToImage("grid_test");
    }
}
