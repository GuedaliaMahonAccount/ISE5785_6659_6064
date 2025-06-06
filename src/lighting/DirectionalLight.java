// lighting/DirectionalLight.java
package lighting;

import primitives.*;

/**
 * Represents a directional light in the scene.
 */
public class DirectionalLight extends Light implements LightSource {
    /**
     * The direction of the light, assumed to be constant.
     */
    private final Vector direction;

    /**
     * Constructor for DirectionalLight.
     *
     * @param intensity The intensity of the light.
     * @param direction The direction of the light.
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    @Override
    public Vector getL(Point p) {
        return direction;
    }

    @Override
    public double getDistance(Point p) {
        return Double.POSITIVE_INFINITY;
    }
}
