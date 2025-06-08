package geometries;

import lighting.LightSource;
import primitives.Point;
import primitives.Ray;
import primitives.Material;
import primitives.Vector;

import java.util.List;

/**
 * Abstract base for all geometric shapes that can be intersected by a ray.
 * Implements the Non-Virtual Interface (NVI) pattern to provide a stable public API
 * while allowing subclasses to implement the specific intersection logic.
 */
public abstract class Intersectable {

    /**
     * Represents a detailed ray–geometry intersection.
     * Includes references to the geometry, material, normal, and light source for shading.
     */
    public static class Intersection {
        /** The geometry that was intersected. */
        public final Geometry geometry;
        /** The exact point of intersection in 3D space. */
        public final Point point;
        /** The material properties at the intersection. */
        public final Material material;
        /** The ray that produced this intersection. */
        public final Ray ray;
        /** The dot product between the surface normal and ray direction for lighting calculations. */
        public final double dotProduct;
        /** The normal vector at the intersection point. */
        public final Vector normal;
        /** Optional light source associated with this intersection (for caching shading results). */
        public LightSource lightSource;

        /**
         * Primary constructor initializing all fields for a complete intersection record.
         * @param geometry    intersected geometry
         * @param point       intersection point
         * @param material    material at the intersection (defaults if null)
         * @param ray         ray that hit the geometry
         * @param normal      surface normal at the intersection point
         * @param lightSource light source relevant to this intersection
         */
        public Intersection(Geometry geometry,
                            Point point,
                            Material material,
                            Ray ray,
                            Vector normal,
                            LightSource lightSource) {
            this.geometry = geometry;
            this.point = point;
            this.material = material != null ? material : new Material();  // use default material if none provided
            this.ray = ray;
            this.normal = normal;
            // compute dot of normal and ray direction for shading
            this.dotProduct = (normal != null && ray != null)
                    ? normal.dotProduct(ray.getDir()) : 0;
            this.lightSource = lightSource;
        }

        /**
         * Simplified constructor for basic intersection tests lacking full shading info.
         * @param geometry the geometry object involved in the intersection
         * @param point    the intersection point
         */
        public Intersection(Geometry geometry, Point point) {
            this(geometry, point, null, null, null, null);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Intersection other = (Intersection) obj;
            // equality based on geometry reference and intersection location
            return this.geometry == other.geometry && this.point.equals(other.point);
        }

        @Override
        public String toString() {
            return "Intersection [geometry=" + geometry
                    + ", point=" + point
                    + ", material=" + material
                    + ", dotProduct=" + dotProduct + "]";
        }
    }

    /**
     * Legacy support structure pairing a geometry with a point.
     */
    public static class GeoPoint {
        /** The intersected geometry. */
        public Geometry geometry;
        /** The location of the intersection. */
        public Point point;

        /**
         * Constructs a GeoPoint for backward compatibility.
         * @param geometry geometry involved
         * @param point    intersection point
         */
        public GeoPoint(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof GeoPoint other)) return false;
            return this.geometry.equals(other.geometry)
                    && this.point.equals(other.point);
        }

        @Override
        public String toString() {
            return "GeoPoint [geometry=" + geometry + ", point=" + point + "]";
        }
    }

    /**
     * Public entry point for computing intersections with this shape.
     * Follows the NVI pattern: not overrideable by subclasses.
     * @param ray the ray to intersect
     * @return list of detailed Intersection objects or null if none
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Subclasses implement this to provide their specific intersection logic.
     * @param ray the ray to intersect
     * @return list of Intersection objects or null if no intersections
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Legacy support returning only raw intersection points.
     * @param ray the ray to intersect
     * @return list of intersection points or null
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return (list == null) ? null
                : list.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Legacy support mapping new Intersection objects to GeoPoint instances.
     * @param ray the ray to intersect
     * @return list of GeoPoint pairs or null
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        var intersections = calculateIntersections(ray);
        return (intersections == null) ? null
                : intersections.stream()
                .map(i -> new GeoPoint(i.geometry, i.point))
                .toList();
    }
}
