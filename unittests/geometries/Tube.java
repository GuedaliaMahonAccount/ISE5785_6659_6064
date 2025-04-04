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

    /**
     * Test method for {@link geometries.Tube#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray crosses the tube (2 intersection points)
        Ray ray1 = new Ray(new Point(-2, 0, 5), new Vector(1, 0, 0));
        var result1 = tube.findIntersections(ray1);
        assertNotNull(result1, "Expected intersections but got null");
        assertEquals(2, result1.size(), "Expected 2 intersection points");

        // TC02: Ray starts inside the tube and exits (1 point)
        Ray ray2 = new Ray(new Point(0.5, 0, 5), new Vector(1, 0, 0));
        var result2 = tube.findIntersections(ray2);
        assertNotNull(result2, "Expected one intersection from inside");
        assertEquals(1, result2.size(), "Expected 1 intersection point");

        // TC03: Ray misses the tube completely
        Ray ray3 = new Ray(new Point(3, 3, 5), new Vector(0, 1, 0));
        assertNull(tube.findIntersections(ray3), "Expected no intersection (misses)");

        // =============== Boundary Value Tests ==================

        // TC11: Ray is tangent to the tube
        Ray ray4 = new Ray(new Point(1, -1, 5), new Vector(0, 1, 0));
        assertNull(tube.findIntersections(ray4), "Expected no intersection (tangent)");

        // TC12: Ray starts on surface and goes outside
        Ray ray5 = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));
        assertNull(tube.findIntersections(ray5), "Expected no intersection (on surface outward)");

        // TC13: Ray starts on surface and goes inside (1 point)
        Ray ray6 = new Ray(new Point(1, 0, 0), new Vector(-1, 0, 0));
        var result6 = tube.findIntersections(ray6);
        assertNotNull(result6, "Expected 1 intersection going inside from surface");
        assertEquals(1, result6.size(), "Expected 1 intersection");

        // TC14: Ray is orthogonal and directed toward tube but starts past it
        Ray ray7 = new Ray(new Point(2, 0, 0), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray7), "Expected no intersection (orthogonal outside)");

        // TC15: Ray is parallel to tube axis and inside
        Ray ray8 = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        var result8 = tube.findIntersections(ray8);
        assertNull(result8, "Tube has infinite length, but axis-parallel rays don't intersect sides");
    }

}
