package renderer;

import primitives.Color;
import primitives.Ray;
import primitives.Point;
import scene.Scene;
import geometries.Intersectable.GeoPoint;
import java.util.List;

/**
 * A simple ray tracer that returns ambient light or background color.
 * Implements basic traceRay with intersection detection and ambient shading.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a SimpleRayTracer for the given scene.
     * @param scene the scene to be traced
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray through the scene, returns background if no hit,
     * otherwise finds closest intersection and computes its color.
     * @param ray the ray to trace
     * @return the computed color
     */
    @Override
    public Color traceRay(Ray ray) {
        // Find all geometry intersections along the ray
        List<GeoPoint> intersections = scene.getGeometries().findIntersections(ray);

        // No hit: return background color
        if (intersections == null || intersections.isEmpty()) {
            return scene.getBackground();
        }

        // Find closest intersection to ray origin
        GeoPoint closest = findClosestGeoPoint(ray.getP0(), intersections);

        // Compute shading color (ambient for now)
        return calcColor(closest);
    }

    /**
     * Finds the GeoPoint closest to the given origin.
     * @param origin the point from which distances are measured
     * @param points list of intersection points
     * @return the closest GeoPoint
     */
    private GeoPoint findClosestGeoPoint(Point origin, List<GeoPoint> points) {
        GeoPoint closest = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (GeoPoint gp : points) {
            double dist = origin.distance(gp.point);
            if (dist < minDist) {
                minDist = dist;
                closest = gp;
            }
        }
        return closest;
    }

    /**
     * Calculates the color at a given intersection point.
     * Currently returns only ambient light intensity.
     * @param geoPoint the intersection to shade
     * @return the resulting color
     */
    private Color calcColor(GeoPoint geoPoint) {
        return scene.getAmbientLight().getIntensity();
    }
}
