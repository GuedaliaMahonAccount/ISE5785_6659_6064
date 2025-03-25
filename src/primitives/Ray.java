package primitives;

/**
 * The {@code Ray} class represents a ray in 3D space.
 * A ray is defined by a starting point and a direction vector.
 */
public class Ray {
    /** The starting point of the ray. */
    private final Point p0;
    /** The normalized direction vector of the ray. */
    private final Vector dir;

    /**
     * Constructs a ray with a given point and direction.
     * The direction is normalized automatically.
     *
     * @param p0  the starting point of the ray
     * @param dir the direction vector of the ray
     */
    public Ray(Point p0, Vector dir) {
        this.p0 = p0;
        this.dir = dir.normalize();
    }

    /**
     * Returns the starting point of the ray.
     *
     * @return the point p0
     */
    public Point getP0() {
        return p0;
    }

    /**
     * Returns the normalized direction vector of the ray.
     *
     * @return the direction vector
     */
    public Vector getDir() {
        return dir;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Ray other
                && p0.equals(other.p0)
                && dir.equals(other.dir));
    }

    @Override
    public String toString() {
        return "Ray{" + p0 + ", " + dir + "}";
    }
}
