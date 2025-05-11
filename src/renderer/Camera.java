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
 * It defines the camera's position, orientation, and view plane properties.
 * The class also provides a builder for constructing a camera with specific parameters.
 */
public class Camera implements Cloneable {
    private Vector vTo = null; // The forward direction vector of the camera
    private Vector vUp = null; // The upward direction vector of the camera
    private Vector vRight = null; // The rightward direction vector of the camera

    // The camera's position point in 3D space
    private Point p0 = null;
    // The center point of the view plane
    private Point pcenter = null;

    // The distance from the camera to the view plane
    private double distance = 0.0;
    // The width of the view plane
    private double width = 0.0;

    private double height = 0.0;

    private ImageWriter imageWriter = null;
    private final RayTracerBase rayTracer = null;

    // The image writer for rendering the scene
    private Camera() {
    }

    /**
     * Returns a builder for constructing a Camera object.
     *
     * @return A Builder instance.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera to a point on the view plane.
     *
     * @param nX The number of pixels in the x-direction (width).
     * @param nY The number of pixels in the y-direction (height).
     * @param j  The pixel index in the x-direction.
     * @param i  The pixel index in the y-direction.
     * @return A Ray object representing the ray from the camera to the specified pixel.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        // Calculate the size of each pixel in the view plane
        double pixelWidth = width / nX;
        double pixelHeight = height / nY;

        // Calculate the center of the pixel relative to the view plane center
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth; // Horizontal offset
        double yI = (i - (nY - 1) / 2.0) * pixelHeight; // Vertical offset

        // Start with the center of the view plane
        Point pIJ = pcenter;

        // Adjust the point horizontally by scaling the right vector
        if (xJ != 0) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }

        // Adjust the point vertically by scaling the up vector (negative for correct orientation)
        if (yI != 0) {
            pIJ = pIJ.add(vUp.scale(-yI));
        }

        // Return a ray from the camera position to the calculated point on the view plane
        return new Ray(p0, pIJ.subtract(p0));
    }

    // Getters for camera properties
    public Vector getVTo() {
        return vTo;
    }

    public Vector getVUp() {
        return vUp;
    }

    public Vector getVRight() {
        return vRight;
    }

    public Point getP0() {
        return p0;
    }

    public Point getPcenter() {
        return pcenter;
    }

    public double getDistance() {
        return distance;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public ImageWriter getImageWriter() {
        return imageWriter;
    }

    public Camera renderImage() {
        //todo
        int nx = imageWriter.nX();
        int ny = imageWriter.nY();
        for (int i = 0; i < ny; i++) {
            for (int j = 0; j < nx; j++) {
                // Construct a ray for each pixel and trace it
                Ray ray = constructRay(nx, ny, j, i);
                // Perform ray tracing and set the pixel color in the image writer


                Color color = rayTracer.traceRay(ray);
                // imageWriter.writePixel(j, i, color);

            }
        }
        return this;
    }

    public Camera printGrid(int intervali, Color color) {
        //todo
        int nx = imageWriter.nX();
        int ny = imageWriter.nY();
        for (int i = 0; i < ny; i++) {
            for (int j = 0; j < nx; j++) {
                if (i % intervali == 0 || j % intervali == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    public void writeToImage(String imageName) {
        imageWriter.writeToImage(imageName);
    }

    /**
     * The Builder class is used to construct a Camera object with specific parameters.
     */
    public static class Builder {
        private final Camera camera = new Camera();
        private Point target = null; // The target point the camera is looking at

