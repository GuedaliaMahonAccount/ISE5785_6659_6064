package geometries;

import primitives.*;

/**
 * Sphere class represents a sphere defined by a center and radius.
 */
public class Sphere extends RadialGeometry {
    private final Point center;

    public Sphere(double radius, Point center) {
        super(radius);
        this.center = center;
    }

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
