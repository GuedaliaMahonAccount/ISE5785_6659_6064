// renderer/StreetSceneBVHThreadTest.java
package renderer;

import org.junit.jupiter.api.Test;
import geometries.*;
import geometries.BVHNode;
import lighting.*;
import primitives.*;
import renderer.sampling.SamplingConfig;
import renderer.sampling.SamplingPattern;
import renderer.sampling.TargetShape;
import scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for rendering a colorful, realistic street scene with BVH acceleration,
 * featuring a blue building with full walls and windows, and a modestly sized rocket
 * entering from the sky at an angle, with a small, aligned smoke trail and reduced ground illumination.
 */
public class StreetSceneBVHThreadTest {

    private final Scene scene = new Scene("Street Scene with Windows and Smaller Rocket");
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    @Test
    public void streetScene() {
        List<Intersectable> geometries = new ArrayList<>();

        // ===== Ground and Road =====
        geometries.add(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setEmission(new Color(45, 90, 45))
                .setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(10)));

        geometries.add(new Polygon(
                new Point(-25, 0.1, 200),
                new Point(25, 0.1, 200),
                new Point(25, 0.1, -500),
                new Point(-25, 0.1, -500))
                .setEmission(new Color(25, 25, 30))
                .setMaterial(new Material().setKD(0.9).setKS(0.05).setShininess(15)));

