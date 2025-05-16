package lighting;

import primitives.*;

/**
 * Represents a point light source in the scene with attenuation based on distance.
 */
public class PointLight extends Light implements LightSource {
    protected final Point position;
    protected double kC = 1.0;
    protected double kL = 0.0;
    protected double kQ = 0.0;
    // Add a default narrowBeam field (can be protected so SpotLight can access it)
    protected double narrowBeam = 1.0;

    /**
     * Constructor for PointLight.
     *
     * @param intensity The intensity of the light.
     * @param position The position of the light source.
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Sets the constant attenuation factor.
     *
     * @param kC The constant attenuation factor.
     * @return The PointLight object (for chaining).
     */
    public PointLight setKc(double kC) {
        this.kC = Math.max(0, kC);
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kL The linear attenuation factor.
     * @return The PointLight object (for chaining).
     */
    public PointLight setKl(double kL) {
        this.kL = Math.max(0, kL);
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kQ The quadratic attenuation factor.
     * @return The PointLight object (for chaining).
     */
    public PointLight setKq(double kQ) {
        this.kQ = Math.max(0, kQ);
        return this;
    }

    /**
     * Sets the narrow beam factor.
     * This method is implemented in PointLight for compatibility but has no effect
     * unless overridden in a subclass like SpotLight.
     *
     * @param narrowBeam The narrow beam factor.
     * @return The PointLight object (for chaining).
     */
    public PointLight setNarrowBeam(double narrowBeam) {
        // This method exists for compatibility but does nothing in PointLight
        return this;
    }

    /**
     * Returns the position of the point light.
     *
     * @return The position of the light source.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Returns the constant attenuation factor (kC).
     *
     * @return The constant attenuation factor.
     */
    public double getKc() {
        return kC;
    }

    /**
     * Returns the linear attenuation factor (kL).
     *
     * @return The linear attenuation factor.
     */
    public double getKl() {
        return kL;
    }

    /**
     * Returns the quadratic attenuation factor (kQ).
     *
     * @return The quadratic attenuation factor.
     */
    public double getKq() {
        return kQ;
    }

    @Override
    public Color getIntensity(Point p) {
        double distance = position.distance(p);
        double attenuation = 1.0 / (kC + kL * distance + kQ * distance * distance);
        return intensity.scale(attenuation);
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    @Override
    public double getDistance(Point p) {
        return position.distance(p);
    }
}