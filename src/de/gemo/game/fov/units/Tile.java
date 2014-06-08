package de.gemo.game.fov.units;

import java.util.*;

import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.units.*;

public class Tile {

    private Hitbox hitbox;
    public Hitbox expanded;

    public Tile(int x, int y, int width, int height) {
        this.createHitbox(x, y, width, height);
    }

    private void createHitbox(int x, int y, int width, int height) {
        this.hitbox = new Hitbox(x, y);
        this.hitbox.addPoint(-width, -height);
        this.hitbox.addPoint(-width - (float) Math.random() * 10f, 0);
        this.hitbox.addPoint(-width, +height);
        this.hitbox.addPoint(0, +height - (float) Math.random() * 10f);
        this.hitbox.addPoint(+width, +height);
        this.hitbox.addPoint(+width - (float) Math.random() * 10f, 0);
        this.hitbox.addPoint(+width, -height);
        this.hitbox.addPoint(0, -height + (float) Math.random() * 10f);

        this.expanded = this.hitbox.clone();
        this.expanded.scaleByPixel(9f);
    }

    public static boolean IsVertexConcave(List<Vector3f> vertices, int vertex) {
        Vector3f current = vertices.get(vertex);
        Vector3f next = vertices.get((vertex + 1) % vertices.size());
        Vector3f previous = vertices.get(vertex == 0 ? vertices.size() - 1 : vertex - 1);
        Vector3f left = Vector3f.sub(current, previous);
        Vector3f right = Vector3f.sub(next, current);
        float cross = (left.getX() * right.getY()) - (left.getY() * right.getX());
        return cross < 0;
    }

    public void renderHitbox() {
        this.hitbox.render();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

}
