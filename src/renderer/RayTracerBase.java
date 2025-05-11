package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracers.
 */
public abstract class RayTracerBase {
    protected final Scene scene;

    /**
     * Constructor that initializes the ray tracer with a scene.
     * @param scene The scene to be traced.
     */
    protected RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Abstract method to trace a ray and return its color.
     * @param ray The ray to trace.
     * @return The color at the intersection point.
     */
    public abstract Color traceRay(Ray ray);
}
