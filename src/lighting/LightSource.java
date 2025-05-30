// lighting/LightSource.java
package lighting;

import primitives.*;

/**
 * Interface for light sources in the scene.
 * Provides methods to compute direction, intensity and distance
 * from the light to any point in space.
 */
public interface LightSource {
    /**
     * Returns the color intensity of this light at point {@code p}.
     * @param p the target point
     * @return {@link Color} intensity
     */
    Color getIntensity(Point p);

    /**
     * Returns the unit vector pointing from the light towards point {@code p}.
     * @param p the target point
     * @return normalized {@link Vector} from light to point
     */
    Vector getL(Point p);

    /**
     * Returns the distance from this light to point {@code p}.
     * Useful for attenuation and shadow checks.
     * @param p the target point
     * @return distance as {@code double}, or {@code Double.POSITIVE_INFINITY}
     *         for directional lights
     */
    double getDistance(Point p);
}
