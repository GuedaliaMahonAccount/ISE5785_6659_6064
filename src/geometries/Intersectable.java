package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Intersectable abstract class represents all the geometric shapes that can be intersected by a ray.
 * Implements the NVI design pattern to ensure consistent external API and flexible internal logic.
 */
public abstract class Intersectable {

    /**
     * Intersection class that represents a point of intersection and its geometry.
     */
    public static class Intersection {
        public final Geometry geometry;
        public final Point point;

        /**
         * Constructs an Intersection object with the given geometry and point.
         * @param geometry the geometry object involved in the intersection (compared by reference)
         * @param point the intersection point (compared using equals)
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Intersection other = (Intersection) obj;
            return this.geometry == other.geometry && this.point.equals(other.point);
        }

        @Override
        public String toString() {
            return "Intersection [geometry=" + geometry + ", point=" + point + "]";
        }
    }

    /**
     * GeoPoint class that represents a point on the geometry shape (legacy support).
     */
    public static class GeoPoint {
        public Geometry geometry;
        public Point point;

        /**
         * Constructs a GeoPoint with the given geometry and point.
         * @param geometry the geometry object
         * @param point the point on the geometry
         */
        public GeoPoint(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;

            return obj instanceof GeoPoint other &&
                    this.geometry.equals(other.geometry) &&
                    this.point.equals(other.point);
        }

        @Override
        public String toString() {
            return "GeoPoint [geometry=" + geometry + ", point=" + point + "]";
        }
    }

    /**
     * Public method to calculate all intersections of a ray with the shape.
     * This method follows the NVI (Non-Virtual Interface) design pattern.
     * @param ray the ray to intersect with
     * @return list of Intersection objects or null if none found
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Abstract helper method to be implemented by each subclass to calculate intersections.
     * According to the NVI pattern, this method should not be public.
     * @param ray the ray to intersect with
     * @return list of Intersection objects or null if no intersections
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Legacy support method that extracts only the points from the Intersection objects.
     * @param ray the ray to intersect with
     * @return list of points or null
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }

    /**
     * Legacy support method that creates GeoPoint list from new Intersection list.
     * @param ray the ray to intersect with
     * @return list of GeoPoints or null
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        var intersections = calculateIntersections(ray);
        return intersections == null ? null :
                intersections.stream().map(i -> new GeoPoint(i.geometry, i.point)).toList();
    }
}
