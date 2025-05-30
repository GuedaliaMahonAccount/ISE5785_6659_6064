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

import static primitives.Util.alignZero;

/**
 * SimpleRayTracer is a basic ray tracer for calculating color at intersection points.
 * It supports ambient and emission shading for basic lighting effects.
 */
public class SimpleRayTracer extends RayTracerBase {
    private static final double EPS = 0.1; // Shadow ray offset constant

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
     * Checks if a point is unshaded (not in shadow) from a light source.
     * @param intersection the intersection point to check
     * @param lightSource the light source to check against
     * @param l direction from light to point
     * @param n normal at the point
     * @param nl dot product of n and l
     * @return true if the point is unshaded, false otherwise
     */
    private boolean unshaded(Intersection intersection, LightSource lightSource, Vector l, Vector n, double nl) {
        Vector lightDirection = l.scale(-1); // from point to light source
        Vector epsVector = n.scale(nl < 0 ? EPS : -EPS);
        Point point = intersection.point.add(epsVector);
        Ray lightRay = new Ray(point, lightDirection);

        List<Intersection> intersections = scene.getGeometries().calculateIntersections(lightRay);
        return intersections == null || intersections.isEmpty();
    }

    /**
     * Calculates local lighting effects at an intersection.
     * @param intersection the intersection information
     * @param ray the ray that caused the intersection
     * @return the color resulting from local lighting effects
     */
    private Color calcLocalEffects(Intersection intersection, Ray ray) {
        Color color = intersection.geometry.getEmission();
        Vector v = ray.getDir();
        Vector n = intersection.geometry.getNormal(intersection.point);
        double nv = alignZero(n.dotProduct(v));
        if (nv == 0) {
            return color;
        }

        Material material = intersection.geometry.getMaterial();

        for (LightSource lightSource : scene.getLights()) {
            Vector l = lightSource.getL(intersection.point);
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) { // sign(nl) == sign(nv)
                if (unshaded(intersection, lightSource, l, n, nl)) {
                    Color iL = lightSource.getIntensity(intersection.point);
                    color = color.add(
                            iL.scale(calcDiffusive(material, nl).add(calcSpecular(material, n, l, nl, v)))
                    );
                }
            }
        }
        return color;
    }

    /**
     * Calculates diffuse reflection component.
     * @param material the material properties
     * @param nl dot product of normal and light direction
     * @return the diffuse component as a Double3
     */
    private Double3 calcDiffusive(Material material, double nl) {
        return material.getKD().scale(Math.abs(nl));
    }

    /**
     * Calculates specular reflection component.
     * @param material the material properties
     * @param n normal vector
     * @param l light direction
     * @param nl dot product of n and l
     * @param v view direction
     * @return the specular component as a Double3
     */
    private Double3 calcSpecular(Material material, Vector n, Vector l, double nl, Vector v) {
        Vector r = l.subtract(n.scale(2 * nl));
        double minusVR = alignZero(-v.dotProduct(r));
        if (minusVR <= 0) {
            return Double3.ZERO;
        }
        return material.getKS().scale(Math.pow(minusVR, material.getShininess()));
    }

    /**
     * Calculates the color at an intersection point.
     * @param intersection the intersection information
     * @param ray the ray that caused the intersection
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (intersection == null) {
            return scene.getBackground();
        }

        // Ensure the intersection has the normal set
        if (intersection.normal == null) {
            Vector normal = intersection.geometry.getNormal(intersection.point);
            intersection = new Intersection(
                    intersection.geometry,
                    intersection.point,
                    intersection.material,
                    intersection.ray,
                    normal,
                    intersection.lightSource
            );
        }

        // Get ambient light contribution
        Material material = intersection.geometry.getMaterial();
        if (material == null) {
            material = new Material();
        }
        Color ambient = scene.getAmbientLight().getIntensity().scale(material.getKA());

        // Add local lighting effects
        Color localEffects = calcLocalEffects(intersection, ray);

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