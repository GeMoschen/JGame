package de.gemo.game.collision.core;

import java.util.*;

import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.units.*;

public class CollisionHelper3D {

    public static boolean collides(AABB aabb, AABB other) {
        return aabb.collides(other);
    }

    public static ArrayList<Vector3f> testCollides(Hitbox3D hitbox, Hitbox3D other) {
        ArrayList<Vector3f> collisions = new ArrayList<Vector3f>();

        // now: check all lines of both boxes
        for (int counter = 0; counter < 12; counter++) {
            int firstIndex = counter;
            int nextIndex = firstIndex + 1;
            if (firstIndex == 3 || firstIndex == 7) {
                nextIndex = firstIndex - 3;
            }

            if (firstIndex > 7) {
                firstIndex -= 4;
                nextIndex = firstIndex - 4;
            }

            collisions.addAll(getAllLineWithBoxCollisions(hitbox.getVector(firstIndex), hitbox.getVector(nextIndex), other));
            collisions.addAll(getAllLineWithBoxCollisions(other.getVector(firstIndex), other.getVector(nextIndex), hitbox));
        }
        return collisions;
    }

    public static boolean collides(Hitbox3D hitbox, Hitbox3D other) {
        // check AABBs first
        if (!CollisionHelper3D.collides(hitbox.getAABB(), other.getAABB())) {
            return false;
        }

        // check all lines of both boxes
        for (int counter = 0; counter < 12; counter++) {
            int firstIndex = counter;
            int nextIndex = firstIndex + 1;
            if (firstIndex == 3 || firstIndex == 7) {
                nextIndex = firstIndex - 3;
            }

            if (firstIndex > 7) {
                firstIndex -= 4;
                nextIndex = firstIndex - 4;
            }

            if (lineHitsBox(hitbox.getVector(firstIndex), hitbox.getVector(nextIndex), other) || lineHitsBox(other.getVector(firstIndex), other.getVector(nextIndex), hitbox)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVectorInFrontOfPlane(Vector3f vector, Vector3f p0, Vector3f p1, Vector3f p2) {
        Vector3f normal = new Vector3f();
        Vector3f dirVector = new Vector3f();
        normal = Vector3f.cross(Vector3f.sub(p1, p0), Vector3f.sub(p2, p0)).normalize();
        dirVector = Vector3f.sub(p0, vector).normalize();
        return (Vector3f.dot(normal, dirVector) > 0);
    }

    private static Vector3f lineToPlanePossibleCollisionPoint(Vector3f p0, Vector3f p1, Vector3f planeCoordinate, Vector3f planeNormal) {
        Vector3f u = Vector3f.sub(p1, p0);
        Vector3f w = Vector3f.sub(p0, planeCoordinate);
        float dot = Vector3f.dot(planeNormal, u);
        float fac = -Vector3f.dot(planeNormal, w) / dot;
        u.scale(fac);
        Vector3f vector = Vector3f.add(p0, u);
        Vector3f.add(vector, planeNormal.scale(0.01f), vector);
        if (CollisionHelper3D.isPointOnLine(p0, p1, vector)) {
            return vector;
        }
        return null;
    }

    private static Vector3f getNormal(Vector3f p0, Vector3f p1, Vector3f p2) {
        return Vector3f.cross(Vector3f.sub(p1, p0), Vector3f.sub(p2, p0)).normalize();
    }

    private static boolean isPointOnLine(Vector3f p0, Vector3f p1, Vector3f point) {
        float dist_0 = p0.getDistance(point);
        float dist_1 = p1.getDistance(point);
        float distances = Math.abs(dist_0 + dist_1 - p0.getDistance(p1));
        return (distances < 0.1f);
    }

    public static boolean lineHitsBox(Vector3f p0, Vector3f p1, Hitbox3D hitbox) {
        // bottom
        Vector3f normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(1), hitbox.getVector(3));
        Vector3f possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(2), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            return true;
        }

        // top
        normal = CollisionHelper3D.getNormal(hitbox.getVector(4), hitbox.getVector(5), hitbox.getVector(7));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(4), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            return true;
        }

        // front
        normal = CollisionHelper3D.getNormal(hitbox.getVector(1), hitbox.getVector(5), hitbox.getVector(0));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(1), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            return true;
        }

