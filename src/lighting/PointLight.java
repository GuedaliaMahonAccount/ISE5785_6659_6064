// lighting/PointLight.java
package lighting;

import primitives.*;
import java.util.Random;

/**
 * Represents a point light source in the scene with distance-based attenuation.
 * Supports circular-area soft shadows by super-sampling across a disk of given radius.
 * Provides a no-op setNarrowBeam(int) method for compatibility with tests.
 */
public class PointLight extends Light implements LightSource {
    /** Position of the point light in 3D space. */
    protected final Point position;

    // Attenuation coefficients (constant, linear, quadratic)
    /** Constant attenuation factor (constant term). */
    protected double kC = 1.0;  // constant term
    /** Linear attenuation factor (linear term). */
    protected double kL = 0.0;  // linear term
    /** Quadratic attenuation factor (quadratic term). */
    protected double kQ = 0.0;  // quadratic term

    // Soft shadow sampling parameters
    /** Radius of the circular area light (for area soft shadows). */
    private double radius = 0.0;
    /** Number of shadow-ray samples per shading point. */
    private int numSamples = 1;
    /** Random generator for jittering sample points on the disk. */
    private final Random random = new Random();

    /**
     * Constructs a PointLight with given intensity and position.
     *
     * @param intensity the base light color/intensity
     * @param position the 3D position of this light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    // ------------------ Attenuation setters ------------------

    /**
     * Sets the constant attenuation factor (>= 0).
     *
     * @param kC the constant term for attenuation
     * @return this PointLight for chaining
     */
    public PointLight setKc(double kC) {
        this.kC = Math.max(0, kC);
        return this;
    }

    /**
     * Sets the linear attenuation factor (>= 0).
     *
     * @param kL the linear term for attenuation
     * @return this PointLight for chaining
     */
    public PointLight setKl(double kL) {
        this.kL = Math.max(0, kL);
        return this;
    }

    /**
     * Sets the quadratic attenuation factor (>= 0).
     *
     * @param kQ the quadratic term for attenuation
     * @return this PointLight for chaining
     */
    public PointLight setKq(double kQ) {
        this.kQ = Math.max(0, kQ);
        return this;
    }

    // ------------- Area light sampling setters -------------

    /**
     * Sets the circular area radius for soft shadows.
     * Radius must be non-negative. A radius of zero yields a point-light.
     *
     * @param radius radius of the light disk
     * @return this PointLight for chaining
     */
    public PointLight setRadius(double radius) {
        this.radius = Math.max(0, radius);
        return this;
    }

    /**
     * Sets the number of samples for area soft shadows (>= 1).
     * More samples yield smoother shadows at higher cost.
     *
     * @param numSamples number of jittered shadow rays
     * @return this PointLight for chaining
     */
    public PointLight setNumSamples(int numSamples) {
        this.numSamples = Math.max(1, numSamples);
        return this;
    }

    /**
     * No-op stub for compatibility: narrow-beam parameter is ignored for PointLight.
     *
     * @param ignored beam width parameter (unused)
     * @return this PointLight for chaining
     */
    public PointLight setNarrowBeam(int ignored) {
        // Compatibility stub; no effect
        return this;
    }

    /**
     * Returns the current area light radius.
     *
     * @return the radius of the light disk (0 for a point light)
     */
    public double getRadius() { return radius; }

    /**
     * Returns the number of samples for soft shadows.
     *
     * @return number of shadow-ray samples per shading point
     */
    public int getNumSamples() { return numSamples; }

    // ------------------ LightSource interface ------------------

    /**
     * Computes attenuated intensity at a given point based on distance.
     * Formula: I' = I / (kC + kL * d + kQ * d^2)
     *
     * @param p target point in scene
     * @return scaled light color at p
     */
    @Override
    public Color getIntensity(Point p) {
        double distance = position.distance(p);
        double attenuation = 1.0 / (kC + kL * distance + kQ * distance * distance);
        return intensity.scale(attenuation);
    }

    /**
     * Samples a random point on the circular disk around this light for soft shadows.
     * The disk lies in a plane orthogonal to vector (p - position).
     *
     * @param p shading point to cast shadows from
     * @return a jittered sample point on the area light disk
     */
    public Point getSamplePoint(Point p) {
        // Direction from light center to shading point
        Vector toP = p.subtract(position).normalize();
        // Choose arbitrary up vector not parallel to toP
        Vector up = Math.abs(toP.getX()) < 1e-6 && Math.abs(toP.getZ()) < 1e-6
                ? new Vector(1, 0, 0) : new Vector(0, 1, 0);
        // Build orthonormal basis (u, v) on plane of the disk
        Vector u = toP.crossProduct(up).normalize();
        Vector v = toP.crossProduct(u).normalize();

        // Generate random point in unit circle (uniform distribution)
        double r = Math.sqrt(random.nextDouble()) * radius;
        double theta = 2 * Math.PI * random.nextDouble();
        double xOff = r * Math.cos(theta);
        double yOff = r * Math.sin(theta);

        // Map to disk around position
        return position.add(u.scale(xOff)).add(v.scale(yOff));
    }

    /**
     * Returns the normalized direction from light to point p.
     *
     * @param p target point
     * @return unit vector from position to p
     */
    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    /**
     * Returns the distance from this light to point p.
     *
     * @param p target point
     * @return the distance between light position and p
     */
    @Override
    public double getDistance(Point p) {
        return position.distance(p);
    }
}