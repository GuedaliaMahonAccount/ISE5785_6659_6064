package primitives;

import java.util.Objects;

/**
 * Wrapper class for java.awt.Color. The constructors operate with any
 * non-negative RGB values. The colors are maintained without upper limit of
 * 255. Some additional operations are added that are useful for manipulating
 * light's colors.
 */
public class Color {
   /**
    * The internal fields maintain RGB components as double numbers from 0 to
    * whatever...
    */
   private final Double3 rgb;

   /** Black color = (0,0,0) */
   public static final Color BLACK = new Color();

   /** White color = (255,255,255) */
   public static final Color WHITE = new Color(255, 255, 255);


   /** Default constructor - to generate Black Color (privately) */
   private Color() {
      rgb = Double3.ZERO;
   }

   /**
    * Constructor to generate a color according to RGB components. Each component
    * in range 0..255 (for printed white color) or more [for lights].
    * @param r Red component
    * @param g Green component
    * @param b Blue component
    */
   public Color(double r, double g, double b) {
      if (r < 0 || g < 0 || b < 0)
         throw new IllegalArgumentException("Negative color component is illegal");
      rgb = new Double3(r, g, b);
   }

   /**
    * Constructor to generate a color from a Double3 object.
    * @param rgb triad of Red/Green/Blue components
    */
   private Color(Double3 rgb) {
      if (rgb.d1() < 0 || rgb.d2() < 0 || rgb.d3() < 0)
         throw new IllegalArgumentException("Negative color component is illegal");
      this.rgb = rgb;
   }

   /**
    * Constructor on the basis of a java.awt.Color object.
    * @param other java.awt.Color's source object
    */
   public Color(java.awt.Color other) {
      rgb = new Double3(other.getRed(), other.getGreen(), other.getBlue());
   }

   /**
    * Color getter - returns the color after converting it into java.awt.Color
    * object. During the conversion any component bigger than 255 is set to 255.
    * @return java.awt.Color object based on this Color RGB components
    */
   public java.awt.Color getColor() {
      int ir = (int) rgb.d1();
      int ig = (int) rgb.d2();
      int ib = (int) rgb.d3();
      return new java.awt.Color(
              Math.min(ir, 255),
              Math.min(ig, 255),
              Math.min(ib, 255)
      );
   }

   /**
    * Adds multiple colors to this color.
    * @param colors one or more other colors to add
    * @return new Color object which is a result of the operation
    */
   public Color add(Color... colors) {
      Double3 result = rgb;
      for (Color c : colors) {
         result = result.add(c.rgb);
      }
      return new Color(result);
   }

   /**
    * Scales the color by a scalar triad per rgb.
    * @param k scale factor per rgb
    * @return new Color object which is the result of the operation
    */
   public Color scale(Double3 k) {
      if (k.d1() < 0.0 || k.d2() < 0.0 || k.d3() < 0.0)
         throw new IllegalArgumentException("Can't scale a color by a negative number");
      return new Color(rgb.product(k));
   }

   /**
    * Scales the color by a scalar.
    * @param k scale factor
    * @return new Color object which is the result of the operation
    */
   public Color scale(double k) {
      if (k < 0.0)
         throw new IllegalArgumentException("Can't scale a color by a negative number");
      return new Color(rgb.scale(k));
   }

   /**
    * Scales the color by (1 / reduction factor).
    * @param k reduction factor
    * @return new Color object which is the result of the operation
    */
   public Color reduce(int k) {
      if (k < 1)
         throw new IllegalArgumentException("Can't scale a color by a number lower than 1");
      return new Color(rgb.reduce(k));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Color color = (Color) obj;
      // Use a small tolerance for comparison to avoid floating-point errors
      double tolerance = 0.001;
      return Math.abs(color.rgb.d1() - rgb.d1()) < tolerance &&
              Math.abs(color.rgb.d2() - rgb.d2()) < tolerance &&
              Math.abs(color.rgb.d3() - rgb.d3()) < tolerance;
   }

   @Override
   public int hashCode() {
      return Objects.hash(rgb);
   }

   @Override
   public String toString() {
      return "rgb:" + rgb;
   }
}
