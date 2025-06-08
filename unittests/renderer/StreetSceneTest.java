// renderer/StreetSceneTest.java
package renderer;

import org.junit.jupiter.api.Test;
import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for rendering a colorful, realistic street scene with planted trees,
 * no cars, and a realistic bus station on the right side at the beginning of the street.
 * All street lamps and the bus shelter face toward the road center.
 * The scene uses simplified facades (no windows), reduced lighting, and soft-area shadows.
 * The camera is placed closer to capture more detail. The shopping mall building is moved off
 * the road and set to non-reflective.
 */
public class StreetSceneTest {

    private final Scene scene = new Scene("Colorful Realistic Street Scene");
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    @Test
    public void streetScene() {
        List<Intersectable> geometries = new ArrayList<>();

        // ===== Ground and Road =====
        // Grass plane
        geometries.add(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setEmission(new Color(45, 90, 45))
                .setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(10)));

        // Asphalt road
        geometries.add(new Polygon(
                new Point(-25, 0.1, 200),
                new Point( 25, 0.1, 200),
                new Point( 25, 0.1, -500),
                new Point(-25, 0.1, -500))
                .setEmission(new Color(25, 25, 30))
                .setMaterial(new Material().setKD(0.9).setKS(0.05).setShininess(15)));

        // Center dashes
        for (int z = 70; z >= -450; z -= 40) {
            geometries.add(new Polygon(
                    new Point(-1,  0.15, z),
                    new Point( 1,  0.15, z),
                    new Point( 1,  0.15, z - 20),
                    new Point(-1,  0.15, z - 20))
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
                new Point(-45, 0.8, 200),
                new Point(-25, 0.8, 200),
                new Point(-25, 0.8, -500),
                new Point(-45, 0.8, -500))
                .setEmission(new Color(150, 150, 155))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
        geometries.add(new Polygon(
                new Point(25, 0.8, 200),
                new Point(45, 0.8, 200),
                new Point(45, 0.8, -500),
                new Point(25, 0.8, -500))
                .setEmission(new Color(150, 150, 155))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // ===== Trees =====
        double[] leftTreeZ = {-80, -120, -160, -200, -240, -280};
        for (double zPos : leftTreeZ) {
            createTree(geometries, new Point(-60, 0, zPos), 1.0 + Math.random() * 0.3);
        }
        double[] rightTreeZ = {-70, -110, -150, -190, -230, -270, -310};
        for (double zPos : rightTreeZ) {
            createTree(geometries, new Point(70, 0, zPos), 0.8 + Math.random() * 0.4);
        }

        // ===== Buildings (No Windows) =====
        createBuildingNoWindows(geometries, new Point(-150, 0, -300), 40, 80, 50, new Color(60, 90, 140));
        createBuildingNoWindows(geometries, new Point(-80,  0, -350), 60,120, 40, new Color(160, 80, 60));
        createNonReflectiveBuilding(geometries, new Point(100, 0, -280),    new Color(140,120,160));
        createBuildingNoWindows(geometries, new Point(120, 0, -320), 25, 15, 20, new Color(130, 60, 50));
        createBuildingNoWindows(geometries, new Point(150, 0, -315), 20, 18, 18, new Color(170,140,100));
        createBuildingNoWindows(geometries, new Point(-200,0,-400), 30,150, 25, new Color(60, 65, 70));

        // ===== Street Lamps (geometry only) =====
        for (int i = 0; i < 6; i++) {
            double zLeft  = -40 - i * 50;
            double zRight = zLeft + 25;
            createStreetLamp(geometries, new Point(-50, 0, zLeft));
            createStreetLamp(geometries, new Point( 55, 0, zRight));
        }

        scene.geometries.add(geometries.toArray(new Intersectable[0]));

        // ===== Lighting =====
        scene.setAmbientLight(new AmbientLight(new Color(20,20,25)));
        scene.lights.add(new DirectionalLight(new Color(90,80,70), new Vector(0.4,-0.6,-0.7)));
        scene.lights.add(new DirectionalLight(new Color(30,35,40), new Vector(-0.2,-0.3,0.5)));

        // soft-area street lamps: 300 samples each
        for (int i = 0; i < 6; i += 2) {
            double zLeft  = -40 - i * 50;
            double zRight = zLeft + 25;
            scene.lights.add(
                    new PointLight(new Color(100,90,80), new Point(-50,8,zLeft))
                            .setKl(0.001).setKq(0.0005)
                            .setRadius(2.0).setNumSamples(300)
            );
            scene.lights.add(
                    new PointLight(new Color(100,90,80), new Point(55,8,zRight))
                            .setKl(0.001).setKq(0.0005)
                            .setRadius(2.0).setNumSamples(300)
            );
        }

        // additional area lights: 300 samples each
        scene.lights.add(
                new PointLight(new Color(8add more and more sampling0,70,60), new Point(-150,40,-295))
                        .setKl(0.0003).setKq(0.00015)
                        .setRadius(1.5).setNumSamples(300)
        );
        scene.lights.add(
                new PointLight(new Color(80,70,60), new Point(-80,60,-350))
                        .setKl(0.0003).setKq(0.00015)
                        .setRadius(1.5).setNumSamples(300)
        );
        scene.lights.add(
                new PointLight(new Color(120,110,100), new Point(35,6,20))
                        .setKl(0.0005).setKq(0.0003)
                        .setRadius(1.0).setNumSamples(300)
        );

        // ===== Camera Setup with Multithreading & Logging =====
        Camera camera = cameraBuilder
                .setLocation(new Point(-15, 12, 60))
                .setDirection(new Vector(0.1, -0.15, -1), new Vector(0, 1, 0))
                .setVpDistance(150)
                .setVpSize(300, 200)
                .setResolution(1500, 1000)
                .setMultithreading(-2)    // use (CPU cores − 2) threads
                .setDebugPrint(0.1)       // print progress every 1%
                .build();

        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());

        long tStart = System.currentTimeMillis();
        camera.renderImage();
        long tEnd = System.currentTimeMillis();

        System.out.printf("Render completed in %.3f seconds.%n", (tEnd - tStart) / 1000.0);

        camera.writeToImage("street");
        System.out.println("Image written to file: street.png");
    }


