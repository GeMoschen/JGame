package de.gemo.game.fov.units;

import org.newdawn.slick.*;

import static org.lwjgl.opengl.GL11.*;

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

    public void render() {
        // translate to center
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // render hitbox
            glPushMatrix();
            {
                Color.gray.bind();
                glBegin(GL_LINE_LOOP);
                for (Vector3f vector : this.hitbox.getPoints()) {
                    glVertex3f(vector.getX(), 0, vector.getY());
                }
                glEnd();
            }
            glPopMatrix();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    public void renderHitbox() {
        this.hitbox.render();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

}
