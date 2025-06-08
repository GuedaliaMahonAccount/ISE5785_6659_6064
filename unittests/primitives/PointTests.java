package primitives;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Point} class.
 * Verifies vector subtraction, point translation, and distance calculations.
 */
class PointTests {

    /** Tolerance for floating-point assertions. */
    private static final double DELTA = 1e-10;

    /**
     * Tests subtraction of two points to produce a vector.
     * Equivalence Partition: p1.subtract(p2) yields correct Vector.
     */
    @Test
    void subtract() {
        // TC01: Subtract (2,3,4) from (1,2,3) should give (-1,-1,-1)
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 3, 4);
        Vector expected = new Vector(-1, -1, -1);
        assertEquals(expected, p1.subtract(p2), "subtract() wrong result");
    }

    /**
     * Tests addition of a vector to a point.
     * Equivalence Partition: p.add(v) yields correct Point.
     */
    @Test
    void add() {
        // TC01: Point(1,2,3) + Vector(1,1,1) => Point(2,3,4)
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(1, 1, 1);
        Point expected = new Point(2, 3, 4);
        assertEquals(expected, p.add(v), "add() wrong result");
    }

    /**
     * Tests squared distance between two points.
     * Equivalence Partition: horizontal distance squared computation.
     */
    @Test
    void distanceSquared() {
        // TC01: Points (1,2,3) and (4,6,3) -> dx=3, dy=4, dz=0 => 3^2+4^2=25
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 3);
        assertEquals(25, p1.distanceSquared(p2), DELTA, "distanceSquared() wrong result");
    }

    /**
     * Tests Euclidean distance between two points.
     * Should equal the square root of the squared distance.
     */
    @Test
    void distance() {
        // TC01: Points (1,2,3) and (4,6,3) -> distance = 5
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 3);
        assertEquals(5.0, p1.distance(p2), DELTA, "distance() wrong result");
    }
}
