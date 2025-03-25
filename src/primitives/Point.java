package primitives;

/**
 * The {@code Point} class represents a point in 3D space using Cartesian coordinates.
 * A point is defined by three double values (x, y, z) encapsulated in a {@link Double3}.
 */
public class Point {
    protected final Double3 xyz;

    /** Static constant representing the origin (0, 0, 0). */
    public static final Point ZERO = new Point(0, 0, 0);

    /**
     * Constructs a {@code Point} from three coordinate values.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a {@code Point} from a {@code Double3} object.
     *
     * @param xyz the {@code Double3} containing the coordinates
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    /**
     * Subtracts another point from this point, returning the resulting vector.
     *
     * @param other the point to subtract
     * @return a {@link Vector} from {@code other} to this point
     */
    public Vector subtract(Point other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /**
     * Adds a vector to this point, returning a new point.
     *
     * @param vector the vector to add
     * @return a new {@code Point} representing the result
     */
    public Point add(Vector vector) {
        return new Point(this.xyz.add(vector.xyz));
    }

    /**
     * Computes the squared distance between this point and another.
     *
     * @param other the other point
     * @return the squared distance
     */
    public double distanceSquared(Point other) {
        Double3 d = this.xyz.subtract(other.xyz);
        return d.d1() * d.d1() + d.d2() * d.d2() + d.d3() * d.d3();
    }

    /**
     * Computes the distance between this point and another.
     *
     * @param other the other point
     * @return the distance
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other) && this.xyz.equals(other.xyz);
    }

    @Override
    public String toString() {
        return "Point" + xyz;
    }
}
