// renderer/SimpleRayTracer.java
package renderer;

import primitives.*;
import geometries.Intersectable.Intersection;
import lighting.LightSource;
import lighting.PointLight;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * {@code SimpleRayTracer} extends {@link RayTracerBase} to support
 * Phong shading with shadows, reflections and simple transparency (including soft shadows).
 */
public class SimpleRayTracer extends RayTracerBase {
    /** Offset to avoid self-intersection when spawning new rays. */
    public static final double DELTA = 0.1;

    private static final double EPS       = 0.1;    // shadow-ray bias
    private static final int    MAX_LEVEL = 10;     // recursion depth
    private static final double MIN_K     = 0.001;  // attenuation threshold
    private static final Double3 INITIAL_K = Double3.ONE;

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        Intersection ip = findClosestIntersection(ray);
        if (ip == null) {
            return scene.getBackground();
        }
        // ambient term
        Material m       = ip.geometry.getMaterial();
        Color    ambient = scene.getAmbientLight()
                .getIntensity()
                .scale(m.getKA());

        // Phong + global (reflection/transparency)
        Color localPlus = calcColor(ip, ray, MAX_LEVEL, INITIAL_K);
        return ambient.add(localPlus);
    }

    private Color calcColor(Intersection ip, Ray ray, int level, Double3 kAcc) {
        // 1. local lighting (with partial-transparency shadows, including soft shadows)
        Color local = calcLocalEffects(ip, ray);

        // 2. stop if recursion depth reached
        if (level == 1) {
            return local;
        }

        // 3. add global reflection & transparency
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
            Vector l  = light.getL(ip.point);
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) {
                // compute partial-transparency factor (kT) with soft shadows
                Double3 ktr = transparency(ip, light, n, nl);
                if (!ktr.lowerThan(MIN_K)) {
                    // attenuate light by transparency factor
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
     * Computes the accumulated transparency between the intersection
     * point and the area light source by firing multiple shadow rays to random samples
     * on the circular disk. Averages their kT results → soft shadows.
     *
     * @param ip    the intersection to shade
     * @param light the LightSource (must be a PointLight to do area sampling)
     * @param n     the normal at the intersection
     * @param nl    the dot product of n and central light direction
     * @return averaged transparency factor (Double3 in [0,1])
     */
    private Double3 transparency(Intersection ip,
                                 LightSource light,
                                 Vector n,
                                 double nl) {
        // 1. offset intersection slightly to avoid self-intersection
        Vector shift = n.scale(nl < 0 ? EPS : -EPS);
        Point  baseP = ip.point.add(shift);

        // If not a PointLight (e.g., directional), fallback to single shadow ray
        if (!(light instanceof PointLight)) {
            // regular single-ray shadow
            Ray r = new Ray(baseP, light.getL(ip.point).scale(-1));
            return singleShadowTransparency(ip, light, r);
        }

        // Cast multiple shadow rays toward random points on the circular area
        PointLight pLight = (PointLight) light;
        int samples = pLight.getNumSamples();
        Double3 totalKt = Double3.ZERO;

        for (int i = 0; i < samples; i++) {
            // a) choose a random sample point on the circular disk
            Point samplePos = pLight.getSamplePoint(ip.point);
            Vector lSample = baseP.subtract(samplePos).normalize();
            Ray rSample = new Ray(baseP, lSample);

            // b) check occlusion along this ray
            Double3 kt = singleShadowTransparency(ip, light, rSample);
            totalKt = totalKt.add(kt);
        }

        // return average transparency
        return totalKt.scale(1.0 / samples);
    }

    /**
     * Helper: cast a single shadow-ray toward the given LightSource origin (or sample point).
     * Returns the kT product of all occluders (or ZERO if fully blocked).
     */
    private Double3 singleShadowTransparency(Intersection ip,
                                             LightSource light,
                                             Ray r) {
        // gather all intersections along the shadow ray
        List<Intersection> lst = scene.getGeometries()
                .calculateIntersections(r);
        if (lst == null) {
            return Double3.ONE;
        }

        // only those closer than the (sampled) light count
        double lightDist = light.getDistance(ip.point);
        Double3 ktr      = Double3.ONE;
        for (Intersection inter : lst) {
            double d = ip.point.distance(inter.point);
            if (d < lightDist) {
                ktr = ktr.product(inter.geometry.getMaterial().getKT());
                if (ktr.lowerThan(MIN_K)) {
                    // completely opaque → full shadow for this sample
                    return Double3.ZERO;
                }
            }
        }
        return ktr;
    }

    private Color calcGlobalEffects(Intersection ip, Ray ray, int level, Double3 kAcc) {
        Material m      = ip.geometry.getMaterial();
        Color    result = Color.BLACK;

        // reflection
        if (!m.getKR().lowerThan(MIN_K)) {
            Ray reflRay = constructReflectedRay(ip, ray);
            result = result.add(
                    calcGlobalEffect(reflRay, level, kAcc, m.getKR())
            );
        }
        // transparency (refraction)
        if (!m.getKT().lowerThan(MIN_K)) {
            Ray refrRay = constructRefractedRay(ip, ray);
            result = result.add(
                    calcGlobalEffect(refrRay, level, kAcc, m.getKT())
            );
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
        // recurse, then attenuate by kX
        return calcColor(ip2, ray, level - 1, kNext).scale(kX);
    }

    private Ray constructReflectedRay(Intersection ip, Ray ray) {
        Vector v = ray.getDir();
        Vector n = ip.geometry.getNormal(ip.point);
        Vector r = v.subtract(n.scale(2 * v.dotProduct(n))).normalize();
        return new Ray(ip.point, r, n);
    }

    private Ray constructRefractedRay(Intersection ip, Ray ray) {
        // straight-through transparency
        return new Ray(ip.point, ray.getDir(), ip.geometry.getNormal(ip.point));
    }

    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> lst = scene.getGeometries()
                .calculateIntersections(ray);
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
