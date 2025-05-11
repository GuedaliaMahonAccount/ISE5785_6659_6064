package geometries;

import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * Interface for intersectable geometries.
 */
public interface Intersectable {

    /**
     * Finds intersection points with a given ray.
     * @param ray the ray to intersect with
     * @return list of GeoPoints, or null if no intersections
     */
    List<GeoPoint> findIntersections(Ray ray);

    /**
     * Class representing a geometry and a point of intersection.
     */
    public static class GeoPoint {
        public final Intersectable geometry;
        public final Point point;

        /**
         * Constructor for GeoPoint.
         * @param geometry The geometry object.
         * @param point The point of intersection.
         */
        public GeoPoint(Intersectable geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return "GeoPoint{geometry=" + geometry + ", point=" + point + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            GeoPoint other = (GeoPoint) obj;
            return geometry.equals(other.geometry) && point.equals(other.point);
        }
    }
}
