package primitives;

import static org.junit.jupiter.api.Assertions.*;

class Point {

    @org.junit.jupiter.api.Test
    void subtract() {
        // Subtract points
        if (!P2.subtract(P1).equals(V1))
            out.println("ERROR: (point2 - point1) does not work correctly");
        try {
            P1.subtract(P1);
            out.println("ERROR: (point - itself) does not throw an exception");
        } catch (IllegalArgumentException ignore) {} catch (Exception ignore) {
            out.println("ERROR: (point - itself) throws wrong exception");
        }
    }

    @org.junit.jupiter.api.Test
    void add() {
        // Add vector to point
        if (!(P1.add(V1).equals(P2)))
            out.println("ERROR: (point + vector) = other point does not work correctly");
        if (!(P1.add(V1_OPPOSITE).equals(Point.ZERO)))
            out.println("ERROR: (point + vector) = center of coordinates does not work correctly");
    }

    @org.junit.jupiter.api.Test
    void distanceSquared() {
        if (!isZero(P1.distanceSquared(P1)))
            out.println("ERROR: point squared distance to itself is not zero");
        if (!isZero(P1.distanceSquared(P3) - 9))
            out.println("ERROR: squared distance between points is wrong");
        if (!isZero(P3.distanceSquared(P1) - 9))
            out.println("ERROR: squared distance between points is wrong");
    }

    @org.junit.jupiter.api.Test
    void distance() {
        // distances
        if (!isZero(P1.distanceSquared(P1)))
            out.println("ERROR: point squared distance to itself is not zero");
        if (!isZero(P1.distance(P1)))
            out.println("ERROR: point distance to itself is not zero");
        if (!isZero(P1.distanceSquared(P3) - 9))
            out.println("ERROR: squared distance between points is wrong");
        if (!isZero(P3.distanceSquared(P1) - 9))
            out.println("ERROR: squared distance between points is wrong");
        if (!isZero(P1.distance(P3) - 3))
            out.println("ERROR: distance between points to itself is wrong");
        if (!isZero(P3.distance(P1) - 3))
            out.println("ERROR: distance between points to itself is wrong");
    }

}