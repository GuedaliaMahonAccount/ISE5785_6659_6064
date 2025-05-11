package lighting;

import primitives.Color;

/**
 * Class representing ambient light in a 3D scene.
 */
public class AmbientLight {
    // The intensity of the ambient light
    private final Color intensity;

    // A constant representing no ambient light (black)
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor that sets the ambient light intensity.
     * @param intensity The color intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of the ambient light.
     * @return The color intensity.
     */
    public Color getIntensity() {
        return intensity;
    }
}
