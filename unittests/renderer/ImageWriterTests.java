package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

public class ImageWriterTests {

    @Test void testYellowImageWriter() {
        int nx = 800;
        int ny = 500;
        int interval = 50;
        Color YELLOW = new Color(java.awt.Color.YELLOW);
        Color RED = new Color(java.awt.Color.RED);
        ImageWriter imageWriter = new ImageWriter( nx, ny);
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                imageWriter.writePixel(i, j,YELLOW);
            }
        }
        for (int i = 0; i < ny; i++) {
            for (int j = 0; j < nx; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, RED);
                }
            }
        }

        imageWriter.writeToImage("yellowsubmarine");
    }
}
