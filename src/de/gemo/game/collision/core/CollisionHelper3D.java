package de.gemo.game.collision.core;

import java.util.*;

import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.units.*;

public class CollisionHelper3D {

    public static boolean collides(AABB aabb, AABB other) {
        return aabb.collides(other);
    }

    public static boolean collides(Hitbox3D hitbox, Hitbox3D other) {
        // check AABBs first
        if (!CollisionHelper3D.collides(hitbox.getAABB(), other.getAABB())) {
            return false;
        }

        // check all points of both hitboxes
        // NOTE: This does not cover all cases of rotated boxes.. still TODO
        for (int index = 0; index < 8; index++) {
            if (CollisionHelper3D.isVectorInHitbox(hitbox.getVector(index), other) || CollisionHelper3D.isVectorInHitbox(other.getVector(index), hitbox)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVectorInFrontOfPlane(Vector3f vector, Vector3f p0, Vector3f p1, Vector3f p2) {
        Vector3f normal = new Vector3f();
        Vector3f dirVector = new Vector3f();
        normal = Vector3f.cross(Vector3f.sub(p1, p0), Vector3f.sub(p2, p0)).normalize();
        dirVector = Vector3f.sub(p0, vector).normalize();
        return (Vector3f.dot(normal, dirVector) > 0);
    }

    public static Vector3f lineHitsPlane(Vector3f p0, Vector3f p1, Vector3f planeCoordinate, Vector3f planeNormal) {
        Vector3f u = Vector3f.sub(p1, p0);
        Vector3f w = Vector3f.sub(p0, planeCoordinate);
        float dot = Vector3f.dot(planeNormal, u);
        float fac = -Vector3f.dot(planeNormal, w) / dot;
        u.scale(fac);
        Vector3f vector = Vector3f.add(p0, u);
        return Vector3f.add(vector, planeNormal.scale(0.01f));
    }

    private static Vector3f getNormal(Vector3f p0, Vector3f p1, Vector3f p2) {
        return Vector3f.cross(Vector3f.sub(p1, p0), Vector3f.sub(p2, p0)).normalize();
    }

    public static Vector3f lineHitsBox(Vector3f p0, Vector3f p1, Hitbox3D hitbox) {
        System.out.println("-----------------");
        ArrayList<Vector3f> collisions = new ArrayList<Vector3f>();
        // bottom
        Vector3f normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(1), hitbox.getVector(3));
        Vector3f test = lineHitsPlane(p0, p1, hitbox.getVector(2), normal);
        if (test != null && CollisionHelper3D.isVectorInHitbox(test, hitbox)) {
            System.out.println("Ray hits BOTTOM : " + lineHitsPlane(p0, p1, hitbox.getVector(2), normal));
            collisions.add(test);
        }

        // top
        normal = CollisionHelper3D.getNormal(hitbox.getVector(4), hitbox.getVector(5), hitbox.getVector(7));
        test = lineHitsPlane(p0, p1, hitbox.getVector(4), normal);
        if (test != null && CollisionHelper3D.isVectorInHitbox(test, hitbox)) {
            System.out.println("Ray hits TOP : " + lineHitsPlane(p0, p1, hitbox.getVector(4), normal));
            collisions.add(test);
        }

        // front
        normal = CollisionHelper3D.getNormal(hitbox.getVector(1), hitbox.getVector(5), hitbox.getVector(0));
        test = lineHitsPlane(p0, p1, hitbox.getVector(1), normal);
        if (test != null && CollisionHelper3D.isVectorInHitbox(test, hitbox)) {
            System.out.println("Ray hits FRONT : " + lineHitsPlane(p0, p1, hitbox.getVector(1), normal));
            collisions.add(test);
        }

        // back
        normal = CollisionHelper3D.getNormal(hitbox.getVector(2), hitbox.getVector(3), hitbox.getVector(6));
        test = lineHitsPlane(p0, p1, hitbox.getVector(2), normal);
        if (test != null && CollisionHelper3D.isVectorInHitbox(test, hitbox)) {
            System.out.println("Ray hits BACK : " + lineHitsPlane(p0, p1, hitbox.getVector(2), normal));
            collisions.add(test);
        }

        // left
        normal = CollisionHelper3D.getNormal(hitbox.getVector(3), hitbox.getVector(0), hitbox.getVector(7));
        test = lineHitsPlane(p0, p1, hitbox.getVector(3), normal);
        if (test != null && CollisionHelper3D.isVectorInHitbox(test, hitbox)) {
            System.out.println("Ray hits LEFT : " + lineHitsPlane(p0, p1, hitbox.getVector(3), normal));
            collisions.add(test);
        }

        // right
        normal = CollisionHelper3D.getNormal(hitbox.getVector(6), hitbox.getVector(5), hitbox.getVector(2));
        test = lineHitsPlane(p0, p1, hitbox.getVector(6), normal);
        if (test != null && CollisionHelper3D.isVectorInHitbox(test, hitbox)) {
            System.out.println("Ray hits RIGHT : " + lineHitsPlane(p0, p1, hitbox.getVector(6), normal));
            collisions.add(test);
        }

        if (collisions.size() == 0) {
            return null;
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

        // bottom
        if (isVectorInFrontOfPlane(vector, hitbox.getVector(2), hitbox.getVector(1), hitbox.getVector(3))) {
            return false;
        }

        // top
        if (isVectorInFrontOfPlane(vector, hitbox.getVector(4), hitbox.getVector(5), hitbox.getVector(7))) {
            return false;
        }

        // front
        if (isVectorInFrontOfPlane(vector, hitbox.getVector(1), hitbox.getVector(5), hitbox.getVector(0))) {
            return false;
        }

        // back
        if (isVectorInFrontOfPlane(vector, hitbox.getVector(2), hitbox.getVector(3), hitbox.getVector(6))) {
            return false;
        }

        // left
        if (isVectorInFrontOfPlane(vector, hitbox.getVector(3), hitbox.getVector(0), hitbox.getVector(7))) {
            return false;
        }

        // right
        if (isVectorInFrontOfPlane(vector, hitbox.getVector(6), hitbox.getVector(5), hitbox.getVector(2))) {
            return false;
        }
        return true;
    }
}
