package geometries;

import primitives.*;

/**
 * The {@code Tube} class represents an infinite cylinder (without caps).
 * It is defined by a central axis (as a {@link Ray}) and a radius.
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis of the tube.
     */
    private final Ray axisRay;

    /**
     * Constructs a tube with a given radius and axis ray.
     *
     * @param radius  the radius of the tube
     * @param axisRay the central axis of the tube
     */
    public Tube(double radius, Ray axisRay) {
        super(radius);
        this.axisRay = axisRay;
    }

    /**
     * Returns the central axis ray of the tube.
     *
     * @return the axis ray
     */
    public Ray getAxisRay() {
        return axisRay;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = axisRay.getP0();
        Vector dir = axisRay.getDir();
        double t = point.subtract(p0).dotProduct(dir);
        Point o = p0.add(dir.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    public String toString() {
        return "Tube{" + axisRay + ", r=" + radius + "}";
    }
}
