package lighting;

import primitives.*;
import static primitives.Util.alignZero;

/**
 * Represents a spot light source in the scene with directional focus.
 */
public class SpotLight extends PointLight {
    private final Vector direction;
    private double narrowBeam = 1.0;

    /**
     * Constructor for SpotLight.
     *
     * @param intensity The intensity of the light.
     * @param position The position of the light source.
     * @param direction The direction of the light beam.
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets the narrow beam factor.
     *
     * @param narrowBeam The narrow beam factor.
     * @return The SpotLight object (for chaining).
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = Math.max(1.0, narrowBeam);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double cosTheta = alignZero(direction.dotProduct(getL(p)));
        return cosTheta <= 0 ? Color.BLACK : super.getIntensity(p).scale(Math.pow(cosTheta, narrowBeam));
    }
}
