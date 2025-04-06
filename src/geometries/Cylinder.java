package geometries;

import primitives.*;
import java.util.LinkedList;
import java.util.List;

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
    public List<Point> findIntersections(Ray ray) {
        Vector axisDir = getAxisRay().getDir();
        Point baseCenter = getAxisRay().getP0();
        Point topCenter = baseCenter.add(axisDir.scale(height));
        double radiusSq = getRadius() * getRadius();
        List<Point> result = new LinkedList<>();

        // Compute the projection of the ray's origin on the cylinder's axis.
        double originProj = axisDir.dotProduct(ray.getP0().subtract(baseCenter));

        // Determine if the ray is vertical (i.e. parallel to the cylinder’s axis)
        boolean isVertical = Math.abs(Math.abs(ray.getDir().dotProduct(axisDir)) - 1) < 1e-10;

        if (isVertical) {
            // For vertical rays, we decide based on where the ray starts:
            double dot = ray.getDir().dotProduct(axisDir);
            if (originProj > height) { // Ray starts above the cylinder: only top cap is hit.
                Plane topPlane = new Plane(topCenter, axisDir);
                List<Point> topIntersections = topPlane.findIntersections(ray);
                if (topIntersections != null) {
                    for (Point p : topIntersections) {
                        if (p.subtract(topCenter).lengthSquared() <= radiusSq) {
                            result.add(p);
                        }
                    }
                }
            } else if (originProj < 0) { // Ray starts below the cylinder: both bottom and top caps are hit.
                Plane bottomPlane = new Plane(baseCenter, axisDir);
                List<Point> bottomIntersections = bottomPlane.findIntersections(ray);
                if (bottomIntersections != null) {
                    for (Point p : bottomIntersections) {
                        if (p.subtract(baseCenter).lengthSquared() <= radiusSq) {
                            result.add(p);
                        }
                    }
                }
                Plane topPlane = new Plane(topCenter, axisDir);
                List<Point> topIntersections = topPlane.findIntersections(ray);
                if (topIntersections != null) {
                    for (Point p : topIntersections) {
                        if (p.subtract(topCenter).lengthSquared() <= radiusSq) {
                            result.add(p);
                        }
                    }
                }
            } else { // Ray starts inside the cylinder (between the caps)
                if (dot > 0) { // Moving upward: exit through the top cap.
                    Plane topPlane = new Plane(topCenter, axisDir);
                    List<Point> topIntersections = topPlane.findIntersections(ray);
                    if (topIntersections != null) {
                        for (Point p : topIntersections) {
                            if (p.subtract(topCenter).lengthSquared() <= radiusSq) {
                                result.add(p);
                            }
                        }
                    }
                } else if (dot < 0) { // Moving downward: exit through the bottom cap.
                    Plane bottomPlane = new Plane(baseCenter, axisDir);
                    List<Point> bottomIntersections = bottomPlane.findIntersections(ray);
                    if (bottomIntersections != null) {
                        for (Point p : bottomIntersections) {
                            if (p.subtract(baseCenter).lengthSquared() <= radiusSq) {
                                result.add(p);
                            }
                        }
                    }
                }
            }
        } else {
            // Non-vertical rays:
            // 1. Get side intersections from the tube and filter by the cylinder's height.
            List<Point> sideIntersections = super.findIntersections(ray);
            if (sideIntersections != null) {
                for (Point p : sideIntersections) {
                    double proj = axisDir.dotProduct(p.subtract(baseCenter));
                    if (proj >= 0 && proj <= height) {
                        result.add(p);
                    }
                }
            }
            // 2. Also compute intersections with both caps.
            Plane bottomPlane = new Plane(baseCenter, axisDir);
            List<Point> bottomIntersections = bottomPlane.findIntersections(ray);
            if (bottomIntersections != null) {
                for (Point p : bottomIntersections) {
                    if (p.subtract(baseCenter).lengthSquared() <= radiusSq) {
                        result.add(p);
                    }
                }
            }
            Plane topPlane = new Plane(topCenter, axisDir);
            List<Point> topIntersections = topPlane.findIntersections(ray);
            if (topIntersections != null) {
                for (Point p : topIntersections) {
                    if (p.subtract(topCenter).lengthSquared() <= radiusSq) {
                        result.add(p);
                    }
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    @Override
    public String toString() {
        return "Cylinder{" + getAxisRay() + ", r=" + getRadius() + ", h=" + height + "}";
    }
}
