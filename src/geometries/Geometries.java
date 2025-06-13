// src/geometries/Geometries.java
package geometries;

import primitives.Ray;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite of multiple Intersectable objects.
 * Also participates in the NVI pattern, so you get one big AABB,
 * with children thrown out early on a per-child basis.
 */
public class Geometries extends Intersectable {

    private final List<Intersectable> children = new ArrayList<>();

    /** Build a composite out of zero or more Intersectables */
    public Geometries(Intersectable... elems) {
        for (var e : elems) {
            if (e != null) children.add(e);
        }
    }

    /** Add more shapes to this grouping */
    public void add(Intersectable... elems) {
        for (var e : elems) {
            if (e != null) children.add(e);
        }
    }

    /** Expose the raw list if you need it elsewhere (e.g. BVH builder) */
    public List<Intersectable> getChildren() {
        return children;
    }

    @Override
    protected BoundingBox computeBoundingBox() {
        return BoundingBox.unionOf(children);
    }

    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> result = null;
        for (Intersectable g : children) {
            var hits = g.calculateIntersections(ray);
            if (hits != null) {
                if (result == null) result = new ArrayList<>();
                result.addAll(hits);
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}
