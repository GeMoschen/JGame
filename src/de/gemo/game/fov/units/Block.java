package de.gemo.game.fov.units;

import org.lwjgl.util.vector.*;

import de.gemo.gameengine.collision.*;

import static org.lwjgl.opengl.GL11.*;

public class Block {

    private int x, y, width, height;
    private Hitbox hitbox;

    public Block(int x, int y, int width, int height) {
        this.createHitbox(x, y, width, height);
    }

    private void createHitbox(int x, int y, int width, int height) {
        this.hitbox = new Hitbox(x, y);
        this.hitbox.addPoint(0, 0);
        this.hitbox.addPoint(0, 0 + height);
        this.hitbox.addPoint(0 + width, 0 + height);
        this.hitbox.addPoint(0 + width, 0);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Vector2f[] getVertices() {
        return new Vector2f[] { new Vector2f(x, y), new Vector2f(x, y + height), new Vector2f(x + width, y + height), new Vector2f(x + width, y) };
    }

    public void render() {
        glBegin(GL_QUADS);
        {
            for (Vector2f vertex : this.getVertices()) {
                glVertex2f(vertex.getX(), vertex.getY());
            }
        }
        glEnd();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
