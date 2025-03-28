package geometries;

import primitives.*;

/**
 * The {@code Cylinder} class represents a finite cylinder in 3D space defined by a radius, a central axis (as a {@link Ray}),
 * and a finite height. It extends the {@link Tube} class by adding a height property.
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder.
     */
    private final double height;

    /**
     * Constructs a {@code Cylinder} with the specified radius, axis ray, and height.
     *
     * @param radius  the radius of the cylinder
     * @param axisRay the central axis of the cylinder
     * @param height  the height of the cylinder
     */
    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        if (axisRay == null) {
            throw new IllegalArgumentException("Axis ray cannot be null");
        }
        this.height = height;
    }


    /**
     * Returns the height of the cylinder.
     *
     * @return the height value
     */
    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Cylinder{" + getAxisRay() + ", r=" + getRadius() + ", h=" + height + "}";
    }


}
