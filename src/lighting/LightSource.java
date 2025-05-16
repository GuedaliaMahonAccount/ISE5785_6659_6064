package lighting;

import primitives.*;

/**
 * Interface for all light sources in the scene.
 * Provides methods to calculate light direction, intensity, and distance.
 */
public interface LightSource {
    /**
     * Calculates the light intensity at a given point in the scene.
     *
     * @param p The point in the scene.
     * @return The color intensity of the light at the given point.
     */
    Color getIntensity(Point p);

    /**
     * Returns the direction vector from the light source to a given point.
     *
     * @param p The point in the scene.
     * @return The direction vector to the given point.
     */
    Vector getL(Point p);

    /**
     * Returns the distance from the light source to a given point.
     *
     * @param p The point in the scene.
     * @return The distance to the given point.
     */
    double getDistance(Point p);
}