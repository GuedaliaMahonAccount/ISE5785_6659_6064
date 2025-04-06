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
        // Step 1: Use the plane's intersection
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p = planeIntersections.getFirst(); // intersection point

        // Triangle vertices
        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        Vector dir = ray.getDir();
        Point p0 = ray.getP0();

        // Vectors from p0 to triangle vertices
        Vector u = v1.subtract(p0);
        Vector v = v2.subtract(p0);
        Vector w = v3.subtract(p0);

        // Normals of sub-triangles
        Vector n1 = u.crossProduct(v).normalize();
        Vector n2 = v.crossProduct(w).normalize();
        Vector n3 = w.crossProduct(u).normalize();

        // Check the direction signs
        double s1 = alignZero(dir.dotProduct(n1));
        double s2 = alignZero(dir.dotProduct(n2));
        double s3 = alignZero(dir.dotProduct(n3));

        boolean allPositive = s1 > 0 && s2 > 0 && s3 > 0;
        boolean allNegative = s1 < 0 && s2 < 0 && s3 < 0;

        return (allPositive || allNegative) ? List.of(p) : null;
    }

}
