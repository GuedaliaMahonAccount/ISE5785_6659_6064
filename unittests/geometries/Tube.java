package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Tube class
 */
class TubeTests {


    /**
     * Test method for {@link geometries.Tube#Tube(double, primitives.Ray)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid tube with positive radius
        assertDoesNotThrow(() -> new Tube(1.0,
                        new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))),
                "Failed constructing a valid tube");

        // =============== Boundary Values Tests ==================

        // TC10: Zero radius
        assertThrows(IllegalArgumentException.class,
                () -> new Tube(0,
                        new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))),
                "Constructed a tube with radius = 0");

        // TC11: Negative radius
        assertThrows(IllegalArgumentException.class,
                () -> new Tube(-1,
                        new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))),
                "Constructed a tube with negative radius");

        // TC12: Null axis ray (if constructor handles it)
        assertThrows(IllegalArgumentException.class,
                () -> new Tube(1.0, null),
                "Constructed a tube with null axis ray");
    }

    /**
     * Test method for {@link geometries.Tube#getAxisRay()}.
     */
    @Test
    void getAxisRay() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Get axis ray
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0, axisRay);
        assertEquals(axisRay, tube.getAxisRay(), "getAxisRay() returned incorrect ray");
    }

    /**
     * Test method for {@link geometries.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Point on side of tube at (1, 0, 5), expect normal (1,0,0)
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
        Point surfacePoint = new Point(1, 0, 5);
        Vector expectedNormal = new Vector(1, 0, 0);
        assertEquals(expectedNormal, tube.getNormal(surfacePoint), "getNormal() returned incorrect normal vector");

        // =============== Boundary Values Tests ==================
        // TC11: Point on axis → should throw exception
        Point axisPoint = new Point(0, 0, 5);
        assertThrows(IllegalArgumentException.class,
                () -> tube.getNormal(axisPoint),
                "getNormal() did not throw on axis point");
    }
}
