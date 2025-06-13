// src/geometries/BVHNode.java
package geometries;

import primitives.Ray;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/** A BVHNode is itself an Intersectable, so it plugs right into your code. */
public class BVHNode extends Intersectable {
    private final BoundingBox box;
    private final Intersectable left, right;   // children
    private final List<Intersectable> leafObjs; // only for leaves
    private static final int MAX_LEAF = 4;

    /** Leaf‐constructor */
    private BVHNode(List<Intersectable> objs) {
        this.leafObjs = objs;
        this.left  = null;
        this.right = null;
        this.box   = BoundingBox.unionOf(objs);
    }

    /** Internal‐node constructor */
    private BVHNode(BVHNode l, BVHNode r) {
        this.leafObjs = null;
        this.left  = l;
        this.right = r;
        this.box   = BoundingBox.union(l.box, r.box);
    }

    @Override
    protected BoundingBox computeBoundingBox() {
        return box;
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsInternal(Ray ray) {
        // we know ray hits box (checked by NVI), so:
        if (leafObjs != null) {
            List<GeoPoint> res = null;
            for (var o : leafObjs) {
                var pts = o.findGeoIntersections(ray);
                if (pts != null) {
                    if (res == null) res = new ArrayList<>();
                    res.addAll(pts);
                }
            }
            return res;
        } else {
            // traverse children in whichever order:
            List<GeoPoint> leftHits  = left.findGeoIntersections(ray);
            List<GeoPoint> rightHits = right.findGeoIntersections(ray);
            if (leftHits == null) return rightHits;
            if (rightHits == null) return leftHits;
            leftHits.addAll(rightHits);
            return leftHits;
        }
    }

    /** Build a BVH over a flat list of primitives */
    public static BVHNode build(List<Intersectable> prims) {
        if (prims.size() <= MAX_LEAF) return new BVHNode(prims);
        // 1. compute bounding‐box and longest axis
        BoundingBox full = BoundingBox.unionOf(prims);
        double dx = full.max.getX() - full.min.getX();
        double dy = full.max.getY() - full.min.getY();
        double dz = full.max.getZ() - full.min.getZ();
        int axis = dx>dy && dx>dz ? 0 : (dy>dz ? 1 : 2);
        // 2. sort by centroid
        prims.sort(Comparator.comparingDouble(o -> {
            BoundingBox b = o.getBoundingBox();
            return axis==0
                    ? (b.min.getX()+b.max.getX())*0.5
                    : axis==1
                    ? (b.min.getY()+b.max.getY())*0.5
                    : (b.min.getZ()+b.max.getZ())*0.5;
        }));
        // 3. split at median
        int mid = prims.size()/2;
        List<Intersectable> leftList  = new ArrayList<>(prims.subList(0, mid));
        List<Intersectable> rightList = new ArrayList<>(prims.subList(mid, prims.size()));
        return new BVHNode(build(leftList), build(rightList));
    }
}
