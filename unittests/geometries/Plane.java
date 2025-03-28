package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Plane class
 */
class PlaneTests {

    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link geometries.Plane#Plane(Point, Point, Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid plane with 3 non-colinear points
        assertDoesNotThrow(() -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0)),
                "Failed constructing a correct plane");

        // TC02: Colinear points
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 0),
                        new Point(1, 1, 1),
                        new Point(2, 2, 2)),
                "Constructed a plane from colinear points");

        // =============== Boundary Values Tests ==================

        // TC10: Two identical points
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(0, 0, 1),
                        new Point(1, 1, 1)),
                "Constructed a plane with duplicated points");

        // TC11: All points identical
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(1, 1, 1),
                        new Point(1, 1, 1),
                        new Point(1, 1, 1)),
                "Constructed a plane with all points the same");
    }

    /**
     * Test method for {@link geometries.Plane#getNormal()}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal should be orthogonal to the plane and of unit length
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane plane = new Plane(p1, p2, p3);

        Vector normal = plane.getNormal();

        // Check unit length
        assertEquals(1, normal.length(), DELTA, "Normal is not normalized");

        // Check orthogonality to both edges
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not orthogonal to vector 1");
        assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not orthogonal to vector 2");
    }

    /**
     * Test method for {@link geometries.Plane#getQ0()}.
     */
    @Test
    void getQ0() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Get Q0 should return the first point used in the constructor
        Point p1 = new Point(1, 1, 1);
        Point p2 = new Point(2, 3, 4);
        Point p3 = new Point(3, 1, 0);
        Plane plane = new Plane(p1, p2, p3);
        assertEquals(p1, plane.getQ0(), "getQ0() returned incorrect point");
    }

    /**
     * Test method for {@link geometries.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: getNormal(point) should return same vector as getNormal()
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane plane = new Plane(p1, p2, p3);
        Point anyPoint = new Point(0.5, 0.5, 0);

        assertEquals(plane.getNormal(), plane.getNormal(anyPoint),
                "getNormal(point) inconsistent with getNormal()");
    }
}
