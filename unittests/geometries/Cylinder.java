package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import geometries.Intersectable.GeoPoint;
import geometries.Cylinder;

class CylinderTests {

    @Test
    void testFindIntersections() {
        Cylinder cyl = new Cylinder(
                1.0,
                new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)),
                3.0
        );

        // ============ Equivalence Partition Tests ==============

        // TC01: Ray crosses the side (2 points)
        Ray r1 = new Ray(new Point(-2, 0, 1.5), new Vector(1, 0, 0));
        List<GeoPoint> result1 = cyl.findGeoIntersections(r1);
        assertNotNull(result1, "Expected intersections but got null");
        assertEquals(2, result1.size(), "Expected 2 intersection points");

        // TC02: Ray starts inside cylinder and exits through side (1 point)
        Ray r2 = new Ray(new Point(0.5, 0, 1), new Vector(1, 0, 0));
        List<GeoPoint> result2 = cyl.findGeoIntersections(r2);
        assertNotNull(result2, "Expected 1 intersection from inside");
        assertEquals(1, result2.size(), "Expected 1 intersection point");

        // TC03: Ray starts outside and intersects top cap (1 point)
        Ray r3 = new Ray(new Point(0.5, 0, 5), new Vector(0, 0, -1));
        List<GeoPoint> result3 = cyl.findGeoIntersections(r3);
        assertNotNull(result3, "Expected 1 intersection with top cap");
        assertEquals(1, result3.size());

        // TC04: Ray intersects both side and bottom cap (2 points)
        Ray r4 = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        List<GeoPoint> result4 = cyl.findGeoIntersections(r4);
        assertNotNull(result4, "Expected 2 intersections (bottom + side)");
        assertEquals(2, result4.size());

        // TC05: Ray passes through top and bottom cap (2 points)
        Ray r5 = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        List<GeoPoint> result5 = cyl.findGeoIntersections(r5);
        assertNotNull(result5, "Expected 2 intersections (through both caps or side+cap)");
        assertEquals(2, result5.size(), "Expected 2 intersection points");

        // =============== Boundary Value Tests ==================

        // TC06: Ray is tangent to side (0 points)
        Ray r6 = new Ray(new Point(1, -1, 1.5), new Vector(0, 1, 0));
        assertNull(cyl.findGeoIntersections(r6), "Expected no intersection (tangent)");

        // TC07: Ray starts on surface and goes outward (0 points)
        Ray r7 = new Ray(new Point(1, 0, 1), new Vector(1, 0, 0));
        assertNull(cyl.findGeoIntersections(r7), "Expected no intersection (on surface outward)");

        // TC08: Ray is parallel to axis inside (should hit caps)
        Ray r8 = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        List<GeoPoint> result8 = cyl.findGeoIntersections(r8);
        assertNotNull(result8, "Expected intersection with bottom and/or top");
        assertFalse(result8.isEmpty());
    }
}
