package geometries;

/**
 * Abstract class for geometries defined by a radius.
 */
public abstract class RadialGeometry extends Geometry {
    protected final double radius;

    public RadialGeometry(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
