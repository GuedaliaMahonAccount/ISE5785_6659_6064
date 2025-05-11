package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.alignZero;
import geometries.Intersectable.GeoPoint;

/**
 * The {@code Sphere} class represents a sphere in 3D space.
 * It is defined by a center point and a radius.
 */
public class Sphere extends RadialGeometry {
    /** The center point of the sphere. */
    private final Point center;

    /**
     * Constructs a sphere with a given radius and center.
     *
     * @param radius the radius of the sphere
     * @param center the center point of the sphere
     */
    public Sphere(double radius, Point center) {
        super(radius);
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        if (center == null) {
            throw new IllegalArgumentException("Center point cannot be null");
        }
        this.center = center;
    }

    /**
     * Returns the center point of the sphere.
     *
     * @return the center
     */
    public Point getCenter() {
        return center;
    }

    @Override
    public Vector getNormal(Point point) {
        double distance = point.distance(center);
        if (Math.abs(distance - radius) > 1e-10) {
            throw new IllegalArgumentException("Point is not on the surface of the sphere");
        }
        return point.subtract(center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere{" + center + ", r=" + radius + "}";
    }

    /**
     * Finds all intersection GeoPoints of the ray with this sphere.
     *
     * @param ray the ray to intersect with
     * @return list of GeoPoints (geometry + point), or null if none
     */
    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        // u = vector from ray origin to sphere center
        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts at the center → single intersection at radius
            return List.of(new GeoPoint(this, ray.getPoint(radius)));
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

        // build GeoPoint list depending on positive ts
        if (t1 > 0 && t2 > 0) {
            return List.of(
                    new GeoPoint(this, ray.getPoint(t1)),
                    new GeoPoint(this, ray.getPoint(t2))
            );
        }
        if (t1 > 0) {
            return List.of(new GeoPoint(this, ray.getPoint(t1)));
        }
        if (t2 > 0) {
            return List.of(new GeoPoint(this, ray.getPoint(t2)));
        }
        return null;
    }
}
