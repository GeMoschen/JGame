package de.gemo.game.collision.core;

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
            if (CollisionHelper3D.vectorInHitbox(hitbox.getVector(index), other) || CollisionHelper3D.vectorInHitbox(other.getVector(index), hitbox)) {
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

    /**
     * Check if a vector is inside of a Hitbox3D.
     * 
     * @param vector
     * @param hitbox
     * @return
     */
    public static boolean vectorInHitbox(Vector3f vector, Hitbox3D hitbox) {
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
