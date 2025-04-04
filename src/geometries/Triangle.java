package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * The {@code Triangle} class represents a triangle in 3D space.
 * It is defined as a special case of a {@link Polygon} with exactly 3 vertices.
 */
public class Triangle extends Polygon {
    /**
     * Constructs a triangle from three vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "Triangle{" + vertices + "}";
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        // Step 1: Intersect with the plane
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p0 = ray.getP0();
        Vector dir = ray.getDir();
        Point p = planeIntersections.getFirst(); // The potential intersection point

        Vector v1 = vertices.get(0).subtract(p0);
        Vector v2 = vertices.get(1).subtract(p0);
        Vector v3 = vertices.get(2).subtract(p0);

        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        double s1 = alignZero(dir.dotProduct(n1));
        double s2 = alignZero(dir.dotProduct(n2));
        double s3 = alignZero(dir.dotProduct(n3));

        // All signs must be the same (either all positive or all negative)
        boolean allPositive = s1 > 0 && s2 > 0 && s3 > 0;
        boolean allNegative = s1 < 0 && s2 < 0 && s3 < 0;

        if (allPositive || allNegative)
            return List.of(p); // Intersection inside triangle

        return null; // Intersection outside or on edge/vertex
    }

}
