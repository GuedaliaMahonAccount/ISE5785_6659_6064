package geometries;

import primitives.*;

/**
 * Tube class represents a cylinder without caps (open-ended).
 */
public class Tube extends RadialGeometry {
    private final Ray axisRay;

    public Tube(double radius, Ray axisRay) {
        super(radius);
        this.axisRay = axisRay;
    }

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
}
