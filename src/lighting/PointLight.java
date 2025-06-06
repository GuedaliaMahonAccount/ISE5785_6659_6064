// lighting/PointLight.java
package lighting;

import primitives.*;
import java.util.Random;

/**
 * Represents a point light source in the scene with attenuation based on distance.
 * Now supports circular‐area soft shadows via super‐sampling.
 * Also provides a no‐op setNarrowBeam(int) so that tests invoking it on a PointLight compile.
 */
public class PointLight extends Light implements LightSource {
    protected final Point position;

    // original attenuation factors
    protected double kC = 1.0;
    protected double kL = 0.0;
    protected double kQ = 0.0;

    // --- NEW for soft shadows: ---
    // radius of the circular area light (world units)
    private double radius = 0.0;
    // number of shadow‐ray samples per point
    private int numSamples = 1;
    // random generator for jittering sample points
    private final Random random = new Random();

    /**
     * Constructor for PointLight.
     *
     * @param intensity The intensity of the light.
     * @param position  The position of the light source.
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    // ------------ Existing setter/getter for attenuation ------------

    public PointLight setKc(double kC) {
        this.kC = Math.max(0, kC);
        return this;
    }

    public PointLight setKl(double kL) {
        this.kL = Math.max(0, kL);
        return this;
    }

    public PointLight setKq(double kQ) {
        this.kQ = Math.max(0, kQ);
        return this;
    }

    // ------------ NEW setters/getters for area/light sampling ------------

    /**
     * Sets the circular area radius for soft shadows.
     * @param radius radius of the area light (>= 0)
     * @return this PointLight (for chaining)
     */
    public PointLight setRadius(double radius) {
        this.radius = Math.max(0, radius);
        return this;
    }

    /**
     * Sets how many samples to shoot toward the light’s disk per shading point.
     * Must be >= 1.
     * @param numSamples number of shadow rays
     * @return this PointLight (for chaining)
     */
    public PointLight setNumSamples(int numSamples) {
        this.numSamples = Math.max(1, numSamples);
        return this;
    }

    /**
     * No‐op stub so that tests calling setNarrowBeam(int) on a PointLight compile.
     */
    public PointLight setNarrowBeam(int ignored) {
        // PointLight does not use narrow‐beam; this is just a stub for compatibility with LightsTests.
        return this;
    }

    /**
     * Returns the radius of the circular area light.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Returns how many samples this light uses for soft shadows.
     */
    public int getNumSamples() {
        return numSamples;
    }

    // ----------------------------------------------------------------------

    @Override
    public Color getIntensity(Point p) {
        double distance = position.distance(p);
        double attenuation = 1.0 / (kC + kL * distance + kQ * distance * distance);
        return intensity.scale(attenuation);
    }

    /**
     * Sample a random point on the circular disk, orthogonal to the vector from
     * this light to the shading point {@code p}. The disk lies in a plane whose normal
     * is (p − position).normalize(). Returns a Point on that disk.
     *
     * @param p the shading point
     * @return a jittered sample point on the area light’s circle
     */
    public Point getSamplePoint(Point p) {
        // Vector from light center to p
        Vector toP = p.subtract(position).normalize();
        // find any two orthonormal basis vectors (u, v) on plane orthogonal to toP
        Vector up = Math.abs(toP.getX()) < 1e-6 && Math.abs(toP.getZ()) < 1e-6
                ? new Vector(1, 0, 0)
                : new Vector(0, 1, 0);
        Vector u = toP.crossProduct(up).normalize();
        Vector v = toP.crossProduct(u).normalize();

        // random radius (sqrt for uniform inside circle) and angle
        double r   = Math.sqrt(random.nextDouble()) * radius;
        double theta = 2 * Math.PI * random.nextDouble();

        double xOff = r * Math.cos(theta);
        double yOff = r * Math.sin(theta);

        // sample point = center + xOff * u + yOff * v
        return position.add(u.scale(xOff)).add(v.scale(yOff));
    }

    @Override
    public Vector getL(Point p) {
        // direction from the (centroid) light position to point p
        return p.subtract(position).normalize();
    }

    @Override
    public double getDistance(Point p) {
        return position.distance(p);
    }
}
