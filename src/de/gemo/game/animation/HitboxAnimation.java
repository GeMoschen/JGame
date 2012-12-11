package de.gemo.game.animation;

import de.gemo.game.collision.Hitbox;

public class HitboxAnimation {
    private Hitbox[] hitboxes;
    private Hitbox currentHitbox;
    private int currentFrame;

    public HitboxAnimation(int size) {
        this.hitboxes = new Hitbox[size];
        this.currentFrame = 0;
        this.currentHitbox = null;
    }

    public void setAnimation(int index, Hitbox hitbox) {
        this.hitboxes[index] = hitbox;
    }

    public void goToFrame(int index) {
        if (index < 0) {
            index = 0;
        }
        if (index > this.hitboxes.length - 1) {
            index = this.hitboxes.length - 1;
        }
        this.currentHitbox = this.hitboxes[index];
    }

}
