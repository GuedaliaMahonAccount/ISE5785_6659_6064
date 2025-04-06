package primitives; // adjust this to match your test package

import org.junit.jupiter.api.Test;
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
}
