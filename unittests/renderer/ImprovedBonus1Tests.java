package renderer;

import primitives.Vector;
import scene.Scene;
import primitives.*;
import geometries.*;
import lighting.*;
import java.util.*;

/**
 * ImprovedBonus1Tests sets up a complex 3D scene with enhanced lighting,
 * realistic materials, multiple geometries, and renders it to an image file.
 */
public class ImprovedBonus1Tests {

    /**
     * Entry point: configures scene lighting, camera, materials, geometries,
     * then renders the final image to 'improved_bonus1.png'.
     */
    public static void main(String[] args) {
        // 1. Create a new scene with soft ambient illumination
        Scene scene = new Scene("ImprovedBonus1");
        scene.setAmbientLight(
                new AmbientLight(
                        new Color(15, 15, 20)
                                .scale(0.1)     // Low-intensity ambient light
                )
        );

        // Add a primary point light with gentle falloff
        scene.lights.add(
                new PointLight(
                        new Color(200, 200, 180),  // Warm light color
                        new Point(-30, 60, 80)      // Position in the scene
                )
                        .setKl(0.0005)                  // Linear attenuation
                        .setKq(0.0000008)               // Quadratic attenuation
        );

        // Add a faint directional fill light for subtle shading
        scene.lights.add(
                new DirectionalLight(
                        new Color(30, 30, 40),     // Dim bluish fill
                        new Vector(1, -0.5, -1)     // Light direction
                )
        );

        // 2. Configure the camera position, orientation, and view plane
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(-40, 50, 150))              // Camera at an elevated angle
                .setDirection(new Point(0, 0, -30), new Vector(0, 1, 0)) // Look at scene center, with Y-up
                .setVpSize(600, 600)                               // View plane dimensions
                .setVpDistance(800)                                // Distance from camera to view plane
                .setResolution(600, 600)                           // Output resolution in pixels
                .setRayTracer(scene, RayTracerType.SIMPLE)         // Use simple ray tracer
                .build();

        // 3. Prepare a list of geometries with varied materials
        List<Intersectable> list = new ArrayList<>();

