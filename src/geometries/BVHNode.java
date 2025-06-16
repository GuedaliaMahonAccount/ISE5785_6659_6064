// src/geometries/BVHNode.java
package geometries;

import primitives.Ray;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Bounding Volume Hierarchy (BVH) node for accelerating ray-geometry intersections.
 * A BVHNode is itself an Intersectable, allowing it to be used transparently
 * in the intersection pipeline.
 */
public class BVHNode extends Intersectable {
    private final BoundingBox box;
    private final Intersectable left;
    private final Intersectable right;
    private final List<Intersectable> leafObjs;
    private static final int MAX_LEAF = 4;

    /**
     * Constructs a leaf BVH node containing up to MAX_LEAF primitives.
     *
     * @param objs list of intersectable primitives
     */
    private BVHNode(List<Intersectable> objs) {
        this.leafObjs = objs;
        this.left = null;
        this.right = null;
        this.box = BoundingBox.unionOf(objs);
    }

    /**
     * Constructs an internal BVH node with two child nodes.
     *
     * @param l left child node
     * @param r right child node
     */
    private BVHNode(BVHNode l, BVHNode r) {
        this.leafObjs = null;
        this.left = l;
        this.right = r;
        this.box = BoundingBox.union(l.box, r.box);
    }

    /**
     * Returns the precomputed bounding box for this node.
     */
    @Override
    protected BoundingBox computeBoundingBox() {
        return box;
    }

    /**
     * Ray-geometry helper: if leaf, tests each primitive directly;
     * otherwise, recurses into children whose bounding box intersects the ray.
     *
     * @param ray the ray to intersect
     * @return list of Intersection records or null if none
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        // Leaf node: test all contained primitives
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

        // Internal node: test children whose AABB intersects the ray
        List<Intersection> result = null;

        BoundingBox lb = left.getBoundingBox();
        if (lb != null && lb.intersects(ray)) {
            List<Intersection> leftHits = left.calculateIntersectionsHelper(ray);
            if (leftHits != null) {
                result = new ArrayList<>(leftHits);
            }
        }

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
     * Builds a BVH over a flat list of primitives. Splits along the longest axis
     * at the median until each leaf has at most MAX_LEAF primitives.
     *
     * @param prims flat list of primitives to accelerate
     * @return root of the constructed BVH
     */
    public static BVHNode build(List<Intersectable> prims) {
        if (prims.size() <= MAX_LEAF) {
            return new BVHNode(prims);
        }

        // Determine longest axis of the bounding box
        BoundingBox full = BoundingBox.unionOf(prims);
        double dx = full.max.getX() - full.min.getX();
        double dy = full.max.getY() - full.min.getY();
        double dz = full.max.getZ() - full.min.getZ();
        int axis = dx > dy && dx > dz ? 0 : (dy > dz ? 1 : 2);

        // Sort by centroid along selected axis
        prims.sort(Comparator.comparingDouble(o -> {
            BoundingBox b = o.getBoundingBox();
            return axis == 0
                    ? (b.min.getX() + b.max.getX()) * 0.5
                    : axis == 1
                    ? (b.min.getY() + b.max.getY()) * 0.5
                    : (b.min.getZ() + b.max.getZ()) * 0.5;
        }));

        // Split at median
        int mid = prims.size() / 2;
        List<Intersectable> leftList  = new ArrayList<>(prims.subList(0, mid));
        List<Intersectable> rightList = new ArrayList<>(prims.subList(mid, prims.size()));

        return new BVHNode(build(leftList), build(rightList));
    }
}
