package geometries;

import primitives.Ray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Composite class that aggregates multiple geometric bodies for intersection testing.
 */
public class Geometries implements Intersectable {

    private final List<Intersectable> geometries = new LinkedList<>();

    public Geometries() {}

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    // New helper method which calls the helper on each geometry and collects Intersection objects
    public List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> result = new LinkedList<>();

        for (Intersectable geometry : geometries) {
            if (geometry instanceof Plane) {
                Plane plane = (Plane) geometry;
                List<Intersection> list = plane.calculateIntersectionsHelper(ray);
                if (list != null) result.addAll(list);
            } else if (geometry instanceof Polygon) {
                Polygon polygon = (Polygon) geometry;
                List<Intersection> list = polygon.calculateIntersectionsHelper(ray);
                if (list != null) result.addAll(list);
            } else if (geometry instanceof Geometries) {
                Geometries geoms = (Geometries) geometry;
                List<Intersection> list = geoms.calculateIntersectionsHelper(ray);
                if (list != null) result.addAll(list);
            }
            // else handle other geometry types similarly or default findIntersections?
            else {
                // fallback: use old findIntersections and convert
                List<GeoPoint> geoPoints = geometry.findIntersections(ray);
                if (geoPoints != null) {
                    for (GeoPoint gp : geoPoints) {
                        result.add(new Intersection(gp.geometry, gp.point));
                    }
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        List<Intersection> intersections = calculateIntersectionsHelper(ray);
        if (intersections == null) return null;

        return intersections.stream()
                .map(i -> new GeoPoint(i.geometry, i.point))
                .toList();
    }

    public boolean isEmpty() {
        return geometries.isEmpty();
    }
}
