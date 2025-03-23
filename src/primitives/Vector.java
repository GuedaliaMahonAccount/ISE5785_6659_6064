package primitives;

/**
 * Class Vector represents a vector in 3D Cartesian coordinate system.
 * The vector is immutable and cannot be the zero vector.
 */
public class Vector {
    final Double3 xyz;

    /** Constructor that receives three coordinate values */
    public Vector(double x, double y, double z) {
        this(new Double3(x, y, z));
    }

    /** Constructor that receives a Double3 and ensures it is not a zero vector */
    public Vector(Double3 xyz) {
        if (xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
        this.xyz = xyz;
    }

    /** Adds another vector and returns the resulting vector */
    public Vector add(Vector other) {
        return new Vector(this.xyz.add(other.xyz));
    }

    /** Subtracts another vector and returns the resulting vector */
    public Vector subtract(Vector other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /** Multiplies this vector by a scalar (scaling) */
    public Vector scale(double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /** Dot product of this vector with another */
    public double dotProduct(Vector other) {
        Double3 a = this.xyz;
        Double3 b = other.xyz;
        return a.d1 * b.d1 + a.d2 * b.d2 + a.d3 * b.d3;
    }

    /** Cross product of this vector with another */
    public Vector crossProduct(Vector other) {
        double x1 = this.xyz.d1, y1 = this.xyz.d2, z1 = this.xyz.d3;
        double x2 = other.xyz.d1, y2 = other.xyz.d2, z2 = other.xyz.d3;

        return new Vector(
                y1 * z2 - z1 * y2,
                z1 * x2 - x1 * z2,
                x1 * y2 - y1 * x2
        );
    }

    /** Returns the squared length of the vector */
    public double lengthSquared() {
        Double3 d = this.xyz;
        return d.d1 * d.d1 + d.d2 * d.d2 + d.d3 * d.d3;
    }

    /** Returns the length (magnitude) of the vector */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /** Normalizes the vector (returns a unit vector in the same direction) */
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
