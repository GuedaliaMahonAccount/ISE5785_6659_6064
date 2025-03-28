package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Polygon class
 */
class PolygonTests {

    /**
     * Test method for {@link geometries.Polygon#getNormal(primitives.Point)}.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Simple square polygon in the XY plane
        Polygon polygon = new Polygon(
                new Point(0, 0, 0),
                new Point(1, 0, 0),
                new Point(1, 1, 0),
                new Point(0, 1, 0)
        );

        // We expect the normal to be (0, 0, 1) or (0, 0, -1)
        Vector normal = polygon.getNormal(new Point(0.5, 0.5, 0));

        // Check the normal is a unit vector
        assertEquals(1.0, normal.length(), 1e-10, "Normal is not normalized");

        // Check orthogonality to polygon's plane vectors
        Vector v1 = new Point(1, 0, 0).subtract(new Point(0, 0, 0));
        Vector v2 = new Point(0, 1, 0).subtract(new Point(0, 0, 0));
        assertEquals(0, normal.dotProduct(v1), 1e-10, "Normal is not orthogonal to edge vector 1");
        assertEquals(0, normal.dotProduct(v2), 1e-10, "Normal is not orthogonal to edge vector 2");
    }
}
