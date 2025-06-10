package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import primitives.*;
import renderer.Camera;

/**
 * Unit tests for the Camera class.
 * <p>
 * Verifies the constructRay method for correct ray directions
 * and tests the Builder for proper configuration and error handling.
 * </p>
 *
 * @author Dan
 */
class CameraTest {
   /**
    * Builder configured with default location at the origin and view plane distance of 10.
    */
   private final Camera.Builder cameraBuilder = Camera.getBuilder()
           .setLocation(Point.ZERO)
           .setVpDistance(10);

   /**
    * Message used when a generated ray does not match the expected result.
    */
   private static final String BAD_RAY = "Bad ray";

   /**
    * Tests the constructRay method for both equivalence partitions and boundary values.
    * <ul>
    *   <li>EP01: inside pixel ray direction for a 4x4 view plane</li>
    *   <li>BV01-06: various edge cases for view planes of different sizes</li>
    * </ul>
    */
   @Test
   void testConstructRay() {
      cameraBuilder.setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0));
      Camera camera1 = cameraBuilder.setVpSize(8, 8).build();
      Camera camera2 = cameraBuilder.setVpSize(6, 6).build();

      // ============ Equivalence Partitions Tests ==============
      // EP01: 4x4 inside (1,1)
      assertEquals(new Ray(Point.ZERO, new Vector(1, -1, -10)),
              camera1.constructRay(4, 4, 1, 1), BAD_RAY);

      // =============== Boundary Values Tests ==================
      // BV01: 4x4 corner (0,0)
      assertEquals(new Ray(Point.ZERO, new Vector(3, -3, -10)),
              camera1.constructRay(4, 4, 0, 0), BAD_RAY);

      // BV02: 4x4 side (1,0)
      assertEquals(new Ray(Point.ZERO, new Vector(1, -3, -10)),
              camera1.constructRay(4, 4, 1, 0), BAD_RAY);

      // BV03: 3x3 center (1,1)
      assertEquals(new Ray(Point.ZERO, new Vector(0, 0, -10)),
              camera2.constructRay(3, 3, 1, 1), BAD_RAY);

      // BV04: 3x3 center of upper side (1,0)
      assertEquals(new Ray(Point.ZERO, new Vector(0, -2, -10)),
              camera2.constructRay(3, 3, 1, 0), BAD_RAY);

      // BV05: 3x3 center of left side (0,1)
      assertEquals(new Ray(Point.ZERO, new Vector(2, 0, -10)),
              camera2.constructRay(3, 3, 0, 1), BAD_RAY);

      // BV06: 3x3 corner (0,0)
      assertEquals(new Ray(Point.ZERO, new Vector(2, -2, -10)),
              camera2.constructRay(3, 3, 0, 0), BAD_RAY);
   }

   /**
    * Tests the Builder's setDirection methods for correct ray construction
    * and verifies that invalid configurations throw exceptions.
    * <ul>
    *   <li>EP01: target point without up vector</li>
    *   <li>EP02: target point with specified up vector</li>
    *   <li>BV01: invalid target at camera location</li>
    * </ul>
    */
   @Test
   void testBuilder() {
      cameraBuilder.setVpSize(4, 4).setResolution(2, 2);

      // EP01: set to a target point without up vector
      Point target1 = new Point(10, -10, 0);
      Camera camera1 = cameraBuilder.setDirection(target1).build();
      Point center1 = target1.subtract(Point.ZERO).normalize().scale(10);
      Vector right1 = Vector.AXIS_Z;
      Vector up1 = new Vector(1, 1, 0).normalize();
      Vector direction1 = center1.add(up1).subtract(right1).subtract(Point.ZERO);
      assertEquals(new Ray(Point.ZERO, direction1), camera1.constructRay(2, 2, 0, 0));

      // EP02: set to a target point with up vector
      Point target2 = new Point(0, 5, 0);
      Camera camera2 = cameraBuilder.setDirection(target2, new Vector(0, 1, 1)).build();
      Point center2 = new Point(0, 10, 0);
      Vector right2 = Vector.AXIS_X;
      Vector up2 = Vector.AXIS_Z;
      Vector direction2 = center2.add(up2).subtract(right2).subtract(Point.ZERO);
      assertEquals(new Ray(Point.ZERO, direction2), camera2.constructRay(2, 2, 0, 0));

      // BV01: invalid target at camera location
      assertThrows(IllegalArgumentException.class,
              () -> cameraBuilder.setDirection(Point.ZERO).build());
   }
}
