package primitives;  // adjust this to match your test source root

import geometries.Intersectable.GeoPoint;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RayTests {

    @Test
    public void testGetPoint() {
        Ray ray = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));

        // TC01: t = 0 (origin)
        assertEquals(new Point(1, 2, 3), ray.getPoint(0), "Ray origin expected");

        // TC02: t > 0
        assertEquals(new Point(1, 2, 5), ray.getPoint(2), "Positive t");

        // TC03: t < 0
        assertEquals(new Point(1, 2, 1), ray.getPoint(-2), "Negative t");
    }

    // ==== findClosestGeoPoint tests ====

    @Test
    public void testFindClosestGeoPoint_nullList() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        assertNull(ray.findClosestGeoPoint(null), "null list should return null");
    }

    @Test
    public void testFindClosestGeoPoint_emptyList() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        assertNull(ray.findClosestGeoPoint(List.of()), "empty list should return null");
    }

    @Test
    public void testFindClosestGeoPoint_firstIsClosest() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        GeoPoint first  = new GeoPoint(null, new Point(1, 1, 1));  // dist ≈ 1.73
        GeoPoint second = new GeoPoint(null, new Point(2, 2, 2));  // dist ≈ 3.46
        GeoPoint third  = new GeoPoint(null, new Point(3, 3, 3));  // dist ≈ 5.20

        List<GeoPoint> list = Arrays.asList(first, second, third);
        assertSame(first, ray.findClosestGeoPoint(list), "first element should be closest");
    }

    @Test
    public void testFindClosestGeoPoint_lastIsClosest() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        GeoPoint first  = new GeoPoint(null, new Point(5, 5, 5));  // dist ≈ 8.66
        GeoPoint second = new GeoPoint(null, new Point(6, 6, 6));  // dist ≈10.39
        GeoPoint third  = new GeoPoint(null, new Point(1, 1, 1));  // dist ≈ 1.73

        List<GeoPoint> list = Arrays.asList(first, second, third);
        assertSame(third, ray.findClosestGeoPoint(list), "last element should be closest");
    }

    @Test
    public void testFindClosestGeoPoint_middleIsClosest() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        GeoPoint far   = new GeoPoint(null, new Point(5, 5, 5));  // dist ≈ 8.66
        GeoPoint close = new GeoPoint(null, new Point(2, 2, 2));  // dist ≈ 3.46
        GeoPoint mid   = new GeoPoint(null, new Point(3, 3, 3));  // dist ≈ 5.20

        List<GeoPoint> list = Arrays.asList(far, close, mid);
        assertSame(close, ray.findClosestGeoPoint(list), "middle element should be closest");
    }
}
