package de.gemo.game.fov.units;

import org.lwjgl.util.vector.*;

import de.gemo.gameengine.collision.*;

import static org.lwjgl.opengl.GL11.*;

public class Block {

    private Hitbox hitbox;

    public Block(int x, int y, int width, int height) {
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
    }

    public Vector2f[] getVertices() {
        Vector2f[] vertices = new Vector2f[this.hitbox.getPointCount()];
        for (int i = 0; i < this.hitbox.getPointCount(); i++) {
            vertices[i] = new Vector2f(this.hitbox.getPoint(i).getX(), this.hitbox.getPoint(i).getY());
        }
        return vertices;
    }

    public void render() {
        glBegin(GL_TRIANGLES);
        {
            for (int i = 0; i < getVertices().length; i++) {
                Vector2f current = getVertices()[i];
                Vector2f last = getVertices()[i == 0 ? getVertices().length - 1 : i - 1];
                glVertex2f(this.hitbox.getCenter().getX(), this.hitbox.getCenter().getY());
                glVertex2f(current.getX(), current.getY());
                glVertex2f(last.getX(), last.getY());
            }
        }
        glEnd();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
