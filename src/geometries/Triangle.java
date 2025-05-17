package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import geometries.Intersectable.Intersection;
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
     * @return list of intersections (geometry + point + full intersection data) or null if no valid intersection
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        // Intersect with the underlying plane
        List<Intersection> planeIntersections = plane.calculateIntersections(ray);
        if (planeIntersections == null || planeIntersections.isEmpty()) {
            return null;
        }

        // The intersection point on the plane
        Intersection planeHit = planeIntersections.get(0);
        Point p0 = ray.getP0();
        Vector dir = ray.getDir();
        Point  p  = planeHit.point;

        // Triangle vertices
        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        // Vectors from ray origin to triangle vertices
        Vector u = v1.subtract(p0);
        Vector v = v2.subtract(p0);
        Vector w = v3.subtract(p0);

        // Normals of sub-triangles
        Vector n1 = u.crossProduct(v).normalize();
        Vector n2 = v.crossProduct(w).normalize();
        Vector n3 = w.crossProduct(u).normalize();

        // Signs of dot products between ray direction and these normals
        double s1 = alignZero(dir.dotProduct(n1));
        double s2 = alignZero(dir.dotProduct(n2));
        double s3 = alignZero(dir.dotProduct(n3));

        boolean allPositive = s1 > 0 && s2 > 0 && s3 > 0;
        boolean allNegative = s1 < 0 && s2 < 0 && s3 < 0;

        // If point is inside the triangle (all signs same), produce a full Intersection
        if (allPositive || allNegative) {
            // Compute the triangle's normal at p
            Vector normal = getNormal(p);
            // Build an Intersection with geometry, point, material, ray, normal, no light yet
            Intersection hit = new Intersection(
                    this,             // the triangle geometry
                    p,                // intersection point
                    getMaterial(),    // the triangle's material
                    ray,              // the incoming ray
                    normal,           // the surface normal
                    null              // lightSource will be set later by the tracer
            );
            return List.of(hit);
        }

        // Outside the triangle bounds
        return null;
    }
}