        /**
         * Sets the direction vectors of the camera.
         *
         * @param vTo The forward direction vector.
         * @param vUp The upward direction vector.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the direction vectors are not orthogonal.
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            // Ensure the direction vectors are orthogonal
            if (!isOrthogonal(vTo, vUp)) {
                throw new IllegalArgumentException("Direction vectors must be orthogonal");
            }
            this.target = null; // Will be calculated in validate
            // Normalize and set the direction vectors
            camera.vUp = vUp.normalize();
            camera.vTo = vTo.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp);
            return this;
        }

        public Builder setDirection(Point target) {
            this.target = target;
            camera.vTo = null; // Will be calculated in validate
            camera.vUp = null; // Will be calculated in validate
            camera.vRight = null; // Will be calculated in validate
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            this.target = target;
            camera.vUp = vUp.normalize();
            camera.vTo = null; // Will be calculated in validate
            camera.vRight = null; // Will be calculated in validate
            return this;
        }

        /**
         * Sets the location of the camera.
         *
         * @param p0 The position of the camera.
         * @return The Builder instance.
         */
        public Builder setLocation(Point p0) {
            // Set the camera's position
            camera.p0 = p0;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance The distance to the view plane.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the distance is not positive.
         */
        public Builder setVpDistance(double distance) {
            // Ensure the distance is positive
            if (alignZero(distance) <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            // Set the distance
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the size of the view plane.
         *
         * @param width  The width of the view plane.
         * @param height The height of the view plane.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the width or height is not positive.
         */
        public Builder setVpSize(double width, double height) {
            // Ensure the width and height are positive
            if (alignZero(width) <= 0 || alignZero(height) <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            // Set the view plane size
            camera.width = width;
            camera.height = height;
            return this;
        }


        /**
         * Builds and returns the constructed Camera object.
         * This method ensures immutability by cloning the Camera instance.
         *
         * @return The constructed Camera object, or null if cloning is not supported.
         */
        public Camera build() {
            try {
                validate(camera);
                // Clone the camera to ensure immutability
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException ignored) {
                return null;
            }
        }


        /**
         * Validates the camera parameters and sets default values if necessary.
         *
         * @param camera The Camera object to validate.
         * @throws IllegalStateException if any required parameters are not set correctly.
         *                               <p>
         *                               The orthogonality checks in the `validate` method ensure that the `vTo` (forward direction vector) and `vUp` (upward direction vector) are perpendicular. Here's how it works:
         *                               <p>
         *                               1. **Dot Product Check**:
         *                               - The method uses the dot product of `vTo` and `vUp` to verify orthogonality.
         *                               - If two vectors are orthogonal, their dot product is zero. The method checks this using `!isZero(camera.vTo.dotProduct(camera.vUp))`.
         *                               <p>
         *                               2. **Recalculation of `vUp`**:
         *                               - If the vectors are not orthogonal (dot product is not zero), the method recalculates `vUp` to make it orthogonal to `vTo`.
         *                               - This is done by:
         *                               - Taking the cross product of `vTo` and the current `vUp` to get a vector perpendicular to both.
         *                               - Taking another cross product of the result with `vTo` to ensure the new `vUp` is orthogonal to `vTo` and lies in the correct plane.
         *                               - Normalizing the result to maintain a unit vector.
         *                               <p>
         *                               This ensures that `vTo` and `vUp` are always orthogonal, which is critical for defining the camera's orientation correctly.
         */
        private void validate(Camera camera) throws IllegalStateException {
            // Ensure the view plane size is set
            if (camera.width == 0 || camera.height == 0) {
                throw new IllegalStateException("View plane size is not set");
            }

            // Default the camera position to the origin if not set
            if (camera.p0 == null) {
                camera.p0 = Point.ZERO;
            }

            // Ensure the distance to the view plane is positive
            if (camera.distance == 0.0) {
                throw new IllegalStateException("Distance to view plane is not set");
            }

            // Ensure the camera is not located at the target point
            if (target != null && target.equals(camera.p0)) {
                throw new IllegalStateException("Camera cannot be at the target point");
            }

            // If a target is provided, calculate the forward direction vector (vTo)
            if (target != null) {
                camera.vTo = target.subtract(camera.p0).normalize();

                // If vUp is not set assign a default orthogonal vector
                if (camera.vUp == null) {
                    camera.vUp = Vector.AXIS_Y;
                }
            }

            // Default vTo to the Z-axis if not set
            if (camera.vTo == null) {
                camera.vTo = Vector.AXIS_Z;
            }

            // Default vUp to the Y-axis if not set
            if (camera.vUp == null) {
                camera.vUp = Vector.AXIS_Y;
            }

            // Ensure vTo and vUp are orthogonal by recalculating vUp if necessary
            if (!isOrthogonal(camera.vTo, camera.vUp)) {
                camera.vUp = camera.vTo.crossProduct(camera.vUp).crossProduct(camera.vTo).normalize();
            }

            // Calculate the rightward direction vector (vRight) as the cross product of vTo and vUp
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            // Calculate the center of the view plane based on the camera position and vTo
            camera.pcenter = camera.p0.add(camera.vTo.scale(camera.distance));

            // Clear the target to avoid retaining unnecessary state
            target = null;
        }

        private boolean isOrthogonal(Vector v1, Vector v2) {
            return isZero(v1.dotProduct(v2));
        }

        public Builder setResolution(int nx, int ny) {
            //todo
            camera.imageWriter = new ImageWriter(nx, ny);
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType rayTracerType) {
            return this;
        }
    }
}