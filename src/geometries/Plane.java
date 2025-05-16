package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import geometries.Intersectable.Intersection;

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
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
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
     * @param q0 the reference point on the plane
     * @param normal the normal vector
     */
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

    /**
     * Calculates the intersections of a ray with this plane.
     *
     * @param ray the ray to intersect with
     * @return list of Intersection objects or null if none found
     */
    @Override
    protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        double nv = alignZero(normal.dotProduct(v));
        if (isZero(nv)) return null; // ray is parallel to the plane

        Vector q0MinusP0;
        try {
            q0MinusP0 = q0.subtract(p0);
        } catch (IllegalArgumentException e) {
            // ray starts at q0
            return null;
        }

        double t = alignZero(normal.dotProduct(q0MinusP0) / nv);
        if (t <= 0) return null;

        // Return a single intersection if the t value is positive
        return List.of(new Intersectable.Intersection(this, ray.getPoint(t)));
    }
}
