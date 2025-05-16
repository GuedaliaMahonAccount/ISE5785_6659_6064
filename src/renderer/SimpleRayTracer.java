package renderer;

import geometries.Geometry;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Ray;
import primitives.Point;
import primitives.Vector;
import scene.Scene;
import lighting.LightSource;
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
        return calcColor(closestIntersection, ray);
    }

    /**
     * Preprocesses intersection information by initializing ray direction, normal, and dot product.
     * @param intersection the intersection to preprocess
     * @param rayDirection the direction of the ray that caused the intersection
     * @return true if the dot product between normal and ray direction is not zero, false otherwise
     */
    private boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
        // Cache ray direction
        // Assume the intersection has normal and geometry already set
        Vector normal = intersection.geometry.getNormal(intersection.point);

        // Save dot product of ray direction and normal
        double dotProduct = normal.dotProduct(rayDirection);

        // If dot product is zero, there's no light contribution
        if (dotProduct == 0) {
            return false;
        }

        return true;
    }

    /**
     * Sets up light source information for an intersection.
     * @param intersection the intersection to update
     * @param lightSource the light source to set
     * @return true if the light contributes to the intersection, false otherwise
     */
    private boolean setLightSource(Intersection intersection, LightSource lightSource) {
        // Get direction from light source to intersection point
        Vector l = lightSource.getL(intersection.point);

        // Calculate dot product between light direction and normal
        Vector normal = intersection.normal;
        double ln = l.dotProduct(normal);

        // If both dot products are zero, light doesn't contribute
        if (ln == 0) {
            return false;
        }

        return true;
    }

    /**
     * Calculates diffuse reflection component.
     * @param intersection the intersection information
     * @return the diffuse component as a Double3
     */
    private Double3 calcDiffusive(Intersection intersection) {
        // Get the material of the geometry
        Material material = intersection.material;

        // Get direction from light source to intersection point
        Vector l = intersection.lightSource.getL(intersection.point);

        // Calculate dot product between light direction and normal
        Vector normal = intersection.normal;
        double ln = Math.abs(l.dotProduct(normal));

        // Calculate diffuse component (kD * |l·n|)
        return material.getKD().scale(ln);
    }

    /**
     * Calculates specular reflection component.
     * @param intersection the intersection information
     * @return the specular component as a Double3
     */
    private Double3 calcSpecular(Intersection intersection) {
        // Get the material of the geometry
        Material material = intersection.material;

        // Get direction from light source to intersection point
        Vector l = intersection.lightSource.getL(intersection.point);

        // Calculate reflection vector r = l - 2(l·n)n
        Vector normal = intersection.normal;
        double ln = l.dotProduct(normal);
        Vector r = l.subtract(normal.scale(2 * ln));

        // Calculate dot product between reflection vector and view vector (-v)
        Vector v = intersection.ray.getDir();
        double minusVR = -v.dotProduct(r);

        // If the angle is obtuse, no specular reflection
        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        // Calculate specular component (kS * (max(0, -v·r))^nsh)
        double pow = Math.pow(minusVR, material.getShininess());
        return material.getKS().scale(pow);
    }

    /**
     * Calculates local lighting effects at an intersection.
     * @param intersection the intersection information
     * @return the color resulting from local lighting effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        // Start with emission color
        Color color = intersection.geometry.getEmission();

        // Get material properties
        Material material = intersection.material;

        // For each light source in the scene
        for (LightSource lightSource : scene.getLights()) {
            // Set up light source information
            if (!setLightSource(intersection, lightSource)) {
                continue; // Skip this light source if it doesn't contribute
            }

            // Get light intensity at the intersection point
            Color lightIntensity = lightSource.getIntensity(intersection.point);

            // Calculate diffuse and specular components
            Double3 diffuse = calcDiffusive(intersection);
            Double3 specular = calcSpecular(intersection);

            // Combine diffuse and specular components
            Double3 combined = diffuse.add(specular);

            // Add light contribution to the color
            color = color.add(lightIntensity.scale(combined));
        }

        return color;
    }

    /**
     * Calculates the color at an intersection point.
     * @param intersection the intersection information
     * @param ray the ray that caused the intersection
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        // Preprocess intersection information
        if (!preprocessIntersection(intersection, ray.getDir())) {
            return Color.BLACK;
        }

        // Get ambient light contribution
        Geometry geometry = intersection.geometry;
        Material material = geometry.getMaterial();
        Color ambient = scene.getAmbientLight().getIntensity().scale(material.getKA());

        // Add local lighting effects
        Color localEffects = calcColorLocalEffects(intersection);

        // Combine ambient and local effects
        return ambient.add(localEffects);
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
}