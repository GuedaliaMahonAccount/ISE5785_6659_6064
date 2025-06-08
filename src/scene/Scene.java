package scene;

import lighting.*;
import primitives.*;
import geometries.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a 3D scene containing geometries and light sources.
 */
public class Scene {
    /** Name identifier for the scene. */
    private String name;

    /** Background color of the scene. */
    private Color background;

    /** Ambient light present in the scene. */
    private AmbientLight ambientLight;

    /** Collection of geometries in the scene. */
    private Geometries geometries;

    /** List of point and directional light sources illuminating the scene. */
    private List<LightSource> lights;

    /**
     * Constructs a Scene with the given name and default settings.
     * Default background is black, ambient light is none, and no lights or geometries.
     *
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
     * Constructs a Scene with all properties specified.
     * Initializes light list to empty.
     *
     * @param name        The name of the scene.
     * @param background  The background color.
     * @param ambientLight The ambient light.
     * @param geometries  The geometries contained in the scene.
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
     *
     * @param background The new background color.
     * @return The Scene instance for chaining.
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight The new ambient light.
     * @return The Scene instance for chaining.
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries that belong to the scene.
     *
     * @param geometries The collection of geometries.
     * @return The Scene instance for chaining.
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

    /**
     * Adds a light source to the scene's list of lights.
     *
     * @param light The light source to add.
     * @return The Scene instance for chaining.
     */
    public Scene addLight(LightSource light) {
        this.lights.add(light);
        return this;
    }

    /**
     * Retrieves the name of the scene.
     *
     * @return The scene name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the background color of the scene.
     *
     * @return The background color.
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Retrieves the ambient light setting of the scene.
     *
     * @return The ambient light.
     */
    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    /**
     * Retrieves the geometries contained within the scene.
     *
     * @return The geometries collection.
     */
    public Geometries getGeometries() {
        return geometries;
    }

    /**
     * Retrieves the list of light sources illuminating the scene.
     *
     * @return The list of LightSource objects.
     */
    public List<LightSource> getLights() {
        return lights;
    }

    /**
     * Generates a string representation of the scene, including name,
     * background, ambient light, geometries, and light sources.
     *
     * @return String summary of the scene's state.
     */
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
