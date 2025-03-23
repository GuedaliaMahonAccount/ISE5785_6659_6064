package geometries;

import primitives.*;

/**
 * Triangle class represents a triangle as a specific case of a Polygon.
 */
public class Triangle extends Polygon {
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "Triangle{" + vertices + "}";
    }
}