    /**
     * Creates a street lamp at the specified position.
     * The lamp consists of a base, a vertical pole, a horizontal arm,
     * a bracket, a lamp housing, and a light sphere.
     */
    private void createStreetLamp(List<Intersectable> geometries, Point pos) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Base
        geometries.add(new Sphere(new Point(x, y + 0.5, z), 0.8)
                .setEmission(new Color(40,40,45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Vertical pole
        for (double h = 1.0; h <= 7.0; h += 0.4) {
            geometries.add(new Sphere(new Point(x, y + h, z), 0.35)
                    .setEmission(new Color(30,30,35))
                    .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(60)));
        }

        // Horizontal arm toward road center
        double dir = x < 0 ? +1.0 : -1.0;
        for (double off = 0.4; off <= 2.4; off += 0.4) {
            geometries.add(new Sphere(new Point(x + dir * off, y + 7, z), 0.28)
                    .setEmission(new Color(30,30,35))
                    .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(60)));
        }

        // Bracket
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.8, z), 0.25)
                .setEmission(new Color(40,40,45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.5, z), 0.25)
                .setEmission(new Color(40,40,45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Lamp housing
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.0, z), 1.1)
                .setEmission(new Color(25,25,30))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // Light sphere
        geometries.add(new Sphere(new Point(x + dir * 2.4, y + 6.0, z), 1.5)
                .setEmission(new Color(255,230,180))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(10)));
    }

    /**
     * Creates a tree at the specified position with a given scale.
     * The tree consists of multiple spheres representing the trunk and foliage.
     */
    private void createTree(List<Intersectable> geometries, Point pos, double scale) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        geometries.add(new Sphere(new Point(x, y + 1.5 * scale, z), 1.5 * scale)
                .setEmission(new Color(80,50,30)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y + 4.0 * scale, z), 1.2 * scale)
                .setEmission(new Color(90,60,40)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y + 5.0 * scale, z), scale)
                .setEmission(new Color(100,70,50)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y +10.0 * scale, z), 6.0 * scale)
                .setEmission(new Color(30,100,40)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x - 2*scale, y + 8.0 * scale, z - 1*scale), 4.0 * scale)
                .setEmission(new Color(25,85,30)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x + 3*scale, y + 9.0 * scale, z + 2*scale), 3.5 * scale)
                .setEmission(new Color(40,120,50)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
    }

    /**
     * Creates a building without windows at the specified position.
     * The building consists of six polygon faces with different colors.
     */
    private void createBuildingNoWindows(List<Intersectable> geometries,
                                         Point pos,
                                         double width, double height, double depth,
                                         Color color) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        // Front face
        geometries.add(new Polygon(
                new Point(x - width/2, y,          z + depth/2),
                new Point(x + width/2, y,          z + depth/2),
                new Point(x + width/2, y + height, z + depth/2),
                new Point(x - width/2, y + height, z + depth/2))
                .setEmission(color)
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Back face
        geometries.add(new Polygon(
                new Point(x - width/2, y,          z - depth/2),
                new Point(x + width/2, y,          z - depth/2),
                new Point(x + width/2, y + height, z - depth/2),
                new Point(x - width/2, y + height, z - depth/2))
                .setEmission(color.scale(0.8))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Right face
        geometries.add(new Polygon(
                new Point(x + width/2, y,          z + depth/2),
                new Point(x + width/2, y,          z - depth/2),
                new Point(x + width/2, y + height, z - depth/2),
                new Point(x + width/2, y + height, z + depth/2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Left face
        geometries.add(new Polygon(
                new Point(x - width/2, y,          z + depth/2),
                new Point(x - width/2, y,          z - depth/2),
                new Point(x - width/2, y + height, z - depth/2),
                new Point(x - width/2, y + height, z + depth/2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
        // Roof
        geometries.add(new Polygon(
                new Point(x - width/2, y + height, z + depth/2),
                new Point(x + width/2, y + height, z + depth/2),
                new Point(x + width/2, y + height, z - depth/2),
                new Point(x - width/2, y + height, z - depth/2))
                .setEmission(color.scale(0.5))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
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

        // Front face
        geometries.add(new Polygon(
                new Point(x - w/2, y,      z + d/2),
                new Point(x + w/2, y,      z + d/2),
                new Point(x + w/2, y + h,  z + d/2),
                new Point(x - w/2, y + h,  z + d/2))
                .setEmission(color)
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Back face
        geometries.add(new Polygon(
                new Point(x - w/2, y,      z - d/2),
                new Point(x + w/2, y,      z - d/2),
                new Point(x + w/2, y + h,  z - d/2),
                new Point(x - w/2, y + h,  z - d/2))
                .setEmission(color.scale(0.8))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Right face
        geometries.add(new Polygon(
                new Point(x + w/2, y,      z + d/2),
                new Point(x + w/2, y,      z - d/2),
                new Point(x + w/2, y + h,  z - d/2),
                new Point(x + w/2, y + h,  z + d/2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Left face
        geometries.add(new Polygon(
                new Point(x - w/2, y,      z + d/2),
                new Point(x - w/2, y,      z - d/2),
                new Point(x - w/2, y + h,  z - d/2),
                new Point(x - w/2, y + h,  z + d/2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
        // Roof
        geometries.add(new Polygon(
                new Point(x - w/2, y + h,  z + d/2),
                new Point(x + w/2, y + h,  z + d/2),
                new Point(x + w/2, y + h,  z - d/2),
                new Point(x - w/2, y + h,  z - d/2))
                .setEmission(color.scale(0.5))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.0).setShininess(40)));
    }
}



//    /**
//     * Creates a realistic bus station at the specified position.
//     * The bus station consists of a platform, back wall, side panels, roof,
//     * support pillars, a bench, and a sign pole.
//     * The bench faces the road, and the sign pole is placed at the end of the platform.
//     */
//    private void createRealisticBusStation(List<Intersectable> geometries, Point pos) {
//        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
//
//        // Platform
//        geometries.add(new Polygon(
//                new Point(x-8, y+0.2, z-6), new Point(x+8, y+0.2, z-6),
//                new Point(x+8, y+0.2, z+6), new Point(x-8, y+0.2, z+6))
//                .setEmission(new Color(140,140,145))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
//
//        // Back wall (away from road)
//        geometries.add(new Polygon(
//                new Point(x-7, y+0.2, z-5), new Point(x+7, y+0.2, z-5),
//                new Point(x+7, y+7,   z-5), new Point(x-7, y+7,   z-5))
//                .setEmission(new Color(180,180,185))
//                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
//
//        // Side panels (open toward road)
//        geometries.add(new Polygon(
//                new Point(x-7, y+0.2, z-5), new Point(x-7, y+0.2, z+2),
//                new Point(x-7, y+7,   z+2), new Point(x-7, y+7,   z-5))
//                .setEmission(new Color(170,170,175))
//                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
//        geometries.add(new Polygon(
//                new Point(x+7, y+0.2, z-5), new Point(x+7, y+0.2, z+2),
//                new Point(x+7, y+7,   z+2), new Point(x+7, y+7,   z-5))
//                .setEmission(new Color(170,170,175))
//                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
//
//        // Roof (extends toward road)
//        geometries.add(new Polygon(
//                new Point(x-7.5, y+7, z-5.5), new Point(x+7.5, y+7, z-5.5),
//                new Point(x+7.5, y+7, z+3),    new Point(x-7.5, y+7, z+3))
//                .setEmission(new Color(120,120,125))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(25)));
//
//        // Support pillars (continuous tubes)
//        for (double h = 0.5; h <= 6.8; h += 0.3) {
//            geometries.add(new Sphere(new Point(x-6.5, y+h, z+2.5), 0.2)
//                    .setEmission(new Color(100,100,105))
//                    .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(50)));
//            geometries.add(new Sphere(new Point(x+6.5, y+h, z+2.5), 0.2)
//                    .setEmission(new Color(100,100,105))
//                    .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(50)));
//            geometries.add(new Sphere(new Point(x-6.5, y+h, z-4.5), 0.2)
//                    .setEmission(new Color(100,100,105))
//                    .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(50)));
//            geometries.add(new Sphere(new Point(x+6.5, y+h, z-4.5), 0.2)
//                    .setEmission(new Color(100,100,105))
//                    .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(50)));
//        }
//
//        // Bench facing road
//        geometries.add(new Polygon(
//                new Point(x-4, y+1.8, z-3), new Point(x+4, y+1.8, z-3),
//                new Point(x+4, y+2.2, z-2), new Point(x-4, y+2.2, z-2))
//                .setEmission(new Color(80,60,40))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
//        // Backrest toward shelter
//        geometries.add(new Polygon(
//                new Point(x-4, y+2.2, z-3.2), new Point(x+4, y+2.2, z-3.2),
//                new Point(x+4, y+3.8, z-3),    new Point(x-4, y+3.8, z-3))
//                .setEmission(new Color(85,65,45))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));
//
//        // Bench legs
//        for (double lx : new double[]{x-3, x+3}) {
//            for (double lh = 0.4; lh <= 1.6; lh += 0.4) {
//                geometries.add(new Sphere(new Point(lx, y+lh, z-2.8), 0.15)
//                        .setEmission(new Color(60,60,65))
//                        .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));
//            }
//        }
//
//        // Sign pole
//        for (double h = 0.5; h <= 8.5; h += 0.25) {
//            geometries.add(new Sphere(new Point(x+9, y+h, z), 0.15)
//                    .setEmission(new Color(40,40,45))
//                    .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(60)));
//        }
//        // Yellow sign
//        geometries.add(new Polygon(
//                new Point(x+8.5, y+8.5, z-1.5), new Point(x+11.5, y+8.5, z-1.5),
//                new Point(x+11.5, y+10,   z-1.5), new Point(x+8.5, y+10,   z-1.5))
//                .setEmission(new Color(255,220,0))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));
//        // Black circle
//        geometries.add(new Sphere(new Point(x+10, y+9.25, z-1.4), 0.4)
//                .setEmission(new Color(20,20,20))
//                .setMaterial(new Material().setKD(0.9).setKS(0.1).setShininess(10)));
//
//        // Trash bin
//        geometries.add(new Sphere(new Point(x+5, y+1.5, z-4), 0.8)
//                .setEmission(new Color(60,80,60))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(25)));
//        geometries.add(new Sphere(new Point(x+5, y+2.5, z-4), 0.9)
//                .setEmission(new Color(50,70,50))
//                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(25)));
//
//        // Info board
//        geometries.add(new Polygon(
//                new Point(x-2, y+3,   z-4.8), new Point(x+2, y+3,   z-4.8),
//                new Point(x+2, y+5.5, z-4.8), new Point(x-2, y+5.5, z-4.8))
//                .setEmission(new Color(200,200,205))
//                .setMaterial(new Material().setKD(0.7).setKS(0.3).setKR(0.1).setShininess(50)));
//        // Glass panel
//        geometries.add(new Polygon(
//                new Point(x-1.8, y+3.2, z-4.7), new Point(x+1.8, y+3.2, z-4.7),
//                new Point(x+1.8, y+5.3, z-4.7), new Point(x-1.8, y+5.3, z-4.7))
//                .setEmission(new Color(220,230,240))
//                .setMaterial(new Material().setKD(0.1).setKS(0.9).setKT(0.8).setShininess(100)));
//    }
