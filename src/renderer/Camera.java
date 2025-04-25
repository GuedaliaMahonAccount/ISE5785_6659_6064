package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

/**
 * Camera class represents a camera in 3D space using a Builder pattern.
 * It supports ray construction through a view plane.
 */
public class Camera implements Cloneable {

    // === Camera core parameters ===
    private Point position;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;
    private double width = 0.0;
    private double height = 0.0;
    private double distance = 0.0;

    // Resolution (number of pixels) on the view plane
    private int nX = 0;
    private int nY = 0;

    private static final String MISSING_RESOURCE = "Missing Resource";
    private static final String CAMERA_CLASS_NAME = "Camera";

    /** Private constructor - use Builder to instantiate */
    private Camera() {}

    /** @return Camera position */
    public Point getPosition() {
        return position;
    }

    /** @return forward vector */
    public Vector getVTo() {
        return vTo;
    }

    /** @return upward vector */
    public Vector getVUp() {
        return vUp;
    }

    /** @return right vector */
    public Vector getVRight() {
        return vRight;
    }

    /** @return view plane width */
    public double getWidth() {
        return width;
    }

    /** @return view plane height */
    public double getHeight() {
        return height;
    }

    /** @return view plane distance */
    public double getDistance() {
        return distance;
    }

    /** @return horizontal pixel count (resolution) */
    public int getNx() {
        return nX;
    }

    /** @return vertical pixel count (resolution) */
    public int getNy() {
        return nY;
    }

    /**
     * Create a Builder to initialize Camera step-by-step.
     * @return Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Create a ray through the center of a pixel on the view plane.
     * @param nX number of columns
     * @param nY number of rows
     * @param j column index
     * @param i row index
     * @return Ray from camera through pixel center
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        Point pCenter = position.add(vTo.scale(distance));
        double rX = width / nX;
        double rY = height / nY;

        double xJ = (j - (nX - 1) / 2.0) * rX;
        double yI = -(i - (nY - 1) / 2.0) * rY;

        Point pIJ = pCenter;
        if (!isZero(xJ)) pIJ = pIJ.add(vRight.scale(xJ));
        if (!isZero(yI)) pIJ = pIJ.add(vUp.scale(yI));

        Vector direction = pIJ.subtract(position).normalize();
        return new Ray(position, direction);
    }

    /**
     * Clone method to support returning camera from builder
     * @return deep copy of this Camera
     */
    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed", e);
        }
    }

    /**
     * Builder for Camera construction
     */
    public static class Builder {
        private final Camera camera = new Camera();

        public Builder setLocation(Point p) {
            if (p == null) throw new IllegalArgumentException("Location must not be null");
            camera.position = p;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {
            if (to == null || up == null) throw new IllegalArgumentException("Direction vectors must not be null");
            if (!isZero(to.dotProduct(up))) {
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            }
            camera.vTo = to.normalize();
            camera.vUp = up.normalize();
            return this;
        }

        public Builder setDirection(Point target) {
            // Compute forward direction
            Vector to = target.subtract(camera.position).normalize();
            Vector defaultUp = new Vector(0, 1, 0);
            double dot = defaultUp.dotProduct(to);
            Vector upProj = defaultUp.subtract(to.scale(dot));
            if (isZero(upProj.length())) {
                throw new IllegalArgumentException("Default up vector is parallel to view direction");
            }
            camera.vTo = to;
            camera.vUp = upProj.normalize();
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            Vector to = target.subtract(camera.position).normalize();
            Vector upNorm = up.normalize();
            double dot = upNorm.dotProduct(to);
            Vector upProj = upNorm.subtract(to.scale(dot));
            if (isZero(upProj.length())) {
                throw new IllegalArgumentException("Provided up vector is parallel to view direction");
            }
            camera.vTo = to;
            camera.vUp = upProj.normalize();
            return this;
        }

        public Builder setVpSize(double width, double height) {
            if (width <= 0 || height <= 0) throw new IllegalArgumentException("View plane size must be positive");
            camera.width = width;
            camera.height = height;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            if (nX <= 0 || nY <= 0) throw new IllegalArgumentException("Resolution must be positive");
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        public Builder setVpDistance(double distance) {
            if (distance <= 0) throw new IllegalArgumentException("View plane distance must be positive");
            camera.distance = distance;
            return this;
        }

        // === Bonus: transformations ===

        /**
         * Translate the camera position by the given vector.
         * @param delta translation vector
         * @return this builder
         */
        public Builder translate(Vector delta) {
            if (delta == null) throw new IllegalArgumentException("Translation vector must not be null");
            camera.position = camera.position.add(delta);
            return this;
        }

        /**
         * Rotate a vector around an axis by the given angle (radians).
         */
        private static Vector rotateVector(Vector v, Vector axis, double angle) {
            Vector k = axis.normalize();
            return v.scale(Math.cos(angle))
                    .add(k.crossProduct(v).scale(Math.sin(angle)))
                    .add(k.scale(k.dotProduct(v) * (1 - Math.cos(angle))));
        }

        /**
         * Rotate camera around its up axis (yaw) by the given angle in degrees.
         * @param angleDegrees rotation angle
         * @return this builder
         */
        public Builder rotateYaw(double angleDegrees) {
            if (isZero(angleDegrees)) return this;
            double rad = Math.toRadians(angleDegrees);
            camera.vTo = rotateVector(camera.vTo, camera.vUp, rad).normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        /**
         * Rotate camera around its right axis (pitch) by the given angle in degrees.
         * @param angleDegrees rotation angle
         * @return this builder
         */
        public Builder rotatePitch(double angleDegrees) {
            if (isZero(angleDegrees)) return this;
            double rad = Math.toRadians(angleDegrees);
            camera.vTo = rotateVector(camera.vTo, camera.vRight, rad).normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            return this;
        }

        /**
         * Rotate camera around its forward axis (roll) by the given angle in degrees.
         * @param angleDegrees rotation angle
         * @return this builder
         */
        public Builder rotateRoll(double angleDegrees) {
            if (isZero(angleDegrees)) return this;
            double rad = Math.toRadians(angleDegrees);
            camera.vUp = rotateVector(camera.vUp, camera.vTo, rad).normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        /**
         * Final build step - validate and return a fully initialized Camera object
         */
        public Camera build() {
            if (camera.position == null)
                throw new MissingResourceException(MISSING_RESOURCE, CAMERA_CLASS_NAME, "position");
            if (camera.vTo == null || camera.vUp == null)
                throw new MissingResourceException(MISSING_RESOURCE, CAMERA_CLASS_NAME, "vTo or vUp");
            if (isZero(camera.distance) || isZero(camera.height) || isZero(camera.width))
                throw new MissingResourceException(MISSING_RESOURCE, CAMERA_CLASS_NAME, "distance, width, or height");

            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return camera.clone();
        }
    }
}
