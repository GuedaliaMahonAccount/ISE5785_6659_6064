// renderer/StreetSceneTest.java
package renderer;

import org.junit.jupiter.api.Test;
import geometries.*;
import lighting.*;
import primitives.*;
import renderer.sampling.SamplingConfig;
import renderer.sampling.SamplingPattern;
import renderer.sampling.TargetShape;
import scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for rendering a colorful, realistic street scene with planted trees,
 * no cars, a realistic crescent moon providing gentle lighting, and elevated sidewalks.
 * All street lamps and the bus shelter face toward the road center.
 * The scene uses simplified facades (no windows), reduced lighting, and soft-area shadows.
 * The camera is placed closer to capture more detail. The shopping mall building is moved off
 * the road and set to non-reflective.
 */
public class StreetSceneTest {

    private final Scene scene = new Scene("Colorful Realistic Street Scene with Crescent Moon");
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

        // Sidewalks - right sidewalk (original color)
        geometries.add(new Polygon(
                new Point(25, 1.2, 200),
                new Point(45, 1.2, 200),
                new Point(45, 1.2, -500),
                new Point(25, 1.2, -500))
                .setEmission(new Color(150, 150, 155))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Sidewalks - left sidewalk (lighter color for better visibility)
        geometries.add(new Polygon(
                new Point(-45, 1.2, 200),
                new Point(-25, 1.2, 200),
                new Point(-25, 1.2, -500),
                new Point(-45, 1.2, -500))
                .setEmission(new Color(180, 180, 185))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Curbs - right curb (original color)
        geometries.add(new Polygon(
                new Point(25, 0.12, 200),
                new Point(25, 1.2, 200),
                new Point(25, 1.2, -500),
                new Point(25, 0.12, -500))
                .setEmission(new Color(120, 120, 125))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // Curbs - left curb (darker color for contrast with lighter sidewalk)
        geometries.add(new Polygon(
                new Point(-25, 0.12, 200),
                new Point(-25, 1.2, 200),
                new Point(-25, 1.2, -500),
                new Point(-25, 0.12, -500))
                .setEmission(new Color(90, 90, 95))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)));

        // ===== Crescent Moon (from first version) =====
        // Create a crescent moon positioned more to the center/right and smaller
        // Main moon body (smaller)
        geometries.add(new Sphere(new Point(50, 120, -250), 15)
                .setEmission(new Color(220, 220, 180))
                .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(30)));

