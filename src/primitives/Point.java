package primitives;

/**
 * The {@code Point} class represents a point in a 3D Cartesian coordinate system.
 * Coordinates are stored internally using a {@link Double3} for precision.
 */
public class Point {
    /**
     * Encapsulates the (x, y, z) coordinates of this point.
     */
    protected final Double3 xyz;

    /**
     * A constant representing the origin point (0, 0, 0).
     */
    public static final Point ZERO = new Point(0, 0, 0);

    /**
     * Constructs a point given explicit x, y, and z coordinate values.
     *
     * @param x x-coordinate value
     * @param y y-coordinate value
     * @param z z-coordinate value
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point from an existing {@link Double3} instance.
     *
     * @param xyz the Double3 encapsulating (x, y, z)
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    /**
     * Subtracts another point from this point, producing the vector from the other point to this one.
     *
     * @param other the point to subtract
     * @return a {@link Vector} representing (this - other)
     */
    public Vector subtract(Point other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /**
     * Translates this point by the inverse of the given vector (point - vector).
     *
     * @param v the vector to subtract from this point
     * @return a new {@code Point} representing (this - v)
     */
    public Point subtract(Vector v) {
        return new Point(this.xyz.subtract(v.xyz));
    }

    /**
     * Translates this point by the given vector (point + vector).
     *
     * @param vector the vector to add to this point
     * @return a new {@code Point} representing (this + vector)
     */
    public Point add(Vector vector) {
        return new Point(this.xyz.add(vector.xyz));
    }

    /**
     * Computes the squared Euclidean distance between this point and another point.
     * Useful for distance comparisons without the cost of a square root.
     *
     * @param other the other point
     * @return squared distance between the points
     */
    public double distanceSquared(Point other) {
        Double3 d = this.xyz.subtract(other.xyz);
        return d.d1() * d.d1() + d.d2() * d.d2() + d.d3() * d.d3();
    }

    /**
     * Computes the Euclidean distance between this point and another point.
     *
     * @param other the other point
     * @return distance between the points
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Checks if this point is equal to another object.
     * Two points are equal if their coordinate triples are equal.
     *
     * @param obj the object to compare
     * @return {@code true} if obj is a Point with the same coordinates
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other) && this.xyz.equals(other.xyz);
    }

    /**
     * Returns a string representation of this point in the form "Point(x, y, z)".
     *
     * @return formatted string of the point's coordinates
     */
    @Override
    public String toString() {
        return "Point" + xyz;
    }

    /**
     * Retrieves the x-coordinate of this point.
     *
     * @return x-coordinate value
     */
    public double getX() {
        return xyz.d1();
    }

    /**
     * Retrieves the y-coordinate of this point.
     *
     * @return y-coordinate value
     */
    public double getY() {
        return xyz.d2();
    }

    /**
     * Retrieves the z-coordinate of this point.
     *
     * @return z-coordinate value
     */
    public double getZ() {
        return xyz.d3();
    }
}
