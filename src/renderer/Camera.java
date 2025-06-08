package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * The Camera class represents a virtual camera in a 3D scene.
 * It defines the camera's position, orientation, and view plane properties,
 * and provides methods to construct rays, render images, and apply a grid overlay.
 */
public class Camera implements Cloneable {
    /** The forward direction vector of the camera. */
    private Vector vTo;
    /** The upward direction vector of the camera. */
    private Vector vUp;
    /** The rightward direction vector of the camera. */
    private Vector vRight;

    /** The camera's position point in 3D space. */
    private Point p0;
    /** The center point of the view plane. */
    private Point pcenter;

    /** The distance from the camera to the view plane. */
    private double distance;
    /** The width of the view plane. */
    private double width;
    /** The height of the view plane. */
    private double height;

    /** The ImageWriter responsible for writing pixel data to an image. */
    private ImageWriter imageWriter;
    /** The RayTracer used to trace rays through the scene. */
    private RayTracerBase rayTracer;

    /**
     * Private default constructor to enforce use of the Builder.
     */
    private Camera() {
        // use Builder to construct
    }

    /**
     * Returns a Builder for setting up and creating a Camera instance.
     *
     * @return a new Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera position through a specific pixel on the view plane.
     *
     * @param nX number of pixels in the x-direction
     * @param nY number of pixels in the y-direction
     * @param j  pixel column index (x)
     * @param i  pixel row index (y)
     * @return a Ray from the camera through the specified pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        double pixelWidth = width / nX;
        double pixelHeight = height / nY;

        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = (i - (nY - 1) / 2.0) * pixelHeight;

        Point pIJ = pcenter;
        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(-yI));
        }

        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Renders the entire image by casting a ray through each pixel.
     *
     * @return this Camera instance for chaining
     */
    public Camera renderImage() {
        int nX = imageWriter.nX();
        int nY = imageWriter.nY();

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(nX, nY, j, i);
            }
        }

        return this;
    }

    /**
     * Casts and traces a ray through a single pixel, then writes the resulting color.
     *
     * @param nX total pixels in x-direction
     * @param nY total pixels in y-direction
     * @param j  pixel column index
     * @param i  pixel row index
     */
    private void castRay(int nX, int nY, int j, int i) {
        Ray ray = constructRay(nX, nY, j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);
    }

    /**
     * Draws a grid overlay on the rendered image at specified pixel intervals.
     *
     * @param interval spacing between grid lines in pixels
     * @param color    color of the grid lines
     * @return this Camera instance for chaining
     */
    public Camera printGrid(int interval, Color color) {
        int nX = imageWriter.nX();
        int nY = imageWriter.nY();

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }

        return this;
    }

    /**
     * Saves the rendered image to a file with the given name.
     *
     * @param imageName output file name (without extension)
     */
    public void writeToImage(String imageName) {
        imageWriter.writeToImage(imageName);
    }

    // ----------------------- Getters -----------------------

    /**
     * Returns the forward direction vector of the camera.
     *
     * @return the forward direction vector of the camera
     */
    public Vector getVTo() { return vTo; }

    /**
     * Returns the upward direction vector of the camera.
     *
     * @return the upward direction vector of the camera
     */
    public Vector getVUp() { return vUp; }

    /**
     * Returns the rightward direction vector of the camera.
     *
     * @return the rightward direction vector of the camera
     */
    public Vector getVRight() { return vRight; }

    /**
     * Returns the camera position point.
     *
     * @return the camera position point
     */
    public Point getP0() { return p0; }

    /**
     * Returns the center point of the view plane.
     *
     * @return the center point of the view plane
     */
    public Point getPcenter() { return pcenter; }

    /**
     * Returns the distance from camera to view plane.
     *
     * @return the distance from camera to view plane
     */
    public double getDistance() { return distance; }

    /**
     * Returns the view plane width.
     *
     * @return the view plane width
     */
    public double getWidth() { return width; }

    /**
     * Returns the view plane height.
     *
     * @return the view plane height
     */
    public double getHeight() { return height; }

    /**
     * Returns the ImageWriter used by this camera.
     *
     * @return the ImageWriter used by this camera
     */
    public ImageWriter getImageWriter() { return imageWriter; }

    /**
     * Builder for constructing Camera instances with custom parameters.
     */
    public static class Builder {
        /** The Camera instance being configured by this Builder. */
        private final Camera camera = new Camera();
        /** The target point the camera will look at, if set. */
        private Point target = null;

        /**
         * Default constructor initializing a new Camera instance for configuration.
         */
        public Builder() {
            // camera field already initialized
        }

        /**
         * Sets the direction vectors for the camera orientation.
         * Both vectors must be normalized and orthogonal.
         *
         * @param vTo  forward direction vector
         * @param vUp  upward direction vector
         * @return this Builder for chaining
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (!isOrthogonal(vTo, vUp)) {
                throw new IllegalArgumentException("Direction vectors must be orthogonal");
            }
            this.target = null;
            camera.vUp = vUp.normalize();
            camera.vTo = vTo.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        /**
         * Sets a target point for the camera to look at.
         *
         * @param target the point to look at
         * @return this Builder for chaining
         */
        public Builder setDirection(Point target) {
            this.target = target;
            camera.vTo = null;
            camera.vUp = null;
            camera.vRight = null;
            return this;
        }

        /**
         * Sets a target point and upward vector for the camera.
         *
         * @param target the point to look at
         * @param vUp    upward direction vector
         * @return this Builder for chaining
         */
        public Builder setDirection(Point target, Vector vUp) {
            this.target = target;
            camera.vUp = vUp.normalize();
            camera.vTo = null;
            camera.vRight = null;
            return this;
        }

        /**
         * Sets the camera position in 3D space.
         *
         * @param p0 position point
         * @return this Builder for chaining
         */
        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        /**
         * Sets the distance to the view plane. Must be positive.
         *
         * @param distance distance value
         * @return this Builder for chaining
         */
        public Builder setVpDistance(double distance) {
            if (alignZero(distance) <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the size of the view plane. Width and height must be positive.
         *
         * @param width  view plane width
         * @param height view plane height
         * @return this Builder for chaining
         */
        public Builder setVpSize(double width, double height) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Initializes the ImageWriter with the given resolution.
         *
         * @param nX pixel count in x-direction
         * @param nY pixel count in y-direction
         * @return this Builder for chaining
         */
        public Builder setResolution(int nX, int nY) {
            camera.imageWriter = new ImageWriter(nX, nY);
            return this;
        }

        /**
         * Sets the RayTracer type for this camera based on the scene and tracer type.
         *
         * @param scene the scene to render
         * @param type  ray tracer type
         * @return this Builder for chaining
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            switch (type) {
                case SIMPLE:
                    camera.rayTracer = new SimpleRayTracer(scene);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported RayTracerType: " + type);
            }
            return this;
        }

        /**
         * Validates camera settings and returns a cloned Camera instance.
         *
         * @return a configured Camera
         */
        public Camera build() {
            validate();
            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError("Camera cloning failed", e);
            }
        }

        /**
         * Validates all required parameters and sets defaults where necessary.
         */
        private void validate() {
            if (camera.width == 0 || camera.height == 0) {
                throw new IllegalStateException("View plane size is not set");
            }
            if (camera.p0 == null) {
                camera.p0 = Point.ZERO;
            }
            if (camera.distance == 0.0) {
                throw new IllegalStateException("Distance to view plane is not set");
            }
            if (target != null && target.equals(camera.p0)) {
                throw new IllegalStateException("Camera cannot be at the target point");
            }
            if (target != null) {
                camera.vTo = target.subtract(camera.p0).normalize();
                if (camera.vUp == null) {
                    camera.vUp = Vector.AXIS_Y;
                }
            }
            if (camera.vTo == null) {
                camera.vTo = Vector.AXIS_Z;
            }
            if (camera.vUp == null) {
                camera.vUp = Vector.AXIS_Y;
            }
            if (!isOrthogonal(camera.vTo, camera.vUp)) {
                camera.vUp = camera.vTo.crossProduct(camera.vUp).crossProduct(camera.vTo).normalize();
            }
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            camera.pcenter = camera.p0.add(camera.vTo.scale(camera.distance));
            target = null;
        }

        /**
         * Checks whether two vectors are orthogonal (dot product is zero).
         *
         * @param v1 first vector
         * @param v2 second vector
         * @return true if orthogonal, false otherwise
         */
        private boolean isOrthogonal(Vector v1, Vector v2) {
            return isZero(v1.dotProduct(v2));
        }
    }
}
