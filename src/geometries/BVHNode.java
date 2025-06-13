package geometries;

import primitives.Ray;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/** A BVHNode is itself an Intersectable, so it plugs right into your code. */
public class BVHNode extends Intersectable {
    private final BoundingBox box;
    private final Intersectable left, right;     // children (null for leaf)
    private final List<Intersectable> leafObjs;  // non-null only for leaves
    private static final int MAX_LEAF = 4;

    /** Leaf‐node constructor */
    private BVHNode(List<Intersectable> objs) {
        this.leafObjs = objs;
        this.left     = null;
        this.right    = null;
        this.box      = BoundingBox.unionOf(objs);
    }

    /** Internal‐node constructor */
    private BVHNode(BVHNode l, BVHNode r) {
        this.leafObjs = null;
        this.left     = l;
        this.right    = r;
        this.box      = BoundingBox.union(l.box, r.box);
    }

    /** Return the precomputed bounding box for this node. */
    @Override
    protected BoundingBox computeBoundingBox() {
        return box;
    }

    /**
     * NVI helper: ray already passed the AABB test, so now:
     * – if leaf: test each object’s calculateIntersections()
     * – else: recurse into children
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        // If this is a leaf node, test each primitive directly.
        if (leafObjs != null) {
            List<Intersection> result = null;
            for (Intersectable o : leafObjs) {
                List<Intersection> hits = o.calculateIntersections(ray);
                if (hits != null) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.addAll(hits);
                }
            }
            return result;
        }

        // Otherwise this is an internal node: test each child's bounding box once,
        // then recurse only into children whose box actually intersects the ray.
        List<Intersection> result = null;

        // Left child
        BoundingBox lb = left.getBoundingBox();
        if (lb != null && lb.intersects(ray)) {
            List<Intersection> leftHits = left.calculateIntersectionsHelper(ray);
            if (leftHits != null) {
                result = new ArrayList<>(leftHits);
            }
        }

        // Right child
        BoundingBox rb = right.getBoundingBox();
        if (rb != null && rb.intersects(ray)) {
            List<Intersection> rightHits = right.calculateIntersectionsHelper(ray);
            if (rightHits != null) {
                if (result == null) {
                    result = new ArrayList<>(rightHits);
                } else {
                    result.addAll(rightHits);
                }
            }
        }

        return result;
    }


    /**
     * Build a BVH over a flat list of primitives.
     * Splits along the longest axis at the median until leaves ≤ MAX_LEAF.
     */
    public static BVHNode build(List<Intersectable> prims) {
        if (prims.size() <= MAX_LEAF) {
            return new BVHNode(prims);
        }

        // 1) find longest axis of the full set
        BoundingBox full = BoundingBox.unionOf(prims);
        double dx = full.max.getX() - full.min.getX();
        double dy = full.max.getY() - full.min.getY();
        double dz = full.max.getZ() - full.min.getZ();
        int axis = dx > dy && dx > dz ? 0 : (dy > dz ? 1 : 2);

        // 2) sort by centroid along that axis
        prims.sort(Comparator.comparingDouble(o -> {
            BoundingBox b = o.getBoundingBox();
            return axis == 0
                    ? (b.min.getX() + b.max.getX()) * 0.5
                    : axis == 1
                    ? (b.min.getY() + b.max.getY()) * 0.5
                    : (b.min.getZ() + b.max.getZ()) * 0.5;
        }));

        // 3) split at median
        int mid = prims.size() / 2;
        var leftList  = new ArrayList<Intersectable>(prims.subList(0, mid));
        var rightList = new ArrayList<Intersectable>(prims.subList(mid, prims.size()));

        return new BVHNode(build(leftList), build(rightList));
    }
}
