// src/main/java/renderer/Camera.java
package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.sampling.SamplingConfig;
import renderer.sampling.SuperSamplingBlackboard;
import scene.Scene;
import lighting.*;
import geometries.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.IntStream;

import static primitives.Util.isZero;

/**
 * The Camera class represents a virtual camera in a 3D scene.
 * It supports super‐sampling (anti‐aliasing) via a SamplingConfig,
 * multithreaded rendering, progress logging, and direct ray construction.
 */
public class Camera implements Cloneable {
    // === super‐sampling fields ===
    private SamplingConfig samplingConfig = new SamplingConfig();
    private SuperSamplingBlackboard blackboard = new SuperSamplingBlackboard(samplingConfig);

    /**
     * Configure anti‐aliasing parameters.
     */
    public Camera setSamplingConfig(SamplingConfig config) {
        this.samplingConfig = config;
        this.blackboard = new SuperSamplingBlackboard(config);
        return this;
    }

    // === multithreading & progress tracking fields ===
    private int threadsCount       = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval   = 0;
    private PixelManager pixelManager;

    // === camera coordinate frame & view-plane ===
    private Vector vTo, vUp, vRight;
    private Point  p0, pcenter;
    private double distance, width, height;

    // === image buffer & ray tracer ===
    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;

    private Camera() { /* use Builder */ }

    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the eye through pixel (j,i) on an nX×nY view-plane.
     * This restores API for direct ray tests.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        double pixelW = width  / (double) nX;
        double pixelH = height / (double) nY;

        double xJ = (j - (nX - 1) / 2.0) * pixelW;
        double yI = (i - (nY - 1) / 2.0) * pixelH;

        Point pIJ = pcenter;
        if (xJ != 0) pIJ = pIJ.add(vRight.scale(xJ));
        if (yI != 0) pIJ = pIJ.add(vUp.scale(-yI));

        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Single-pixel rendering with optional super-sampling.
     */
    private void castRay(int nX, int nY, int j, int i) {
        double pixelW = width  / nX;
        double pixelH = height / nY;

        List<Point2D.Double> samples = blackboard.getSampleOffsets(pixelW, pixelH);
        Color pixelColor = new Color(0, 0, 0);

        for (Point2D.Double off : samples) {
            double xJ = (j - (nX - 1) / 2.0) * pixelW + off.x;
            double yI = (i - (nY - 1) / 2.0) * pixelH + off.y;

            Point pIJ = pcenter;
            if (xJ != 0) pIJ = pIJ.add(vRight.scale(xJ));
            if (yI != 0) pIJ = pIJ.add(vUp.scale(-yI));

            Ray sampleRay = new Ray(p0, pIJ.subtract(p0));
            pixelColor = pixelColor.add(rayTracer.traceRay(sampleRay));
        }

        pixelColor = pixelColor.scale(1.0 / samples.size());
        imageWriter.writePixel(j, i, pixelColor);
        pixelManager.pixelDone();
    }

    // ─── rendering strategies ─────────────────────────────────────────────────────

    /** Single‐threaded rendering. */
    private Camera renderImageNoThreads() {
        int nX = imageWriter.nX(), nY = imageWriter.nY();
        for (int i = 0; i < nY; i++)
            for (int j = 0; j < nX; j++)
                castRay(nX, nY, j, i);
        return this;
    }

    /** Parallel-streams rendering. */
    private Camera renderImageStream() {
        int nX = imageWriter.nX(), nY = imageWriter.nY();
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(nX, nY, j, i)));
        return this;
    }

    /** Raw-threads rendering via PixelManager. */
    private Camera renderImageRawThreads() {
        int nX = imageWriter.nX(), nY = imageWriter.nY();
        List<Thread> threads = new java.util.LinkedList<>();
        for (int t = 0; t < threadsCount; t++) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel p;
                while ((p = pixelManager.nextPixel()) != null) {
                    castRay(nX, nY, p.col(), p.row());
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        return this;
    }

    /**
     * Render the image using the chosen strategy:
     *   threadsCount = 0  → no threads
     *                 -1 → parallel streams
     *                 >0 → raw threads
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(
                imageWriter.nY(),
                imageWriter.nX(),
                printInterval
        );
        return switch (threadsCount) {
            case  0  -> renderImageNoThreads();
            case -1  -> renderImageStream();
            default  -> renderImageRawThreads();
        };
    }

    /** Draw a grid overlay at the given interval. */
    public Camera printGrid(int interval, Color color) {
        int nX = imageWriter.nX(), nY = imageWriter.nY();
        for (int i = 0; i < nY; i++)
            for (int j = 0; j < nX; j++)
                if (i % interval == 0 || j % interval == 0)
                    imageWriter.writePixel(j, i, color);
        return this;
    }

    /** Save the rendered image to disk. */
    public void writeToImage(String name) {
        imageWriter.writeToImage(name);
    }

    // ─── Builder ────────────────────────────────────────────────────────────────────

    public static class Builder {
        private final Camera camera = new Camera();
        private Point target = null;

        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        public Builder setDirection(Point target) {
            this.target = target;
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            camera.vUp = vUp.normalize();
            this.target = target;
            return this;
        }

        public Builder setVpDistance(double dist) {
            camera.distance = dist;
            return this;
        }

        public Builder setVpSize(double w, double h) {
            camera.width = w;
            camera.height = h;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            camera.imageWriter = new ImageWriter(nX, nY);
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            switch (type) {
                case SIMPLE -> camera.rayTracer = new SimpleRayTracer(scene);
                default -> throw new IllegalArgumentException("Unsupported RayTracerType");
            }
            return this;
        }

        /**
         * Configure multithreading:
         * -2 → auto (cores – SPARE_THREADS)
         * -1 → parallel streams
         *  0 → off
         * >0 → exact thread count
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be ≥ -2");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = Math.max(1, cores);
            } else {
                camera.threadsCount = threads;
            }
            return this;
        }

        /**
         * Enable progress printing every `interval` percent (0 = off).
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("Print interval must be non-negative");
            camera.printInterval = interval;
            return this;
        }

        public Camera build() {
            if (camera.p0 == null) camera.p0 = Point.ZERO;
            if (camera.distance == 0) throw new IllegalStateException("View-plane distance not set");
            if (camera.width == 0 || camera.height == 0)
                throw new IllegalStateException("View-plane size not set");

            if (target != null) {
                if (target.equals(camera.p0)) {
                    throw new IllegalArgumentException("Target cannot be at camera location");
                }
                Vector toTarget = target.subtract(camera.p0);
                camera.vTo = toTarget.normalize();
            }

            if (camera.vUp == null) {
                camera.vUp = Vector.AXIS_Y;
            }
            if (camera.vTo == null) {
                camera.vTo = Vector.AXIS_Z;
            }

            if (!isZero(camera.vTo.dotProduct(camera.vUp))) {
                camera.vUp = camera.vTo
                        .crossProduct(camera.vUp)
                        .crossProduct(camera.vTo)
                        .normalize();
            }

            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            camera.pcenter = camera.p0.add(camera.vTo.scale(camera.distance));

            return camera.clone();
        }
    }

    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
