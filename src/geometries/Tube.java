package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static primitives.Util.isZero;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code Tube} class represents an infinite cylinder (without caps).
 * It is defined by a central axis (as a {@link Ray}) and a radius.
 */
public class Tube extends RadialGeometry {
    /** The central axis of the tube. */
    private final Ray axisRay;

    /**
     * Constructs a tube with a given radius and axis ray.
     *
     * @param radius  the radius of the tube
     * @param axisRay the central axis of the tube (must not be null)
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

    /**
     * Compute the normal vector to the tube at the given point.
     * Handles the case t == 0 to avoid creating a zero vector.
     */
    @Override
    public Vector getNormal(Point point) {
        Point  p0  = axisRay.getP0();
        Vector dir = axisRay.getDir();
        // vector from axis origin to hit point
        Vector v   = point.subtract(p0);
        // projection length onto axis
        double t   = v.dotProduct(dir);
        // the closest point on the axis
        Point o    = p0;
        if (!isZero(t)) {
            o = p0.add(dir.scale(t));
        }
        // normal is the radial vector
        return point.subtract(o).normalize();
    }

    @Override
    public String toString() {
        return "Tube{" + axisRay + ", r=" + radius + "}";
    }

    /**
     * Compute a bounded AABB for culling (approximates the infinite extent).
     */
    @Override
    protected BoundingBox computeBoundingBox() {
        Point  origin = axisRay.getP0();
        Vector dir    = axisRay.getDir().normalize();
        double EXTENT = 1000.0;
        Point axisMin = origin.add(dir.scale(-EXTENT));
        Point axisMax = origin.add(dir.scale( EXTENT));
        double r = radius;

        double minX = Math.min(axisMin.getX(), axisMax.getX()) - r;
        double minY = Math.min(axisMin.getY(), axisMax.getY()) - r;
        double minZ = Math.min(axisMin.getZ(), axisMax.getZ()) - r;
        double maxX = Math.max(axisMin.getX(), axisMax.getX()) + r;
        double maxY = Math.max(axisMin.getY(), axisMax.getY()) + r;
        double maxZ = Math.max(axisMin.getZ(), axisMax.getZ()) + r;

        return new BoundingBox(
                new Point(minX, minY, minZ),
                new Point(maxX, maxY, maxZ)
        );
    }

    /**
     * Ray–tube intersection using the quadratic formula for the perpendicular components.
     */
    @Override
    protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.getP0();
        Vector v = ray.getDir();
        Point pa = axisRay.getP0();
        Vector va = axisRay.getDir();

        Vector deltaP = p0.subtract(pa);
        double vDotVa = v.dotProduct(va);
        // if parallel, no side intersections
        if (Math.abs(Math.abs(vDotVa) - 1) < 1e-10) {
            return null;
        }

        // perpendicular components
        Vector deltaPPerp = isZero(deltaP.dotProduct(va))
                ? deltaP
                : deltaP.subtract(va.scale(deltaP.dotProduct(va)));
        Vector vPerp      = isZero(vDotVa)
                ? v
                : v.subtract(va.scale(vDotVa));

        double a = vPerp.lengthSquared();
        if (isZero(a)) return null;
        double b = 2 * vPerp.dotProduct(deltaPPerp);
        double c = deltaPPerp.lengthSquared() - radius * radius;
        double disc = b*b - 4*a*c;
        if (disc <= 0) return null;

        double sqrtD = Math.sqrt(disc);
        double t1     = (-b - sqrtD)/(2*a);
        double t2     = (-b + sqrtD)/(2*a);

        List<Intersectable.Intersection> result = new LinkedList<>();
        if (t1 > 0) result.add(new Intersectable.Intersection(
                this, ray.getPoint(t1), getMaterial(), ray, getNormal(ray.getPoint(t1)), null
        ));
        if (t2 > 0) result.add(new Intersectable.Intersection(
                this, ray.getPoint(t2), getMaterial(), ray, getNormal(ray.getPoint(t2)), null
        ));
        return result.isEmpty() ? null : result;
    }
}