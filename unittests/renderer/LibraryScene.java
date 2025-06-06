package renderer;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import geometries.Intersectable;
import geometries.Plane;
import geometries.Polygon;
import geometries.Sphere;
import geometries.Triangle;
import geometries.Geometries;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import primitives.Color;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import scene.Scene;

public class LibraryScene {

    private Scene scene;

    @Test
    void libraryScene() {
        // ============ Scene Initialization ==============
        scene = new Scene("LibraryScene");
        scene.setBackground(new Color(15, 12, 10));

        // ============ Geometry Collection ==============
        List<Intersectable> geometriesList = new ArrayList<>();

        // Floor - proper wooden texture
        geometriesList.add(
                new Plane(new Point(0, 0, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(60, 45, 30))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1).setKR(0.05)
                                .setShininess(20))
        );

        // Back wall
        geometriesList.add(
                new Plane(new Point(0, 0, -80), new Vector(0, 0, 1))
                        .setEmission(new Color(70, 60, 50))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(10))
        );

        // Left wall
        geometriesList.add(
                new Plane(new Point(-50, 0, 0), new Vector(1, 0, 0))
                        .setEmission(new Color(65, 55, 45))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(10))
        );

        // Right wall
        geometriesList.add(
                new Plane(new Point(50, 0, 0), new Vector(-1, 0, 0))
                        .setEmission(new Color(65, 55, 45))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(10))
        );

        // Ceiling
        geometriesList.add(
                new Plane(new Point(0, 50, 0), new Vector(0, -1, 0))
                        .setEmission(new Color(80, 70, 60))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(5))
        );

        // ============ REALISTIC CHAIR DESIGN ==============
        // Chair seat - rectangular, not spherical
        geometriesList.add(
                new Polygon(
                        new Point(-25, 12, -15),
                        new Point(-10, 12, -15),
                        new Point(-10, 12, -30),
                        new Point(-25, 12, -30)
                )
                        .setEmission(new Color(80, 50, 30))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.2).setKR(0.05)
                                .setShininess(30))
        );

        // Chair backrest - vertical
        geometriesList.add(
                new Polygon(
                        new Point(-25, 12, -30),
                        new Point(-10, 12, -30),
                        new Point(-10, 30, -30),
                        new Point(-25, 30, -30)
                )
                        .setEmission(new Color(75, 45, 25))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.2).setKR(0.05)
                                .setShininess(30))
        );

        // Chair arms - boxes, not spheres
        geometriesList.add(
                new Polygon(
                        new Point(-25, 12, -15),
                        new Point(-25, 12, -30),
                        new Point(-25, 20, -28),
                        new Point(-25, 20, -17)
                )
                        .setEmission(new Color(70, 40, 20))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.2).setKR(0.05)
                                .setShininess(30))
        );

        geometriesList.add(
                new Polygon(
                        new Point(-10, 12, -15),
                        new Point(-10, 20, -17),
                        new Point(-10, 20, -28),
                        new Point(-10, 12, -30)
                )
                        .setEmission(new Color(70, 40, 20))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.2).setKR(0.05)
                                .setShininess(30))
        );

        // Chair legs - cylindrical, not spherical
        for (int i = 0; i < 4; i++) {
            double[] legPositions = {-23, -12, -23, -12};
            double[] legZPositions = {-17, -17, -28, -28};

            geometriesList.add(
                    new Sphere(new Point(legPositions[i], 6, legZPositions[i]), 1)
                            .setEmission(new Color(40, 25, 15))
                            .setMaterial(new Material()
                                    .setKD(0.8).setKS(0.1)
                                    .setShininess(15))
            );
        }

        // ============ BOOKSHELF ==============
        // Bookshelf backing
        geometriesList.add(
                new Polygon(
                        new Point(25, 0, -79),
                        new Point(49, 0, -79),
                        new Point(49, 40, -79),
                        new Point(25, 40, -79)
                )
                        .setEmission(new Color(50, 35, 25))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1)
                                .setShininess(20))
        );

        // Shelf boards
        for (int shelf = 1; shelf <= 5; shelf++) {
            double shelfY = shelf * 7;
            geometriesList.add(
                    new Polygon(
                            new Point(26, shelfY, -78),
                            new Point(48, shelfY, -78),
                            new Point(48, shelfY + 0.8, -78),
                            new Point(26, shelfY + 0.8, -78)
                    )
                            .setEmission(new Color(45, 30, 20))
                            .setMaterial(new Material()
                                    .setKD(0.8).setKS(0.1)
                                    .setShininess(20))
            );
        }

        // Books on shelves with realistic colors
        createBooks(geometriesList, 27, 7.5, -77, 18, 5);
        createBooks(geometriesList, 27, 14.5, -77, 16, 5);
        createBooks(geometriesList, 27, 21.5, -77, 17, 5);
        createBooks(geometriesList, 27, 28.5, -77, 15, 5);
        createBooks(geometriesList, 27, 35.5, -77, 19, 5);

        // ============ SIDE TABLE ==============
        // Table top - flat, not spherical
        geometriesList.add(
                new Polygon(
                        new Point(-8, 18, -45),
                        new Point(8, 18, -45),
                        new Point(8, 18, -35),
                        new Point(-8, 18, -35)
                )
                        .setEmission(new Color(60, 40, 25))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.2).setKR(0.1)
                                .setShininess(40))
        );

        // Table legs - smaller spheres
        geometriesList.add(
                new Sphere(new Point(-6, 9, -42), 0.8)
                        .setEmission(new Color(45, 30, 20))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1)
                                .setShininess(20))
        );
        geometriesList.add(
                new Sphere(new Point(6, 9, -42), 0.8)
                        .setEmission(new Color(45, 30, 20))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1)
                                .setShininess(20))
        );
        geometriesList.add(
                new Sphere(new Point(-6, 9, -38), 0.8)
                        .setEmission(new Color(45, 30, 20))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1)
                                .setShininess(20))
        );
        geometriesList.add(
                new Sphere(new Point(6, 9, -38), 0.8)
                        .setEmission(new Color(45, 30, 20))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1)
                                .setShininess(20))
        );

        // Coffee cup on table
        geometriesList.add(
                new Sphere(new Point(-3, 19, -40), 1.2)
                        .setEmission(new Color(120, 100, 80))
                        .setMaterial(new Material()
                                .setKD(0.6).setKS(0.3)
                                .setShininess(50))
        );

        // Book on table
        geometriesList.add(
                new Polygon(
                        new Point(2, 18.2, -42),
                        new Point(6, 18.2, -42),
                        new Point(6, 19, -38),
                        new Point(2, 19, -38)
                )
                        .setEmission(new Color(100, 60, 40))
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.1)
                                .setShininess(25))
        );

        // ============ RUG ==============
        geometriesList.add(
                new Polygon(
                        new Point(-35, 0.1, -50),
                        new Point(15, 0.1, -50),
                        new Point(15, 0.1, -10),
                        new Point(-35, 0.1, -10)
                )
                        .setEmission(new Color(90, 50, 30))
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.05)
                                .setShininess(5))
        );

        // ============ Add to Scene ==============
        scene.setGeometries(new Geometries(geometriesList.toArray(new Intersectable[0])));

        // ============ FIXED LIGHTING ==============
        // Softer ambient light
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 15)));

        // Main directional light - much softer
        scene.addLight(
                new DirectionalLight(new Color(80, 70, 60), new Vector(0.3, -0.7, -0.2))
        );

        // Warm point light - reduced intensity
        scene.addLight(
                new PointLight(new Color(120, 100, 70), new Point(-20, 35, 10))
                        .setKl(0.001).setKq(0.0005)
        );

        // Fill light - very soft
        scene.addLight(
                new DirectionalLight(new Color(30, 25, 20), new Vector(-0.2, -0.5, 0.3))
        );

        // ============ Camera Setup ==============
        Point cameraPosition = new Point(-15, 25, 20);
        Point targetPoint = new Point(-5, 15, -30);

        Camera camera = Camera.getBuilder()
                .setLocation(cameraPosition)
                .setDirection(targetPoint, new Vector(0, 1, 0))
                .setVpDistance(120)
                .setVpSize(300, 200)
                .setResolution(800, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // ============ Render & Output ==============
        camera.renderImage();
        camera.writeToImage("libraryScene");
    }

    private void createBooks(List<Intersectable> geometries,
                             double startX, double y, double z,
                             int numBooks, double shelfHeight) {
        double bookWidth = 1.1;
        Color[] bookColors = {
                new Color(100, 60, 40),   // Brown
                new Color(80, 100, 60),   // Green
                new Color(60, 80, 120),   // Blue
                new Color(120, 80, 60),   // Orange
                new Color(100, 60, 100),  // Purple
                new Color(120, 120, 80),  // Yellow
                new Color(70, 100, 100),  // Teal
                new Color(120, 80, 80)    // Red
        };

        for (int i = 0; i < numBooks; i++) {
            double x = startX + i * bookWidth;
            Color bookColor = bookColors[i % bookColors.length];

            geometries.add(
                    new Polygon(
                            new Point(x, y, z),
                            new Point(x + bookWidth * 0.9, y, z),
                            new Point(x + bookWidth * 0.9, y + shelfHeight, z),
                            new Point(x, y + shelfHeight, z)
                    )
                            .setEmission(bookColor)
                            .setMaterial(new Material()
                                    .setKD(0.8).setKS(0.1)
                                    .setShininess(20))
            );
        }
    }
}