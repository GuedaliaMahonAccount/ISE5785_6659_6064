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

    /**
     * Default constructor: creates an empty list of geometries.
     */
    public Geometries() {
        // List is initialized at declaration
    }

    /**
     * Constructor to initialize with a variable number of geometries.
     *
     * @param geometries initial geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more geometries to the collection.
     *
     * @param geometries geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Finds all intersection points of the ray with the geometries in the collection.
     *
     * @param ray the ray to intersect with
     * @return list of intersection points, or null if no intersections are found
     */
    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        List<GeoPoint> result = null;

        for (Intersectable geometry : geometries) {
            List<GeoPoint> intersections = geometry.findIntersections(ray);
            if (intersections != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(intersections);
            }
        }

        return result;
    }

    public boolean isEmpty() {
        return geometries.isEmpty();
    }
}
