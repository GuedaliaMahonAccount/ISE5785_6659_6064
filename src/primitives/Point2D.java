// in primitives/Point2D.java
package primitives;

/**
 * A simple 2D point or offset in the plane.
 * Used for sampling offsets in anti-aliasing, DOF, etc.
 */
public class Point2D {
    private final double x;
    private final double y;

    /** Create an offset (x, y) in normalized pixel units (e.g. –0.5…+0.5). */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** @return the x coordinate */
    public double getX() { return x; }

    /** @return the y coordinate */
    public double getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D p)) return false;
        return Double.compare(p.x, x) == 0 &&
                Double.compare(p.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Point2D(%.3f, %.3f)", x, y);
    }
}
