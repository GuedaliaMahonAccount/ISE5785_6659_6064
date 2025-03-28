package geometries;

import primitives.*;

/**
 * The {@code Sphere} class represents a sphere in 3D space.
 * It is defined by a center point and a radius.
 */
public class Sphere extends RadialGeometry {
    /** The center point of the sphere. */
    private final Point center;

    /**
     * Constructs a sphere with a given radius and center.
     *
     * @param radius the radius of the sphere
     * @param center the center point of the sphere
     */
    public Sphere(double radius, Point center) {
        super(radius);
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        if (center == null) {
            throw new IllegalArgumentException("Center point cannot be null");
        }
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
        double distance = point.distance(center);
        if (Math.abs(distance - radius) > 1e-10) {
            throw new IllegalArgumentException("Point is not on the surface of the sphere");
        }
        return point.subtract(center).normalize();
    }


    @Override
    public String toString() {
        return "Sphere{" + center + ", r=" + radius + "}";
    }
}
