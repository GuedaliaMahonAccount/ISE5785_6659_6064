package renderer;

import primitives.Vector;
import scene.Scene;
import primitives.*;
import geometries.*;
import lighting.*;
import java.util.*;

public class ImprovedBonus1Tests {
    public static void main(String[] args) {
        // 1. Scene avec éclairage plus doux
        Scene scene = new Scene("ImprovedBonus1");
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20).scale(0.1)));

        // Un seul éclairage principal plus doux
        scene.lights.add(
                new PointLight(new Color(200, 200, 180), new Point(-30, 60, 80))
                        .setKl(0.0005).setKq(0.0000008)
        );

        // Un éclairage d'appoint très faible
        scene.lights.add(
                new DirectionalLight(new Color(30, 30, 40), new Vector(1, -0.5, -1))
        );

        // 2. Caméra repositionnée
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(-40, 50, 150))
                .setDirection(new Point(0, 0, -30), new Vector(0, 1, 0))
                .setVpSize(600, 600)
                .setVpDistance(800)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // 3. Scène avec des matériaux plus réalistes
        List<Intersectable> list = new ArrayList<>();

        // ─── Sol mat avec légère réflexion ────────────────────────────
        list.add(new Plane(new Point(0, -30, 0), new Vector(0, 1, 0))
                .setEmission(new Color(40, 40, 50))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setKR(new Double3(0.1))
                        .setShininess(50)));

        // ─── Mur arrière sombre ───────────────────────────────────────
        list.add(new Plane(new Point(0, 0, -150), new Vector(0, 0, 1))
                .setEmission(new Color(25, 25, 35))
                .setMaterial(new Material()
                        .setKD(0.9).setKS(0.1).setShininess(20)));

        // ─── Sphère centrale métallique (pas trop réfléchissante) ─────
        list.add(new Sphere(new Point(0, 0, -50), 25)
                .setEmission(new Color(60, 80, 120))
                .setMaterial(new Material()
                        .setKD(0.4).setKS(0.6).setKR(new Double3(0.4))
                        .setShininess(200)));

        // ─── Sphère transparente bleue ────────────────────────────────
        list.add(new Sphere(new Point(-40, 5, -30), 18)
                .setEmission(new Color(50, 100, 200))
                .setMaterial(new Material()
                        .setKD(0.2).setKS(0.3).setKT(new Double3(0.6))
                        .setShininess(100)));

        // ─── Sphère rouge mate ────────────────────────────────────────
        list.add(new Sphere(new Point(40, -5, -40), 20)
                .setEmission(new Color(150, 50, 50))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setShininess(30)));

        // ─── Sphère verte semi-réfléchissante ────────────────────────
        list.add(new Sphere(new Point(-60, -10, -80), 15)
                .setEmission(new Color(50, 120, 50))
                .setMaterial(new Material()
                        .setKD(0.5).setKS(0.5).setKR(new Double3(0.3))
                        .setShininess(100)));

        // ─── Sphère dorée ─────────────────────────────────────────────
        list.add(new Sphere(new Point(50, 10, -70), 12)
                .setEmission(new Color(180, 150, 80))
                .setMaterial(new Material()
                        .setKD(0.3).setKS(0.7).setKR(new Double3(0.2))
                        .setShininess(300)));

        // ─── Petites sphères colorées ─────────────────────────────────
        list.add(new Sphere(new Point(-20, 20, -20), 8)
                .setEmission(new Color(200, 100, 200))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(80)));

        list.add(new Sphere(new Point(20, 15, -25), 6)
                .setEmission(new Color(100, 200, 200))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setKT(new Double3(0.3))
                        .setShininess(150)));

        // ─── Triangles colorés ────────────────────────────────────────
        list.add(new Polygon(
                new Point(-80, -30, -60),
                new Point(-60, -30, -60),
                new Point(-70, 0, -60))
                .setEmission(new Color(150, 0, 100))
                .setMaterial(new Material()
                        .setKD(0.7).setKS(0.3).setShininess(60)));

        list.add(new Polygon(
                new Point(60, -30, -50),
                new Point(80, -30, -50),
                new Point(70, -5, -50))
                .setEmission(new Color(100, 150, 0))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setShininess(40)));

        // ─── Polygone rectangulaire vertical ──────────────────────────
        list.add(new Polygon(
                new Point(-90, -30, -100),
                new Point(-75, -30, -100),
                new Point(-75, 20, -100),
                new Point(-90, 20, -100))
                .setEmission(new Color(80, 120, 160))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setKR(new Double3(0.2))
                        .setShininess(120)));

        // ─── Autre structure géométrique ──────────────────────────────
        list.add(new Polygon(
                new Point(75, -30, -90),
                new Point(90, -30, -90),
                new Point(90, 10, -90),
                new Point(75, 10, -90))
                .setEmission(new Color(160, 120, 80))
                .setMaterial(new Material()
                        .setKD(0.5).setKS(0.5).setKT(new Double3(0.4))
                        .setShininess(100)));

        // ─── Petit hexagone au sol ────────────────────────────────────
        double radius = 12;
        Point center = new Point(0, -29, -10);
        Point[] hexPoints = new Point[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            hexPoints[i] = new Point(
                    center.getX() + radius * Math.cos(angle),
                    center.getY(),
                    center.getZ() + radius * Math.sin(angle)
            );
        }
        list.add(new Polygon(hexPoints)
                .setEmission(new Color(200, 200, 100))
                .setMaterial(new Material()
                        .setKD(0.8).setKS(0.2).setShininess(50)));

        // ─── Quelques détails supplémentaires ─────────────────────────
        list.add(new Sphere(new Point(-25, -20, -15), 4)
                .setEmission(new Color(255, 200, 150))
                .setMaterial(new Material()
                        .setKD(0.6).setKS(0.4).setShininess(100)));

        list.add(new Sphere(new Point(25, -15, -20), 5)
                .setEmission(new Color(150, 255, 200))
                .setMaterial(new Material()
                        .setKD(0.5).setKS(0.5).setKT(new Double3(0.5))
                        .setShininess(200)));

        // 4. Ajouter toutes les géométries à la scène
        scene.geometries.add(list.toArray(new Intersectable[0]));

        // 5. Rendu avec qualité modérée
        camera.renderImage()
                .writeToImage("improved_bonus1");
    }
}