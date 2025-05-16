package scene;

import lighting.*;
import primitives.*;
import geometries.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a 3D scene.
 */
public class Scene {
    public String name;
    public Color background;
    public AmbientLight ambientLight;
    public Geometries geometries;
    public List<LightSource> lights;

    /**
     * Constructor that initializes the scene with default values.
     * @param name The name of the scene.
     */
    public Scene(String name) {
        this.name = name;
        this.background = Color.BLACK;
        this.ambientLight = AmbientLight.NONE;
        this.geometries = new Geometries();
        this.lights = new LinkedList<>();
    }

    /**
     * Constructor that initializes the scene with all properties.
     * @param name The name of the scene.
     * @param background The background color.
     * @param ambientLight The ambient light.
     * @param geometries The geometries in the scene.
     */
    public Scene(String name, Color background, AmbientLight ambientLight, Geometries geometries) {
        this.name = name;
        this.background = background;
        this.ambientLight = ambientLight;
        this.geometries = geometries;
        this.lights = new LinkedList<>();
    }

    /**
     * Sets the background color of the scene.
     * @param background The background color.
     * @return The Scene object (for chaining).
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     * @param ambientLight The ambient light.
     * @return The Scene object (for chaining).
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries in the scene.
     * @param geometries The geometries.
     * @return The Scene object (for chaining).
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

    /**
     * Adds a light source to the scene.
     * @param light The light source to add.
     * @return The Scene object (for chaining).
     */
    public Scene addLight(LightSource light) {
        this.lights.add(light);
        return this;
    }

    /**
     * Returns the name of the scene.
     * @return The name of the scene.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the background color of the scene.
     * @return The background color.
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Returns the ambient light of the scene.
     * @return The ambient light.
     */
    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    /**
     * Returns the geometries in the scene.
     * @return The geometries in the scene.
     */
    public Geometries getGeometries() {
        return geometries;
    }

    /**
     * Returns the list of light sources in the scene.
     * @return The list of light sources.
     */
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
