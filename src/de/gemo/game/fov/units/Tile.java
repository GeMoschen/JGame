package de.gemo.game.fov.units;

import de.gemo.gameengine.collision.*;

public class Tile {

    private Hitbox hitbox;
    public Hitbox expanded;

    public Tile(int x, int y, int width, int height) {
        this.createHitbox(x, y, width, height);
    }

    private void createHitbox(int x, int y, int width, int height) {
        this.hitbox = new Hitbox(x, y);
        this.hitbox.addPoint(-width, -height);
        // this.hitbox.addPoint(-width - (float) Math.random() * 10f, 0);
        this.hitbox.addPoint(-width, +height);
        // this.hitbox.addPoint(0, +height - (float) Math.random() * 10f);
        this.hitbox.addPoint(+width, +height);
        // this.hitbox.addPoint(+width - (float) Math.random() * 10f, 0);
        this.hitbox.addPoint(+width, -height);
        // this.hitbox.addPoint(0, -height + (float) Math.random() * 10f);

        this.expanded = this.hitbox.clone();
        this.expanded.scaleByPixel(9f);
    }

    public void renderHitbox() {
        this.hitbox.render();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

}
