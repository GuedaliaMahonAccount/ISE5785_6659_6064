// Cylinder.java
package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.LinkedList;
import java.util.List;
import geometries.Intersectable.GeoPoint;

/**
 * The {@code Cylinder} class represents a finite cylinder with caps.
 */
public class Cylinder extends Tube {
    private final double height;

    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        if (height <= 0)
            throw new IllegalArgumentException("Height must be positive");
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        Vector axisDir = getAxisRay().getDir();
        Point baseCenter = getAxisRay().getP0();
        Point topCenter = baseCenter.add(axisDir.scale(height));
        double radiusSq = getRadius() * getRadius();
        List<GeoPoint> result = new LinkedList<>();

        // Compute the projection of the ray's origin on the cylinder's axis.
        double originProj = axisDir.dotProduct(ray.getP0().subtract(baseCenter));

        // Determine if the ray is vertical (i.e. parallel to the cylinder's axis)
        boolean isVertical = Math.abs(Math.abs(ray.getDir().dotProduct(axisDir)) - 1) < 1e-10;

        if (isVertical) {
            // For vertical rays, we decide based on where the ray starts:
            double dot = ray.getDir().dotProduct(axisDir);
            if (originProj > height) { // Ray starts above the cylinder: only top cap is hit.
                Plane topPlane = new Plane(topCenter, axisDir);
                List<GeoPoint> topIntersections = topPlane.findIntersections(ray);
                if (topIntersections != null) {
                    for (GeoPoint gp : topIntersections) {
                        if (gp.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                            result.add(new GeoPoint(this, gp.point));
                        }
                    }
                }
            } else if (originProj < 0) { // Ray starts below the cylinder: both bottom and top caps are hit.
                Plane bottomPlane = new Plane(baseCenter, axisDir);
                List<GeoPoint> bottomIntersections = bottomPlane.findIntersections(ray);
                if (bottomIntersections != null) {
                    for (GeoPoint gp : bottomIntersections) {
                        if (gp.point.subtract(baseCenter).lengthSquared() <= radiusSq) {
                            result.add(new GeoPoint(this, gp.point));
                        }
                    }
                }
                Plane topPlane = new Plane(topCenter, axisDir);
                List<GeoPoint> topIntersections = topPlane.findIntersections(ray);
                if (topIntersections != null) {
                    for (GeoPoint gp : topIntersections) {
                        if (gp.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                            result.add(new GeoPoint(this, gp.point));
                        }
                    }
                }
            } else { // Ray starts inside the cylinder (between the caps)
                if (dot > 0) { // Moving upward: exit through the top cap.
                    Plane topPlane = new Plane(topCenter, axisDir);
                    List<GeoPoint> topIntersections = topPlane.findIntersections(ray);
                    if (topIntersections != null) {
                        for (GeoPoint gp : topIntersections) {
                            if (gp.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                                result.add(new GeoPoint(this, gp.point));
                            }
                        }
                    }
                } else if (dot < 0) { // Moving downward: exit through the bottom cap.
                    Plane bottomPlane = new Plane(baseCenter, axisDir);
                    List<GeoPoint> bottomIntersections = bottomPlane.findIntersections(ray);
                    if (bottomIntersections != null) {
                        for (GeoPoint gp : bottomIntersections) {
                            if (gp.point.subtract(baseCenter).lengthSquared() <= radiusSq) {
                                result.add(new GeoPoint(this, gp.point));
                            }
                        }
                    }
                }
            }
        } else {
            // Non-vertical rays:
            // 1. Get side intersections from the tube and filter by the cylinder's height.
            List<GeoPoint> sideIntersections = super.findIntersections(ray);
            if (sideIntersections != null) {
                for (GeoPoint gp : sideIntersections) {
                    double proj = axisDir.dotProduct(gp.point.subtract(baseCenter));
                    if (proj >= 0 && proj <= height) {
                        result.add(new GeoPoint(this, gp.point));
                    }
                }
            }
            // 2. Also compute intersections with both caps.
            Plane bottomPlane = new Plane(baseCenter, axisDir);
            List<GeoPoint> bottomIntersections = bottomPlane.findIntersections(ray);
            if (bottomIntersections != null) {
                for (GeoPoint gp : bottomIntersections) {
                    if (gp.point.subtract(baseCenter).lengthSquared() <= radiusSq) {
                        result.add(new GeoPoint(this, gp.point));
                    }
                }
            }
            Plane topPlane = new Plane(topCenter, axisDir);
            List<GeoPoint> topIntersections = topPlane.findIntersections(ray);
            if (topIntersections != null) {
                for (GeoPoint gp : topIntersections) {
                    if (gp.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                        result.add(new GeoPoint(this, gp.point));
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
