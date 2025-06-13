package scene;

import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;
import geometries.Geometries;
import geometries.Intersectable;

import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a 3D scene.
 */
public class Scene {
    public String name;
    public Color background;
    public AmbientLight ambientLight;

    /**
     * The root of the scene's geometry hierarchy, always held in a Geometries composite.
     */
    public Geometries geometries;

    public List<LightSource> lights;

    /**
     * Constructor that initializes the scene with default values.
     * @param name The name of the scene.
     */
    public Scene(String name) {
        this.name         = name;
        this.background   = Color.BLACK;
        this.ambientLight = AmbientLight.NONE;
        this.geometries   = new Geometries();
        this.lights       = new LinkedList<>();
    }

    /**
     * Constructor that initializes the scene with all properties.
     * @param name         The name of the scene.
     * @param background   The background color.
     * @param ambientLight The ambient light.
     * @param geometries   The root Geometries composite.
     */
    public Scene(String name,
                 Color background,
                 AmbientLight ambientLight,
                 Geometries geometries) {
        this.name         = name;
        this.background   = background;
        this.ambientLight = ambientLight;
        this.geometries   = geometries;
        this.lights       = new LinkedList<>();
    }

    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Explicit setter for a Geometries composite.
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

    /**
     * Overloaded setter: wrap any Intersectable (e.g. BVHNode) into a Geometries composite.
     */
    public Scene setGeometries(Intersectable root) {
        this.geometries = new Geometries(root);
        return this;
    }

    public Scene addLight(LightSource light) {
        this.lights.add(light);
        return this;
    }

    public String getName() {
        return name;
    }

    public Color getBackground() {
        return background;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public Geometries getGeometries() {
        return geometries;
    }

    public List<LightSource> getLights() {
        return lights;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", background=" + background +
                ", ambientLight=" + ambientLight +
                ", geometries=" + geometries +
                ", lights=" + lights +
                '}';
    }
}
