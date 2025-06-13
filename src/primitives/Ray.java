package primitives;

import geometries.Intersectable.GeoPoint;
import geometries.Intersectable.Intersection;
import renderer.SimpleRayTracer;

import java.util.List;

/**
 * The {@code Ray} class represents a ray in 3D space, defined by
 * a starting point {@link #p0} and a normalized direction {@link #dir}.
 * Provides utilities for computing points along the ray and
 * finding closest intersections or geo-points.
 */
public class Ray {
    /** The ray’s origin point. */
    private final Point p0;
    /** The ray’s normalized direction vector. */
    private final Vector dir;

    /**
     * Constructs a ray from an origin and direction.
     * @param p0  origin point
     * @param dir direction vector (will be normalized)
     */
    public Ray(Point p0, Vector dir) {
        this.p0  = p0;
        this.dir = dir.normalize();
    }

    /**
     * Constructs a ray whose origin is shifted by ±DELTA along the normal
     * to avoid self-intersection artifacts.
     * @param head   the hit-point
     * @param dir    the (already normalized) direction
     * @param normal the surface normal at the hit point
     */
    public Ray(Point head, Vector dir, Vector normal) {
        double sign = dir.dotProduct(normal) < 0 ? -1 : 1;
        this.p0  = head.add(normal.scale(sign * SimpleRayTracer.DELTA));
        this.dir = dir.normalize();
    }

    public Point getP0()  { return p0; }
    public Vector getDir() { return dir; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ray other)) return false;
        return p0.equals(other.p0) && dir.equals(other.dir);
    }

    @Override
    public String toString() {
        return "Ray{" + p0 + ", " + dir + "}";
    }

    /**
     * Computes the point along this ray at distance {@code d} from {@link #p0}.
     * @param d distance from the origin
     * @return the computed {@link Point} on the ray
     */
    public Point getPoint(double d) {
        if (Util.isZero(d)) {
            return p0;
        }
        try {
            return p0.add(dir.scale(d));
        } catch (IllegalArgumentException e) {
            // scaling created a zero vector -> return origin
            return p0;
        }
    }

    /**
     * Finds the closest {@link GeoPoint} in the given list to this ray’s origin.
     */
    public GeoPoint findClosestGeoPoint(List<GeoPoint> geoPoints) {
        if (geoPoints == null || geoPoints.isEmpty()) return null;
        GeoPoint closest   = null;
        double   minDist   = Double.POSITIVE_INFINITY;
        for (GeoPoint gp : geoPoints) {
            double d = p0.distance(gp.point);
            if (d < minDist) {
                minDist = d;
                closest = gp;
            }
        }
        return closest;
    }

    /**
     * Finds the closest {@link Intersection} in the given list to this ray’s origin.
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) return null;
        Intersection closest = null;
        double       minDist = Double.POSITIVE_INFINITY;
        for (Intersection inter : intersections) {
            double d = p0.distance(inter.point);
            if (d < minDist) {
                minDist = d;
                closest = inter;
            }
        }
        return closest;
    }

    /**
     * Finds the closest {@link Point} in the given list to this ray’s origin.
     * @param points list of points
     * @return the nearest point, or {@code null} if none
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }
        Point closest      = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point p : points) {
            double dist = p0.distance(p);
            if (dist < minDistance) {
                minDistance = dist;
                closest     = p;
            }
        }
        return closest;
    }
}
