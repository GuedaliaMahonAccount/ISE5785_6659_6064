// SuperSamplingBlackboard.java
package renderer.sampling;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates 2D sample-offsets (relative to pixel center)
 * according to the SamplingConfig.
 */
public class SuperSamplingBlackboard {
    private final SamplingConfig config;

    public SuperSamplingBlackboard(SamplingConfig config) {
        this.config = config;
    }

    /**
     * @param pixelWidth  width of a pixel on the view-plane
     * @param pixelHeight height of a pixel on the view-plane
     * @return list of (dx,dy) offsets from the pixel center
     */
    public List<Point2D.Double> getSampleOffsets(double pixelWidth, double pixelHeight) {
        int nSamples = config.getSampleCount();
        List<Point2D.Double> samples = new ArrayList<>(nSamples);

        switch (config.getSamplingPattern()) {
            case GRID      -> generateGrid(samples, pixelWidth, pixelHeight);
            case JITTERED  -> generateJittered(samples, pixelWidth, pixelHeight);
            case RANDOM    -> generateRandom(samples, pixelWidth, pixelHeight);
            default        -> generateGrid(samples, pixelWidth, pixelHeight);
        }

        if (config.getTargetShape() == TargetShape.CIRCLE) {
            samples = cropToCircle(samples, pixelWidth, pixelHeight);
        }

        return samples;
    }

    private void generateGrid(List<Point2D.Double> out, double pw, double ph) {
        int n = (int)Math.sqrt(config.getSampleCount());
        if (n < 1) n = 1;
        double dx = pw / n, dy = ph / n;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double ox = (j + 0.5) * dx - pw/2.0;
                double oy = (i + 0.5) * dy - ph/2.0;
                out.add(new Point2D.Double(ox, oy));
            }
        }
    }

    private void generateJittered(List<Point2D.Double> out, double pw, double ph) {
        int n = (int)Math.sqrt(config.getSampleCount());
        if (n < 1) n = 1;
        double cellW = pw / n, cellH = ph / n;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double x0 = j * cellW - pw/2.0;
                double y0 = i * cellH - ph/2.0;
                double ox = x0 + rnd.nextDouble(cellW);
                double oy = y0 + rnd.nextDouble(cellH);
                out.add(new Point2D.Double(ox, oy));
            }
        }
    }

    private void generateRandom(List<Point2D.Double> out, double pw, double ph) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < config.getSampleCount(); i++) {
            double ox = rnd.nextDouble(-pw/2.0, pw/2.0);
            double oy = rnd.nextDouble(-ph/2.0, ph/2.0);
            out.add(new Point2D.Double(ox, oy));
        }
    }

    private List<Point2D.Double> cropToCircle(List<Point2D.Double> in, double pw, double ph) {
        List<Point2D.Double> circle = new ArrayList<>(in.size());
        double rx = pw/2.0, ry = ph/2.0;
        for (Point2D.Double p : in) {
            double nx = p.x/rx, ny = p.y/ry;
            if (nx*nx + ny*ny <= 1.0) {
                circle.add(p);
            }
        }
        return circle;
    }
}
