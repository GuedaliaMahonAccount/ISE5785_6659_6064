package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.Material;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import geometries.Intersectable;
import geometries.Intersectable.Intersection;

/**
 * The {@code Plane} class represents an infinite plane in 3D space.
 * Implements the NVI (Non-Virtual Interface) pattern by providing
 * its own bounding-box culling and delegating the detailed
 * ray-intersection test to the helper method.
 */
public class Plane extends Geometry {

    /** A reference point on the plane. */
    private final Point q0;

    /** The unit normal vector perpendicular to the plane. */
    private final Vector normal;

    /**
     * Construct a plane through three non-collinear points.
     * The normal is computed as the (v1 × v2) of the two edge vectors.
     *
     * @param p1 first point on the plane
     * @param p2 second point on the plane
     * @param p3 third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
        // build two edge vectors
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        // normal is perpendicular to both, then normalize
        this.normal = v1.crossProduct(v2).normalize();
        this.q0     = p1;
    }

    /**
     * Construct a plane from a point and a normal vector.
     * The normal is normalized internally.
     *
     * @param q0 reference point on the plane
     * @param normal direction perpendicular to the plane
     */
    public Plane(Point q0, Vector normal) {
        this.q0     = q0;
        this.normal = normal.normalize();
    }

    /**
     * Computes an (infinite) axis-aligned bounding box for the plane.
     * Since a plane is unbounded, it extends to ±∞ on all axes.
     */
    @Override
    protected BoundingBox computeBoundingBox() {
        return new BoundingBox(
                new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
                new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        );
    }

    /**
     * The plane normal is constant everywhere, so the input point
     * is ignored.
     *
     * @param point arbitrary point (ignored)
     * @return the unit normal vector of the plane
     */
    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    /**
     * Detailed ray-plane intersection test. Called by the NVI entry point
     * only if the ray passes the plane’s bounding box (always true here).
     *
     * @param ray the ray to intersect with this plane
     * @return a singleton list containing one Intersection, or null if none
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v  = ray.getDir();

        // denominator = n · v
        double nv = alignZero(normal.dotProduct(v));
        if (isZero(nv)) {
            // ray is parallel to plane
            return null;
        }

        // vector from ray origin to plane point
        Vector q0MinusP0 = q0.subtract(p0);

        // compute ray parameter t
        double t = alignZero(normal.dotProduct(q0MinusP0) / nv);
        if (t <= 0) {
            // intersection is behind the origin or at origin
            return null;
        }

        // compute the intersection point
        Point intersectionPoint = ray.getPoint(t);

        // wrap in our Intersection record
        return List.of(new Intersection(
                this,                        // the geometry
                intersectionPoint,           // intersection point
                getMaterial(),               // the plane’s material
                ray,                         // the incoming ray
                normal,                      // the plane normal
                null                         // no specific light source here
        ));
    }

    @Override
    public String toString() {
        return "Plane{" +
                "point="  + q0     +
                ", normal=" + normal +
                '}';
    }
}
