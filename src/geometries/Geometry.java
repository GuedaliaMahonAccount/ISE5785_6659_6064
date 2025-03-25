package geometries;

import primitives.*;

/**
 * Abstract class representing any 3D geometric shape.
 * All geometries must be able to return a normal vector at a given point on the surface.
 */
public abstract class Geometry {
    /**
     * Returns the normal vector to the geometry at the specified point.
     *
     * @param point the point on the geometry
     * @return the normal vector
     */
    public abstract Vector getNormal(Point point);
}
