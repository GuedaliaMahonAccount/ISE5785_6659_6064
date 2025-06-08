package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Vector;
import primitives.Point;

/**
 * Abstract base class for all geometric shapes in the scene.
 * <p>
 * Provides common properties such as emission color and material,
 * and declares an abstract method to obtain the surface normal at a point.
 * </p>
 */
public abstract class Geometry extends Intersectable {

    /**
     * Emission (intrinsic) color of the geometry used for shading.
     */
    protected Color emission = Color.BLACK;

    /**
     * Material properties of the geometry (diffuse, specular, etc.).
     */
    private Material material = new Material();

    /**
     * Retrieves the emission color of the geometry.
     *
     * @return the current emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the emission color for this geometry.
     *
     * @param emission the emission color to set
     * @return this geometry (for method chaining)
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Computes the normal vector to the geometry at the specified point.
     *
     * @param p the point on the surface where the normal is computed
     * @return a normalized vector perpendicular to the surface at point p
     */
    public abstract Vector getNormal(Point p);

    /**
     * Retrieves the material of the geometry.
     *
     * @return the material properties
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material properties for this geometry.
     *
     * @param material the material to set
     * @return this geometry (for method chaining)
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }
}
