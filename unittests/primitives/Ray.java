package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for primitives.Ray class
 */
class RayTests {

    /**
     * Test method for {@link primitives.Ray#getP0()}.
     */
    @Test
    void getP0() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Create ray with p0 = (1,2,3), check getP0()
        Point p0 = new Point(1, 2, 3);
        Vector dir = new Vector(1, 0, 0);
        Ray ray = new Ray(p0, dir);
        assertEquals(p0, ray.getP0(), "getP0() returned wrong point");
    }

    /**
     * Test method for {@link primitives.Ray#getDir()}.
     */
    @Test
    void getDir() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Create ray with direction (0,1,0), check getDir()
        Point p0 = new Point(0, 0, 0);
        Vector dir = new Vector(0, 1, 0);
        Ray ray = new Ray(p0, dir);
        assertEquals(dir, ray.getDir(), "getDir() returned wrong vector");
    }
}
