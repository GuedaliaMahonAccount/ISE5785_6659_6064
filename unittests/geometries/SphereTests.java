package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import geometries.Intersectable.Intersection;

/**
 * Unit tests for geometries.Sphere class
 */
class SphereTests {

    /**
     * Test method for {@link geometries.Sphere#Sphere(primitives.Point, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid sphere with positive radius
        assertDoesNotThrow(() -> new Sphere(new Point(0, 0, 0), 1.0),
                "Failed constructing a valid sphere");

        // =============== Boundary Values Tests ==================

        // TC10: Zero radius
        assertThrows(IllegalArgumentException.class,
                () -> new Sphere(new Point(0, 0, 0), 0),
                "Constructed a sphere with radius = 0");

        // TC11: Negative radius
        assertThrows(IllegalArgumentException.class,
                () -> new Sphere(new Point(0, 0, 0), -2.0),
                "Constructed a sphere with negative radius");

        // TC12: Null center point
        assertThrows(IllegalArgumentException.class,
                () -> new Sphere(null, 1.0),
                "Constructed a sphere with null center point");
    }

    /**
     * Test method for {@link geometries.Sphere#getCenter()}.
     */
    @Test
    void getCenter() {
        Point center = new Point(0, 0, 0);
        Sphere sphere = new Sphere(center, 1.0);
        assertEquals(center, sphere.getCenter(), "getCenter() returned incorrect center point");
    }

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        Point center = new Point(0, 0, 0);
        Sphere sphere = new Sphere(center, 1.0);

        // TC01: Point on surface (1,0,0), normal should be (1,0,0)
        Point surfacePoint = new Point(1, 0, 0);
        Vector expectedNormal = new Vector(1, 0, 0);
        assertEquals(expectedNormal, sphere.getNormal(surfacePoint), "getNormal() returned incorrect normal vector");

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

    /**
     * Test method for {@link geometries.Sphere#calculateIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1.0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray starts outside and intersects the sphere at two points
        Ray ray1 = new Ray(new Point(-2, 0, 0), new Vector(1, 0, 0));
        List<Intersection> result1 = sphere.calculateIntersections(ray1);
        assertNotNull(result1, "Expected intersections but got null");
        assertEquals(2, result1.size(), "Expected two intersection points");

        // Ensure points are sorted from closest to furthest
        if (result1.get(0).point.getX() > result1.get(1).point.getX()) {
            result1 = List.of(result1.get(1), result1.get(0));
        }
        assertEquals(new Point(-1, 0, 0), result1.get(0).point, "Wrong entry point");
        assertEquals(new Point(1, 0, 0), result1.get(1).point, "Wrong exit point");

        // TC02: Ray starts outside and misses the sphere
        Ray ray2 = new Ray(new Point(0, 2, 0), new Vector(0, 1, 0));
        assertNull(sphere.calculateIntersections(ray2), "Expected no intersection (ray misses sphere)");

        // TC03: Ray starts inside the sphere (1 point)
        Ray ray3 = new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0));
        List<Intersection> result3 = sphere.calculateIntersections(ray3);
        assertEquals(1, result3.size(), "Expected one intersection (ray exits sphere)");
        assertEquals(new Point(1, 0, 0), result3.get(0).point, "Wrong exit point");

        // TC04: Ray starts after the sphere (no intersection)
        Ray ray4 = new Ray(new Point(2, 0, 0), new Vector(1, 0, 0));
        assertNull(sphere.calculateIntersections(ray4), "Expected no intersection (ray after sphere)");

        // =============== Boundary Values Tests ==================

        // TC11: Ray is tangent and starts before point of tangency (no intersection)
        Ray ray5 = new Ray(new Point(-1, 1, 0), new Vector(1, 0, 0));
        assertNull(sphere.calculateIntersections(ray5), "Ray is tangent — should return null");

        // TC12: Ray starts at sphere surface and goes inside (1 intersection)
        Ray ray6 = new Ray(new Point(1, 0, 0), new Vector(-1, 0, 0));
        List<Intersection> result6 = sphere.calculateIntersections(ray6);
        assertEquals(1, result6.size(), "Expected one intersection (from surface inward)");
        assertEquals(new Point(-1, 0, 0), result6.get(0).point, "Wrong exit point");

        // TC13: Ray starts at center (1 intersection)
        Ray ray7 = new Ray(new Point(0, 0, 0), new Vector(0, 1, 0));
        List<Intersection> result7 = sphere.calculateIntersections(ray7);
        assertEquals(1, result7.size(), "Expected one intersection from center");
        assertEquals(new Point(0, 1, 0), result7.get(0).point, "Wrong exit point");

        // TC14: Ray starts at surface and goes outward (no intersection)
        Ray ray8 = new Ray(new Point(0, 1, 0), new Vector(0, 1, 0));
        assertNull(sphere.calculateIntersections(ray8), "Expected no intersection (surface outward)");

        // TC15: Ray is orthogonal to line between ray start and sphere center and misses
        Ray ray9 = new Ray(new Point(0, 2, 0), new Vector(1, 0, 0));
        assertNull(sphere.calculateIntersections(ray9), "Expected no intersection (orthogonal miss)");
    }
}
