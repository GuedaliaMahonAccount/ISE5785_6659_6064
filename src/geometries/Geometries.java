package geometries;

import primitives.Ray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A composite of multiple Intersectable geometries.
 * <p>
 * Aggregates a collection of geometries and computes all intersection points
 * between a given ray and the contained geometries.
 * </p>
 */
public class Geometries extends Intersectable {
    /**
     * The internal list of geometries making up this group.
     */
    private final List<Intersectable> geometries;

    /**
     * Creates an empty Geometries collection. Additional geometries can be added later.
     */
    public Geometries() {
        this.geometries = new LinkedList<>();
    }

    /**
     * Constructs a Geometries group containing the given intersectable geometries.
     *
     * @param geometries one or more Intersectable objects to include in this group
     */
    public Geometries(Intersectable... geometries) {
        this.geometries = new LinkedList<>(Arrays.asList(geometries));
    }

    /**
     * Adds one or more geometries to this group.
     *
     * @param geometries one or more Intersectable objects to add
     */
    public void add(Intersectable... geometries) {
        this.geometries.addAll(Arrays.asList(geometries));
    }

    /**
     * Calculates all intersection points between the provided ray and
     * each geometry in this collection.
     *
     * @param ray the ray to test for intersections
     * @return a list of Intersection objects, or null if there are no intersections
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> result = new LinkedList<>();
        for (Intersectable geo : geometries) {
            List<Intersection> hits = geo.calculateIntersections(ray);
            if (hits != null) {
                result.addAll(hits);
            }
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * Checks whether this Geometries group contains any geometries.
     *
     * @return true if the group is empty, false otherwise
     */
    public boolean isEmpty() {
        return geometries.isEmpty();
    }

    /**
     * Returns a string representation of this group,
     * listing all contained geometries.
     *
     * @return a descriptive string of this Geometries group
     */
    @Override
    public String toString() {
        return "Geometries" + geometries;
    }
}
