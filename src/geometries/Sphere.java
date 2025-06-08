package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * {@code Sphere} represents a sphere defined by a center point and radius.
 * Extends {@link RadialGeometry} to inherit radius property and surface normal behavior.
 */
public class Sphere extends RadialGeometry {
    /** The center point of the sphere in 3D space. */
    private final Point center;

    /**
     * Constructs a sphere with the specified center and radius.
     *
     * @param center the center point of the sphere; must not be null
     * @param radius the radius of the sphere; must be positive
     * @throws IllegalArgumentException if radius &le; 0 or center is null
     */
    public Sphere(Point center, double radius) {
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
     * Returns the center point of this sphere.
     *
     * @return the sphere's center point
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Computes the normal vector at a given point on the sphere's surface.
     * The point must lie on the sphere within a small epsilon tolerance.
     *
     * @param point the surface point where the normal is computed
     * @return the normalized vector from center to the point
     * @throws IllegalArgumentException if the point is not on the sphere surface
     */
    @Override
    public Vector getNormal(Point point) {
        double dist = point.distance(center);
        double diff = Math.abs(dist - radius);
        // allow small floating‐point error up to EPS
        final double EPS = 1e-6;
        if (diff > EPS) {
            throw new IllegalArgumentException(
                    String.format("Point %s is not on the sphere surface (|dist−r|=%.3e > %.3e)",
                            point, diff, EPS));
        }
        // normal is (point - center) normalized
        return point.subtract(center).normalize();
    }

    /**
     * Returns a string representation of the sphere.
     *
     * @return a string in the form {@code Sphere{center, r=radius}}
     */
    @Override
    public String toString() {
        return "Sphere{" + center + ", r=" + radius + "}";
    }

    /**
     * Internal helper for calculating all ray–sphere intersections.
     * Implements the NVI pattern: public methods call this helper.
     *
     * @param ray the ray to intersect with this sphere
     * @return a list of {@link Intersectable.Intersection} or null if none
     */
    @Override
    protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        // vector from ray origin to sphere center
        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts at center → one intersection at t = radius
            return List.of(
                    new Intersectable.Intersection(
                            this,
                            ray.getPoint(radius)
                    )
            );
        }

        double tm = alignZero(v.dotProduct(u));
        double d2 = alignZero(u.lengthSquared() - tm * tm);
        double r2 = radius * radius;
        // no intersections if ray misses or just grazes
        if (d2 >= r2) {
            return null;
        }

        double th = alignZero(Math.sqrt(r2 - d2));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        List<Intersectable.Intersection> intersections = new LinkedList<>();
        // add intersection at t1 if in front of ray origin
        if (t1 > 0) {
            Point p1 = ray.getPoint(t1);
            Vector n1 = p1.subtract(center).normalize();
            intersections.add(
                    new Intersectable.Intersection(
                            this,
                            p1,
                            getMaterial(),
                            ray,
                            n1,
                            null // light source placeholder
                    )
            );
        }
        // add intersection at t2 if in front of ray origin
        if (t2 > 0) {
            Point p2 = ray.getPoint(t2);
            Vector n2 = p2.subtract(center).normalize();
            intersections.add(
                    new Intersectable.Intersection(
                            this,
                            p2,
                            getMaterial(),
                            ray,
                            n2,
                            null
                    )
            );
        }

        return intersections.isEmpty() ? null : intersections;
    }
}
