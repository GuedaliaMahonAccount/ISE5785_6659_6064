// src/geometries/Sphere.java
package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * {@code Sphere} represents a sphere defined by a center point and radius.
 * Inherits radius and material from RadialGeometry, and participates in
 * the NVI pattern defined by Intersectable to enable AABB culling.
 */
public class Sphere extends RadialGeometry {
    /** The center point of the sphere in 3D space. */
    private final Point center;

    /**
     * Constructs a sphere with the specified center and radius.
     *
     * @param center the center point of the sphere; must not be null
     * @param radius the radius of the sphere; must be positive
     * @throws IllegalArgumentException if radius ≤ 0 or center is null
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
     * Compute this sphere’s axis‐aligned bounding box for cheap rejection.
     */
    @Override
    protected BoundingBox computeBoundingBox() {
        double r = radius;  // inherited from RadialGeometry
        Point min = new Point(
                center.getX() - r,
                center.getY() - r,
                center.getZ() - r
        );
        Point max = new Point(
                center.getX() + r,
                center.getY() + r,
                center.getZ() + r
        );
        return new BoundingBox(min, max);
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
        final double EPS = 1e-6;
        if (diff > EPS) {
            throw new IllegalArgumentException(
                    String.format("Point %s is not on the sphere surface (|dist−r|=%.3e > %.3e)",
                            point, diff, EPS));
        }
        return point.subtract(center).normalize();
    }

    /**
     * String representation of the sphere.
     *
     * @return "Sphere{center, r=radius}"
     */
    @Override
    public String toString() {
        return "Sphere{" + center + ", r=" + radius + "}";
    }

    /**
     * Internal helper for calculating all ray–sphere intersections.
     * The public NVI method in Intersectable will have already done
     * the AABB test, so here we only compute the exact hits.
     *
     * @param ray the ray to intersect with this sphere
     * @return a list of Intersection records or null if none
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
                            ray.getPoint(radius),
                            getMaterial(),
                            ray,
                            getNormal(ray.getPoint(radius)),
                            null
                    )
            );
        }

        double tm = alignZero(v.dotProduct(u));
        double d2 = alignZero(u.lengthSquared() - tm * tm);
        double r2 = radius * radius;
        if (d2 >= r2) {
            return null;
        }

        double th = alignZero(Math.sqrt(r2 - d2));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        List<Intersectable.Intersection> intersections = new LinkedList<>();

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
                            null
                    )
            );
        }
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
