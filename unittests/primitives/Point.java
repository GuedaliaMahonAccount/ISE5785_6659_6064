package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for primitives.Point class
 */
class PointTests {

    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void subtract() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Subtract (2,3,4) from (1,2,3)
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 3, 4);
        Vector expected = new Vector(-1, -1, -1);
        assertEquals(expected, p1.subtract(p2), "subtract() wrong result");
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void add() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Add (1,1,1) to (1,2,3)
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(1, 1, 1);
        Point expected = new Point(2, 3, 4);
        assertEquals(expected, p.add(v), "add() wrong result");
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void distanceSquared() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: distanceSquared between (1,2,3) and (4,6,3) = 25
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 3);
        assertEquals(25, p1.distanceSquared(p2), DELTA, "distanceSquared() wrong result");
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void distance() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: distance between (1,2,3) and (4,6,3) = 5
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 3);
        assertEquals(5.0, p1.distance(p2), DELTA, "distance() wrong result");
    }


}
