package geometries;

/**
 * Abstract class for geometries that have a radius.
 */
public abstract class RadialGeometry extends Geometry {
    /** The radius of the geometry. */
    protected final double radius;

    /**
     * Constructs a {@code RadialGeometry} with a given radius.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
    }

    /**
     * Returns the radius of the geometry.
     *
     * @return the radius value
     */
    public double getRadius() {
        return radius;
    }
}
