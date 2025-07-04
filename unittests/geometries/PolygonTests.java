package geometries;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import primitives.*;
import geometries.Intersectable.GeoPoint;

/**
 * Testing Polygons
 */
class PolygonTests {
    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /** Test method for {@link geometries.Polygon#Polygon(primitives.Point...)}. */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct concave quadrangular with vertices in correct order
        assertDoesNotThrow(() -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(-1, 1, 1)),
                "Failed constructing a correct polygon");

        // TC02: Wrong vertices order
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(0, 1, 0), new Point(1, 0, 0), new Point(-1, 1, 1)), //
                "Constructed a polygon with wrong order of vertices");

        // TC03: Not in the same plane
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 2, 2)), //
                "Constructed a polygon with vertices that are not in the same plane");

        // TC04: Concave quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                        new Point(0.5, 0.25, 0.5)), //
                "Constructed a concave polygon");

        // =============== Boundary Values Tests ==================

        // TC10: Vertex on a side of a quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                        new Point(0, 0.5, 0.5)),
                "Constructed a polygon with vertex on a side");

        // TC11: Last point = first point
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
                "Constructed a polygon with last point identical to first point");

        // TC12: Co-located points
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 1, 0)),
                "Constructed a polygon with co-located points");
    }

    /** Test method for {@link geometries.Polygon#getNormal(primitives.Point)}. */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here - using a quad
        Point[] pts =
                { new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(-1, 1, 1) };
        Polygon pol = new Polygon(pts);
        // ensure there are no exceptions
        assertDoesNotThrow(() -> pol.getNormal(new Point(0, 0, 1)), "");
        // generate the test result
        Vector result = pol.getNormal(new Point(0, 0, 1));
        // ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Polygon's normal is not a unit vector");
        // ensure the result is orthogonal to all the edges
        for (int i = 0; i < 3; ++i)
            assertEquals(0d, result.dotProduct(pts[i].subtract(pts[i == 0 ? 3 : i - 1])), DELTA,
                    "Polygon's normal is not orthogonal to one of the edges");
    }

    /**
     * Test method for {@link geometries.Polygon#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Polygon polygon = new Polygon(
                new Point(0, 0, 1),
                new Point(1, 0, 1),
                new Point(1, 1, 1),
                new Point(0, 1, 1)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the polygon at the center
        Ray ray1 = new Ray(new Point(0.5, 0.5, 0), new Vector(0, 0, 1));
        List<GeoPoint> result1 = polygon.findGeoIntersections(ray1);
        assertNotNull(result1, "Expected intersection point");
        assertEquals(1, result1.size(), "Expected exactly one intersection point");
        assertEquals(new Point(0.5, 0.5, 1), result1.get(0).point, "Wrong intersection point");

        // TC02: Ray is outside the polygon
        Ray ray2 = new Ray(new Point(2, 2, 0), new Vector(0, 0, 1));
        assertNull(polygon.findIntersections(ray2), "Expected no intersection (outside polygon)");

        // TC03: Ray is parallel to the polygon but outside
        Ray ray3 = new Ray(new Point(0, 0, 2), new Vector(0, 0, 1));
        assertNull(polygon.findIntersections(ray3), "Expected no intersection (parallel and outside)");

        // TC04: Ray is on the plane but outside the polygon
        Ray ray4 = new Ray(new Point(-1, -1, 1), new Vector(1, 1, 0));
        assertNull(polygon.findIntersections(ray4), "Expected no intersection (in plane but outside polygon)");
    }
}
