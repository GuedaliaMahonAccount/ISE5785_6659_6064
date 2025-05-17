package geometries;


import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

/**
 * A finite cylinder: a Tube of given radius and finite height, with two circular caps.
 */
public class Cylinder extends Tube {
    private final double height;

    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    @Override
    protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
        Vector axisDir   = getAxisRay().getDir();
        Point  baseCenter = getAxisRay().getP0();
        Point  topCenter  = baseCenter.add(axisDir.scale(height));
        double radiusSq   = getRadius() * getRadius();

        List<Intersectable.Intersection> result = new LinkedList<>();

        // Projection of ray origin on cylinder axis
        double originProj = axisDir.dotProduct(ray.getP0().subtract(baseCenter));

        // Check if ray is parallel to axis
        boolean isVertical = Math.abs(Math.abs(ray.getDir().dotProduct(axisDir)) - 1) < 1e-10;

        // Prepare cap-planes once
        Plane bottomPlane = new Plane(baseCenter, axisDir);
        Plane topPlane    = new Plane(topCenter, axisDir);

        if (isVertical) {
            double dirProj = ray.getDir().dotProduct(axisDir);

            if (originProj > height) {
                // Starts above → only top cap
                var topHits = topPlane.calculateIntersectionsHelper(ray);
                if (topHits != null) {
                    for (var h : topHits) {
                        if (h.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                            result.add(new Intersectable.Intersection(this, h.point));
                        }
                    }
                }
            }
            else if (originProj < 0) {
                // Starts below → bottom then top
                var botHits = bottomPlane.calculateIntersectionsHelper(ray);
                if (botHits != null) {
                    for (var h : botHits) {
                        if (h.point.subtract(baseCenter).lengthSquared() <= radiusSq) {
                            result.add(new Intersectable.Intersection(this, h.point));
                        }
                    }
                }
                var topHits = topPlane.calculateIntersectionsHelper(ray);
                if (topHits != null) {
                    for (var h : topHits) {
                        if (h.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                            result.add(new Intersectable.Intersection(this, h.point));
                        }
                    }
                }
            }
            else {
                // Starts inside → exit through one cap
                if (dirProj > 0) {
                    var topHits = topPlane.calculateIntersectionsHelper(ray);
                    if (topHits != null) {
                        for (var h : topHits) {
                            if (h.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                                result.add(new Intersectable.Intersection(this, h.point));
                            }
                        }
                    }
                }
                else if (dirProj < 0) {
                    var botHits = bottomPlane.calculateIntersectionsHelper(ray);
                    if (botHits != null) {
                        for (var h : botHits) {
                            if (h.point.subtract(baseCenter).lengthSquared() <= radiusSq) {
                                result.add(new Intersectable.Intersection(this, h.point));
                            }
                        }
                    }
                }
            }
        }
        else {
            // 1. Side-intersections
            var sideHits = super.calculateIntersectionsHelper(ray);
            if (sideHits != null) {
                for (var h : sideHits) {
                    double proj = axisDir.dotProduct(h.point.subtract(baseCenter));
                    if (proj >= 0 && proj <= height) {
                        result.add(new Intersectable.Intersection(this, h.point));
                    }
                }
            }
            // 2. Bottom cap
            var botHits = bottomPlane.calculateIntersectionsHelper(ray);
            if (botHits != null) {
                for (var h : botHits) {
                    if (h.point.subtract(baseCenter).lengthSquared() <= radiusSq) {
                        result.add(new Intersectable.Intersection(this, h.point));
                    }
                }
            }
            // 3. Top cap
            var topHits = topPlane.calculateIntersectionsHelper(ray);
            if (topHits != null) {
                for (var h : topHits) {
                    if (h.point.subtract(topCenter).lengthSquared() <= radiusSq) {
                        result.add(new Intersectable.Intersection(this, h.point));
                    }
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    @Override
    public String toString() {
        return "Cylinder{" +
                "axis=" + getAxisRay() +
                ", radius=" + getRadius() +
                ", height=" + height +
                '}';
    }
}
