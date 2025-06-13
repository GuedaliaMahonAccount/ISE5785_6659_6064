package geometries;

import lighting.LightSource;
import primitives.Point;
import primitives.Ray;
import primitives.Material;
import primitives.Vector;

import java.util.List;

/**
 * Abstract base for all geometric shapes that can be intersected by a ray.
 * Implements the Non-Virtual Interface (NVI) pattern to provide bounding-box culling
 * before dispatching to the detailed intersection logic.
 */
public abstract class Intersectable {

    // Bounding box is computed lazily after subclass initialization
    private BoundingBox bbox;

    protected Intersectable() {
        // delay compute until after subclass fields are initialized
    }

    /**
     * Compute this shape’s axis-aligned bounding box.
     */
    protected abstract BoundingBox computeBoundingBox();

    /**
     * @return the lazily computed bounding box for this shape, or null if none
     */
    public final BoundingBox getBoundingBox() {
        if (bbox == null) {
            bbox = computeBoundingBox();
        }
        return bbox;
    }

    /**
     * Public entry point: test the ray against the AABB first (if present), then
     * call the subclass’s intersection code.
     *
     * @param ray the ray to intersect
     * @return list of detailed Intersection records, or null if none
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        BoundingBox b = getBoundingBox();
        // if there's a bounding box and the ray misses it, cull early
        if (b != null && !b.intersects(ray)) {
            return null;
        }
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Subclasses override *only* this method with their actual ray-shape test.
     *
     * @param ray the ray to intersect
     * @return list of Intersection objects or null if no hits
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Legacy support: convert the detailed Intersection list into GeoPoint pairs.
     *
     * @param ray the ray to intersect
     * @return list of GeoPoint (geometry+point) or null
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        var hits = calculateIntersections(ray);
        return (hits == null) ? null
                : hits.stream()
                .map(i -> new GeoPoint(i.geometry, i.point))
                .toList();
    }

    /**
     * Legacy support: return only the raw intersection points.
     *
     * @param ray the ray to intersect
     * @return list of Points or null
     */
    public final List<Point> findIntersections(Ray ray) {
        var hits = calculateIntersections(ray);
        return (hits == null) ? null
                : hits.stream()
                .map(i -> i.point)
                .toList();
    }

    /**
     * @return true if this shape has no volume/boundary
     */
    public boolean isEmpty() {
        BoundingBox b = getBoundingBox();
        return b == null || b.isEmpty();
    }

    /**
     * A full record of one ray–geometry intersection, including
     * material, normal, dot-product, and an optional LightSource.
     */
    public static class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;
        public final Ray ray;
        public final Vector normal;
        public final double dotProduct;
        public LightSource lightSource;

        public Intersection(Geometry geometry,
                            Point point,
                            Material material,
                            Ray ray,
                            Vector normal,
                            LightSource lightSource) {
            this.geometry    = geometry;
            this.point       = point;
            this.material    = material != null ? material : new Material();
            this.ray         = ray;
            this.normal      = normal;
            this.lightSource = lightSource;
            this.dotProduct  = (normal != null && ray != null)
                    ? normal.dotProduct(ray.getDir())
                    : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Intersection other)) return false;
            return geometry.equals(other.geometry)
                    && point.equals(other.point);
        }

        @Override
        public String toString() {
            return "Intersection [geometry=" + geometry
                    + ", point="    + point
                    + ", material=" + material
                    + ", dot="      + dotProduct + "]";
        }
    }

    /**
     * Simple legacy pairing of a geometry with one hit point.
     */
    public static class GeoPoint {
        public final Geometry geometry;
        public final Point point;

        public GeoPoint(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point    = point;
        }

        @Override
        public String toString() {
            return "GeoPoint [geometry=" + geometry + ", point=" + point + "]";
        }
    }
}
