package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import geometries.Intersectable.GeoPoint;
import geometries.Triangle;

/**
 * Test class for Triangle class
 */
class TriangleTests {

    @Test
    void getNormal() {
        // Equivalence Partitions tests
        Triangle triangle = new Triangle(
                new Point(0, 0, 0),
                new Point(0, 5, 0),
                new Point(5, 0, 0)
        );
        Vector normal = new Vector(0, 0, 1);
        // The normal should be (0,0,1) up to direction – just check it's not the opposite
        assertFalse(normal.equals(triangle.getNormal(new Point(1, 1, 0))) &&
                        normal.equals(triangle.getNormal(new Point(-1, -1, 0))),
                "Bad normal to triangle");
    }

    @Test
    void findIntersections(){
        // EP01: ray passes through triangle
        Ray ray = new Ray(new Point(3, 3, 2), new Vector(-1, -1, -4));
        Triangle triangle = new Triangle(
                new Point(1, 0, 0),
                new Point(1, 5, 0),
                new Point(6, 0, 0)
        );
        List<GeoPoint> hits1 = triangle.findGeoIntersections(ray);
        assertNotNull(hits1, "Expected one intersection");
        assertEquals(1, hits1.size());
        assertEquals(
                new Point(2.5, 2.5, 0),
                hits1.get(0).point,
                "Wrong intersection point"
        );

        // EP02: ray misses triangle on one side
        ray = new Ray(new Point(3, 3, 2), new Vector(1, 1, -4));
        assertNull(triangle.findGeoIntersections(ray));

        // EP03: ray misses triangle on two sides
        ray = new Ray(new Point(3, 3, 2), new Vector(-5, 5.5, -4));
        assertNull(triangle.findGeoIntersections(ray));

        // BV01: ray intersects vertex
        ray = new Ray(new Point(1, 0, 3), new Vector(0, 0, -1));
        assertNull(triangle.findGeoIntersections(ray));

        // BV02: ray intersects edge
        ray = new Ray(new Point(1, 0, 3), new Vector(1, 0, -6));
        assertNull(triangle.findGeoIntersections(ray));

        // BV03: ray intersects edge continuation imaginary line
        ray = new Ray(new Point(0.5, 0, 3), new Vector(0, 0, -1));
        assertNull(triangle.findGeoIntersections(ray));
    }
}
