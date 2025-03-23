package primitives;

/**
 * Ray class represents a ray in 3D space defined by a point and a direction vector.
 */
public class Ray {
    private final Point p0;
    private final Vector dir;

    /** Constructor that ensures the direction is normalized */
    public Ray(Point p0, Vector dir) {
        this.p0 = p0;
        this.dir = dir.normalize(); // Always store as normalized vector
    }

    public Point getP0() {
        return p0;
    }

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