        // Center dashed lines
        for (int z = 70; z >= -450; z -= 40) {
            geometries.add(new Polygon(
                    new Point(-1, 0.15, z),
                    new Point(1, 0.15, z),
                    new Point(1, 0.15, z - 20),
                    new Point(-1, 0.15, z - 20))
                    .setEmission(new Color(200, 170, 0))
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));
        }

        // Road edge lines
        geometries.add(new Polygon(
                new Point(-24, 0.12, 200),
                new Point(-22, 0.12, 200),
                new Point(-22, 0.12, -500),
                new Point(-24, 0.12, -500))
                .setEmission(new Color(200, 200, 200))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(25)));
        geometries.add(new Polygon(
                new Point(22, 0.12, 200),
                new Point(24, 0.12, 200),
                new Point(24, 0.12, -500),
                new Point(22, 0.12, -500))
                .setEmission(new Color(200, 200, 200))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(25)));

        // Sidewalks
        geometries.add(new Polygon(
                new Point(25, 1.2, 200),
                new Point(45, 1.2, 200),
                new Point(45, 1.2, -500),
                new Point(25, 1.2, -500))
                .setEmission(new Color(150, 150, 155))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
        geometries.add(new Polygon(
                new Point(-45, 1.2, 200),
                new Point(-25, 1.2, 200),
                new Point(-25, 1.2, -500),
                new Point(-45, 1.2, -500))
                .setEmission(new Color(180, 180, 185))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Curbs
        geometries.add(new Polygon(
                new Point(25, 0.12, 200),
                new Point(25, 1.2, 200),
                new Point(25, 1.2, -500),
                new Point(25, 0.12, -500))
                .setEmission(new Color(120, 120, 125))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
        geometries.add(new Polygon(
                new Point(-25, 0.12, 200),
                new Point(-25, 1.2, 200),
                new Point(-25, 1.2, -500),
                new Point(-25, 0.12, -500))
                .setEmission(new Color(90, 90, 95))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // ===== Crescent Moon =====
        geometries.add(new Sphere(new Point(50, 120, -250), 15)
                .setEmission(new Color(220, 220, 180))
                .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(30)));
        geometries.add(new Sphere(new Point(52, 118, -248), 2)
                .setEmission(new Color(180, 180, 140))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));
        geometries.add(new Sphere(new Point(55, 125, -252), 1.5)
                .setEmission(new Color(190, 190, 150))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));

        // ===== Rocket Entry =====
        Vector rocketDir = new Vector(0.3, -1, 0).normalize();
        // use an even smaller scale (0.6) for a much smaller rocket
        createRocket(geometries, new Point(80, 80, -150), rocketDir, 0.6);

        // ===== Trees =====
        double[] leftTreeZ = {-80, -120, -160, -200, -240, -280};
        for (double zPos : leftTreeZ) {
            createTree(geometries, new Point(-60, 1.2, zPos), 1.0 + Math.random() * 0.3);
        }
        double[] rightTreeZ = {-70, -110, -150, -190, -230, -270, -310};
        for (double zPos : rightTreeZ) {
            createTree(geometries, new Point(70, 1.2, zPos), 0.8 + Math.random() * 0.4);
        }

        // ===== Buildings =====
        // <-- only this first building has been changed -->
        createBuildingWithWindows(geometries, new Point(-150, 0, -300), 40, 80, 50, new Color(60, 90, 140));

        // all other buildings unchanged:
        createBuildingNoWindows(geometries, new Point(-80, 0, -350), 60, 120, 40, new Color(160, 80, 60));
        createNonReflectiveBuilding(geometries, new Point(100, 0, -280), new Color(140, 120, 160));
        createBuildingNoWindows(geometries, new Point(120, 0, -320), 25, 15, 20, new Color(130, 60, 50));
        createBuildingNoWindows(geometries, new Point(150, 0, -315), 20, 18, 18, new Color(170, 140, 100));
        createBuildingNoWindows(geometries, new Point(-200, 0, -400), 30, 150, 25, new Color(60, 65, 70));

        // ===== Street Lamps =====
        for (int i = 0; i < 6; i++) {
            double zLeft = -40 - i * 50;
            double zRight = zLeft + 25;
            createStreetLamp(geometries, new Point(-50, 1.2, zLeft));
            createStreetLamp(geometries, new Point(55, 1.2, zRight));
        }

        // ===== BVH & Scene Setup =====
        BVHNode bvhRoot = BVHNode.build(geometries);
        scene.setGeometries(bvhRoot);

        // ===== Lighting =====
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));
        scene.lights.add(new DirectionalLight(new Color(60, 50, 40), new Vector(0.4, -0.6, -0.7)));
        scene.lights.add(new DirectionalLight(new Color(20, 25, 30), new Vector(-0.2, -0.3, 0.5)));

        // moon glow
        scene.lights.add(
                new PointLight(new Color(90, 85, 65), new Point(50, 120, -250))
                        .setKl(0.00001).setKq(0.000005).setRadius(10.0).setNumSamples(3)
        );
        scene.lights.add(
                new SpotLight(new Color(70, 65, 50), new Point(50, 120, -250), new Vector(0.1, -1, 0.3))
                        .setKl(0.00005).setKq(0.00001).setNarrowBeam(15)
        );

        // reduced rocket exhaust glow
        scene.lights.add(
                new PointLight(new Color(30, 20, 10), new Point(80, 75, -150))
                        .setKl(0.001).setKq(0.001).setRadius(0.5).setNumSamples(3)
        );

        // lamp post lights
        for (int i = 0; i < 6; i++) {
            double zLeft = -40 - i * 50;
            double zRight = zLeft + 25;
            scene.lights.add(
                    new PointLight(new Color(100, 90, 80), new Point(-50, 9.2, zLeft))
                            .setKl(0.001).setKq(0.0005).setRadius(10.0).setNumSamples(3)
            );
            scene.lights.add(
                    new PointLight(new Color(100, 90, 80), new Point(55, 9.2, zRight))
                            .setKl(0.001).setKq(0.0005).setRadius(10.0).setNumSamples(3)
            );
        }
        scene.lights.add(
                new PointLight(new Color(80, 70, 60), new Point(-150, 40, -295))
                        .setKl(0.0003).setKq(0.00015).setRadius(1.5).setNumSamples(3)
        );
        scene.lights.add(
                new PointLight(new Color(80, 70, 60), new Point(-80, 60, -350))
                        .setKl(0.0003).setKq(0.00015).setRadius(1.5).setNumSamples(3)
        );
        scene.lights.add(
                new PointLight(new Color(120, 110, 100), new Point(35, 6, 20))
                        .setKl(0.0005).setKq(0.0003).setRadius(1.0).setNumSamples(3)
        );

        // ===== Camera & Render =====
        Camera camera = cameraBuilder
                .setLocation(new Point(-15, 12, 60))
                .setDirection(new Vector(0.1, -0.15, -1), new Vector(0, 1, 0))
                .setVpDistance(150)
                .setVpSize(300, 200)
                .setResolution(1500, 1000)
                .setMultithreading(-2)
                .setDebugPrint(1.0)
                .build();
        camera.setSamplingConfig(new SamplingConfig(5, TargetShape.RECTANGLE, SamplingPattern.GRID));

        long tStart = System.currentTimeMillis();
        camera.renderImage();
        long tEnd = System.currentTimeMillis();
        System.out.printf("Render completed in %.3f seconds.%n", (tEnd - tStart) / 1000.0);
        camera.writeToImage("streetBVHThreads");
    }

    /**
     * Creates a better rocket with smoke trail behind it, smaller head, and smaller overall size.
     */
    private void createRocket(List<Intersectable> geometries, Point nose, Vector dir, double scale) {
        Vector d = dir.normalize();
        double length = 20 * scale;  // Shorter rocket
        double step = 2.0 * scale;   // Smaller segments
        double maxBodyRadius = 2.0 * scale;  // Maximum body radius (at the rear)
        double noseRadius = 0.8 * scale;     // Much smaller nose

        // 1) Rocket Body - tapered from small nose to larger rear
        for (double t = step; t <= length; t += step) {
            Point center = nose.add(d.scale(-t));
            // Taper: nose is small, rear is large
            double progress = t / length;  // 0 at nose, 1 at rear
            double currentRadius = noseRadius + (maxBodyRadius - noseRadius) * progress;

            geometries.add(new Sphere(center, currentRadius)
                    .setEmission(new Color(160, 160, 170))  // Metallic gray
                    .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.05).setShininess(70)));
        }

        // 2) Nose Cone - small and pointed
        geometries.add(new Sphere(nose, noseRadius)
                .setEmission(new Color(140, 30, 30))  // Dark red nose
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(50)));

        // 3) Fins - smaller and at the rear
        Vector axis = d.cross(Vector.AXIS_Y);
        if (Util.isZero(axis.length())) axis = d.cross(Vector.AXIS_X);
        Vector perp1 = axis.normalize();
        Vector perp2 = d.cross(perp1).normalize();
        double finLen = 3 * scale;  // Smaller fins
        Point tailBase = nose.add(d.scale(-length));

        for (Vector pdir : new Vector[]{perp1, perp2, perp1.scale(-1), perp2.scale(-1)}) {
            Point p1 = tailBase.add(pdir.scale(finLen));
            Point p2 = tailBase.add(pdir.scale(finLen * 0.7)).add(d.scale(-step * 0.2));
            Point p3 = tailBase.add(pdir.scale(finLen * 0.4)).add(d.scale(-step * 0.8));
            Point p4 = tailBase.add(pdir.scale(finLen * 0.8)).add(d.scale(-step * 0.5));
            geometries.add(new Polygon(p1, p2, p3, p4)
                    .setEmission(new Color(80, 80, 90))  // Dark fins
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(40)));
        }

        // 4) Extended smoke trail behind the rocket
        double smokeStart = length + step;
        double[] smokeRadii = {
                1.5 * scale,    // First smoke particle (largest)
                1.8 * scale,    // Second particle (bigger)
                2.1 * scale,    // Third particle (even bigger)
                2.3 * scale,    // Fourth particle
                2.0 * scale,    // Fifth particle (starts to dissipate)
                1.6 * scale,    // Sixth particle
                1.2 * scale,    // Seventh particle
                0.8 * scale     // Last particle (smallest)
        };

        Color[] smokeColors = {
                new Color(100, 70, 50),   // Orange-brown exhaust
                new Color(80, 60, 45),    // Darker brown
                new Color(70, 50, 35),    // Even darker
                new Color(60, 45, 30),    // Dark brown
                new Color(50, 40, 25),    // Very dark brown
                new Color(45, 35, 25),    // Almost black brown
                new Color(40, 30, 20),    // Very dark smoke
                new Color(35, 25, 15)     // Almost black
        };

        for (int i = 0; i < smokeRadii.length; i++) {
            double t = smokeStart + i * (2.0 * scale);  // Spacing between smoke particles
            Point c = nose.add(d.scale(-t));

            // Add some random offset to make smoke look more natural
            double offsetX = (Math.random() - 0.5) * scale * 0.5;
            double offsetY = (Math.random() - 0.5) * scale * 0.5;
            double offsetZ = (Math.random() - 0.5) * scale * 0.5;
            c = c.add(new Vector(offsetX, offsetY, offsetZ));

            geometries.add(new Sphere(c, smokeRadii[i])
                    .setEmission(smokeColors[i])
                    .setMaterial(new Material().setKD(0.95).setKS(0.02).setShininess(3)));
        }

        // 5) Engine nozzle detail at the rear
        Point engineCenter = nose.add(d.scale(-length - step * 0.3));
        geometries.add(new Sphere(engineCenter, maxBodyRadius * 0.9)
                .setEmission(new Color(40, 40, 50))  // Dark engine
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // Hot engine exhaust glow
        geometries.add(new Sphere(engineCenter.add(d.scale(-step * 0.2)), maxBodyRadius * 0.6)
                .setEmission(new Color(150, 80, 40))  // Hot orange glow
                .setMaterial(new Material().setKD(0.9).setKS(0.1).setShininess(10)));
    }

    /**
     * Creates a street lamp at the given position.
     */
    private void createStreetLamp(List<Intersectable> geometries, Point pos) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Base
        geometries.add(new Sphere(new Point(x, y - 0.3, z), 0.8)
                .setEmission(new Color(40, 40, 45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Ground disc
        geometries.add(new Polygon(
                new Point(x - 0.8, y + 0.01, z + 0.8),
                new Point(x + 0.8, y + 0.01, z + 0.8),
                new Point(x + 0.8, y + 0.01, z - 0.8),
                new Point(x - 0.8, y + 0.01, z - 0.8))
                .setEmission(new Color(35, 35, 40))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Pole segments
        for (double h = 0.5; h <= 7.0; h += 0.4) {
            geometries.add(new Sphere(new Point(x, y + h, z), 0.35)
                    .setEmission(new Color(30, 30, 35))
                    .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(60)));
        }

        // Horizontal arm
        double dir = x < 0 ? +1.0 : -1.0;
        for (double off = 0.4; off <= 2.4; off += 0.4) {
            geometries.add(new Sphere(new Point(x + dir * off, y + 7, z), 0.28)
                    .setEmission(new Color(30, 30, 35))
                    .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(60)));
        }

        // Bracket
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.8, z), 0.25)
                .setEmission(new Color(40, 40, 45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.5, z), 0.25)
                .setEmission(new Color(40, 40, 45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Lamp housing & globe
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.0, z), 1.1)
                .setEmission(new Color(25, 25, 30))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.0, z), 1.5)
                .setEmission(new Color(255, 230, 180))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(10)));
    }

    /**
     * Creates a tree at the given position with a specified scale.
     */
    private void createTree(List<Intersectable> geometries, Point pos, double scale) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Trunk
        geometries.add(new Sphere(new Point(x, y + 0.8 * scale, z), 1.5 * scale)
                .setEmission(new Color(80, 50, 30)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y + 2.5 * scale, z), 1.2 * scale)
                .setEmission(new Color(90, 60, 40)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y + 3.5 * scale, z), scale)
                .setEmission(new Color(100, 70, 50)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));

        // Foliage
        geometries.add(new Sphere(new Point(x, y + 8.5 * scale, z), 6.0 * scale)
                .setEmission(new Color(30, 100, 40)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x - 2 * scale, y + 6.5 * scale, z - scale), 4.0 * scale)
                .setEmission(new Color(25, 85, 30)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x + 3 * scale, y + 7.5 * scale, z + 2 * scale), 3.5 * scale)
                .setEmission(new Color(40, 120, 50)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
    }

    /**
     * Creates a building with windows on front, left, right, and full rear wall.
     */
    private void createBuildingWithWindows(List<Intersectable> geometries,
                                           Point pos,
                                           double width, double height, double depth,
                                           Color color) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Front face
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z + depth / 2),
                new Point(x + width / 2, y, z + depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2),
                new Point(x - width / 2, y + height, z + depth / 2))
                .setEmission(color)
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Front windows (3 × 6)
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                double winX = x - width / 3 + col * (width / 3);
                double winY = y + 10 + row * 12;
                double winZ = z + depth / 2 + 0.1;

                geometries.add(new Polygon(
                        new Point(winX - 4, winY - 4, winZ),
                        new Point(winX + 4, winY - 4, winZ),
                        new Point(winX + 4, winY + 4, winZ),
                        new Point(winX - 4, winY + 4, winZ))
                        .setEmission(new Color(80, 80, 85))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

                geometries.add(new Polygon(
                        new Point(winX - 3, winY - 3, winZ + 0.05),
                        new Point(winX + 3, winY - 3, winZ + 0.05),
                        new Point(winX + 3, winY + 3, winZ + 0.05),
                        new Point(winX - 3, winY + 3, winZ + 0.05))
                        .setEmission(new Color(100, 150, 200))
                        .setMaterial(new Material().setKD(0.2).setKS(0.8).setKR(0.3).setShininess(100)));
            }
        }

        // Rear face (full wall)
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z - depth / 2),
                new Point(x + width / 2, y, z - depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2))
                .setEmission(color.scale(0.8))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Left face
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z + depth / 2),
                new Point(x - width / 2, y, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z + depth / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Left windows (2 × 5)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                double winX = x - width / 2 - 0.1;
                double winY = y + 12 + row * 12;
                double winZ = z - depth / 4 + col * (depth / 2);

                geometries.add(new Polygon(
                        new Point(winX, winY - 3, winZ - 3),
                        new Point(winX, winY - 3, winZ + 3),
                        new Point(winX, winY + 3, winZ + 3),
                        new Point(winX, winY + 3, winZ - 3))
                        .setEmission(new Color(80, 80, 85))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

                geometries.add(new Polygon(
                        new Point(winX - 0.05, winY - 2.5, winZ - 2.5),
                        new Point(winX - 0.05, winY - 2.5, winZ + 2.5),
                        new Point(winX - 0.05, winY + 2.5, winZ + 2.5),
                        new Point(winX - 0.05, winY + 2.5, winZ - 2.5))
                        .setEmission(new Color(120, 160, 220))
                        .setMaterial(new Material().setKD(0.2).setKS(0.8).setKR(0.3).setShininess(100)));
            }
        }

        // Right face
        geometries.add(new Polygon(
                new Point(x + width / 2, y, z + depth / 2),
                new Point(x + width / 2, y, z - depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Right windows (2 × 5)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                double winX = x + width / 2 + 0.1;
                double winY = y + 12 + row * 12;
                double winZ = z - depth / 4 + col * (depth / 2);

                geometries.add(new Polygon(
                        new Point(winX, winY - 3, winZ - 3),
                        new Point(winX, winY - 3, winZ + 3),
                        new Point(winX, winY + 3, winZ + 3),
                        new Point(winX, winY + 3, winZ - 3))
                        .setEmission(new Color(80, 80, 85))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

                geometries.add(new Polygon(
                        new Point(winX + 0.05, winY - 2.5, winZ - 2.5),
                        new Point(winX + 0.05, winY - 2.5, winZ + 2.5),
                        new Point(winX + 0.05, winY + 2.5, winZ + 2.5),
                        new Point(winX + 0.05, winY + 2.5, winZ - 2.5))
                        .setEmission(new Color(120, 160, 220))
                        .setMaterial(new Material().setKD(0.2).setKS(0.8).setKR(0.3).setShininess(100)));
            }
        }

        // Roof
        geometries.add(new Polygon(
                new Point(x - width / 2, y + height, z + depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2))
                .setEmission(color.scale(0.5))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
    }

    /**
     * Creates a simple box building without windows.
     */
    private void createBuildingNoWindows(List<Intersectable> geometries,
                                         Point pos,
                                         double width, double height, double depth,
                                         Color color) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Original building implementation (unchanged)
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z + depth / 2),
                new Point(x + width / 2, y, z + depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2),
                new Point(x - width / 2, y + height, z + depth / 2))
                .setEmission(color)
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Back
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z - depth / 2),
                new Point(x + width / 2, y, z - depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2))
                .setEmission(color.scale(0.8))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Right
        geometries.add(new Polygon(
                new Point(x + width / 2, y, z + depth / 2),
                new Point(x + width / 2, y, z - depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Left
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z + depth / 2),
                new Point(x - width / 2, y, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z + depth / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Roof
        geometries.add(new Polygon(
                new Point(x - width / 2, y + height, z + depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2))
                .setEmission(color.scale(0.5))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Decoration based on building color
        if (color.getColor().getRed() > 150 && color.getColor().getGreen() < 90) {
            // Red/orange building - no decoration
            // Do nothing
        } else if (color.getColor().getBlue() > 130 && color.getColor().getRed() < 70) {
            // Blue building - no decoration
            // Do nothing
        } else if (color.getColor().getRed() < 70 && color.getColor().getGreen() < 70 && color.getColor().getBlue() < 80) {
            // Gray building - keep original floor stripes
            addFloorStripes(geometries, x, y, z, width, height, depth, color);
        } else {
            // Default/other buildings (including pink) - floor stripes
            addFloorStripes(geometries, x, y, z, width, height, depth, color);
        }
    }

    /**
     * Adds a single thick decorative stripe around the middle of a building
     */
    private void addThickStripe(List<Intersectable> geometries,
                                double x, double y, double z,
                                double width, double height, double depth,
                                Color color) {

        // Create a contrasting color for the red building
        Color decorColor = new Color(
                Math.max(20, color.getColor().getRed() - 60),
                Math.min(255, color.getColor().getGreen() + 90),
                Math.min(255, color.getColor().getBlue() + 100)
        );

        double stripeThickness = height / 7.0;
        double stripeY = y + (height / 2) - (stripeThickness / 2);

        // Front stripe
        geometries.add(new Polygon(
                new Point(x - width / 2, stripeY, z + depth / 2 + 0.05),
                new Point(x + width / 2, stripeY, z + depth / 2 + 0.05),
                new Point(x + width / 2, stripeY + stripeThickness, z + depth / 2 + 0.05),
                new Point(x - width / 2, stripeY + stripeThickness, z + depth / 2 + 0.05))
                .setEmission(decorColor)
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // Back stripe
        geometries.add(new Polygon(
                new Point(x - width / 2, stripeY, z - depth / 2 - 0.05),
                new Point(x + width / 2, stripeY, z - depth / 2 - 0.05),
                new Point(x + width / 2, stripeY + stripeThickness, z - depth / 2 - 0.05),
                new Point(x - width / 2, stripeY + stripeThickness, z - depth / 2 - 0.05))
                .setEmission(decorColor)
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // Right stripe
        geometries.add(new Polygon(
                new Point(x + width / 2 + 0.05, stripeY, z + depth / 2),
                new Point(x + width / 2 + 0.05, stripeY, z - depth / 2),
                new Point(x + width / 2 + 0.05, stripeY + stripeThickness, z - depth / 2),
                new Point(x + width / 2 + 0.05, stripeY + stripeThickness, z + depth / 2))
                .setEmission(decorColor)
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // Left stripe
        geometries.add(new Polygon(
                new Point(x - width / 2 - 0.05, stripeY, z + depth / 2),
                new Point(x - width / 2 - 0.05, stripeY, z - depth / 2),
                new Point(x - width / 2 - 0.05, stripeY + stripeThickness, z - depth / 2),
                new Point(x - width / 2 - 0.05, stripeY + stripeThickness, z + depth / 2))
                .setEmission(decorColor)
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
    }

    /**
     * Adds two thick decorative stripes to a building
     */
    private void addDoubleStripe(List<Intersectable> geometries,
                                 double x, double y, double z,
                                 double width, double height, double depth,
                                 Color color) {

        // Create a contrasting color for the blue building
        Color decorColor = new Color(
                Math.min(255, color.getColor().getRed() + 120),
                Math.min(255, color.getColor().getGreen() + 70),
                Math.min(255, color.getColor().getBlue() + 20)
        );

        double stripeThickness = height / 12.0;
        double[] stripePositions = {height / 3, 2 * height / 3};

        for (double relativePos : stripePositions) {
            double stripeY = y + relativePos - (stripeThickness / 2);

            // Front stripe
            geometries.add(new Polygon(
                    new Point(x - width / 2, stripeY, z + depth / 2 + 0.05),
                    new Point(x + width / 2, stripeY, z + depth / 2 + 0.05),
                    new Point(x + width / 2, stripeY + stripeThickness, z + depth / 2 + 0.05),
                    new Point(x - width / 2, stripeY + stripeThickness, z + depth / 2 + 0.05))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Back stripe
            geometries.add(new Polygon(
                    new Point(x - width / 2, stripeY, z - depth / 2 - 0.05),
                    new Point(x + width / 2, stripeY, z - depth / 2 - 0.05),
                    new Point(x + width / 2, stripeY + stripeThickness, z - depth / 2 - 0.05),
                    new Point(x - width / 2, stripeY + stripeThickness, z - depth / 2 - 0.05))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Right stripe
            geometries.add(new Polygon(
                    new Point(x + width / 2 + 0.05, stripeY, z + depth / 2),
                    new Point(x + width / 2 + 0.05, stripeY, z - depth / 2),
                    new Point(x + width / 2 + 0.05, stripeY + stripeThickness, z - depth / 2),
                    new Point(x + width / 2 + 0.05, stripeY + stripeThickness, z + depth / 2))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Left stripe
            geometries.add(new Polygon(
                    new Point(x - width / 2 - 0.05, stripeY, z + depth / 2),
                    new Point(x - width / 2 - 0.05, stripeY, z - depth / 2),
                    new Point(x - width / 2 - 0.05, stripeY + stripeThickness, z - depth / 2),
                    new Point(x - width / 2 - 0.05, stripeY + stripeThickness, z + depth / 2))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
        }
    }

    /**
     * Adds horizontal floor stripes to a building (original decoration)
     */
    private void addFloorStripes(List<Intersectable> geometries,
                                 double x, double y, double z,
                                 double width, double height, double depth,
                                 Color color) {
        // Calculate number of floors based on height
        int numFloors = Math.max(1, (int) (height / 8));
        double stripeThickness = 0.8;
        // Create an accent color by modifying the main color
        Color decorColor = new Color(
                Math.min(255, color.getColor().getRed() + 60),
                Math.min(255, color.getColor().getGreen() + 30),
                Math.max(0, color.getColor().getBlue() - 30)
        );

        // Add horizontal stripes at each floor level
        for (int floor = 1; floor <= numFloors; floor++) {
            double floorY = y + (height * floor / numFloors) - stripeThickness / 2;

            // Front stripe
            geometries.add(new Polygon(
                    new Point(x - width / 2, floorY, z + depth / 2 + 0.05),
                    new Point(x + width / 2, floorY, z + depth / 2 + 0.05),
                    new Point(x + width / 2, floorY + stripeThickness, z + depth / 2 + 0.05),
                    new Point(x - width / 2, floorY + stripeThickness, z + depth / 2 + 0.05))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Back stripe
            geometries.add(new Polygon(
                    new Point(x - width / 2, floorY, z - depth / 2 - 0.05),
                    new Point(x + width / 2, floorY, z - depth / 2 - 0.05),
                    new Point(x + width / 2, floorY + stripeThickness, z - depth / 2 - 0.05),
                    new Point(x - width / 2, floorY + stripeThickness, z - depth / 2 - 0.05))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Right stripe
            geometries.add(new Polygon(
                    new Point(x + width / 2 + 0.05, floorY, z + depth / 2),
                    new Point(x + width / 2 + 0.05, floorY, z - depth / 2),
                    new Point(x + width / 2 + 0.05, floorY + stripeThickness, z - depth / 2),
                    new Point(x + width / 2 + 0.05, floorY + stripeThickness, z + depth / 2))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Left stripe
            geometries.add(new Polygon(
                    new Point(x - width / 2 - 0.05, floorY, z + depth / 2),
                    new Point(x - width / 2 - 0.05, floorY, z - depth / 2),
                    new Point(x - width / 2 - 0.05, floorY + stripeThickness, z - depth / 2),
                    new Point(x - width / 2 - 0.05, floorY + stripeThickness, z + depth / 2))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
        }
    }

    /**
     * Creates a non-reflective building at the specified position.
     * The building consists of six polygon faces with different colors,
     * and does not have reflective properties.
     */
    private void createNonReflectiveBuilding(List<Intersectable> geometries,
                                             Point pos,
                                             Color color) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        double w = 100, h = 25, d = 60;

        // Original implementation
        geometries.add(new Polygon(
                new Point(x - w / 2, y, z + d / 2),
                new Point(x + w / 2, y, z + d / 2),
                new Point(x + w / 2, y + h, z + d / 2),
                new Point(x - w / 2, y + h, z + d / 2))
                .setEmission(color)
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Back
        geometries.add(new Polygon(
                new Point(x - w / 2, y, z - d / 2),
                new Point(x + w / 2, y, z - d / 2),
                new Point(x + w / 2, y + h, z - d / 2),
                new Point(x - w / 2, y + h, z - d / 2))
                .setEmission(color.scale(0.8))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Right
        geometries.add(new Polygon(
                new Point(x + w / 2, y, z + d / 2),
                new Point(x + w / 2, y, z - d / 2),
                new Point(x + w / 2, y + h, z - d / 2),
                new Point(x + w / 2, y + h, z + d / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Left
        geometries.add(new Polygon(
                new Point(x - w / 2, y, z + d / 2),
                new Point(x - w / 2, y, z - d / 2),
                new Point(x - w / 2, y + h, z - d / 2),
                new Point(x - w / 2, y + h, z + d / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Roof
        geometries.add(new Polygon(
                new Point(x - w / 2, y + h, z + d / 2),
                new Point(x + w / 2, y + h, z + d / 2),
                new Point(x + w / 2, y + h, z - d / 2),
                new Point(x - w / 2, y + h, z - d / 2))
                .setEmission(color.scale(0.5))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));

        // Create a contrasting decoration color
        Color decorColor = new Color(
                Math.max(20, color.getColor().getRed() - 50),
                Math.min(255, color.getColor().getGreen() + 40),
                Math.min(255, color.getColor().getBlue() + 50)
        );

        // Add decorative horizontal stripes (3 floors for this building)
        double stripeThickness = 1.2;
        double[] floorHeights = {h / 4, h / 2, 3 * h / 4};

        for (double floorY : floorHeights) {
            // Front stripe
            geometries.add(new Polygon(
                    new Point(x - w / 2, y + floorY, z + d / 2 + 0.05),
                    new Point(x + w / 2, y + floorY, z + d / 2 + 0.05),
                    new Point(x + w / 2, y + floorY + stripeThickness, z + d / 2 + 0.05),
                    new Point(x - w / 2, y + floorY + stripeThickness, z + d / 2 + 0.05))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Back stripe
            geometries.add(new Polygon(
                    new Point(x - w / 2, y + floorY, z - d / 2 - 0.05),
                    new Point(x + w / 2, y + floorY, z - d / 2 - 0.05),
                    new Point(x + w / 2, y + floorY + stripeThickness, z - d / 2 - 0.05),
                    new Point(x - w / 2, y + floorY + stripeThickness, z - d / 2 - 0.05))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Right stripe
            geometries.add(new Polygon(
                    new Point(x + w / 2 + 0.05, y + floorY, z + d / 2),
                    new Point(x + w / 2 + 0.05, y + floorY, z - d / 2),
                    new Point(x + w / 2 + 0.05, y + floorY + stripeThickness, z - d / 2),
                    new Point(x + w / 2 + 0.05, y + floorY + stripeThickness, z + d / 2))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

            // Left stripe
            geometries.add(new Polygon(
                    new Point(x - w / 2 - 0.05, y + floorY, z + d / 2),
                    new Point(x - w / 2 - 0.05, y + floorY, z - d / 2),
                    new Point(x - w / 2 - 0.05, y + floorY + stripeThickness, z - d / 2),
                    new Point(x - w / 2 - 0.05, y + floorY + stripeThickness, z + d / 2))
                    .setEmission(decorColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
        }
    }
}
