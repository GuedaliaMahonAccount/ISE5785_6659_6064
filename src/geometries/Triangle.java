package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Triangle class represents a triangle in 3D space, defined as a Polygon with exactly 3 vertices.
 */
public class Triangle extends Polygon {

    /**
     * Constructor for a triangle with 3 vertices.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "Triangle{" + vertices + "}";
    }

    /**
     * Override the intersection calculation for triangle.
     * The intersection is calculated with the plane and then checked if the point lies within the triangle bounds.
     *
     * @param ray the ray to check intersections with
     * @return list of intersections (geometry + point) or null if no valid intersection
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        // Step 1: Intersect with the plane
        var planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p = planeIntersections.get(0).point;
        Point p0 = ray.getP0();
        Vector dir = ray.getDir();

        // Triangle vertices
        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

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

        // If point is inside the triangle, return intersection
        return (allPositive || allNegative) ?
                List.of(new Intersection(this, p)) :
                null;
    }
}
