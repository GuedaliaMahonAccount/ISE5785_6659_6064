package primitives;

import static primitives.Util.isZero;

/**
 * The {@code Vector} class represents a non-zero vector in 3D Cartesian space.
 * It *extends* Point so that you can use a Vector anywhere a Point is expected.
 */
public class Vector extends Point {
    // standard unit axes:
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructs a non-zero vector from three coordinates.
     * @throws IllegalArgumentException if (x,y,z) == (0,0,0)
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (this.xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
    }

    /**
     * Constructs a non-zero vector from a Double3.
     * @throws IllegalArgumentException if the triple is zero
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
    }

    /** Vector-vector addition */
    public Vector add(Vector other) {
        return new Vector(this.xyz.add(other.xyz));
    }

    /** Vector-vector subtraction */
    public Vector subtract(Vector other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /** Scale this vector by a scalar */
    public Vector scale(double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /** Dot product */
    public double dotProduct(Vector other) {
        Double3 a = this.xyz, b = other.xyz;
        return a.d1()*b.d1() + a.d2()*b.d2() + a.d3()*b.d3();
    }

    /** Cross product */
    public Vector crossProduct(Vector other) {
        double x1 = xyz.d1(), y1 = xyz.d2(), z1 = xyz.d3();
        double x2 = other.xyz.d1(), y2 = other.xyz.d2(), z2 = other.xyz.d3();
        return new Vector(
                y1*z2 - z1*y2,
                z1*x2 - x1*z2,
                x1*y2 - y1*x2
        );
    }

    /**
     * Alias for crossProduct, so you can call v.cross(u) directly.
     */
    public Vector cross(Vector other) {
        return crossProduct(other);
    }

    /** Squared length */
    public double lengthSquared() {
        Double3 d = this.xyz;
        return d.d1()*d.d1() + d.d2()*d.d2() + d.d3()*d.d3();
    }

    /** Length */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /** Normalize to a unit vector */
    public Vector normalize() {
        double len = length();
        if (isZero(len))
            throw new ArithmeticException("Cannot normalize zero vector");
        return scale(1.0 / len);
    }

    /**
     * Rotate this vector around the given axis by the given angle (in radians).
     * Uses Rodrigues' rotation formula: v_rot = v*cosθ + (k × v)*sinθ + k*(k·v)*(1−cosθ),
     * where k is the (unit) rotation axis.
     *
     * @param axis  the axis to rotate around (will be normalized internally)
     * @param angle rotation angle in radians
     * @return new, rotated Vector
     */
    public Vector rotate(Vector axis, double angle) {
        Vector k = axis.normalize();
        Vector v = this;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        // v * cosθ
        Vector term1 = v.scale(cos);
        // (k × v) * sinθ
        Vector term2 = k.crossProduct(v).scale(sin);
        // k * (k·v) * (1−cosθ)
        Vector term3 = k.scale(k.dotProduct(v) * (1 - cos));
        return term1.add(term2).add(term3);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj ||
                (obj instanceof Vector other && this.xyz.equals(other.xyz));
    }

    @Override
    public String toString() {
        return "Vector" + xyz;
    }
}
