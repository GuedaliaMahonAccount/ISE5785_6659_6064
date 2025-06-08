package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import geometries.Intersectable.GeoPoint;

/**
 * Unit tests for the {@link Geometries} class.
 * <p>
 * Verifies that adding geometries and finding intersections behave correctly
 * under various scenarios, including empty collections, single and multiple
 * shape intersections, and boundary conditions.
 * </p>
 */
public class GeometriesTests {
    /**
     * Tests adding geometries to an empty collection and ensuring
     * that subsequent intersection queries return expected results.
     */
    @Test
    public void testAddGeometries() {
        // Empty collection
        Geometries geometries = new Geometries();

        // Create individual geometries
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1.0);
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        Triangle triangle = new Triangle(
                new Point(0, 1, 0),
                new Point(1, -1, 0),
                new Point(-1, -1, 0)
        );

        // Add a single geometry
        geometries.add(sphere);
        // Optionally test adding multiple: geometries.add(plane, triangle);

        // Ray that intersects the scene
        Ray ray = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        List<GeoPoint> result = geometries.findGeoIntersections(ray);

        // Verify at least one intersection is found
        assertNotNull(result, "Expected at least one intersection after adding geometries");
        assertFalse(result.isEmpty(), "Unexpected number of intersections");
    }

    /**
     * Tests finding intersections on a variety of geometry collections.
     * <ul>
     *   <li>Empty group returns null.</li>
     *   <li>Single shape intersection returns correct count.</li>
     *   <li>Multiple shapes intersection returns correct counts per scenario.</li>
     * </ul>
     */
    @Test
    public void testFindIntersections() {
        // Empty collection should yield no intersections
        Geometries empty = new Geometries();
        assertNull(
                empty.findGeoIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0))),
                "Empty collection should return null"
        );

        // Prepare individual shapes
        Sphere sphere = new Sphere(new Point(2, 0, 0), 1.0);
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        Triangle triangle = new Triangle(
                new Point(0, 1, 0),
                new Point(1, -1, 0),
                new Point(-1, -1, 0)
        );

        // Collection with no intersections
        Geometries geo = new Geometries(sphere, plane, triangle);
        Ray ray1 = new Ray(new Point(-2, 0, 0), new Vector(0, 1, 0));
        assertNull(
                geo.findGeoIntersections(ray1),
                "No geometry should be intersected"
        );

        // Only triangle should intersect
        Geometries geoTriangleOnly = new Geometries(triangle);
        Ray ray2 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        List<GeoPoint> result2 = geoTriangleOnly.findGeoIntersections(ray2);
        assertNotNull(result2, "Expected one intersection");
        assertEquals(1, result2.size(), "Wrong number of intersections (expect 1)");

        // Triangle and plane intersect, sphere does not
        Ray ray3 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        List<GeoPoint> result3 = geo.findGeoIntersections(ray3);
        assertNotNull(result3, "Expected multiple intersections");
        assertEquals(2, result3.size(), "Expected exactly 2 intersections (triangle and plane)");

        // Ray intersects all three shapes
        Ray ray4 = new Ray(new Point(0, 0, 2), new Vector(1, 0, -1));
        List<GeoPoint> result4 = geo.findGeoIntersections(ray4);
        assertNotNull(result4, "Expected intersections with all shapes");
        assertEquals(3, result4.size(), "Wrong number of total intersections");
    }
}
