package renderer;

import primitives.Point;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates and stores sample points for anti-aliasing
 */
public class Blackboard {
    private final List<Point> samplePoints;
    private final int numSamples;
    private final double pixelWidth;
    private final double pixelHeight;

    /**
     * Constructor for Blackboard
     * @param numSamples Number of samples per pixel (must be a perfect square if using grid)
     * @param pixelWidth Width of a pixel in world coordinates
     * @param pixelHeight Height of a pixel in world coordinates
     */
    public Blackboard(int numSamples, double pixelWidth, double pixelHeight) {
        this.numSamples = numSamples;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.samplePoints = generateGridPoints(); // or generateJitteredPoints()
    }

    private List<Point> generateGridPoints() {
        // Generate evenly spaced grid points within the pixel area
        int gridSize = (int) Math.sqrt(numSamples);
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double x = (i + 0.5) * pixelWidth / gridSize - pixelWidth/2;
                double y = (j + 0.5) * pixelHeight / gridSize - pixelHeight/2;
                points.add(new Point(x, y, 0));
            }
        }
        return points;
    }

    private List<Point> generateJitteredPoints() {
        // Generate jittered points for better distribution (bonus)
        int gridSize = (int) Math.sqrt(numSamples);
        List<Point> points = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double xOffset = (i + random.nextDouble()) * pixelWidth / gridSize - pixelWidth/2;
                double yOffset = (j + random.nextDouble()) * pixelHeight / gridSize - pixelHeight/2;
                points.add(new Point(xOffset, yOffset, 0));
            }
        }
        return points;
    }

    public List<Point> getSamplePoints() {
        return samplePoints;
    }

    public int getNumSamples() {
        return numSamples;
    }
}