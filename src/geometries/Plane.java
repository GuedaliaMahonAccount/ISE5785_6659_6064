package geometries;

import primitives.*;

/**
 * The {@code Plane} class represents a plane in 3D space.
 * It is defined either by three points or by a point and a normal vector.
 */
public class Plane extends Geometry {
    private final Point q0;
    private final Vector normal;

    /**
     * Constructs a plane from three points.
     *
     * @param p1 first point on the plane
     * @param p2 second point on the plane
     * @param p3 third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        normal = v1.crossProduct(v2).normalize();
        q0 = p1;
    }

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param q0     a point on the plane
     * @param normal the normal vector to the plane
     */
    public Plane(Point q0, Vector normal) {
        this.q0 = q0;
        this.normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    /**
     * Returns a point on the plane.
     *
     * @return the point q0
     */
    public Point getQ0() {
        return q0;
    }

    /**
     * Returns the normal vector of the plane.
     *
     * @return the normalized normal vector
     */
    public Vector getNormal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane{" + q0 + ", " + normal + "}";
    }
}
