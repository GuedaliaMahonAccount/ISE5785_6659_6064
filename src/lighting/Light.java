package lighting;

import primitives.Color;

/**
 * Abstract base class representing a light source in a 3D scene.
 * Holds the common property of light intensity.
 */
public abstract class Light {
    /**
     * The color intensity of the light source.
     */
    protected final Color intensity;

    /**
     * Constructs a Light with the specified intensity.
     *
     * @param intensity The color intensity of the light.
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of this light source.
     *
     * @return The color intensity.
     */
    public Color getIntensity() {
        return intensity;
    }
}
