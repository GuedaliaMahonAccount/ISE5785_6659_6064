// renderer/sampling/Sampler.java
package renderer;

import primitives.Point2D;
import java.util.List;

/**
 * Given a 2D “target area” (pixel, circle, aperture, etc.) and a sample count,
 * produces a list of normalized 2D sample offsets in [–0.5, …, +0.5]² (or inscribed circle).
 */
public interface Sampler {
    /**
     * @param count total number of samples (≥50 for MP1; e.g. 9×9=81 grid, ~300–1000 for final)
     * @return list of 2D offsets to add to the canonical pixel center (or aperture center)
     */
    List<Point2D> sample(int count);
}