        // Matte floor with slight reflection
        list.add(
                new Plane(new Point(0, -30, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(40, 40, 50))             // Dark gray emission
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2)
                                .setKR(new Double3(0.1))                     // Small reflection coefficient
                                .setShininess(50)
                        )
        );

        // Dark rear wall for contrast
        list.add(
                new Plane(new Point(0, 0, -150), new Vector(0, 0, 1))
                        .setEmission(new Color(25, 25, 35))             // Very dark
                        .setMaterial(new Material()
                                .setKD(0.9).setKS(0.1)
                                .setShininess(20)
                        )
        );

        // Central metallic sphere with moderate reflectivity
        list.add(
                new Sphere(new Point(0, 0, -50), 25)
                        .setEmission(new Color(60, 80, 120))            // Cool blue emission
                        .setMaterial(new Material()
                                .setKD(0.4).setKS(0.6)
                                .setKR(new Double3(0.4))                     // Reflective metal
                                .setShininess(200)
                        )
        );

        // Transparent blue sphere
        list.add(
                new Sphere(new Point(-40, 5, -30), 18)
                        .setEmission(new Color(50, 100, 200))           // Bright blue
                        .setMaterial(new Material()
                                .setKD(0.2).setKS(0.3)
                                .setKT(new Double3(0.6))                     // Transparency
                                .setShininess(100)
                        )
        );

        // Matte red sphere
        list.add(
                new Sphere(new Point(40, -5, -40), 20)
                        .setEmission(new Color(150, 50, 50))            // Deep red
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2)
                                .setShininess(30)
                        )
        );

        // Semi-reflective green sphere
        list.add(
                new Sphere(new Point(-60, -10, -80), 15)
                        .setEmission(new Color(50, 120, 50))            // Soft green
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.5)
                                .setKR(new Double3(0.3))                     // Mild reflection
                                .setShininess(100)
                        )
        );

        // Golden sphere with high shininess
        list.add(
                new Sphere(new Point(50, 10, -70), 12)
                        .setEmission(new Color(180, 150, 80))           // Gold tone
                        .setMaterial(new Material()
                                .setKD(0.3).setKS(0.7)
                                .setKR(new Double3(0.2))                     // Light gold reflection
                                .setShininess(300)
                        )
        );

        // Small colorful spheres for detail
        list.add(
                new Sphere(new Point(-20, 20, -20), 8)
                        .setEmission(new Color(200, 100, 200))          // Purple
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.3)
                                .setShininess(80)
                        )
        );
        list.add(
                new Sphere(new Point(20, 15, -25), 6)
                        .setEmission(new Color(100, 200, 200))          // Cyan
                        .setMaterial(new Material()
                                .setKD(0.6).setKS(0.4)
                                .setKT(new Double3(0.3))                     // Partial transparency
                                .setShininess(150)
                        )
        );

        // Colored triangles for variation
        list.add(
                new Polygon(
                        new Point(-80, -30, -60),
                        new Point(-60, -30, -60),
                        new Point(-70, 0, -60)
                )
                        .setEmission(new Color(150, 0, 100))                 // Magenta
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.3)
                                .setShininess(60)
                        )
        );
        list.add(
                new Polygon(
                        new Point(60, -30, -50),
                        new Point(80, -30, -50),
                        new Point(70, -5, -50)
                )
                        .setEmission(new Color(100, 150, 0))                 // Olive
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2)
                                .setShininess(40)
                        )
        );

        // Vertical rectangular polygon as a wall segment
        list.add(
                new Polygon(
                        new Point(-90, -30, -100),
                        new Point(-75, -30, -100),
                        new Point(-75, 20, -100),
                        new Point(-90, 20, -100)
                )
                        .setEmission(new Color(80, 120, 160))                 // Slate blue
                        .setMaterial(new Material()
                                .setKD(0.6).setKS(0.4)
                                .setKR(new Double3(0.2))
                                .setShininess(120)
                        )
        );

        // Another vertical panel with different color/material
        list.add(
                new Polygon(
                        new Point(75, -30, -90),
                        new Point(90, -30, -90),
                        new Point(90, 10, -90),
                        new Point(75, 10, -90)
                )
                        .setEmission(new Color(160, 120, 80))                 // Tan
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.5).setKT(new Double3(0.4))   // Slight transparency
                                .setShininess(100)
                        )
        );

        // Create a hexagon on the floor for decorative detail
        double radius = 12;
        Point center = new Point(0, -29, -10);                  // Slightly above floor
        Point[] hexPoints = new Point[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            hexPoints[i] = new Point(
                    center.getX() + radius * Math.cos(angle),
                    center.getY(),
                    center.getZ() + radius * Math.sin(angle)
            );
        }
        list.add(
                new Polygon(hexPoints)
                        .setEmission(new Color(200, 200, 100))           // Yellowish
                        .setMaterial(new Material()
                                .setKD(0.8).setKS(0.2)
                                .setShininess(50)
                        )
        );

        // Additional small spheres for finishes
        list.add(
                new Sphere(new Point(-25, -20, -15), 4)
                        .setEmission(new Color(255, 200, 150))          // Peach
                        .setMaterial(new Material()
                                .setKD(0.6).setKS(0.4)
                                .setShininess(100)
                        )
        );
        list.add(
                new Sphere(new Point(25, -15, -20), 5)
                        .setEmission(new Color(150, 255, 200))          // Mint green
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.5)
                                .setKT(new Double3(0.5))                     // Semi-transparent
                                .setShininess(200)
                        )
        );

        // 4. Add all geometries to the scene
        scene.geometries.add(list.toArray(new Intersectable[0]));

        // 5. Render the scene and write to an image file
        camera.renderImage()
                .writeToImage("improved_bonus1");
    }
}
