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
    private Vector vTo;      // The forward direction vector of the camera
    private Vector vUp;      // The upward direction vector of the camera
    private Vector vRight;   // The rightward direction vector of the camera

    private Point p0;        // The camera's position point in 3D space
    private Point pcenter;   // The center point of the view plane

    private double distance; // The distance from the camera to the view plane
    private double width;    // The width of the view plane
    private double height;   // The height of the view plane

    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;  // assigned by the builder

    private Camera() {
        // private constructor: use Builder
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
        double pixelWidth = width / nX;
        double pixelHeight = height / nY;

        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = (i - (nY - 1) / 2.0) * pixelHeight;

        // Start with the center of the view plane
        Point pIJ = pcenter;

        // Adjust the point horizontally by scaling the right vector
        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }

        // Adjust the point vertically by scaling the up vector (negative for correct orientation)
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(-yI));
        }

        // Return a ray from the camera position to the calculated point on the view plane
        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Renders the image by tracing a ray through each pixel.
     * Delegates to castRay for each pixel.
     *
     * @return this camera object for method chaining
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
     * Casts a ray through a specific pixel, traces it, and writes the resulting color to the image.
     *
     * @param nX number of pixels in x-direction
     * @param nY number of pixels in y-direction
     * @param j  pixel column index (x)
     * @param i  pixel row index (y)
     */
    private void castRay(int nX, int nY, int j, int i) {
        Ray ray = constructRay(nX, nY, j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);
    }

    /**
     * Draws a grid overlay on the rendered image.
     * Only pixels on the grid lines are colored; other pixels remain untouched.
     *
     * @param interval spacing between grid lines in pixels
     * @param color    grid line color
     * @return this camera object for method chaining
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
     * Saves the rendered image to file.
     *
     * @param imageName name (or path) of output image (without extension)
     */
    public void writeToImage(String imageName) {
        imageWriter.writeToImage(imageName);
    }

    // Getters for camera properties...

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

    /**
     * The Builder class is used to construct a Camera object with specific parameters.
     */
    public static class Builder {
        private final Camera camera = new Camera();
        private Point target = null; // The target point the camera is looking at

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

        public Builder setDirection(Point target) {
            this.target = target;
            camera.vTo = null;
            camera.vUp = null;
            camera.vRight = null;
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            this.target = target;
            camera.vUp = vUp.normalize();
            camera.vTo = null;
            camera.vRight = null;
            return this;
        }

        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        public Builder setVpDistance(double distance) {
            if (alignZero(distance) <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            camera.distance = distance;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            camera.width = width;
            camera.height = height;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            camera.imageWriter = new ImageWriter(nX, nY);
            return this;
        }

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

        public Camera build() {
            validate();
            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError("Camera cloning failed", e);
            }
        }

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

        private boolean isOrthogonal(Vector v1, Vector v2) {
            return isZero(v1.dotProduct(v2));
        }
    }
}
