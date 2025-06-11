// renderer/SimpleRayTracer.java
package renderer;

import geometries.Intersectable;
import geometries.Intersectable.Intersection;
import lighting.LightSource;
import lighting.PointLight;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * {@code SimpleRayTracer} extends {@link RayTracerBase} to support
 * Phong shading with hard and soft shadows, reflections, and simple transparency.
 * Soft shadows use area sampling (50+ rays) via the light’s built-in jitter method.
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

    @Override
    public Color traceRay(Ray ray) {
        Intersection ip = findClosestIntersection(ray);
        if (ip == null) {
            return scene.getBackground();
        }
        // Ambient term
        Material m       = ip.geometry.getMaterial();
        Color    ambient = scene.getAmbientLight()
                .getIntensity()
                .scale(m.getKA());

        // Local + global
        Color localPlus = calcColor(ip, ray, MAX_LEVEL, INITIAL_K);
        return ambient.add(localPlus);
    }

    private Color calcColor(Intersection ip, Ray ray, int level, Double3 kAcc) {
        // Local shading
        Color local = calcLocalEffects(ip, ray);
        if (level == 1) {
            return local;
        }
        // Reflection & refraction
        return local.add(calcGlobalEffects(ip, ray, level, kAcc));
    }

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
            Vector l  = light.getL(ip.point);        // direction from point to light
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) {
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

    private Double3 transparency(Intersection ip,
                                 LightSource light,
                                 Vector l,
                                 Vector n,
                                 double nl) {
        // 1. offset the start point to avoid self‐intersection
        Vector bias = n.scale(nl < 0 ? EPS : -EPS);
        Point  p0   = ip.point.add(bias);

        // 2. soft shadows: area light sampling
        if (light instanceof PointLight pl && pl.getNumSamples() > 1) {
            int     samples   = pl.getNumSamples();
            double  lightDist = light.getDistance(ip.point);
            Double3 sumK      = Double3.ZERO;
            for (int i = 0; i < samples; i++) {
                Point samplePos  = pl.getSamplePoint(ip.point);
                Vector dirSample = samplePos.subtract(p0).normalize();
                double maxDist   = p0.distance(samplePos);
                Ray    shadow    = new Ray(p0, dirSample);
                List<Intersection> hits = scene.getGeometries().calculateIntersections(shadow);
                boolean blocked = hits != null &&
                        hits.stream().anyMatch(inter -> p0.distance(inter.point) < maxDist);
                sumK = sumK.add(blocked ? Double3.ZERO : Double3.ONE);
            }
            return sumK.scale(1.0 / samples);
        }

        // 3. hard shadows: single shadow ray toward the light
        double  lightDist = light.getDistance(ip.point);
        Ray     shadowRay = new Ray(p0, l.scale(-1));             // ← invert here
        List<Intersection> blockers = scene.getGeometries().calculateIntersections(shadowRay);
        if (blockers != null &&
                blockers.stream().anyMatch(i -> p0.distance(i.point) < lightDist)) {
            return Double3.ZERO;
        }
        return Double3.ONE;
    }



    private Color calcGlobalEffects(Intersection ip, Ray ray, int level, Double3 kAcc) {
        Material m      = ip.geometry.getMaterial();
        Color    result = Color.BLACK;

        if (!m.getKR().lowerThan(MIN_K)) {
            Ray   reflRay = constructReflectedRay(ip, ray);
            result = result.add(calcGlobalEffect(reflRay, level, kAcc, m.getKR()));
        }
        if (!m.getKT().lowerThan(MIN_K)) {
            Ray   refrRay = constructRefractedRay(ip, ray);
            result = result.add(calcGlobalEffect(refrRay, level, kAcc, m.getKT()));
        }
        return result;
    }

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

    private Ray constructReflectedRay(Intersection ip, Ray ray) {
        Vector v = ray.getDir();
        Vector n = ip.geometry.getNormal(ip.point);
        Vector r = v.subtract(n.scale(2 * v.dotProduct(n))).normalize();
        return new Ray(ip.point, r, n);
    }

    private Ray constructRefractedRay(Intersection ip, Ray ray) {
        return new Ray(ip.point, ray.getDir(), ip.geometry.getNormal(ip.point));
    }

    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> lst = scene.getGeometries().calculateIntersections(ray);
        if (lst == null || lst.isEmpty()) {
            return null;
        }
        return findClosestIntersection(ray.getP0(), lst);
    }

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

    private Double3 calcDiffusive(Material m, double nl) {
        return m.getKD().scale(Math.abs(nl));
    }

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