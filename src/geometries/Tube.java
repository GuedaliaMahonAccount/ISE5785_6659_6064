package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.isZero;
import geometries.Intersectable.GeoPoint;

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
    protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();

        Point pa = axisRay.getP0();
        Vector va = axisRay.getDir();

        Vector deltaP = p0.subtract(pa);

        // Check if ray is parallel to tube axis
        double vDotVa = v.dotProduct(va);
        if (Math.abs(Math.abs(vDotVa) - 1) < 1e-10) {
            Vector cross = deltaP.crossProduct(va);
            double distanceSquared = cross.lengthSquared() / va.lengthSquared();

            if (Math.abs(distanceSquared - radius * radius) < 1e-10 || distanceSquared > radius * radius) {
                return null;
            }
            return null;
        }

        // Component of deltaP perpendicular to va
        Vector deltaPPerp = deltaP;
        double deltaPDotVa = deltaP.dotProduct(va);
        if (!isZero(deltaPDotVa)) {
            deltaPPerp = deltaP.subtract(va.scale(deltaPDotVa));
        }

        // Component of v perpendicular to va
        Vector vPerp = v;
        if (!isZero(vDotVa)) {
            vPerp = v.subtract(va.scale(vDotVa));
        }

        // Quadratic equation coefficients
        double a = vPerp.lengthSquared();
        double b = 2 * vPerp.dotProduct(deltaPPerp);
        double c = deltaPPerp.lengthSquared() - radius * radius;

        if (isZero(a)) return null;

        double discriminant = b * b - 4 * a * c;
        if (discriminant <= 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDisc) / (2 * a);
        double t2 = (-b + sqrtDisc) / (2 * a);

        List<Intersectable.Intersection> result = new LinkedList<>();

        if (t1 > 0) {
            result.add(new Intersectable.Intersection(this, ray.getPoint(t1)));
        }
        if (t2 > 0) {
            result.add(new Intersectable.Intersection(this, ray.getPoint(t2)));
        }

        return result.isEmpty() ? null : result;
    }


}
