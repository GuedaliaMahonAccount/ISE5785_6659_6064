package primitives;  // adjust this to match your test source root

import geometries.Intersectable.GeoPoint;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Ray} class.
 * Verifies point calculation along the ray and finding the closest intersection point.
 */
class RayTests {

    /**
     * Tests the {@link Ray#getPoint(double)} method for various t values.
     */
    @Test
    public void testGetPoint() {
        // Arrange: create a ray at origin (1,2,3) pointing along +z axis
        Ray ray = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));

        // Act & Assert:
        // TC01: t = 0 should return the ray's origin
        assertEquals(
                new Point(1, 2, 3),
                ray.getPoint(0),
                "getPoint(0) should return the ray's origin"
        );

        // TC02: positive t should move forward along direction
        assertEquals(
                new Point(1, 2, 5),
                ray.getPoint(2),
                "getPoint(2) should move 2 units along the z-axis"
        );

        // TC03: negative t should move backward along direction
        assertEquals(
                new Point(1, 2, 1),
                ray.getPoint(-2),
                "getPoint(-2) should move -2 units along the z-axis"
        );
    }

    // ==== Tests for findClosestGeoPoint ====

    /**
     * When passed a null list, {@link Ray#findClosestGeoPoint(List)} should return null.
     */
    @Test
    public void testFindClosestGeoPoint_nullList() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        assertNull(
                ray.findClosestGeoPoint(null),
                "findClosestGeoPoint(null) should return null"
        );
    }

    /**
     * When passed an empty list, {@link Ray#findClosestGeoPoint(List)} should return null.
     */
    @Test
    public void testFindClosestGeoPoint_emptyList() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        assertNull(
                ray.findClosestGeoPoint(List.of()),
                "findClosestGeoPoint(empty list) should return null"
        );
    }

    /**
     * Tests that the first element is returned when it is the closest.
     */
    @Test
    public void testFindClosestGeoPoint_firstIsClosest() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        GeoPoint first  = new GeoPoint(null, new Point(1, 1, 1));  // dist ≈ 1.732
        GeoPoint second = new GeoPoint(null, new Point(2, 2, 2));  // dist ≈ 3.464
        GeoPoint third  = new GeoPoint(null, new Point(3, 3, 3));  // dist ≈ 5.196

        List<GeoPoint> list = Arrays.asList(first, second, third);
        assertSame(
                first,
                ray.findClosestGeoPoint(list),
                "When first is closest, it should be returned"
        );
    }

    /**
     * Tests that the last element is returned when it is the closest.
     */
    @Test
    public void testFindClosestGeoPoint_lastIsClosest() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        GeoPoint first  = new GeoPoint(null, new Point(5, 5, 5));  // dist ≈ 8.660
        GeoPoint second = new GeoPoint(null, new Point(6, 6, 6));  // dist ≈10.392
        GeoPoint third  = new GeoPoint(null, new Point(1, 1, 1));  // dist ≈ 1.732

        List<GeoPoint> list = Arrays.asList(first, second, third);
        assertSame(
                third,
                ray.findClosestGeoPoint(list),
                "When last is closest, it should be returned"
        );
    }

    /**
     * Tests that a middle element is returned when it is the closest.
     */
    @Test
    public void testFindClosestGeoPoint_middleIsClosest() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        GeoPoint far   = new GeoPoint(null, new Point(5, 5, 5));  // dist ≈ 8.660
        GeoPoint close = new GeoPoint(null, new Point(2, 2, 2));  // dist ≈ 3.464
        GeoPoint mid   = new GeoPoint(null, new Point(3, 3, 3));  // dist ≈ 5.196

        List<GeoPoint> list = Arrays.asList(far, close, mid);
        assertSame(
                close,
                ray.findClosestGeoPoint(list),
                "When a middle element is closest, it should be returned"
        );
    }
}
