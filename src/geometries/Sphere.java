package geometries;

import primitives.*;

/**
 * The {@code Sphere} class represents a sphere in 3D space.
 * It is defined by a center point and a radius.
 */
public class Sphere extends RadialGeometry {
    private final Point center;

    /**
     * Constructs a sphere with a given radius and center.
     *
     * @param radius the radius of the sphere
     * @param center the center point of the sphere
     */
    public Sphere(double radius, Point center) {
        super(radius);
        this.center = center;
    }

    /**
     * Returns the center point of the sphere.
     *
     * @return the center
     */
    public Point getCenter() {
        return center;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere{" + center + ", r=" + radius + "}";
    }
}
