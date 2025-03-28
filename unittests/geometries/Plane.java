package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Plane class
 */
class PlaneTests {

    /**
     * Test method for {@link geometries.Plane#getNormal()}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Create a plane from 3 non-colinear points
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane plane = new Plane(p1, p2, p3);

        // Expected normal (orthogonal to both vectors p2-p1 and p3-p1)
        Vector normal = plane.getNormal();

        // Check that the normal is unit length
        assertEquals(1, normal.length(), 1e-10, "Normal is not normalized");

        // Check that the normal is orthogonal to the plane vectors
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        assertEquals(0, normal.dotProduct(v1), 1e-10, "Normal is not orthogonal to vector 1");
        assertEquals(0, normal.dotProduct(v2), 1e-10, "Normal is not orthogonal to vector 2");
    }

    /**
     * Test method for {@link geometries.Plane#getQ0()}.
     */
    @Test
    void getQ0() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Get Q0 after creating the plane
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
        // TC01: Check that getNormal(point) returns the same as getNormal()
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane plane = new Plane(p1, p2, p3);
        Point pointOnPlane = new Point(1, 1, -1); // any point, assumed to be on the plane
        assertEquals(plane.getNormal(), plane.getNormal(pointOnPlane), "getNormal(point) inconsistent with getNormal()");
    }
}
