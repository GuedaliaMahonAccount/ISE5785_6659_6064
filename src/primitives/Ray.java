package primitives;

import geometries.Intersectable.GeoPoint;
import java.util.List;

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
     * Constructs a ray from a starting point and a direction.
     * @param p0 origin point
     * @param dir direction vector
     */
    public Ray(Point p0, Vector dir) {
        this.p0 = p0;
        this.dir = dir.normalize();
    }

    public Point getP0() {
        return p0;
    }

    public Vector getDir() {
        return dir;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj ||
                (obj instanceof Ray other &&
                        p0.equals(other.p0) &&
                        dir.equals(other.dir));
    }

    @Override
    public String toString() {
        return "Ray{" + p0 + ", " + dir + "}";
    }

    /**
     * Returns a point on the ray at a distance d from the starting point.
     * @param d the distance from the starting point
     * @return the point on the ray
     */
    public Point getPoint(double d) {
        if (Util.isZero(d))
            return p0;
        return p0.add(dir.scale(d));
    }

    /**
     * Finds the closest GeoPoint to the origin of this ray.
     * @param geoPoints List of GeoPoints to check.
     * @return The closest GeoPoint, or null if the list is null or empty.
     */
    public GeoPoint findClosestGeoPoint(List<GeoPoint> geoPoints) {
        if (geoPoints == null || geoPoints.isEmpty()) {
            return null;
        }
        GeoPoint closest = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (GeoPoint gp : geoPoints) {
            double d = p0.distance(gp.point);
            if (d < minDist) {
                minDist = d;
                closest = gp;
            }
        }
        return closest;
    }
}
