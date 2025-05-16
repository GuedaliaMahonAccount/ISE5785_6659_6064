package geometries;

import primitives.Ray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A composite of multiple Intersectable geometries.
 * Collects all Intersection points from its children.
 */
public class Geometries extends Intersectable {
    private final List<Intersectable> geometries;

    /** Create an empty Geometries collection for later additions. */
    public Geometries() {
        this.geometries = new LinkedList<>();
    }

    /** Build a Geometries group from any number of Intersectables. */
    public Geometries(Intersectable... geometries) {
        this.geometries = new LinkedList<>(Arrays.asList(geometries));
    }

    /** Add another geometry to this group. */
    public void add(Intersectable geometry) {
        this.geometries.add(geometry);
    }

    /**
     * NVI helper: for each child, collect its Intersection list.
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

    @Override
    public String toString() {
        return "Geometries" + geometries;
    }
}
