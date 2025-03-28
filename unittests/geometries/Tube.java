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
     * Test method for {@link geometries.Tube#getAxisRay()}.
     */
    @Test
    void getAxisRay() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Create tube and check axisRay
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        double radius = 1.0;
        Tube tube = new Tube(radius, axisRay);
        assertEquals(axisRay, tube.getAxisRay(), "getAxisRay() returned incorrect ray");
    }

    /**
     * Test method for {@link geometries.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Point on side of tube at (1, 0, 5), expect normal (1,0,0)
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        double radius = 1.0;
        Tube tube = new Tube(radius, axisRay);

        Point surfacePoint = new Point(1, 0, 5);
        Vector expectedNormal = new Vector(1, 0, 0);

        assertEquals(expectedNormal, tube.getNormal(surfacePoint), "getNormal() returned incorrect normal vector");

        // =============== Boundary Values Tests ==================
        // TC11: Point exactly on the axis line should not be allowed as input
        Point axisPoint = new Point(0, 0, 5);
        assertThrows(IllegalArgumentException.class,
                () -> tube.getNormal(axisPoint),
                "getNormal() should throw exception when point is on axis");
    }
}
