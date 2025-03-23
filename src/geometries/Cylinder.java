package geometries;

import primitives.*;

/**
 * Cylinder class represents a finite cylinder with height.
 */
public class Cylinder extends Tube {
    private final double height;

    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Cylinder{" + getAxisRay() + ", r=" + getRadius() + ", h=" + height + "}";
    }
}
