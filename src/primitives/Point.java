package primitives;

/**
 * Class Point represents a point in 3D Cartesian coordinate system.
 * Each point has three coordinates (x, y, z) represented by Double3.
 */
public class Point {
    protected final Double3 xyz;

    /** Static constant representing the origin (0,0,0) */
    public static final Point ZERO = new Point(0, 0, 0);

    /** Constructor with three coordinate values */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    /** Constructor with a Double3 object */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    /** Subtracts another point and returns a Vector from this point to the other */
    public Vector subtract(Point other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /** Adds a vector to the point and returns a new point */
    public Point add(Vector vector) {
        return new Point(this.xyz.add(vector.xyz));
    }

    /** Calculates the squared distance between two points */
    public double distanceSquared(Point other) {
        Double3 d = this.xyz.subtract(other.xyz);
        return d.d1() * d.d1() + d.d2() * d.d2() + d.d3() * d.d3();
    }




    /** Calculates the distance between two points */
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
