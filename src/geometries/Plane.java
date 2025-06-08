package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import geometries.Intersectable.Intersection;

/**
 * The {@code Plane} class represents an infinite plane in 3D space.
 * A plane can be defined either by three non-collinear points or by a point and a normal vector.
 */
public class Plane extends Geometry {
    /** A reference point on the plane. */
    private final Point q0;
    /** The normalized normal vector perpendicular to the plane's surface. */
    private final Vector normal;

    /**
     * Constructs a plane through three given points.
     * Computes the normal as the cross product of two edge vectors.
     *
     * @param p1 first point on the plane
     * @param p2 second point on the plane
     * @param p3 third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
        // Compute two direction vectors from p1
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        // Cross product yields a vector perpendicular to both v1 and v2
        normal = v1.crossProduct(v2).normalize();
        // Store one of the defining points
        q0 = p1;
    }

    /**
     * Constructs a plane from a given point and normal vector.
     * Normal vector is normalized internally.
     *
     * @param q0 reference point on the plane
     * @param normal vector perpendicular to the plane's surface
     */
    public Plane(Point q0, Vector normal) {
        this.q0 = q0;
        // Ensure the normal is a unit vector
        this.normal = normal.normalize();
    }

    /**
     * Returns the constant normal vector of this plane (same for any point).
     *
     * @param point ignored; plane normal is uniform
     * @return normalized normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    /**
     * Retrieves the reference point q0 on the plane.
     *
     * @return reference point used in plane definition
     */
    public Point getQ0() {
        return q0;
    }

    /**
     * Retrieves the normalized normal vector of the plane.
     *
     * @return plane's normal vector
     */
    public Vector getNormal() {
        return normal;
    }

    /**
     * Returns a string representation of the plane, including its point and normal.
     *
     * @return formatted string with q0 and normal
     */
    @Override
    public String toString() {
        return "Plane{" + q0 + ", normal=" + normal + '}';
    }

    /**
     * Calculates intersection points between a given ray and this plane.
     * Implements the helper for the NVI pattern.
     *
     * @param ray the ray used for intersection test
     * @return singleton list with one Intersection if it exists, otherwise null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        // Ray origin and direction
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        // Dot product between ray direction and plane normal
        double nv = alignZero(normal.dotProduct(v));
        // If zero, ray is parallel to plane -> no intersections
        if (isZero(nv)) {
            return null;
        }

        // Vector from ray origin to plane point
        Vector q0MinusP0;
        try {
            q0MinusP0 = q0.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts exactly on the plane point -> treat as no intersection
            return null;
        }

        // Compute t parameter for ray equation: t = (normal·(q0 - p0)) / (normal·v)
        double t = alignZero(normal.dotProduct(q0MinusP0) / nv);
        // If t <= 0, intersection is behind ray origin or at origin
        if (t <= 0) {
            return null;
        }

        // Compute the exact intersection point and return it
        return List.of(new Intersection(this, ray.getPoint(t)));
    }
}
