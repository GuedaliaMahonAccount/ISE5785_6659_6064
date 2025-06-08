package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracers.
 * <p>
 * Provides a reference to the scene (geometries and light sources)
 * and defines the contract for tracing rays through the scene.
 */
public abstract class RayTracerBase {
    /**
     * The scene containing geometries and lights used for rendering.
     */
    protected final Scene scene;

    /**
     * Constructs a ray tracer with the specified scene.
     *
     * @param scene The scene to be traced; must not be null.
     */
    protected RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces the given ray into the scene and computes the resulting color.
     * Implementations should perform intersection tests, shading, and
     * handle effects such as shadows, reflections, and transparency.
     *
     * @param ray The ray to trace; should be non-null.
     * @return The Color computed by tracing the ray, or background if no hit.
     */
    public abstract Color traceRay(Ray ray);
}
