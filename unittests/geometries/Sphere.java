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
     * Test method for {@link geometries.Sphere#Sphere(double, primitives.Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid sphere with positive radius
        assertDoesNotThrow(() -> new Sphere(1.0, new Point(0, 0, 0)),
                "Failed constructing a valid sphere");

        // =============== Boundary Values Tests ==================

        // TC10: Zero radius
        assertThrows(IllegalArgumentException.class,
                () -> new Sphere(0, new Point(0, 0, 0)),
                "Constructed a sphere with radius = 0");

        // TC11: Negative radius
        assertThrows(IllegalArgumentException.class,
                () -> new Sphere(-2.0, new Point(0, 0, 0)),
                "Constructed a sphere with negative radius");

        // TC12: Null center point (if constructor handles it)
        assertThrows(IllegalArgumentException.class,
                () -> new Sphere(1.0, null),
                "Constructed a sphere with null center point");
    }

    /**
     * Test method for {@link geometries.Sphere#getCenter()}.
     */
    @Test
    void getCenter() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Get center after construction
        Point center = new Point(0, 0, 0);
        Sphere sphere = new Sphere(1.0, center);
        assertEquals(center, sphere.getCenter(), "getCenter() returned incorrect center point");
    }

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Point on surface (1,0,0), normal should be (1,0,0)
        Point center = new Point(0, 0, 0);
        Sphere sphere = new Sphere(1.0, center);
        Point surfacePoint = new Point(1, 0, 0);
        Vector expectedNormal = new Vector(1, 0, 0);
        Vector actualNormal = sphere.getNormal(surfacePoint);
        assertEquals(expectedNormal, actualNormal, "getNormal() returned incorrect normal vector");

        // =============== Boundary Values Tests ==================

        // TC11: Point exactly on top (0,1,0)
        Point topPoint = new Point(0, 1, 0);
        Vector expectedTopNormal = new Vector(0, 1, 0);
        assertEquals(expectedTopNormal, sphere.getNormal(topPoint), "getNormal() failed at top point");

        // TC12: Point not on surface → should throw exception
        Point invalidPoint = new Point(2, 0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> sphere.getNormal(invalidPoint),
                "getNormal() should throw for point not on surface");
    }
}
