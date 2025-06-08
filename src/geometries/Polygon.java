package geometries;

import java.util.List;
import static primitives.Util.*;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * The {@code Polygon} class represents a convex polygon in 3D space.
 * It is defined by an ordered list of vertices lying in the same plane.
 * The constructor verifies planarity, convexity, and vertex ordering.
 */
public class Polygon extends Geometry {
   /** Ordered list of polygon vertices. */
   protected final List<Point> vertices;
   /** Underlying plane in which the polygon lies. */
   protected final Plane plane;
   /** Number of vertices in the polygon. */
   private final int size;

   /**
    * Constructs a convex polygon from given vertices.
    * Validates: minimum 3 vertices, coplanarity, convexity, and consistent winding.
    *
    * @param vertices list of vertices in order (clockwise or counter-clockwise)
    * @throws IllegalArgumentException if vertices < 3, or if coplanarity/convexity conditions fail
    */
   public Polygon(Point... vertices) {
      // Must have at least 3 points
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      this.vertices = List.of(vertices);
      size = vertices.length;

      // Define the polygon's plane from first three vertices
      plane = new Plane(vertices[0], vertices[1], vertices[2]);
      // No further checks needed for triangle
      if (size == 3) return;

      // Compute the plane normal once
      Vector n = plane.getNormal(vertices[0]);
      // Initialize edge vectors for winding and convexity test
      Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
      Vector edge2 = vertices[0].subtract(vertices[size - 1]);

      // Determine initial sign of cross-product dot normal
      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;

      // Iterate through remaining edges to validate planarity and convexity
      for (var i = 1; i < size; ++i) {
         // 1. Planarity: each vertex must lie in the same plane
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException(
                    "All vertices of a polygon must lay in the same plane");

         // Prepare next edge vectors
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);

         // 2. Convexity & ordering: cross-product dot normal must have consistent sign
         boolean currentSign = edge1.crossProduct(edge2).dotProduct(n) > 0;
         if (positive != currentSign)
            throw new IllegalArgumentException(
                    "All vertices must be ordered and the polygon must be convex");
      }
   }

   /**
    * Returns the plane's normal vector (same for any point on the polygon).
    * @param point ignored; normal is constant for the polygon's plane
    * @return normalized normal vector of the polygon's plane
    */
   @Override
   public Vector getNormal(Point point) {
      return plane.getNormal(point);
   }

   /**
    * Calculates intersection(s) of the given ray with this polygon.
    * Uses first plane intersection, then inside-outside test per edge.
    *
    * @param ray the ray to intersect
    * @return list with one Intersection if the ray hits inside polygon; otherwise null
    */
   @Override
   protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
      // 1. Intersect ray with the bounding plane
      List<GeoPoint> planeIntersections = plane.findGeoIntersections(ray);
      if (planeIntersections == null) return null;

      // 2. Extract the single intersection point
      Point p = planeIntersections.get(0).point;

      // 3. Prepare normal for inside-outside tests
      Vector n = plane.getNormal(p);

      // 4. Edge-by-edge inside-outside test
      for (int i = 0; i < size; ++i) {
         Point vi = vertices.get(i);
         Point vj = vertices.get((i + 1) % size);

         // Vector along edge and from edge start to intersection
         Vector edge = vj.subtract(vi);
         Vector vp = p.subtract(vi);

         Vector cross;
         try {
            // Cross product; may throw if vectors are parallel (intersection on edge)
            cross = edge.crossProduct(vp);
         } catch (IllegalArgumentException ignore) {
            // Intersection exactly on edge or vertex => consider inside
            continue;
         }

         // Sign of cross.dot(normal) indicates side; must be non-negative for inside
         if (alignZero(cross.dotProduct(n)) < 0) {
            return null; // outside
         }
      }

      // 5. All tests passed; return the intersection
      return List.of(new Intersectable.Intersection(this, p));
   }
}
