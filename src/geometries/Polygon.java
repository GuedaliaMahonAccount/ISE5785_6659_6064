package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * The {@code Polygon} class represents a convex, planar polygon in 3D space.
 * It participates in the NVI pattern by providing an AABB culling step,
 * then delegating the detailed ray–polygon test to the helper.
 */
public class Polygon extends Geometry {

   /** The ordered list of vertices defining the polygon. */
   public final List<Point> vertices;

   /** The underlying plane in which all vertices lie. */
   public final Plane plane;

   /** Number of vertices (>= 3). */
   private final int size;

   /**
    * Constructs a convex polygon from the given vertices.
    * Validates: at least 3 vertices, coplanarity, convexity, consistent winding.
    *
    * @param vertices the vertices in order (CW or CCW)
    * @throws IllegalArgumentException on invalid input
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon must have at least 3 vertices");
      this.vertices = List.of(vertices);
      this.size     = vertices.length;

      // Build supporting plane from first three points:
      this.plane = new Plane(vertices[0], vertices[1], vertices[2]);
      // No further checks needed for a triangle
      if (size == 3) return;

      Vector n = plane.getNormal(vertices[0]);

      // Initialize winding test using last two edges:
      Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
      Vector edge2 = vertices[0].subtract(vertices[size - 1]);
      boolean initialSign = edge1.crossProduct(edge2).dotProduct(n) > 0;

      // Validate coplanarity + convexity for each subsequent vertex
      for (int i = 1; i < size; ++i) {
         // 1) coplanarity:
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices must lie in the same plane");

         // 2) convexity & consistent winding:
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         boolean currentSign = edge1.crossProduct(edge2).dotProduct(n) > 0;
         if (initialSign != currentSign)
            throw new IllegalArgumentException("Polygon must be convex and vertices consistently ordered");
      }
   }

   /**
    * Compute the axis-aligned bounding box that encloses all vertices.
    */
   @Override
   protected BoundingBox computeBoundingBox() {
      double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

      for (Point p : vertices) {
         minX = Math.min(minX, p.getX());
         minY = Math.min(minY, p.getY());
         minZ = Math.min(minZ, p.getZ());
         maxX = Math.max(maxX, p.getX());
         maxY = Math.max(maxY, p.getY());
         maxZ = Math.max(maxZ, p.getZ());
      }

      return new BoundingBox(
              new Point(minX, minY, minZ),
              new Point(maxX, maxY, maxZ)
      );
   }

   /**
    * The polygon’s normal is uniform; delegate to the underlying plane.
    */
   @Override
   public Vector getNormal(Point point) {
      return plane.getNormal(point);
   }

   /**
    * Detailed NVI helper: (1) intersect the plane, (2) inside–outside test.
    */
   @Override
   protected List<Intersectable.Intersection> calculateIntersectionsHelper(Ray ray) {
      // 1) Ray–plane intersection
      var planeHits = plane.findGeoIntersections(ray);
      if (planeHits == null) return null;

      Point p = planeHits.get(0).point;
      Vector n = plane.getNormal(p);

      // 2) Inside–outside test against each edge:
      for (int i = 0; i < size; ++i) {
         Point vi = vertices.get(i);
         Point vj = vertices.get((i + 1) % size);

         Vector edge = vj.subtract(vi);
         Vector vp   = p.subtract(vi);

         Vector cross;
         try {
            cross = edge.crossProduct(vp);
         } catch (IllegalArgumentException e) {
            // On an edge or vertex → count as inside
            continue;
         }

         // If cross·n is negative → outside
         if (alignZero(cross.dotProduct(n)) < 0)
            return null;
      }

      // 3) All tests passed: build full Intersection record
      return List.of(new Intersectable.Intersection(
              this,             // geometry
              p,                // hit point
              getMaterial(),    // now using the public getter
              ray,              // incoming ray
              n,                // normal at hit
              null              // no specific light source
      ));
   }

   @Override
   public String toString() {
      return "Polygon" + vertices;
   }
}
