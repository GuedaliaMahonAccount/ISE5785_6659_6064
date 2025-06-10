// renderer/sampling/GridSampler.java
package renderer;

import primitives.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple uniform grid over a square (or filter out the inscribed circle if needed).
 */
public class GridSampler implements Sampler {
    private final boolean circleInscribed;

    public GridSampler(boolean circleInscribed) {
        this.circleInscribed = circleInscribed;
    }

    @Override
    public List<Point2D> sample(int count) {
        int n = (int)Math.round(Math.sqrt(count)); // assume perfect square
        double step = 1.0 / n;
        List<Point2D> pts = new ArrayList<>(n*n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double x = (j + 0.5)*step - 0.5;
                double y = (i + 0.5)*step - 0.5;
                if (!circleInscribed || x*x + y*y <= 0.25) {
                    pts.add(new Point2D(x, y));
                }
            }
        }
        return pts;
    }
}
