package renderer;

import geometries.Geometry;
import primitives.Color;
import primitives.Ray;
import primitives.Point;
import scene.Scene;
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
     * @param ray the ray to trace
     * @return the computed color
     */
    @Override
    public Color traceRay(Ray ray) {
        // Now call calculateIntersections (returns List<Intersection>)
        List<Intersection> intersections = scene.getGeometries().calculateIntersections(ray);

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
     * Calculates the color at the intersection point.
     * This method combines the ambient light and the emission color,
     * and multiplies the ambient light by the material's kA factor.
     *
     * @param intersection the intersection information
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection) {
        Geometry geometry = (Geometry) intersection.geometry;

        // Get ambient light and scale it by material's kA
        Color ambient = scene.getAmbientLight().getIntensity()
                .scale(geometry.getMaterial().kA);

        // Get emission color
        Color emission = geometry.getEmission();

        // Combine ambient and emission
        return ambient.add(emission);
    }
}
