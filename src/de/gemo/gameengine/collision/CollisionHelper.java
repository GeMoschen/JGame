package de.gemo.gameengine.collision;

import java.util.ArrayList;

import de.gemo.gameengine.units.*;

public class CollisionHelper {

    // /////////////////////////////////////////////
    //
    // RECTANGLE COLLISION
    //
    // /////////////////////////////////////////////

    public static boolean isPointInRectangle(float x, float y, float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
        return (x >= topLeftX && x <= bottomRightX && y >= topLeftY && y <= bottomRightY);
    }

    // /////////////////////////////////////////////
    //
    // WITH INHETIRANCE
    //
    // /////////////////////////////////////////////

    // Jordan Curve Theorem - very fast
    public static boolean isVectorInHitbox(Vector3f vector, Hitbox hitbox) {
        if (vector == null || hitbox == null) {
            return false;
        }
        int nvert = hitbox.getPointCount();
        int i, j, count = 0;
        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            if (((hitbox.getPoint(i).getY() > vector.getY()) != (hitbox.getPoint(j).getY() > vector.getY())) && (vector.getX() < (hitbox.getPoint(j).getX() - hitbox.getPoint(i).getX()) * (vector.getY() - hitbox.getPoint(i).getY()) / (hitbox.getPoint(j).getY() - hitbox.getPoint(i).getY()) + hitbox.getPoint(i).getX())) {
                count++;
            }
        }
        return (count % 2 != 0);
    }

    // Use the JC-Theorem for collisiondetection
    public static boolean isColliding(Hitbox hitboxA, Hitbox hitboxB) {
        if (hitboxA == null || hitboxB == null) {
            return false;
        }
        for (Vector3f vector : hitboxA.getPoints()) {
            if (isVectorInHitbox(vector, hitboxB)) {
                return true;
            }
        }
        for (Vector3f vector : hitboxB.getPoints()) {
            if (isVectorInHitbox(vector, hitboxA)) {
                return true;
            }
        }
        return findIntersection(hitboxA, hitboxB) != null;
    }

    // Use the JC-Theorem for collisiondetection
    public static boolean isCollidingFast(Hitbox hitboxA, Hitbox hitboxB) {
        if (hitboxA == null || hitboxB == null) {
            return false;
        }
        for (Vector3f vector : hitboxA.getPoints()) {
            if (isVectorInHitbox(vector, hitboxB)) {
                return true;
            }
        }
        for (Vector3f vector : hitboxB.getPoints()) {
            if (isVectorInHitbox(vector, hitboxA)) {
                return true;
            }
        }
        return false;
    }

    // find intersections
    public static ArrayList<Vector3f> findIntersection(Hitbox hitboxA, Hitbox hitboxB) {
        if (hitboxA == null || hitboxB == null) {
            return null;
        }

        ArrayList<Vector3f> result = null;

        Vector3f a1 = null, a2 = null;
        Vector3f b1 = null, b2 = null;
        for (int i = 0; i < hitboxA.getPointCount(); i++) {
            a1 = hitboxA.getPoint(i);
            if (i < hitboxA.getPointCount() - 1) {
                a2 = hitboxA.getPoint(i + 1);
            } else {
                a2 = hitboxA.getPoint(0);
            }
            for (int j = 0; j < hitboxB.getPointCount(); j++) {
                b1 = hitboxB.getPoint(j);
                if (j < hitboxB.getPointCount() - 1) {
                    b2 = hitboxB.getPoint(j + 1);
                } else {
                    b2 = hitboxB.getPoint(0);
                }

                Vector3f vector = findIntersection(a1, a2, b1, b2);
                if (vector != null) {
                    if (result == null) {
                        result = new ArrayList<Vector3f>();
                    }
                    result.add(vector);
                }
            }
        }
        return result;
    }

    public static Vector3f findIntersection(Vector3f start1, Vector3f end1, Vector3f start2, Vector3f end2) {
        float denom = ((end1.getX() - start1.getX()) * (end2.getY() - start2.getY())) - ((end1.getY() - start1.getY()) * (end2.getX() - start2.getX()));

        // AB & CD are parallel
        if ((int) denom == 0) {
            return null;
        }

        float numer = ((start1.getY() - start2.getY()) * (end2.getX() - start2.getX())) - ((start1.getX() - start2.getX()) * (end2.getY() - start2.getY()));
        float r = numer / denom;

        float numer2 = ((start1.getY() - start2.getY()) * (end1.getX() - start1.getX())) - ((start1.getX() - start2.getX()) * (end1.getY() - start1.getY()));
        float s = numer2 / denom;

        // no intersection
        if ((r < 0 || r > 1) || (s < 0 || s > 1)) {
            return null;
        }

        // Find intersection point
        return new Vector3f((int) (start1.getX() + (r * (end1.getX() - start1.getX()))), (int) (start1.getY() + (r * (end1.getY() - start1.getY()))), 0);
    }

}
