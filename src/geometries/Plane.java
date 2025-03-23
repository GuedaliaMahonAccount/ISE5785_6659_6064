package geometries;

import primitives.*;

/**
 * Class Plane represents a plane in 3D defined by a point and a normal vector.
 */
public class Plane extends Geometry {
    private final Point q0;
    private final Vector normal;

    /** Constructor with three points */
    public Plane(Point p1, Point p2, Point p3) {
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        normal = v1.crossProduct(v2).normalize();
        q0 = p1;
    }

    /** Constructor with a point and a normal vector */
    public Plane(Point q0, Vector normal) {
        this.q0 = q0;
        this.normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    public Point getQ0() {
        return q0;
    }

    public Vector getNormal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane{" + q0 + ", " + normal + "}";
    }
}
