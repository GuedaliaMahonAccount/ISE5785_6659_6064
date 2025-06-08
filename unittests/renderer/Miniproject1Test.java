package renderer;

import primitives.*;
import scene.Scene;
import geometries.*;
import lighting.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test application for Mini-project 1 showcasing soft shadows via supersampling.
 * Builds a scene with diverse geometries and multiple light sources,
 * then renders it to demonstrate smooth shadow gradients.
 */
public class Miniproject1Test {

    /**
     * Main entry point: sets up scene, lights, geometries, camera, and renders output.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // -------------------- Scene Setup --------------------
        Scene scene = new Scene("Mini-project 1: Soft Shadows");
        scene.setBackground(new Color(5, 5, 10));  // Dark bluish background

        // Soft ambient light for subtle base illumination
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        // -------------------- Light Sources --------------------
        // Main point light: high intensity with gentle attenuation
        scene.lights.add(
                new PointLight(new Color(600, 500, 400), new Point(-80, 120, 200))
                        .setKl(0.0003)  // linear attenuation
                        .setKq(0.0002)  // quadratic attenuation
        );
        // Directional fill light: cool tone, uniform direction
        scene.lights.add(
                new DirectionalLight(new Color(80, 120, 160), new Vector(1, -0.8, -1))
        );
        // Spotlight accent: warm tone, focused beam
        scene.lights.add(
                new SpotLight(new Color(400, 250, 150), new Point(100, 150, 120), new Vector(-0.6, -1, -0.8))
                        .setKl(0.0001)
                        .setKq(0.00003)
        );
        // Secondary point light: fill for darker areas
        scene.lights.add(
                new PointLight(new Color(180, 180, 120), new Point(50, 100, -20))
                        .setKl(0.0002)
                        .setKq(0.0001)
        );

        // -------------------- Geometry Collection --------------------
        List<Intersectable> geometries = new ArrayList<>();

        // Floor plane: large, reflective surface lowered for depth
        geometries.add(
                new Plane(new Point(0, -50, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(30, 30, 35))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2).setKR(0.1)
                                .setShininess(40))
        );
        // Back wall: distant backdrop
        geometries.add(
                new Plane(new Point(0, 0, -200), new Vector(0, 0, 1))
                        .setEmission(new Color(25, 25, 30))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(20))
        );
        // Left wall: provides lateral boundary
        geometries.add(
                new Plane(new Point(-150, 0, 0), new Vector(1, 0, 0))
                        .setEmission(new Color(30, 20, 20))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(20))
        );

        // -------------------- Main Spheres --------------------
        // Central large sphere: elevated for shadow casting
        geometries.add(
                new Sphere(new Point(0, 20, -100), 35)
                        .setEmission(new Color(100, 120, 140))
                        .setMaterial(new Material()
                                .setKD(0.4).setKS(0.6).setKR(0.4)
                                .setShininess(180))
        );
        // Transparent left sphere
        geometries.add(
                new Sphere(new Point(-80, 0, -80), 25)
                        .setEmission(new Color(60, 140, 220))
                        .setMaterial(new Material()
                                .setKD(0.2).setKS(0.3).setKT(0.6)
                                .setShininess(120))
        );
        // Matte right sphere
        geometries.add(
                new Sphere(new Point(80, 10, -90), 28)
                        .setEmission(new Color(200, 80, 80))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2)
                                .setShininess(25))
        );
        // Glossy far-left sphere
        geometries.add(
                new Sphere(new Point(-120, 15, -130), 20)
                        .setEmission(new Color(80, 180, 80))
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.5).setKR(0.3)
                                .setShininess(140))
        );
        // Metallic far-right sphere
        geometries.add(
                new Sphere(new Point(120, 25, -120), 18)
                        .setEmission(new Color(220, 200, 120))
                        .setMaterial(new Material()
                                .setKD(0.3).setKS(0.7).setKR(0.5)
                                .setShininess(250))
        );

        // -------------------- Detail Spheres --------------------
        geometries.add(
                new Sphere(new Point(-50, 30, -40), 12)
                        .setEmission(new Color(220, 120, 220))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.3)
                                .setShininess(70))
        );
        geometries.add(
                new Sphere(new Point(50, 35, -50), 10)
                        .setEmission(new Color(120, 220, 220))
                        .setMaterial(new Material()
                                .setKD(0.6).setKS(0.4).setKT(0.2)
                                .setShininess(130))
        );

        // -------------------- Triangles for Shadows --------------------
        geometries.add(
                new Triangle(
                        new Point(-140, -30, -90),
                        new Point(-100, -30, -90),
                        new Point(-120, 40, -90)
                )
                        .setEmission(new Color(180, 60, 120))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.3)
                                .setShininess(50))
        );
        geometries.add(
                new Triangle(
                        new Point(100, -30, -70),
                        new Point(140, -30, -70),
                        new Point(120, 30, -70)
                )
                        .setEmission(new Color(120, 180, 60))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2)
                                .setShininess(35))
        );

        // -------------------- Hexagon Floor Patterns --------------------
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
        geometries.add(
                new Polygon(hexPoints)
                        .setEmission(new Color(200, 200, 80))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2).setKR(0.1)
                                .setShininess(60))
        );
        double smallRadius = 12;
        Point smallCenter = new Point(-30, -49, 10);
        Point[] smallHex = new Point[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            smallHex[i] = new Point(
                    smallCenter.getX() + smallRadius * Math.cos(angle),
                    smallCenter.getY(),
                    smallCenter.getZ() + smallRadius * Math.sin(angle)
            );
        }
        geometries.add(
                new Polygon(smallHex)
                        .setEmission(new Color(160, 80, 200))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.3)
                                .setShininess(45))
        );

        // Add all geometries into the scene for rendering
        scene.geometries.add(geometries.toArray(new Intersectable[0]));

        // -------------------- Camera Configuration --------------------
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(-100, 100, 250))
                .setDirection(new Point(0, 0, -100), new Vector(0, 1, 0))
                .setVpSize(300, 300)
                .setVpDistance(200)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // -------------------- Rendering --------------------
        camera.renderImage()
                .writeToImage("miniproject1_soft_shadows_spaced");
    }
}