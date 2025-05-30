package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * Sphere class represents a sphere in 3D space.
 */
public class Sphere extends RadialGeometry {
    private final Point center;

    public Sphere(Point center, double radius) {
        super(radius);
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        if (center == null) {
            throw new IllegalArgumentException("Center point cannot be null");
        }
        this.center = center;
    }

    public Point getCenter() {
        return center;
    }

    @Override
    public Vector getNormal(Point point) {
        double dist = point.distance(center);
        double diff = Math.abs(dist - radius);
        // allow very small floating-point error up to 1e-6
        final double EPS = 1e-6;
        if (diff > EPS) {
            throw new IllegalArgumentException(
                    String.format("Point %s is not on the sphere surface (|dist−r|=%.3e > %.3e)",
                            point, diff, EPS));
        }
        return point.subtract(center).normalize();
    }


    @Override
    public String toString() {
        return "Sphere{" + center + ", r=" + radius + "}";
    }

    /**
     * NVI helper: calculate all intersections of the ray with the sphere,
     * returning the new Intersection(type,point) objects.
     */
    @Override
    protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        // vector from ray origin to sphere center
        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts at the center → one intersection at t = radius
            return List.of(new Intersectable.Intersection(this, ray.getPoint(radius)));
        }

        double tm = alignZero(v.dotProduct(u));
        double d2 = alignZero(u.lengthSquared() - tm * tm);
        double r2 = radius * radius;
        if (d2 >= r2) {
            return null;
        }

        double th = alignZero(Math.sqrt(r2 - d2));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        var intersections = new LinkedList<Intersectable.Intersection>();
        if (t1 > 0) {
            Point p1  = ray.getPoint(t1);
            Vector n1 = p1.subtract(center).normalize();  // sphere normal
            intersections.add(
                    new Intersectable.Intersection(
                            this,            // the geometry
                            p1,              // intersection point
                            getMaterial(),   // the material at that point
                            ray,             // the incoming ray
                            n1,              // the normal
                            null             // light-source (filled later)
                    )
            );
        }
        if (t2 > 0) {
            Point p2  = ray.getPoint(t2);
            Vector n2 = p2.subtract(center).normalize();
            intersections.add(
                    new Intersectable.Intersection(
                            this, p2, getMaterial(), ray, n2, null
                    )
            );
        }


        return intersections.isEmpty() ? null : intersections;
    }
}
