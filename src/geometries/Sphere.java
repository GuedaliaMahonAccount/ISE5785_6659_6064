package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;

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


    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray origin is at the center
            return List.of(ray.getPoint(radius));
        }

        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);
        double rSquared = radius * radius;

        if (dSquared >= rSquared) return null;

        double th = alignZero(Math.sqrt(rSquared - dSquared));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 > 0 && t2 > 0)
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        if (t1 > 0)
            return List.of(ray.getPoint(t1));
        if (t2 > 0)
            return List.of(ray.getPoint(t2));

        return null;
    }

}
