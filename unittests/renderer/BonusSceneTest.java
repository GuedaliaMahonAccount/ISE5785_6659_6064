package renderer;

import org.junit.jupiter.api.Test;
import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Test class for the bonus scene demonstrating multiple geometries and effects
 */
class BonusSceneTest {
    /** Scene for the test */
    private final Scene scene = new Scene("Bonus Scene");
    /** Camera builder for the test */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    /**
     * Produce a complex scene with multiple geometries and effects
     */
    @Test
    void complexScene() {
        // ============ Geometries ==============

        // Background - large reflective plane as floor
        scene.geometries.add(
                new Plane(new Point(0, -200, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(30, 30, 30))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100).setKR(0.3))
        );

        // Central glass sphere
        scene.geometries.add(
                new Sphere(new Point(0, 0, -100), 40)
                        .setEmission(new Color(50, 100, 150))
                        .setMaterial(new Material()
                                .setKD(0.2).setKS(0.2).setShininess(30)
                                .setKT(0.8).setKR(0.1))
        );

        // Reflective sphere
        scene.geometries.add(
                new Sphere(new Point(-100, -50, -150), 30)
                        .setEmission(new Color(150, 50, 50))
                        .setMaterial(new Material()
                                .setKD(0.3).setKS(0.7).setShininess(100)
                                .setKR(0.8))
        );

        // Transparent pyramid made of triangles
        scene.geometries.add(
                new Triangle(new Point(100, -200, -200), new Point(150, -200, -50), new Point(50, -200, -50))
                        .setEmission(new Color(100, 200, 100))
                        .setMaterial(new Material().setKD(0.3).setKS(0.5).setShininess(80).setKT(0.6)),
                new Triangle(new Point(100, -100, -100), new Point(150, -200, -50), new Point(50, -200, -50))
                        .setEmission(new Color(100, 200, 100))
                        .setMaterial(new Material().setKD(0.3).setKS(0.5).setShininess(80).setKT(0.6)),
                new Triangle(new Point(100, -100, -100), new Point(150, -200, -50), new Point(100, -200, -200))
                        .setEmission(new Color(100, 200, 100))
                        .setMaterial(new Material().setKD(0.3).setKS(0.5).setShininess(80).setKT(0.6)),
                new Triangle(new Point(100, -100, -100), new Point(50, -200, -50), new Point(100, -200, -200))
                        .setEmission(new Color(100, 200, 100))
                        .setMaterial(new Material().setKD(0.3).setKS(0.5).setShininess(80).setKT(0.6))
        );

        // Tube (cylinder without caps)
        scene.geometries.add(
                new Tube(20, new Ray(new Point(-50, -200, 50), new Vector(0, 1, 0)))
                        .setEmission(new Color(200, 100, 50))
                        .setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(50))
        );

        // Polygon (hexagon)
        scene.geometries.add(
                new Polygon(
                        new Point(150, -200, 150),
                        new Point(120, -200, 180),
                        new Point(80, -200, 180),
                        new Point(50, -200, 150),
                        new Point(80, -200, 120),
                        new Point(120, -200, 120))
                        .setEmission(new Color(150, 150, 200))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(70).setKR(0.2))
        );

        // Additional spheres with different materials
        scene.geometries.add(
                new Sphere(new Point(150, -150, 100), 25)
                        .setEmission(new Color(200, 200, 50))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(90).setKT(0.3).setKR(0.2)),
                new Sphere(new Point(-150, -150, -50), 35)
                        .setEmission(new Color(100, 50, 200))
                        .setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(80))
        );

        // Reflective triangle
        scene.geometries.add(
                new Triangle(new Point(-200, -200, 200), new Point(-100, -100, 100), new Point(-200, -200, 50))
                        .setEmission(new Color(50, 200, 200))
                        .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(60).setKR(0.5))
        );

        // ============ Lights ==============
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(0, 200, 0), new Vector(0, -1, -0.5))
                        .setKl(4E-5).setKq(2E-7));

        scene.lights.add(
                new PointLight(new Color(400, 300, 100), new Point(-100, 100, 100))
                        .setKl(0.0001).setKq(0.00005));

        scene.lights.add(
                new DirectionalLight(new Color(100, 150, 200), new Vector(1, -1, -1)));

        // ============ Camera ==============
        Camera camera = cameraBuilder
                .setLocation(new Point(0, 300, 500))
                .setDirection(new Vector(0, -1, -0.5))
                .setVpDistance(400)
                .setVpSize(200, 200)
                .setResolution(1000, 1000)
                .build();

        camera.renderImage();
        camera.writeToImage("complexScene");
    }
}