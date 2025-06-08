package lighting;

import primitives.Color;
import java.util.Objects;

/**
 * Class representing ambient light in a 3D scene.
 */
public class AmbientLight extends Light {
    /**
     * The intensity of the ambient light (IA).
     */
    private final Color intensity;

    /**
     * A constant representing no ambient light (black).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an AmbientLight with the specified intensity.
     *
     * @param intensity The color intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        super(intensity); // Pass the intensity to the parent Light class
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of the ambient light.
     *
     * @return The color intensity.
     */
    @Override
    public Color getIntensity() {
        return intensity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AmbientLight that = (AmbientLight) obj;
        return Objects.equals(intensity, that.intensity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intensity);
    }

    @Override
    public String toString() {
        return "AmbientLight{" +
                "intensity=" + intensity +
                '}';
    }
}
