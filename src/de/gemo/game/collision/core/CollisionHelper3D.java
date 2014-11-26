package de.gemo.game.collision.core;

import de.gemo.gameengine.collision.*;

public class CollisionHelper3D {
    
    public static boolean collides(AABB aabb, AABB other) {
        return aabb.collides(other);
    }
    
    public static boolean collides(Hitbox3D hitbox, Hitbox3D other) {
        // check AABBs first
        if(!CollisionHelper3D.collides(hitbox.getAABB(), other.getAABB())) {
            return false;
        }
        return false;
    }
}
