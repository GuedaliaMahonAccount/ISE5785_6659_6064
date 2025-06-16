// src/geometries/BoundingBox.java
package geometries;

import primitives.Point;
import primitives.Ray;

/**
 * Axis-aligned bounding box (AABB) for 3D intersection culling.
 */
public class BoundingBox {
    /** Minimum corner of the box (smallest x, y, z). */
    public final Point min;
    /** Maximum corner of the box (largest x, y, z). */
    public final Point max;

    /**
     * Constructs a BoundingBox defined by its minimum and maximum corners.
     *
     * @param min the minimum corner point
     * @param max the maximum corner point
     */
    public BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Returns the smallest box that encloses both {@code a} and {@code b}.
     *
     * @param a first bounding box
     * @param b second bounding box
     * @return new BoundingBox enclosing both a and b
     */
    public static BoundingBox union(BoundingBox a, BoundingBox b) {
        Point min = new Point(
                Math.min(a.min.getX(), b.min.getX()),
                Math.min(a.min.getY(), b.min.getY()),
                Math.min(a.min.getZ(), b.min.getZ())
        );
        Point max = new Point(
                Math.max(a.max.getX(), b.max.getX()),
                Math.max(a.max.getY(), b.max.getY()),
                Math.max(a.max.getZ(), b.max.getZ())
        );
        return new BoundingBox(min, max);
    }

    /**
     * Builds the union of the bounding boxes of the provided objects.
     *
     * @param objs iterable of intersectable objects
     * @return BoundingBox enclosing all objects' boxes
     */
    public static BoundingBox unionOf(Iterable<? extends Intersectable> objs) {
        BoundingBox bb = null;
        for (Intersectable o : objs) {
            if (bb == null) bb = o.getBoundingBox();
            else bb = union(bb, o.getBoundingBox());
        }
        return bb;
    }

    /**
     * Tests whether a ray intersects this bounding box using the slab method.
     *
     * @param ray the ray to test
     * @return true if the ray intersects, false otherwise
     */
    public boolean intersects(Ray ray) {
        double tmin = (min.getX() - ray.getP0().getX()) / ray.getDir().getX();
        double tmax = (max.getX() - ray.getP0().getX()) / ray.getDir().getX();
        if (tmin > tmax) { double tmp = tmin; tmin = tmax; tmax = tmp; }

        double tymin = (min.getY() - ray.getP0().getY()) / ray.getDir().getY();
        double tymax = (max.getY() - ray.getP0().getY()) / ray.getDir().getY();
        if (tymin > tymax) { double tmp = tymin; tymin = tymax; tymax = tmp; }
        if ((tmin > tymax) || (tymin > tmax)) return false;
        if (tymin > tmin) tmin = tymin;
        if (tymax < tmax) tmax = tymax;

        double tzmin = (min.getZ() - ray.getP0().getZ()) / ray.getDir().getZ();
        double tzmax = (max.getZ() - ray.getP0().getZ()) / ray.getDir().getZ();
        if (tzmin > tzmax) { double tmp = tzmin; tzmin = tzmax; tzmax = tmp; }
        return (!(tmin > tzmax)) && (!(tzmin > tmax));
    }

    /**
     * Checks if this bounding box is empty (min > max on any axis).
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return min.getX() > max.getX() ||
                min.getY() > max.getY() ||
                min.getZ() > max.getZ();
    }
}