package geometries;
import primitives.*;
import java.util.List;


public interface Intersectable {
    /**
     * Finds intersection points with a given ray.
     * @param ray the ray to intersect with
     * @return list of intersection points, or null if no intersections
     */
    List<Point> findIntersections(Ray ray);
}
