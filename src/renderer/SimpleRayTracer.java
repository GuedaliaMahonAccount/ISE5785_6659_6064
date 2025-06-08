// renderer/SimpleRayTracer.java
package renderer;

import primitives.*;
import geometries.Intersectable.Intersection;
import lighting.LightSource;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * {@code SimpleRayTracer} extends {@link RayTracerBase} to support
 * Phong shading with shadows, reflections, and simple transparency.
 */
public class SimpleRayTracer extends RayTracerBase {
    /** Offset to avoid self‐intersection when spawning new rays. */
    public static final double DELTA = 0.1;

    /** Shadow‐ray bias to prevent acne. */
    private static final double EPS       = 0.1;
    /** Maximum recursion depth for global illumination. */
    private static final int    MAX_LEVEL = 10;
    /** Minimum attenuation coefficient threshold. */
    private static final double MIN_K     = 0.001;
    /** Initial attenuation factor. */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a SimpleRayTracer for the given scene.
     *
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray into the scene, computing ambient and shaded contributions.
     *
     * @param ray the ray to trace
     * @return the resulting color after shading
     */
    @Override
    public Color traceRay(Ray ray) {
        Intersection ip = findClosestIntersection(ray);
        if (ip == null) {
            return scene.getBackground();
        }
        // Ambient contribution
        Material m       = ip.geometry.getMaterial();
        Color    ambient = scene.getAmbientLight()
                .getIntensity()
                .scale(m.getKA());

        // Local + global shading
        Color localPlus = calcColor(ip, ray, MAX_LEVEL, INITIAL_K);
        return ambient.add(localPlus);
    }

    /**
     * Computes combined local and global lighting effects recursively.
     *
     * @param ip    the intersection record
     * @param ray   the incoming ray
     * @param level remaining recursion depth
     * @param kAcc  accumulated attenuation so far
     * @return shaded color contribution
     */
    private Color calcColor(Intersection ip, Ray ray, int level, Double3 kAcc) {
        // Local lighting (diffuse, specular, shadows)
        Color local = calcLocalEffects(ip, ray);

        // Terminate recursion
        if (level == 1) {
            return local;
        }

        // Add reflections and refractions
        return local.add(calcGlobalEffects(ip, ray, level, kAcc));
    }

