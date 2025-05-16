package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Vector;
import primitives.Point;

import java.util.List;

/**
 * Geometry interface represents all the geometric shapes
 */
public abstract class Geometry extends Intersectable
{

    protected Color emission = Color.BLACK;

    private Material material = new Material();

    /**
     * Getter for the emission of the body
     *
     * @return the emission of the body
     */
    public Color getEmission()
    {
        return emission;
    }

    /**
     * Setter for the emission of the body
     *
     * @param emission the emission of the body
     * @return the body
     */
    public Geometry setEmission(Color emission)
    {
        this.emission = emission;
        return this;
    }


    /**
     * @param p the function's Point parameter
     * @return normal vector on the body from the Point parameter
     */
    public abstract Vector getNormal(Point p);

    /**
     * Get the material of this object
     * @return the material of this object
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material of the geometry
     * @param material the material to set
     * @return the updated geometry with the new material
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

}
