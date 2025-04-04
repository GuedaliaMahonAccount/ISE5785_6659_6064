package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * The {@code Tube} class represents an infinite cylinder (without caps).
 * It is defined by a central axis (as a {@link Ray}) and a radius.
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis of the tube.
     */
    private final Ray axisRay;

    /**
     * Constructs a tube with a given radius and axis ray.
     *
     * @param radius  the radius of the tube
     * @param axisRay the central axis of the tube
     */
    public Tube(double radius, Ray axisRay) {
        super(radius);
        if (radius <= 0) {
            throw new IllegalArgumentException("Tube radius must be greater than 0");
        }
        if (axisRay == null) {
            throw new IllegalArgumentException("Tube axisRay cannot be null");
        }
        this.axisRay = axisRay;
    }


    /**
     * Returns the central axis ray of the tube.
     *
     * @return the axis ray
     */
    public Ray getAxisRay() {
        return axisRay;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = axisRay.getP0();
        Vector dir = axisRay.getDir();
        double t = point.subtract(p0).dotProduct(dir);
        Point o = p0.add(dir.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    public String toString() {
        return "Tube{" + axisRay + ", r=" + radius + "}";
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        Point pa = axisRay.getP0();
        Vector va = axisRay.getDir();

        // First, check if ray is parallel to axis
        double vDotVa = v.dotProduct(va);

        // Check if v and va are parallel (their directions are either the same or opposite)
        double vLength = v.length();
        double vaLength = va.length();
        boolean isParallel = Math.abs(Math.abs(vDotVa) - vLength * vaLength) < 1e-10;

        if (isParallel) {
            // Ray is parallel to tube axis - calculate distance from ray to axis
            Vector vac = p0.subtract(pa); // Vector from axis point to ray point
            double distanceSquared = vac.lengthSquared() - Math.pow(vac.dotProduct(va) / vaLength, 2);

            // If distance equals radius, ray is tangent (no intersection points)
            // If distance is less than radius, ray is inside tube (no intersections with tube's sides)
            // If distance is greater than radius, ray misses tube (no intersections)
            return null;
        }

        // Ray is not parallel to axis - proceed with normal intersection calculation
        Vector deltaP = p0.subtract(pa);

        // Calculate vVa = v - va * (v · va) / (va · va)
        // Normalize the scale factor to prevent zero vector
        double vaLengthSquared = va.lengthSquared();
        Vector vVa = v.subtract(va.scale(vDotVa / vaLengthSquared));

        // Calculate dpVa = deltaP - va * (deltaP · va) / (va · va)
        double dpVaDot = deltaP.dotProduct(va);
        Vector dpVa = deltaP.subtract(va.scale(dpVaDot / vaLengthSquared));

        // Calculate quadratic coefficients
        double a = vVa.lengthSquared();
        double b = 2 * vVa.dotProduct(dpVa);
        double c = dpVa.lengthSquared() - radius * radius;

        double discriminant = b * b - 4 * a * c;

        if (discriminant <= 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDisc) / (2 * a);
        double t2 = (-b + sqrtDisc) / (2 * a);

        if (t1 > 0 && t2 > 0)
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        if (t1 > 0)
            return List.of(ray.getPoint(t1));
        if (t2 > 0)
            return List.of(ray.getPoint(t2));

        return null;
    }
}