    /**
     * Calculates local lighting including shadows and partial transparency.
     *
     * @param ip  the intersection record
     * @param ray the incoming ray
     * @return local shading color
     */
    private Color calcLocalEffects(Intersection ip, Ray ray) {
        Color  result = ip.geometry.getEmission();
        Vector v      = ray.getDir();
        Vector n      = ip.geometry.getNormal(ip.point);
        double nv     = alignZero(n.dotProduct(v));
        if (nv == 0) {
            return result;
        }

        Material m = ip.geometry.getMaterial();
        for (LightSource light : scene.getLights()) {
            Vector l  = light.getL(ip.point);
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) {
                // Compute shadow transparency
                Double3 ktr = transparency(ip, light, l, n, nl);
                if (!ktr.lowerThan(MIN_K)) {
                    Color   iL   = light.getIntensity(ip.point).scale(ktr);
                    Double3 diff = calcDiffusive(m, nl);
                    Double3 spec = calcSpecular(m, n, l, nl, v);
                    result = result.add(iL.scale(diff.add(spec)));
                }
            }
        }
        return result;
    }

    /**
     * Marches a shadow ray to compute transparency between point and light.
     *
     * @param ip    the intersection record
     * @param light the light source
     * @param l     direction to light
     * @param n     surface normal
     * @param nl    dot product of normal and light direction
     * @return cumulative transparency factor
     */
    private Double3 transparency(Intersection ip,
                                 LightSource light,
                                 Vector l,
                                 Vector n,
                                 double nl) {
        // Offset start to avoid self‐intersection
        Vector shift = n.scale(nl < 0 ? EPS : -EPS);
        Point  p     = ip.point.add(shift);
        Ray    r     = new Ray(p, l.scale(-1));

        List<Intersection> lst = scene.getGeometries()
                .calculateIntersections(r);
        if (lst == null) {
            return Double3.ONE;
        }

        double lightDist = light.getDistance(ip.point);
        Double3 ktr      = Double3.ONE;
        for (Intersection inter : lst) {
            double d = ip.point.distance(inter.point);
            if (d < lightDist) {
                ktr = ktr.product(inter.geometry.getMaterial().getKT());
                if (ktr.lowerThan(MIN_K)) {
                    return Double3.ZERO;
                }
            }
        }
        return ktr;
    }

    /**
     * Combines reflections and refractions at the intersection.
     *
     * @param ip    the intersection record
     * @param ray   the incoming ray
     * @param level remaining recursion depth
     * @param kAcc  accumulated attenuation so far
     * @return global shading color
     */
    private Color calcGlobalEffects(Intersection ip, Ray ray, int level, Double3 kAcc) {
        Material m      = ip.geometry.getMaterial();
        Color    result = Color.BLACK;

        // Reflection
        if (!m.getKR().lowerThan(MIN_K)) {
            Ray reflRay = constructReflectedRay(ip, ray);
            result = result.add(
                    calcGlobalEffect(reflRay, level, kAcc, m.getKR())
            );
        }
        // Refraction
        if (!m.getKT().lowerThan(MIN_K)) {
            Ray refrRay = constructRefractedRay(ip, ray);
            result = result.add(
                    calcGlobalEffect(refrRay, level, kAcc, m.getKT())
            );
        }
        return result;
    }

    /**
     * Helper for recursion: traces a ray and attenuates by material factor.
     *
     * @param ray   the ray to trace
     * @param level remaining recursion depth
     * @param kAcc  accumulated attenuation
     * @param kX    current material coefficient (KR or KT)
     * @return color contribution
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 kAcc, Double3 kX) {
        Double3 kNext = kAcc.product(kX);
        if (kNext.lowerThan(MIN_K)) {
            return Color.BLACK;
        }
        Intersection ip2 = findClosestIntersection(ray);
        if (ip2 == null) {
            return scene.getBackground().scale(kX);
        }
        return calcColor(ip2, ray, level - 1, kNext).scale(kX);
    }

    /**
     * Constructs a reflected ray from the intersection.
     *
     * @param ip  the intersection record
     * @param ray the incoming ray
     * @return reflected ray
     */
    private Ray constructReflectedRay(Intersection ip, Ray ray) {
        Vector v = ray.getDir();
        Vector n = ip.geometry.getNormal(ip.point);
        Vector r = v.subtract(n.scale(2 * v.dotProduct(n))).normalize();
        return new Ray(ip.point, r, n);
    }

    /**
     * Constructs a refracted (transparent) ray through the surface.
     *
     * @param ip  the intersection record
     * @param ray the incoming ray
     * @return refracted ray
     */
    private Ray constructRefractedRay(Intersection ip, Ray ray) {
        return new Ray(ip.point, ray.getDir(), ip.geometry.getNormal(ip.point));
    }

    /**
     * Finds the closest intersection of the ray with the scene geometries.
     *
     * @param ray the ray to test
     * @return closest intersection record or null if none
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> lst = scene.getGeometries()
                .calculateIntersections(ray);
        if (lst == null || lst.isEmpty()) {
            return null;
        }
        return findClosestIntersection(ray.getP0(), lst);
    }

    /**
     * Chooses the closest intersection from a list.
     *
     * @param origin        ray origin for distance measure
     * @param intersections list of intersection records
     * @return the closest intersection
     */
    private Intersection findClosestIntersection(Point origin,
                                                 List<Intersection> intersections) {
        Intersection closest = null;
        double      minD    = Double.POSITIVE_INFINITY;
        for (Intersection i : intersections) {
            double d = origin.distance(i.point);
            if (d < minD) {
                minD    = d;
                closest = i;
            }
        }
        return closest;
    }

    /**
     * Computes the diffuse component based on Lambert's law.
     *
     * @param m  material at intersection
     * @param nl dot product of normal and light direction
     * @return diffuse factor
     */
    private Double3 calcDiffusive(Material m, double nl) {
        return m.getKD().scale(Math.abs(nl));
    }

    /**
     * Computes the specular component based on Phong model.
     *
     * @param m         material at intersection
     * @param n         surface normal
     * @param l         light direction
     * @param nl        dot product of normal and light
     * @param v         view direction
     * @return specular factor
     */
    private Double3 calcSpecular(Material m,
                                 Vector n,
                                 Vector l,
                                 double nl,
                                 Vector v) {
        Vector r  = l.subtract(n.scale(2 * nl));
        double vr = alignZero(-v.dotProduct(r));
        if (vr <= 0) {
            return Double3.ZERO;
        }
        return m.getKS().scale(Math.pow(vr, m.getShininess()));
    }
}
