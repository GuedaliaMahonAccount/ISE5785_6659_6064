package scene;

import lighting.AmbientLight;
import primitives.Color;
import geometries.Geometries;

/**
 * Class representing a 3D scene.
 */
public class Scene {
    private String name;
    private Color background;
    private AmbientLight ambientLight;
    private Geometries geometries;

    /**
     * Constructor that initializes the scene with default values.
     * @param name The name of the scene.
     */
    public Scene(String name) {
        this.name = name;
        this.background = Color.BLACK;
        this.ambientLight = AmbientLight.NONE;
        this.geometries = new Geometries();
    }

    // Setters for chaining
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

    // Getters
    public Color getBackground() {
        return background;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public Geometries getGeometries() {
        return geometries;
    }

    @Override
    public String toString() {
        return "Scene{name='" + name + "', background=" + background + ", ambientLight=" + ambientLight + "}";
    }
}
