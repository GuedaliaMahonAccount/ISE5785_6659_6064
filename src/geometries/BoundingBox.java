// src/geometries/BoundingBox.java
package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class BoundingBox {
    public final Point min;
    public final Point max;

    public BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /** Build the union of two boxes */
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

    /** Union of a list of intersectables (leaf or internal) */
    public static BoundingBox unionOf(Iterable<? extends Intersectable> objs) {
        BoundingBox bb = null;
        for (Intersectable o : objs) {
            if (bb == null) bb = o.getBoundingBox();
            else bb = union(bb, o.getBoundingBox());
        }
        return bb;
    }

    /** Slab‐method AABB vs. Ray test */
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
        if ((tmin > tzmax) || (tzmin > tmax)) return false;

        return true;
    }

    public boolean isEmpty() {
        return min.getX() > max.getX() ||
               min.getY() > max.getY() ||
               min.getZ() > max.getZ();
    }
}
