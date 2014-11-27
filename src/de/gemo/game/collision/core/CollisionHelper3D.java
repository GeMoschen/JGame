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

        for (int index = 0; index < 8; index++) {
            if (CollisionHelper3D.vectorInHitbox(hitbox.getVector(index), other) || CollisionHelper3D.vectorInHitbox(other.getVector(index), hitbox)) {
                return true;
            }
        }
        return false;
    }

    public static boolean vectorInHitbox(Vector3f vector, Hitbox3D hitbox) {
        Vector3f normal = new Vector3f();
        boolean collision = true;
        
   
        // bottom
        normal = Vector3f.cross(Vector3f.sub(hitbox.getVector(1), hitbox.getVector(0)), Vector3f.sub(hitbox.getVector(2), hitbox.getVector(0)), normal);
        System.out.println("normal: " + normal.normalize(normal));
        if (Vector3f.dot(normal, Vector3f.sub(vector, hitbox.getVector(0))) >= 0) {
            collision = false;
        }
        
 
//
//        // top
//        if (collision) {
//            normal = Vector3f.cross(Vector3f.sub(hitbox.getVector(6), hitbox.getVector(4)), Vector3f.sub(hitbox.getVector(5), hitbox.getVector(4)), normal);
//            if (Vector3f.dot(normal, Vector3f.sub(vector, hitbox.getVector(4))) >= 0) {
//                collision = false;
//            }
//        }
//
//        // front
//        if (collision) {
//            normal = Vector3f.cross(Vector3f.sub(hitbox.getVector(6), hitbox.getVector(2)), Vector3f.sub(hitbox.getVector(3), hitbox.getVector(2)), normal);
//            if (Vector3f.dot(normal, Vector3f.sub(vector, hitbox.getVector(2))) >= 0) {
//                collision = false;
//            }
//        }
//
//        // back
//        if (collision) {
//            normal = Vector3f.cross(Vector3f.sub(hitbox.getVector(4), hitbox.getVector(0)), Vector3f.sub(hitbox.getVector(1), hitbox.getVector(0)), normal);
//            if (Vector3f.dot(normal, Vector3f.sub(vector, hitbox.getVector(0))) >= 0) {
//                collision = false;
//            }
//        }
//
//        // right
//        if (collision) {
//            normal = Vector3f.cross(Vector3f.sub(hitbox.getVector(5), hitbox.getVector(1)), Vector3f.sub(hitbox.getVector(2), hitbox.getVector(1)), normal);
//            if (Vector3f.dot(normal, Vector3f.sub(vector, hitbox.getVector(1))) >= 0) {
//                collision = false;
//            }
//        }
//
//        // left
//        if (collision) {
//            normal = Vector3f.cross(Vector3f.sub(hitbox.getVector(3), hitbox.getVector(0)), Vector3f.sub(hitbox.getVector(4), hitbox.getVector(0)), normal);
//            if (Vector3f.dot(normal, Vector3f.sub(vector, hitbox.getVector(0))) >= 0) {
//                collision = false;
//            }
//        }
        return collision;
    }
}
