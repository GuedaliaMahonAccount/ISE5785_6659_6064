package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Sphere class
 */
class SphereTests {

    /**
     * Test method for {@link geometries.Sphere#getCenter()}.
     */
    @Test
    void getCenter() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Simple sphere at origin
        Point center = new Point(0, 0, 0);
        double radius = 1.0;
        Sphere sphere = new Sphere(radius,center);
        assertEquals(center, sphere.getCenter(), "getCenter() returned incorrect center point");
    }

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal to point on surface (1,0,0)
        Point center = new Point(0, 0, 0);
        double radius = 1.0;
        Sphere sphere = new Sphere(radius,center);
        Point surfacePoint = new Point(1, 0, 0);
        Vector expectedNormal = new Vector(1, 0, 0); // from center to surfacePoint
        Vector actualNormal = sphere.getNormal(surfacePoint);
        assertEquals(expectedNormal, actualNormal, "getNormal() returned incorrect normal vector");

        // =============== Boundary Values Tests ==================
        // TC11: Point exactly at top of sphere
        Point top = new Point(0, 1, 0);
        Vector expectedTopNormal = new Vector(0, 1, 0);
        assertEquals(expectedTopNormal, sphere.getNormal(top), "getNormal() failed at top point");
    }
}
