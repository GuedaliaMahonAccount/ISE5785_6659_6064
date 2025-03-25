package primitives;

/**
 * The {@code Vector} class represents a vector in 3D Cartesian space.
 * Vectors are immutable and cannot be the zero vector.
 */
public class Vector {
    final Double3 xyz;

    /**
     * Constructs a vector with the specified x, y, and z components.
     *
     * @param x the x-component
     * @param y the y-component
     * @param z the z-component
     */
    public Vector(double x, double y, double z) {
        this(new Double3(x, y, z));
    }

    /**
     * Constructs a vector using a {@code Double3} object.
     *
     * @param xyz the {@code Double3} representing the components
     * @throws IllegalArgumentException if the vector is the zero vector
     */
    public Vector(Double3 xyz) {
        if (xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
        this.xyz = xyz;
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other the other vector to add
     * @return the resulting vector
     */
    public Vector add(Vector other) {
        return new Vector(this.xyz.add(other.xyz));
    }

    /**
     * Subtracts another vector from this vector.
     *
     * @param other the vector to subtract
     * @return the resulting vector
     */
    public Vector subtract(Vector other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /**
     * Scales this vector by a scalar.
     *
     * @param scalar the scalar value
     * @return the scaled vector
     */
    public Vector scale(double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /**
     * Computes the dot product with another vector.
     *
     * @param other the other vector
     * @return the dot product result
     */
    public double dotProduct(Vector other) {
        Double3 a = this.xyz;
        Double3 b = other.xyz;
        return a.d1() * b.d1() + a.d2() * b.d2() + a.d3() * b.d3();
    }

    /**
     * Computes the cross product with another vector.
     *
     * @param other the other vector
     * @return the resulting vector perpendicular to both
     */
    public Vector crossProduct(Vector other) {
        double x1 = this.xyz.d1(), y1 = this.xyz.d2(), z1 = this.xyz.d3();
        double x2 = other.xyz.d1(), y2 = other.xyz.d2(), z2 = other.xyz.d3();

        return new Vector(
                y1 * z2 - z1 * y2,
                z1 * x2 - x1 * z2,
                x1 * y2 - y1 * x2
        );
    }

    /**
     * Returns the squared length of the vector.
     *
     * @return the squared length
     */
    public double lengthSquared() {
        Double3 d = this.xyz;
        return d.d1() * d.d1() + d.d2() * d.d2() + d.d3() * d.d3();
    }

    /**
     * Returns the length (magnitude) of the vector.
     *
     * @return the length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes this vector (returns a unit vector).
     *
     * @return the normalized vector
     * @throws ArithmeticException if the vector is zero
     */
    public Vector normalize() {
        double len = length();
        if (Util.isZero(len)) throw new ArithmeticException("Cannot normalize zero vector");
        return scale(1 / len);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Vector other && this.xyz.equals(other.xyz));
    }

    @Override
    public String toString() {
        return "Vector" + xyz;
    }
}
