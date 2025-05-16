package geometries;

import java.util.List;
import static primitives.Util.*;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate system
 */
public class Polygon extends Geometry {
   protected final List<Point> vertices;
   protected final Plane plane;
   private final int size;

   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      this.vertices = List.of(vertices);
      size = vertices.length;

      plane = new Plane(vertices[0], vertices[1], vertices[2]);
      if (size == 3) return; // triangle no more tests

      Vector n = plane.getNormal(vertices[0]);
      Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
      Vector edge2 = vertices[0].subtract(vertices[size - 1]);

      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < size; ++i) {
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");

         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }
   }

   @Override
   public Vector getNormal(Point point) { return plane.getNormal(point); }

   @Override
   protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
      // Intersect with the plane first
      List<GeoPoint> planeIntersections = plane.findGeoIntersections(ray);
      if (planeIntersections == null) return null;

      GeoPoint planeIntersection = planeIntersections.getFirst();
      Point p = planeIntersection.point;

      Vector n = plane.getNormal();

      // Check if the intersection is inside the polygon
      for (int i = 0; i < size; i++) {
         Point vi = vertices.get(i);
         Point vj = vertices.get((i + 1) % size);

         Vector edge = vj.subtract(vi);
         Vector vp = p.subtract(vi);

         Vector cross = edge.crossProduct(vp);
         double sign = alignZero(cross.dotProduct(n));

         // If the point is outside the polygon
         if (sign < 0) return null;
      }

      // Return the intersection as an Intersectable.Intersection
      return List.of(new Intersectable.Intersection(this, p));
   }

}
