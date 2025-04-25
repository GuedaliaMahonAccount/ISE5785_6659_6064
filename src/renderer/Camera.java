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

        /**
         * Set camera position
         * @param p point in 3D space
         * @return this builder
         */
        public Builder setLocation(Point p) {
            camera.position = p;
            return this;
        }

        /**
         * Set direction vectors explicitly
         * @param to forward direction vector
         * @param up upward vector
         * @return this builder
         * @throws IllegalArgumentException if vectors are not orthogonal
         */
        public Builder setDirection(Vector to, Vector up) {
            if (!isZero(to.dotProduct(up))) {
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            }
            camera.vTo = to.normalize();
            camera.vUp = up.normalize();
            return this;
        }

        /**
         * Set camera to look at a target point using a default up vector (0,1,0)
         * @param target point to look at
         * @return this builder
         * @throws IllegalArgumentException if the default up vector is parallel to vTo
         */
        public Builder setDirection(Point target) {
            // Compute forward direction
            Vector to = target.subtract(camera.position).normalize();
            // Default up direction
            Vector defaultUp = new Vector(0, 1, 0);
            // Remove component along to
            double dot = defaultUp.dotProduct(to);
            Vector upProj = defaultUp.subtract(to.scale(dot));
            // Check for degeneracy
            if (isZero(upProj.length())) {
                throw new IllegalArgumentException("Default up vector is parallel to view direction");
            }
            camera.vTo = to;
            camera.vUp = upProj.normalize();
            return this;
        }

        /**
         * Set camera to look at a target point with a provided up vector
         * @param target point to look at
         * @param up    user-provided up vector
         * @return this builder
         * @throws IllegalArgumentException if the provided up vector is parallel to vTo
         */
        public Builder setDirection(Point target, Vector up) {
            // Compute forward direction
            Vector to = target.subtract(camera.position).normalize();
            // Normalize the provided up vector
            Vector upNorm = up.normalize();
            // Remove component along to
            double dot = upNorm.dotProduct(to);
            Vector upProj = upNorm.subtract(to.scale(dot));
            // Check for degeneracy
            if (isZero(upProj.length())) {
                throw new IllegalArgumentException("Provided up vector is parallel to view direction");
            }
            camera.vTo = to;
            camera.vUp = upProj.normalize();
            return this;
        }

        /**
         * Set the size of the view plane
         * @param width  width in world units
         * @param height height in world units
         * @return this builder
         */
        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Set the resolution (pixel count) on the view plane
         * @param nX number of columns (pixels)
         * @param nY number of rows (pixels)
         * @return this builder
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Set the distance from camera to view plane
         * @param distance in world units
         * @return this builder
         */
        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        /**
         * Final build step - validate and return a fully initialized Camera object
         * @return built Camera
         * @throws MissingResourceException if required fields are missing or invalid
         */
        public Camera build() {
            if (camera.position == null)
                throw new MissingResourceException(MISSING_RESOURCE, CAMERA_CLASS_NAME, "position");
            if (camera.vTo == null || camera.vUp == null)
                throw new MissingResourceException(MISSING_RESOURCE, CAMERA_CLASS_NAME, "vTo or vUp");
            if (isZero(camera.distance) || isZero(camera.height) || isZero(camera.width))
                throw new MissingResourceException(MISSING_RESOURCE, CAMERA_CLASS_NAME, "distance, width, or height");

            // Compute right direction
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return camera.clone();
        }
    }
}
