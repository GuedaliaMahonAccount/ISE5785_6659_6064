package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Cylinder class
 */
class CylinderTests {

    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link geometries.Cylinder#Cylinder(double, primitives.Ray, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid cylinder with positive radius and height
        assertDoesNotThrow(() -> new Cylinder(
                        1.0,
                        new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)),
                        5.0),
                "Failed constructing a valid cylinder");

        // =============== Boundary Values Tests ==================

        // TC10: Zero radius
        assertThrows(IllegalArgumentException.class,
                () -> new Cylinder(0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 5.0),
                "Constructed a cylinder with radius = 0");

        // TC11: Negative radius
        assertThrows(IllegalArgumentException.class,
                () -> new Cylinder(-1, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 5.0),
                "Constructed a cylinder with negative radius");

        // TC12: Zero height
        assertThrows(IllegalArgumentException.class,
                () -> new Cylinder(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 0),
                "Constructed a cylinder with height = 0");

        // TC13: Negative height
        assertThrows(IllegalArgumentException.class,
                () -> new Cylinder(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), -5.0),
                "Constructed a cylinder with negative height");

        // TC14: Null axis ray (if not allowed)
        assertThrows(IllegalArgumentException.class,
                () -> new Cylinder(1.0, null, 5.0),
                "Constructed a cylinder with null axis ray");
    }

    /**
     * Test method for {@link geometries.Cylinder#getHeight()}.
     */
    @Test
    void getHeight() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Create cylinder with height 5, check getHeight()
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        double radius = 2.0;
        double height = 5.0;
        Cylinder cylinder = new Cylinder(radius, axisRay, height);
        assertEquals(height, cylinder.getHeight(), DELTA, "getHeight() returned wrong value");
    }

    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cylinder = new Cylinder(1.0, axisRay, 5.0);

        // TC01: Point on the side surface (1,0,2)
        Point sidePoint = new Point(1, 0, 2);
        Vector expectedSideNormal = new Vector(1, 0, 0);
        assertEquals(expectedSideNormal, cylinder.getNormal(sidePoint), "getNormal() wrong for side surface");

        // =============== Boundary Values Tests ==================

        // TC11: Point on bottom base center (0,0,0)
        Point bottomCenter = new Point(0, 0, 0);
        Vector expectedBottomNormal = new Vector(0, 0, -1); // or (0, 0, 1), depending on implementation
        Vector actualBottomNormal = cylinder.getNormal(bottomCenter);
        assertTrue(actualBottomNormal.equals(expectedBottomNormal) ||
                        actualBottomNormal.equals(expectedBottomNormal.scale(-1)),
                "getNormal() wrong for bottom base");

        // TC12: Point on top base center (0,0,5)
        Point topCenter = new Point(0, 0, 5);
        Vector expectedTopNormal = new Vector(0, 0, 1);
        Vector actualTopNormal = cylinder.getNormal(topCenter);
        assertTrue(actualTopNormal.equals(expectedTopNormal) ||
                        actualTopNormal.equals(expectedTopNormal.scale(-1)),
                "getNormal() wrong for top base");
    }
}
