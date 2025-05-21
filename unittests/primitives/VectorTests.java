package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for primitives.Vector class
 */
class VectorTests {

    /**
     * A small number
     */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid vector should not throw
        assertDoesNotThrow(() -> new Vector(1, 2, 3),
                "Failed constructing a correct vector");

        // =============== Boundary Values Tests ==================

        // TC10: Zero vector (0,0,0) → should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(0, 0, 0),
                "Constructed zero vector which is invalid");
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void add() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: (1,2,3) + (4,-5,6) = (5,-3,9)
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, -5, 6);
        Vector expected = new Vector(5, -3, 9);
        assertEquals(expected, v1.add(v2), "add() wrong result");

        // =============== Boundary Values Tests ==================
        // TC11: Add vector and its opposite → should throw exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> v1.add(new Vector(-1, -2, -3)),
                "add() did not throw when result is zero vector");
    }

    /**
     * Test method for {@link primitives.Vector#subtract(primitives.Vector)}.
     */
    @Test
    void subtract() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: (4,5,6) - (1,2,3) = (3,3,3)
        Vector v1 = new Vector(4, 5, 6);
        Vector v2 = new Vector(1, 2, 3);
        Vector expected = new Vector(3, 3, 3);
        assertEquals(expected, v1.subtract(v2), "subtract() wrong result");

        // =============== Boundary Values Tests ==================
        // TC11: Subtract vector from itself → should throw exception
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1),
                "subtract() did not throw when result is zero vector");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void scale() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: (1,2,3) * 2 = (2,4,6)
        Vector v = new Vector(1, 2, 3);
        assertEquals(new Vector(2, 4, 6), v.scale(2), "scale() wrong result");

        // =============== Boundary Values Tests ==================
        // TC11: Scale by 0 → should throw exception
        assertThrows(IllegalArgumentException.class, () -> v.scale(0),
                "scale() did not throw when scaling by 0");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void dotProduct() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: (1,2,3) · (4,5,6) = 1*4 + 2*5 + 3*6 = 32
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);
        assertEquals(32, v1.dotProduct(v2), DELTA, "dotProduct() wrong result");

        // =============== Boundary Values Tests ==================
        // TC11: Dot product with orthogonal vector = 0
        Vector v3 = new Vector(-3, 6, -3);
        assertEquals(0, v1.dotProduct(v3), DELTA, "dotProduct() of orthogonal vectors should be 0");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void crossProduct() {
        // ============ Equivalence Partitions Tests ==============
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        Vector vr = v1.crossProduct(v2);

        // TC01: Result should be orthogonal to both
        assertEquals(0, vr.dotProduct(v1), DELTA, "crossProduct() result not orthogonal to v1");
        assertEquals(0, vr.dotProduct(v2), DELTA, "crossProduct() result not orthogonal to v2");

        // TC02: Length should be |v1| * |v2| * sin(angle)
        double expectedLength = v1.length() * v2.length() * Math.sin(
                Math.acos(v1.dotProduct(v2) / (v1.length() * v2.length())));
        assertEquals(expectedLength, vr.length(), DELTA, "crossProduct() result has wrong length");

        // =============== Boundary Values Tests ==================
        // TC11: Parallel vectors → should throw exception
        Vector v3 = new Vector(-2, -4, -6);
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v3),
                "crossProduct() with parallel vectors did not throw exception");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void lengthSquared() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: lengthSquared of (1,2,2) = 1^2 + 2^2 + 2^2 = 9
        Vector v = new Vector(1, 2, 2);
        assertEquals(9, v.lengthSquared(), DELTA, "lengthSquared() wrong result");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void length() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: length of (0,3,4) = 5
        Vector v = new Vector(0, 3, 4);
        assertEquals(5, v.length(), DELTA, "length() wrong result");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void normalize() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Check length after normalization is 1
        Vector v = new Vector(0, 3, 4);
        Vector normalized = v.normalize();
        assertEquals(1, normalized.length(), DELTA, "normalize() result not unit vector");

        // TC02: Check direction remains the same
        assertEquals(1, v.normalize().dotProduct(normalized), DELTA, "normalize() changed direction");
        assertTrue(normalized.dotProduct(v) > 0, "normalize() flipped direction");
    }
}
