package geometries;

import primitives.*;

/**
 * The {@code Triangle} class represents a triangle in 3D space.
 * It is defined as a special case of a {@link Polygon} with exactly 3 vertices.
 */
public class Triangle extends Polygon {
    /**
     * Constructs a triangle from three vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "Triangle{" + vertices + "}";
    }
}
