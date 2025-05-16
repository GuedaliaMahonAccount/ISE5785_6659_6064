package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import java.util.Collections;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import geometries.Intersectable.GeoPoint;

/**
 * The {@code Plane} class represents a plane in 3D space.
 * It is defined either by three points or by a point and a normal vector.
 */
public class Plane extends Geometry {
    private final Point q0;
    private final Vector normal;

    public Plane(Point p1, Point p2, Point p3) {
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        normal = v1.crossProduct(v2).normalize();
        q0 = p1;
    }

    public Plane(Point q0, Vector normal) {
        this.q0 = q0;
        this.normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    public Point getQ0() {
        return q0;
    }

    public Vector getNormal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane{" + q0 + ", " + normal + "}";
    }

    // New helper method as per instructions - returns list of Intersections with 'this' geometry
    public List<Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        double nv = alignZero(normal.dotProduct(v));
        if (isZero(nv)) return null; // ray parallel to plane

        Vector q0MinusP0;
        try {
            q0MinusP0 = q0.subtract(p0);
        } catch (IllegalArgumentException e) {
            // ray starts at q0
            return null;
        }

        double t = alignZero(normal.dotProduct(q0MinusP0) / nv);
        if (t <= 0) return null;

        // Return new Intersection object list
        return List.of(new Intersection(this, ray.getPoint(t)));
    }

    // Override findIntersections to use new helper
    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        // Since interface expects GeoPoint, we convert from Intersection to GeoPoint
        List<Intersection> intersections = calculateIntersectionsHelper(ray);
        if (intersections == null) return null;

        // Convert List<Intersection> to List<GeoPoint>
        return intersections.stream()
                .map(i -> new GeoPoint(this, i.point))
                .toList();
    }
}
