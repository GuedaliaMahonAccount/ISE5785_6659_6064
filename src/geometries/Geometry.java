package geometries;

import primitives.*;

/**
 * Interface for geometrical shapes that can provide a normal vector.
 */
public abstract class Geometry {
    public abstract Vector getNormal(Point point);
}
