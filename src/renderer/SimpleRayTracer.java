package renderer;

import primitives.Color;
import primitives.Ray;
import primitives.Point;
import scene.Scene;
import geometries.Intersectable.GeoPoint;
import geometries.Intersectable.Intersection;
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
     * Uses calculateIntersections and findClosestIntersection.
     * @param ray the ray to trace
     * @return the computed color
     */
    @Override
    public Color traceRay(Ray ray) {
        // Calculate all intersections using the new method
        List<Intersection> intersections = calculateIntersections(ray);

        // No hit: return background color
        if (intersections == null || intersections.isEmpty()) {
            return scene.getBackground();
        }

        // Find the closest intersection to the ray origin
        Intersection closestIntersection = findClosestIntersection(ray.getP0(), intersections);

        // Compute shading color using the updated calcColor method
        return calcColor(closestIntersection);
    }

    /**
     * Calculates all intersections of the ray with the scene's geometries.
     * This method delegates to the scene geometries intersection function.
     * @param ray the ray to check intersections for
     * @return list of all intersections or null if none
     */
    private List<Intersection> calculateIntersections(Ray ray) {
        return scene.getGeometries().findIntersections(ray);
    }

    /**
     * Finds the closest intersection to a given origin point.
     * @param origin the starting point to measure distances from
     * @param intersections list of all intersections
     * @return the closest intersection
     */
    private Intersection findClosestIntersection(Point origin, List<Intersection> intersections) {
        Intersection closest = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Intersection intersection : intersections) {
            double distance = origin.distance(intersection.point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = intersection;
            }
        }
        return closest;
    }

    /**
     * Calculates the color at the given intersection.
     * Adds a turquoise (teal) glowing effect to the color.
     * @param intersection the intersection containing geometry and point info
     * @return resulting color with ambient light and glow added
     */
    private Color calcColor(Intersection intersection) {
        // Get ambient light color
        Color ambient = scene.getAmbientLight().getIntensity();

        // Define a turquoise (glowing) color to add to the point color
        Color turquoiseGlow = new Color(0, 255, 255); // RGB for cyan/turquoise glow

        // Combine ambient and glow colors
        return ambient.add(turquoiseGlow);
    }
}
