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
 * Test class for rendering a colorful, realistic street scene with planted trees and no cars,
 * with windows removed and lighting toned down for a more balanced, “perfect” look,
 * now including circular-area soft shadows.
 */
class StreetSceneTest {
    /** Scene for the test */
    private final Scene scene = new Scene("Colorful Realistic Street Scene (No Windows, Soft Shadows)");

    /** Camera builder for the test */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    /**
     * Create a photorealistic street scene with vibrant colors, trees planted on the ground,
     * without any vehicles, without windows on buildings, and with reduced lighting intensity,
     * plus soft shadows.
     */
    @Test
    void streetScene() {
        // Create a list to hold all geometries
        List<Intersectable> geometries = new ArrayList<>();

        // ============ Ground and Road Foundation ==============
        // Large ground plane (grass/earth) extending far in all directions
        geometries.add(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setEmission(new Color(45, 90, 45))  // Dark green for grass
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.1).setShininess(10)));

        // Main asphalt road - realistic dark gray
        geometries.add(new Polygon(
                new Point(-25, 0.1, 200),
                new Point(25, 0.1, 200),
                new Point(25, 0.1, -500),
                new Point(-25, 0.1, -500))
                .setEmission(new Color(25, 25, 30))   // Darker asphalt
                .setMaterial(new Material()
                        .setKD(0.9).setKS(0.05).setShininess(15)));

        // Road center line - dashed bright yellow
        for (int z = 150; z >= -450; z -= 40) {
            geometries.add(new Polygon(
                    new Point(-1, 0.15, z),
                    new Point(1, 0.15, z),
                    new Point(1, 0.15, z - 20),
                    new Point(-1, 0.15, z - 20))
                    .setEmission(new Color(200, 170, 0))  // Less intense yellow
                    .setMaterial(new Material()
                            .setKD(0.8).setKS(0.2).setShininess(20)));
        }

        // Road edges - solid white lines
        geometries.add(new Polygon(
                new Point(-24, 0.12, 200),
                new Point(-22, 0.12, 200),
                new Point(-22, 0.12, -500),
                new Point(-24, 0.12, -500))
                .setEmission(new Color(200, 200, 200))  // Less intense white
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setShininess(25)));

        geometries.add(new Polygon(
                new Point(22, 0.12, 200),
                new Point(24, 0.12, 200),
                new Point(24, 0.12, -500),
                new Point(22, 0.12, -500))
                .setEmission(new Color(200, 200, 200))  // Less intense white
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setShininess(25)));

        // Sidewalks - lighter concrete color
        geometries.add(new Polygon(
                new Point(-45, 0.8, 200),
                new Point(-25, 0.8, 200),
                new Point(-25, 0.8, -500),
                new Point(-45, 0.8, -500))
                .setEmission(new Color(150, 150, 155))  // Darker gray
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(40)));

        geometries.add(new Polygon(
                new Point(25, 0.8, 200),
                new Point(45, 0.8, 200),
                new Point(45, 0.8, -500),
                new Point(25, 0.8, -500))
                .setEmission(new Color(150, 150, 155))  // Darker gray
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(40)));

        // ============ Realistic Trees - Properly Planted ==============
        // Left side trees - trunk base at ground level (y = 0)
        double[] leftTreeZ = {-80, -120, -160, -200, -240, -280};
        for (double zPos : leftTreeZ) {
            createTree(geometries, new Point(-60, 0, zPos), 1.0 + Math.random() * 0.3);
        }

        // Right side trees - trunk base at ground level (y = 0)
        double[] rightTreeZ = {-70, -110, -150, -190, -230, -270, -310};
        for (double zPos : rightTreeZ) {
            createTree(geometries, new Point(70, 0, zPos), 0.8 + Math.random() * 0.4);
        }

        // ============ Realistic Buildings - Varied Colors, No Windows ==============
        // Building 1 - Tall apartment building (blue)
        createBuildingNoWindows(geometries, new Point(-150, 0, -300), 40, 80, 50, new Color(60, 90, 140));

        // Building 2 - Office building (terracotta)
        createBuildingNoWindows(geometries, new Point(-80, 0, -350), 60, 120, 40, new Color(160, 80, 60));

        // Building 3 - Shopping mall (light purple)
        createBuildingNoWindows(geometries, new Point(50, 0, -280), 100, 25, 60, new Color(140, 120, 160));

        // Building 4 - Residential house (brick red)
        createBuildingNoWindows(geometries, new Point(120, 0, -320), 25, 15, 20, new Color(130, 60, 50));

        // Building 5 - Residential house (tan)
        createBuildingNoWindows(geometries, new Point(150, 0, -315), 20, 18, 18, new Color(170, 140, 100));

        // Building 6 - High-rise in background (steel gray)
        createBuildingNoWindows(geometries, new Point(-200, 0, -400), 30, 150, 25, new Color(60, 65, 70));

        // ============ Street Infrastructure ==============
        // Traffic lights (set to green)
        createTrafficLight(geometries, new Point(30, 0, -80), new Color(40, 160, 40));
        createTrafficLight(geometries, new Point(-30, 0, -150), new Color(40, 160, 40));

        // Street lamps – now with soft shadows enabled
        for (int i = 0; i < 6; i++) {
            double zPos = -40 - i * 50;
            createStreetLamp(geometries, new Point(-50, 0, zPos));
            createStreetLamp(geometries, new Point(55, 0, zPos + 25));
        }

        // Add all geometries to the scene
        scene.geometries.add(geometries.toArray(new Intersectable[0]));

        // ============ Realistic Lighting – Further Reduced Intensity ==============
        // Ambient light (much softer daylight)
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 25)));

        // Main sun (warm late-afternoon) – significantly reduced intensity
        scene.lights.add(
                new DirectionalLight(new Color(90, 80, 70), new Vector(0.4, -0.6, -0.7)));

        // Sky fill light (cooler) – significantly reduced intensity
        scene.lights.add(
                new DirectionalLight(new Color(30, 35, 40), new Vector(-0.2, -0.3, 0.5)));

        // Street lamp point lights – fewer lights, lowered intensity, AND soft shadows:
        for (int i = 0; i < 6; i += 2) {  // Use only every second lamp
            double zPos = -40 - i * 50;
            scene.lights.add(
                    new PointLight(new Color(100, 90, 80), new Point(-50, 8, zPos))
                            .setKl(0.001).setKq(0.0005)
                            .setRadius(2.0)        // ← radius of area light
                            .setNumSamples(20));  // ← number of shadow-ray samples
            scene.lights.add(
                    new PointLight(new Color(100, 90, 80), new Point(55, 8, zPos + 25))
                            .setKl(0.001).setKq(0.0005)
                            .setRadius(2.0)        // ← radius of area light
                            .setNumSamples(20));  // ← number of shadow-ray samples
        }

        // Building interior lighting (warm window glow) – minimized (just a subtle warm glow),
        // but still with a small soft-shadow radius:
        scene.lights.add(
                new PointLight(new Color(80, 70, 60), new Point(-150, 40, -295))
                        .setKl(0.0003).setKq(0.00015)
                        .setRadius(1.5).setNumSamples(10));
        scene.lights.add(
                new PointLight(new Color(80, 70, 60), new Point(-80, 60, -350))
                        .setKl(0.0003).setKq(0.00015)
                        .setRadius(1.5).setNumSamples(10));

        // ============ Camera Setup for Realistic View ==============
        Camera camera = cameraBuilder
                .setLocation(new Point(-15, 12, 100))       // Slightly off-center, eye level
                .setDirection(new Vector(0.1, -0.15, -1))   // Pointing down the street
                .setVpDistance(150)
                .setVpSize(300, 200)                        // Wider aspect ratio
                .setResolution(1500, 1000)                  // High resolution
                .build();

        // Render the photorealistic image (with soft shadows)
        camera.renderImage();
        camera.writeToImage("street");
    }

    /**
     * Create a realistic tree with its trunk base at ground level (y = position.getY())
     */
    private void createTree(List<Intersectable> geometries, Point position, double scale) {
        double x = position.getX();
        double y = position.getY();  // Ground level (0)
        double z = position.getZ();

        // Trunk bottom sphere touching the ground
        geometries.add(new Sphere(new Point(x, y + 1.5 * scale, z), 1.5 * scale)
                .setEmission(new Color(80, 50, 30))   // Dark wood
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.1).setShininess(15)));

        // Trunk mid section
        geometries.add(new Sphere(new Point(x, y + 4 * scale, z), 1.2 * scale)
                .setEmission(new Color(90, 60, 40))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.1).setShininess(15)));

        // Trunk upper section
        geometries.add(new Sphere(new Point(x, y + 5 * scale, z), 1.0 * scale)
                .setEmission(new Color(100, 70, 50))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.1).setShininess(15)));

        // Canopy main sphere
        geometries.add(new Sphere(new Point(x, y + 10 * scale, z), 6 * scale)
                .setEmission(new Color(30, 100, 40))   // Bright green leaves
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(25)));

        // Additional canopy spheres for natural variation
        geometries.add(new Sphere(new Point(x - 2 * scale, y + 8 * scale, z - 1 * scale), 4 * scale)
                .setEmission(new Color(25, 85, 30))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(25)));
        geometries.add(new Sphere(new Point(x + 3 * scale, y + 9 * scale, z + 2 * scale), 3.5 * scale)
                .setEmission(new Color(40, 120, 50))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(25)));
    }

    /**
     * Create a realistic building with a colored facade but no windows.
     */
    private void createBuildingNoWindows(List<Intersectable> geometries,
                                         Point position, double width, double height, double depth, Color color) {
        double x = position.getX();
        double z = position.getZ();
        double y = position.getY();  // Ground level

        // Front face (facing camera) at z + depth/2
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z + depth / 2),
                new Point(x + width / 2, y, z + depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2),
                new Point(x - width / 2, y + height, z + depth / 2))
                .setEmission(color)
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Back face (away from camera) at z - depth/2
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z - depth / 2),
                new Point(x + width / 2, y, z - depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2))
                .setEmission(color.scale(0.8))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Right side face
        geometries.add(new Polygon(
                new Point(x + width / 2, y, z + depth / 2),
                new Point(x + width / 2, y, z - depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Left side face
        geometries.add(new Polygon(
                new Point(x - width / 2, y, z + depth / 2),
                new Point(x - width / 2, y, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z + depth / 2))
                .setEmission(color.scale(0.7))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));

        // Building roof
        geometries.add(new Polygon(
                new Point(x - width / 2, y + height, z + depth / 2),
                new Point(x + width / 2, y + height, z + depth / 2),
                new Point(x + width / 2, y + height, z - depth / 2),
                new Point(x - width / 2, y + height, z - depth / 2))
                .setEmission(color.scale(0.5))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setKR(0.1).setShininess(40)));
    }

    /**
     * Create a realistic street lamp with warm yellow glass.
     */
    private void createStreetLamp(List<Intersectable> geometries, Point position) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        // Solid base at ground level
        geometries.add(new Sphere(new Point(x, y + 0.5, z), 0.7)
                .setEmission(new Color(40, 40, 45))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(40)));

        // Main pole – approximated by spheres
        for (int i = 1; i <= 6; i++) {
            geometries.add(new Sphere(new Point(x, y + i, z), 0.3)
                    .setEmission(new Color(30, 30, 35))
                    .setMaterial(new Material()
                            .setKD(0.6).setKS(0.4).setShininess(60)));
        }

        // Horizontal arm extending outward
        geometries.add(new Sphere(new Point(x + 0.8, y + 7, z), 0.25)
                .setEmission(new Color(30, 30, 35))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setShininess(60)));
        geometries.add(new Sphere(new Point(x + 1.6, y + 7, z), 0.25)
                .setEmission(new Color(30, 30, 35))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setShininess(60)));

        // Lamp support
        geometries.add(new Sphere(new Point(x + 2.2, y + 7, z), 0.3)
                .setEmission(new Color(40, 40, 45))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(40)));

        // Lamp itself (warm yellow glass, more subtle)
        geometries.add(new Sphere(new Point(x + 2.2, y + 6.2, z), 1.0)
                .setEmission(new Color(160, 140, 100))
                .setMaterial(new Material()
                        .setKD(0.4).setKS(0.6).setKT(0.2).setShininess(80)));
    }

    /**
     * Create a traffic light with a specified lit color (e.g. green).
     */
    private void createTrafficLight(List<Intersectable> geometries, Point position, Color litColor) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        // Traffic light post base
        geometries.add(new Sphere(new Point(x, y + 0.5, z), 0.5)
                .setEmission(new Color(30, 30, 30))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(30)));

        // Traffic light pole (dark metal)
        geometries.add(new Sphere(new Point(x, y + 6, z), 0.5)
                .setEmission(new Color(40, 40, 40))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(30)));

        // Traffic light housing (matte black)
        geometries.add(new Polygon(
                new Point(x - 1, y + 10, z - 0.5),
                new Point(x + 1, y + 10, z - 0.5),
                new Point(x + 1, y + 12, z - 0.5),
                new Point(x - 1, y + 12, z - 0.5))
                .setEmission(new Color(60, 60, 60))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setShininess(50)));

        // Lit signal (green/yellow/red as specified)
        geometries.add(new Sphere(new Point(x, y + 11.5, z - 0.4), 0.3)
                .setEmission(litColor)
                .setMaterial(new Material()
                        .setKD(0.3).setKS(0.7).setShininess(100)));
    }

    /**
     * Create a bus stop with a colored roof and metallic supports.
     */
    private void createBusStop(List<Intersectable> geometries, Point position) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        // Bus stop shelter roof (blue)
        geometries.add(new Polygon(
                new Point(x - 3, y + 4, z - 2),
                new Point(x + 3, y + 4, z - 2),
                new Point(x + 3, y + 4, z + 2),
                new Point(x - 3, y + 4, z + 2))
                .setEmission(new Color(40, 80, 130))
                .setMaterial(new Material()
                        .setKD(0.5).setKS(0.5).setKR(0.2).setShininess(70)));

        // Support posts (metallic gray)
        geometries.add(new Sphere(new Point(x - 2.5, y + 2, z - 1.5), 0.2)
                .setEmission(new Color(90, 90, 95))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(40)));
        geometries.add(new Sphere(new Point(x + 2.5, y + 2, z - 1.5), 0.2)
                .setEmission(new Color(90, 90, 95))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(40)));

        // Bus stop bench (dark brown wood)
        geometries.add(new Polygon(
                new Point(x - 2.5, y + 1.2, z + 0.5),
                new Point(x + 2.5, y + 1.2, z + 0.5),
                new Point(x + 2.5, y + 1, z + 0.5),
                new Point(x - 2.5, y + 1, z + 0.5))
                .setEmission(new Color(90, 60, 20))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.3).setShininess(30)));
    }
}
