package renderer;

import geometries.Geometry;
import primitives.Color;
import primitives.Material;
import primitives.Ray;
import primitives.Point;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

/**
 * SimpleRayTracer is a basic ray tracer for calculating color at intersection points.
 * It supports ambient and emission shading for basic lighting effects.
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
     * Traces a ray through the scene to compute its color.
     * If the ray hits no objects, the background color is returned.
     * @param ray the ray to trace
     * @return the computed color
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.getGeometries().calculateIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return scene.getBackground();
        }
        Intersection closestIntersection = findClosestIntersection(ray.getP0(), intersections);
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
     * Calculates the color at an intersection point.
     * @param intersection the intersection information
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection) {
        Geometry geometry = (Geometry) intersection.geometry;
        Material material = geometry.getMaterial();
        Color ambient = scene.getAmbientLight().getIntensity().scale(material.getKA());
        Color emission = geometry.getEmission();
        return ambient.add(emission);
    }
}
