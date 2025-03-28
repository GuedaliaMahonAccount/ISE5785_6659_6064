package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Cylinder class
 */
class CylinderTests {

    /**
     * Test method for {@link geometries.Cylinder#getHeight()}.
     */
    @Test
    void getHeight() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Create cylinder with height 5, check getHeight()
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        double radius = 2.0;
        double height = 5.0;
        Cylinder cylinder = new Cylinder( radius,axisRay, height);
        assertEquals(height, cylinder.getHeight(), 1e-10, "getHeight() returned wrong value");
    }
}
