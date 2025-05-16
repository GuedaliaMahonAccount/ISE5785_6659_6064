package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Vector;
import primitives.Point;

/**
 * Geometry abstract class represents all the geometric shapes
 */
public abstract class Geometry extends Intersectable {

    /**
     * The emission color of the geometry.
     */
    protected Color emission = Color.BLACK;

    /**
     * The material of the geometry.
     */
    private Material material = new Material(); // Default initialization

    /**
     * Gets the emission color of the geometry.
     *
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission the new emission color
     * @return the geometry itself (for method chaining)
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Gets the normal vector to the geometry at a given point.
     *
     * @param p the point on the geometry
     * @return the normal vector
     */
    public abstract Vector getNormal(Point p);

    /**
     * Gets the material of the geometry.
     *
     * @return the material of the geometry
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material of the geometry.
     *
     * @param material the material to set
     * @return the geometry itself (for method chaining)
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }
}