        // back
        normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(3), hitbox.getVector(6));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(2), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            return true;
        }

        // left
        normal = CollisionHelper3D.getNormal(hitbox.getVector(3), hitbox.getVector(0), hitbox.getVector(7));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(3), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            return true;
        }

        // right
        normal = CollisionHelper3D.getNormal(hitbox.getVector(6), hitbox.getVector(5), hitbox.getVector(2));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(6), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            return true;
        }

        return false;
    }

    public static ArrayList<Vector3f> getAllLineWithBoxCollisions(Vector3f p0, Vector3f p1, Hitbox3D hitbox) {
        ArrayList<Vector3f> collisions = new ArrayList<Vector3f>();
        // bottom
        Vector3f normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(1), hitbox.getVector(3));
        Vector3f possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(2), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // top
        normal = CollisionHelper3D.getNormal(hitbox.getVector(4), hitbox.getVector(5), hitbox.getVector(7));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(4), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // front
        normal = CollisionHelper3D.getNormal(hitbox.getVector(1), hitbox.getVector(5), hitbox.getVector(0));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(1), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // back
        normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(3), hitbox.getVector(6));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(2), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // left
        normal = CollisionHelper3D.getNormal(hitbox.getVector(3), hitbox.getVector(0), hitbox.getVector(7));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(3), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // right
        normal = CollisionHelper3D.getNormal(hitbox.getVector(6), hitbox.getVector(5), hitbox.getVector(2));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(6), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }
        return collisions;
    }

    public static Vector3f getLineWithBoxCollision(Vector3f p0, Vector3f p1, Hitbox3D hitbox) {
        ArrayList<Vector3f> collisions = new ArrayList<Vector3f>();
        // bottom
        Vector3f normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(1), hitbox.getVector(3));
        Vector3f possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(2), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // top
        normal = CollisionHelper3D.getNormal(hitbox.getVector(4), hitbox.getVector(5), hitbox.getVector(7));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(4), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // front
        normal = CollisionHelper3D.getNormal(hitbox.getVector(1), hitbox.getVector(5), hitbox.getVector(0));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(1), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // back
        normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(3), hitbox.getVector(6));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(2), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // left
        normal = CollisionHelper3D.getNormal(hitbox.getVector(3), hitbox.getVector(0), hitbox.getVector(7));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(3), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        // right
        normal = CollisionHelper3D.getNormal(hitbox.getVector(6), hitbox.getVector(5), hitbox.getVector(2));
        possibleCollision = lineToPlanePossibleCollisionPoint(p0, p1, hitbox.getVector(6), normal);
        if (possibleCollision != null && CollisionHelper3D.isVectorInHitbox(possibleCollision, hitbox)) {
            collisions.add(possibleCollision);
        }

        Vector3f minimum = null;
        for (Vector3f vector : collisions) {
            if (minimum == null || Math.abs(p0.distanceTo(vector)) < Math.abs(p0.distanceTo(minimum))) {
                minimum = vector;
            }
        }
        return minimum;
    }

    /**
     * Check if a vector is inside of a Hitbox3D.
     * 
     * @param vector
     * @param hitbox
     * @return
     */
    public static boolean isVectorInHitbox(Vector3f vector, Hitbox3D hitbox) {
        // DESCRIPTION OF THE ALGORITHM:
        // If the vector is in front of at least one plane of the hitbox,
        // it MUST be outside of the box.
        // In other words:
        // If the the vector is behind ALL planes, it is inside.

        boolean behindBottom = !isVectorInFrontOfPlane(vector, hitbox.getVector(2), hitbox.getVector(1), hitbox.getVector(3));
        boolean behindTop = !isVectorInFrontOfPlane(vector, hitbox.getVector(4), hitbox.getVector(5), hitbox.getVector(7));
        boolean behindFront = !isVectorInFrontOfPlane(vector, hitbox.getVector(1), hitbox.getVector(5), hitbox.getVector(0));
        boolean behindBack = !isVectorInFrontOfPlane(vector, hitbox.getVector(2), hitbox.getVector(3), hitbox.getVector(6));
        boolean behindLeft = !isVectorInFrontOfPlane(vector, hitbox.getVector(3), hitbox.getVector(0), hitbox.getVector(7));
        boolean behindRight = !isVectorInFrontOfPlane(vector, hitbox.getVector(6), hitbox.getVector(5), hitbox.getVector(2));

        return (behindBottom && behindTop && behindFront && behindBack && behindLeft && behindRight);
    }
}
