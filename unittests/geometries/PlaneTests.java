package unittests.geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import geometries.Intersectable.GeoPoint;
import geometries.Plane;

/**
 * Unit tests for the {@link geometries.Plane} class.
 * Verifies correct behavior of constructors, normal calculations, reference point retrieval,
 * and ray–plane intersection logic.
 */
class PlaneTests {

    /** Tolerance for floating-point comparisons. */
    private static final double DELTA = 1e-10;

    /**
     * Tests Plane constructor for valid and invalid input scenarios.
     * Equivalence partitions and boundary values are covered.
     */
    @Test
    void testConstructor() {
        // Equivalence: three non-collinear points should construct successfully
        assertDoesNotThrow(() -> new Plane(
                new Point(0, 0, 1),
                new Point(1, 0, 0),
                new Point(0, 1, 0)), "Failed constructing a correct plane");

        // Equivalence: collinear points should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(0, 0, 0),
                new Point(1, 1, 1),
                new Point(2, 2, 2)), "Constructed a plane from colinear points");

        // Boundary: duplicate first and second points
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(0, 0, 1),
                new Point(0, 0, 1),
                new Point(1, 1, 1)), "Constructed a plane with duplicated points");

        // Boundary: all three points identical
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(1, 1, 1),
                new Point(1, 1, 1),
                new Point(1, 1, 1)), "Constructed a plane with all points the same");

        // Boundary: first and third points identical
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(0, 0, 1),
                new Point(1, 1, 1),
                new Point(0, 0, 1)), "Constructed a plane with first and third points identical");

        // Boundary: second and third points identical
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(1, 0, 0),
                new Point(0, 0, 1),
                new Point(0, 0, 1)), "Constructed a plane with second and third points identical");
    }

    /**
     * Verifies that the normal vector is unit length and orthogonal to plane edges.
     */
    @Test
    void getNormal() {
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane plane = new Plane(p1, p2, p3);

        Vector normal = plane.getNormal();

        // Normal should be normalized to length 1
        assertEquals(1, normal.length(), DELTA, "Normal is not normalized");

        // Normal must be orthogonal to edge vectors v1 and v2
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not orthogonal to v1");
        assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not orthogonal to v2");
    }

    /**
     * Checks that getQ0() returns the first defining point of the plane.
     */
    @Test
    void getQ0() {
        Point p1 = new Point(1, 1, 1);
        Point p2 = new Point(2, 3, 4);
        Point p3 = new Point(3, 1, 0);
        Plane plane = new Plane(p1, p2, p3);

        assertEquals(p1, plane.getQ0(), "getQ0() returned incorrect point");
    }

    /**
     * Ensures getNormal(point) yields the same result as getNormal() when given any point.
     */
    @Test
    void testGetNormalWithPoint() {
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane plane = new Plane(p1, p2, p3);

        Point anyPoint = new Point(0.5, 0.5, 0);
        assertEquals(plane.getNormal(), plane.getNormal(anyPoint),
                "getNormal(point) inconsistent with getNormal()");
    }

    /**
     * Tests ray–plane intersection for intersecting and non-intersecting rays.
     */
    @Test
    void testFindIntersections() {
        Plane plane = new Plane(
                new Point(0, 0, 1),
                new Point(1, 0, 1),
                new Point(0, 1, 1)
        );

        // TC01: Ray orthogonal to plane and intersecting at one point
        Ray ray1 = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        List<GeoPoint> result1 = plane.findGeoIntersections(ray1);
        assertNotNull(result1, "Ray intersects the plane but returned null");
        assertEquals(1, result1.size(), "Expected one intersection point");
        assertEquals(new Point(0, 0, 1), result1.get(0).point, "Wrong intersection point");

        // TC02: Ray parallel to plane and not intersecting -> should return null
        Ray ray2 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        assertNull(plane.findGeoIntersections(ray2), "Ray is parallel and outside – should return null");
    }
}
