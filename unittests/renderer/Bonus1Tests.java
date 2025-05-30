package renderer;

import primitives.Vector;
import scene.Scene;
import primitives.*;
import geometries.*;
import lighting.*;
import java.util.*;

public class Bonus1Tests {
    public static void main(String[] args) {
        // 1. Scene & ambient/light
        Scene scene = new Scene("Bonus1");
        // low‐intensity ambient (20,20,20) × 0.1 ⇒ a soft global light
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20).scale(0.1)));
        // one point light for highlights & shadows
        scene.lights.add(
                new PointLight(new Color(255, 240, 240), new Point(50, 50, 50))
                        .setKl(0.0004).setKq(0.0000006)
        );

        // 2. Camera (raised & tilted toward origin)
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 100, 1000))
                .setDirection(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setVpSize(600, 600)
                .setVpDistance(1000)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // 3. Build 10+ geometries
        List<Intersectable> list = new ArrayList<>();

        // ─── floor plane ───────────────────────────────────────────────
        list.add(new Plane(new Point(0, -50, 0), new Vector(0, 1, 0))
                .setEmission(new Color(100, 100, 120))
                .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(30)));

        // ─── three diffuse spheres ────────────────────────────────────
        list.add(new Sphere(new Point(-60, 0, -100), 30)
                .setEmission(new Color(0, 0, 200))
                .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(20)));
        list.add(new Sphere(new Point(0, 0, -120), 20)
                .setEmission(new Color(200, 0, 0))
                .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(20)));
        list.add(new Sphere(new Point(60, 0, -100), 30)
                .setEmission(new Color(0, 200, 0))
                .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(20)));

        // ─── two reflective spheres ────────────────────────────────────
        list.add(new Sphere(new Point(-30, 15, -120), 15)
                .setEmission(new Color(50, 50, 200))      // bluish tint
                .setMaterial(new Material()
                        .setKD(0.2).setKS(0.6).setKR(0.5).setShininess(200)));
        list.add(new Sphere(new Point(30, 15, -120), 15)
                .setEmission(new Color(50, 200, 50))      // greenish tint
                .setMaterial(new Material()
                        .setKD(0.2).setKS(0.6).setKR(0.5).setShininess(200)));

        // ─── two transparent spheres ──────────────────────────────────
        list.add(new Sphere(new Point(-40, -10, -60), 10)
                .setEmission(new Color(150, 200, 255))    // light bluish base
                .setMaterial(new Material()
                        .setKD(0.1).setKS(0.1).setKT(0.5).setShininess(100)));
        list.add(new Sphere(new Point(40, -10, -60), 10)
                .setEmission(new Color(255, 200, 200))    // light reddish base
                .setMaterial(new Material()
                        .setKD(0.1).setKS(0.1).setKT(0.5).setShininess(100)));

        // ─── a small triangle for sharp shadows ───────────────────────
        list.add(new Polygon(
                new Point(-10, -50, -50),
                new Point(10, -50, -50),
                new Point(0, -20, -50))
                .setEmission(new Color(255, 200, 200))
                .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

        // ─── a tilted quad ─────────────────────────────────────────────
        list.add(new Polygon(
                new Point(80, -50, -80),
                new Point(100, -50, -120),
                new Point(100, 20, -120),
                new Point(80, 20, -80))
                .setEmission(new Color(200, 200, 50))
                .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

        // (You can add more tiny shapes here to exceed 10)

        // 4. Attach to scene
        scene.geometries.add(list.toArray(new Intersectable[0]));

        // 5. Render & write
        camera.renderImage()
                .writeToImage("bonus1");
    }
}
