package renderer;

import primitives.*;
import scene.Scene;
import geometries.*;
import lighting.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Mini-project 1 demonstrating soft shadows using super sampling
 */
public class Miniproject1Test {
    /**
     * Creates a complex scene with multiple geometries and light sources to demonstrate soft shadows
     */
    public static void main(String[] args) {
        // Create the scene
        Scene scene = new Scene("Mini-project 1: Soft Shadows");
        scene.setBackground(new Color(5, 5, 10));

        // Set ambient light - softer ambient for better shadow contrast
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        // Add multiple light sources with different colors and intensities
        // Main light - point light with soft shadows (positioned higher and further)
        scene.lights.add(
                new PointLight(new Color(600, 500, 400), new Point(-80, 120, 200))
                        .setKl(0.0003).setKq(0.0002)
        );

        // Secondary light - directional light (softer)
        scene.lights.add(
                new DirectionalLight(new Color(80, 120, 160), new Vector(1, -0.8, -1))
        );

        // Spot light for accent lighting (repositioned)
        scene.lights.add(
                new SpotLight(new Color(400, 250, 150), new Point(100, 150, 120), new Vector(-0.6, -1, -0.8))
                        .setKl(0.0001).setKq(0.00003)
        );

        // Additional point light (moved for better lighting distribution)
        scene.lights.add(
                new PointLight(new Color(180, 180, 120), new Point(50, 100, -20))
                        .setKl(0.0002).setKq(0.0001)
        );

        // Create a list to hold all geometries
        List<Intersectable> geometries = new ArrayList<>();

        // ============ Floor and Walls ==============
        // Floor - large plane with slight reflection (lowered for more shadow space)
        geometries.add(new Plane(new Point(0, -50, 0), new Vector(0, 1, 0))
                .setEmission(new Color(30, 30, 35))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setKR(0.1)
                        .setShininess(40)));

        // Back wall (moved further back)
        geometries.add(new Plane(new Point(0, 0, -200), new Vector(0, 0, 1))
                .setEmission(new Color(25, 25, 30))
                .setMaterial(new Material()
                        .setKD(0.9).setKS(0.1)
                        .setShininess(20)));

        // Left wall (moved further left)
        geometries.add(new Plane(new Point(-150, 0, 0), new Vector(1, 0, 0))
                .setEmission(new Color(30, 20, 20))
                .setMaterial(new Material()
                        .setKD(0.9).setKS(0.1)
                        .setShininess(20)));

        // ============ Main Objects - Widely Spaced ==============
        // Central large sphere - elevated for better shadow casting
        geometries.add(new Sphere(new Point(0, 20, -100), 35)
                .setEmission(new Color(100, 120, 140))
                .setMaterial(new Material()
                        .setKD(0.4).setKS(0.6).setKR(0.4)
                        .setShininess(180)));

        // Left sphere - transparent with good spacing
        geometries.add(new Sphere(new Point(-80, 0, -80), 25)
                .setEmission(new Color(60, 140, 220))
                .setMaterial(new Material()
                        .setKD(0.2).setKS(0.3).setKT(0.6)
                        .setShininess(120)));

        // Right sphere - matte with elevation
        geometries.add(new Sphere(new Point(80, 10, -90), 28)
                .setEmission(new Color(200, 80, 80))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2)
                        .setShininess(25)));

        // Far left sphere - glossy and elevated
        geometries.add(new Sphere(new Point(-120, 15, -130), 20)
                .setEmission(new Color(80, 180, 80))
                .setMaterial(new Material()
                        .setKD(0.5).setKS(0.5).setKR(0.3)
                        .setShininess(140)));

        // Far right sphere - metallic and elevated
        geometries.add(new Sphere(new Point(120, 25, -120), 18)
                .setEmission(new Color(220, 200, 120))
                .setMaterial(new Material()
                        .setKD(0.3).setKS(0.7).setKR(0.5)
                        .setShininess(250)));

        // ============ Smaller Objects for Detail ==============
        // Front left small sphere - elevated
        geometries.add(new Sphere(new Point(-50, 30, -40), 12)
                .setEmission(new Color(220, 120, 220))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3)
                        .setShininess(70)));

        // Front right small sphere - elevated
        geometries.add(new Sphere(new Point(50, 35, -50), 10)
                .setEmission(new Color(120, 220, 220))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setKT(0.2)
                        .setShininess(130)));

        // ============ Geometric Shapes for Shadow Casting ==============
        // Left triangle - elevated and angled for interesting shadows
        geometries.add(new Triangle(
                new Point(-140, -30, -90),
                new Point(-100, -30, -90),
                new Point(-120, 40, -90))
                .setEmission(new Color(180, 60, 120))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3)
                        .setShininess(50)));

        // Right triangle - elevated and angled
        geometries.add(new Triangle(
                new Point(100, -30, -70),
                new Point(140, -30, -70),
                new Point(120, 30, -70))
                .setEmission(new Color(120, 180, 60))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2)
                        .setShininess(35)));

        // Large hexagon on the floor - centered with good spacing from other objects
        double radius = 20;
        Point center = new Point(0, -49, -30);
        Point[] hexPoints = new Point[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            hexPoints[i] = new Point(
                    center.getX() + radius * Math.cos(angle),
                    center.getY(),
                    center.getZ() + radius * Math.sin(angle)
            );
        }
        geometries.add(new Polygon(hexPoints)
                .setEmission(new Color(200, 200, 80))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setKR(0.1)
                        .setShininess(60)));

        // Additional small hexagon for more shadow detail
        double smallRadius = 12;
        Point smallCenter = new Point(-30, -49, 10);
        Point[] smallHexPoints = new Point[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            smallHexPoints[i] = new Point(
                    smallCenter.getX() + smallRadius * Math.cos(angle),
                    smallCenter.getY(),
                    smallCenter.getZ() + smallRadius * Math.sin(angle)
            );
        }
        geometries.add(new Polygon(smallHexPoints)
                .setEmission(new Color(160, 80, 200))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3)
                        .setShininess(45)));

        // Add all geometries to the scene
        scene.geometries.add(geometries.toArray(new Intersectable[0]));

        // ============ Camera Setup ==============
        // Camera positioned to capture the wide scene with good shadow visibility
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(-100, 100, 250))
                .setDirection(new Point(0, 0, -100), new Vector(0, 1, 0))
                .setVpSize(300, 300)
                .setVpDistance(200)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // Render the image
        camera.renderImage()
                .writeToImage("miniproject1_soft_shadows_spaced");
    }
}