        // Moon surface details (smaller craters on visible crescent)
        geometries.add(new Sphere(new Point(52, 118, -248), 2)
                .setEmission(new Color(180, 180, 140))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));
        geometries.add(new Sphere(new Point(55, 125, -252), 1.5)
                .setEmission(new Color(190, 190, 150))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));

        // ===== Trees (adjusted for sidewalk height from second version) =====
        double[] leftTreeZ = {-80, -120, -160, -200, -240, -280};
        for (double zPos : leftTreeZ) {
            createTree(geometries, new Point(-60, 1.2, zPos), 1.0 + Math.random() * 0.3);
        }
        double[] rightTreeZ = {-70, -110, -150, -190, -230, -270, -310};
        for (double zPos : rightTreeZ) {
            createTree(geometries, new Point(70, 1.2, zPos), 0.8 + Math.random() * 0.4);
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
            // Add 1.2 to y-coordinate to position lamps on the sidewalk
            createStreetLamp(geometries, new Point(-50, 1.2, zLeft));
            createStreetLamp(geometries, new Point( 55, 1.2, zRight));
        }

        scene.geometries.add(geometries.toArray(new Intersectable[0]));

        // ===== Lighting =====
        // Darker ambient light for night scene
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        // Reduced directional lights for night atmosphere
        scene.lights.add(new DirectionalLight(new Color(60, 50, 40), new Vector(0.4, -0.6, -0.7)));
        scene.lights.add(new DirectionalLight(new Color(20, 25, 30), new Vector(-0.2, -0.3, 0.5)));

        // ===== Moon Light (adjusted position and direction) =====
        // Soft moonlight from the new moon position, directed more downward
        scene.lights.add(
                new PointLight(new Color(90, 85, 65), new Point(50, 120, -250))
                        .setKl(0.00001).setKq(0.000005)
                        .setRadius(10.0).setNumSamples(5)
        );

        // Additional moonlight to illuminate the ground below
        scene.lights.add(
                new SpotLight(new Color(70, 65, 50), new Point(50, 120, -250), new Vector(0.1, -1, 0.3))
                        .setKl(0.00005).setKq(0.00001)
                        .setNarrowBeam(15)
        );

        // Soft-area street lamps: adjusted for sidewalk height
        for (int i = 0; i < 6; i++) {
            double zLeft  = -40 - i * 50;
            double zRight = zLeft + 25;
            scene.lights.add(
                    new PointLight(new Color(100, 90, 80), new Point(-50, 8 + 1.2, zLeft))
                            .setKl(0.001).setKq(0.0005)
                            .setRadius(10.0).setNumSamples(5)
            );
            scene.lights.add(
                    new PointLight(new Color(100, 90, 80), new Point(55, 8 + 1.2, zRight))
                            .setKl(0.001).setKq(0.0005)
                            .setRadius(10.0).setNumSamples(5)
            );
        }

        // Additional area lights
        scene.lights.add(
                new PointLight(new Color(80, 70, 60), new Point(-150, 40, -295))
                        .setKl(0.0003).setKq(0.00015)
                        .setRadius(1.5).setNumSamples(5)
        );
        scene.lights.add(
                new PointLight(new Color(80, 70, 60), new Point(-80, 60, -350))
                        .setKl(0.0003).setKq(0.00015)
                        .setRadius(1.5).setNumSamples(5)
        );
        scene.lights.add(
                new PointLight(new Color(120, 110, 100), new Point(35, 6, 20))
                        .setKl(0.0005).setKq(0.0003)
                        .setRadius(1.0).setNumSamples(5)
        );

        // ===== Camera Setup with Multithreading, Logging & AA =====
        Camera camera = cameraBuilder
                .setLocation(new Point(-15, 12, 60))
                .setDirection(new Vector(0.1, -0.15, -1), new Vector(0, 1, 0))
                .setVpDistance(150)
                .setVpSize(300, 200)
                .setResolution(1500, 1000)
                .setMultithreading(-2)
                .setDebugPrint(1.0)
                .build();
        // Enable sampling
        camera.setSamplingConfig(new SamplingConfig(
                5, TargetShape.RECTANGLE, SamplingPattern.GRID
        ));

        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());

        long tStart = System.currentTimeMillis();
        camera.renderImage();
        long tEnd = System.currentTimeMillis();
        System.out.printf("Render completed in %.3f seconds.%n", (tEnd - tStart) / 1000.0);

        camera.writeToImage("street3");
        System.out.println("Image written to file: street3.png");
    }

    /**
     * Creates a street lamp at the specified position.
     * The lamp consists of:
     *  - a spherical base that sits flush with the ground,
     *  - a vertical pole made of stacked spheres,
     *  - a horizontal arm pointing toward the road center,
     *  - a bracket supporting the lamp housing,
     *  - the lamp housing itself,
     *  - and the glowing light sphere.
     *
     * @param geometries the list to which all lamp parts will be added
     * @param pos        the (x,y,z) base position on the sidewalk
     */
    private void createStreetLamp(List<Intersectable> geometries, Point pos) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Base - lower the center to be more embedded in the sidewalk
        // Center at y-0.3 with radius 0.8 makes the base appear embedded in the ground
        geometries.add(new Sphere(new Point(x, y - 0.3, z), 0.8)
                .setEmission(new Color(40,40,45))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Add a flat disc at sidewalk level for better grounding effect
        geometries.add(new Polygon(
                new Point(x - 0.8, y + 0.01, z + 0.8),
                new Point(x + 0.8, y + 0.01, z + 0.8),
                new Point(x + 0.8, y + 0.01, z - 0.8),
                new Point(x - 0.8, y + 0.01, z - 0.8))
                .setEmission(new Color(35,35,40))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(40)));

        // Vertical pole - start slightly lower to connect cleanly with embedded base
        for (double h = 0.5; h <= 7.0; h += 0.4) {
            geometries.add(new Sphere(new Point(x, y + h, z), 0.35)
                    .setEmission(new Color(30,30,35))
                    .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(60)));
        }

        // Rest of the lamp components remain unchanged
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
     * Adjusted for proper positioning on sidewalks.
     */
    private void createTree(List<Intersectable> geometries, Point pos, double scale) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        // Trunk - positioned to start from the sidewalk level
        geometries.add(new Sphere(new Point(x, y + 0.8 * scale, z), 1.5 * scale)
                .setEmission(new Color(80,50,30)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y + 2.5 * scale, z), 1.2 * scale)
                .setEmission(new Color(90,60,40)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));
        geometries.add(new Sphere(new Point(x, y + 3.5 * scale, z), scale)
                .setEmission(new Color(100,70,50)).setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(15)));

        // Foliage
        geometries.add(new Sphere(new Point(x, y + 8.5 * scale, z), 6.0 * scale)
                .setEmission(new Color(30,100,40)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x - 2*scale, y + 6.5 * scale, z - 1*scale), 4.0 * scale)
                .setEmission(new Color(25,85,30)).setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x + 3*scale, y + 7.5 * scale, z + 2*scale), 3.5 * scale)
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