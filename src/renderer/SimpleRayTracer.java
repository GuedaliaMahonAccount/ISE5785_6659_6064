package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;
import geometries.Intersectable.GeoPoint;
import java.util.List;

/**
 * Simple ray tracer that returns ambient light if no intersections are found.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructor that initializes the ray tracer with a scene.
     * @param scene The scene to be traced.
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // Find all intersections (returns GeoPoints)
        List<GeoPoint> intersections = scene.getGeometries().findIntersections(ray);

        // No intersections, return background color
        if (intersections == null || intersections.isEmpty()) {
            return scene.getBackground();
        }

        // Find the closest intersection
        GeoPoint closest = findClosestGeoPoint(ray, intersections);

        // Compute and return the color at that point (currently just ambient)
        return calcColor(closest);
    }

    /**
     * Find the closest GeoPoint to the ray origin from a list of intersections.
     * @param ray The ray that intersected the objects.
     * @param geoPoints The list of intersection GeoPoints.
     * @return The closest GeoPoint.
     */
    private GeoPoint findClosestGeoPoint(Ray ray, List<GeoPoint> geoPoints) {
        GeoPoint closestPoint = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (GeoPoint gp : geoPoints) {
            double d = ray.getP0().distance(gp.point);
            if (d < minDist) {
                minDist = d;
                closestPoint = gp;
            }
        }
        return closestPoint;
    }

    /**
     * Calculate the color at a given point (only ambient light for now).
     * @param geoPoint The GeoPoint where the color is calculated.
     * @return The color at the point.
     */
    private Color calcColor(GeoPoint geoPoint) {
        return scene.getAmbientLight().getIntensity();
    }
}
