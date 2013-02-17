package de.gemo.engine.entity;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;

import de.gemo.engine.interfaces.rendering.IDebugRenderable;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.units.Vector;

public class Entity extends EntityLogic implements IDebugRenderable {

    protected Vector center = null;
    protected float angle = 0;

    public Entity(Vector center) {
        this.center = center;
    }

    public Entity(float x, float y) {
        this(new Vector(x, y));
    }

    public int getEntityID() {
        return entityID;
    }

    public float getX() {
        return this.center.getX();
    }

    public float getY() {
        return this.center.getY();
    }

    public float getZ() {
        return this.center.getZ();
    }

    public final Vector getCenter() {
        return center;
    }

    public void setCenter(Vector vector) {
        this.setCenter(vector.getX(), vector.getY());
    }

    public void setCenter(float x, float y) {
        float dX = x - center.getX();
        float dY = y - center.getY();
        this.move(dX, dY);
    }

    public final void setZ(float z) {
        this.center.setZ(z);
    }

    public void move(Vector vector) {
        this.center.move(vector.getX(), vector.getY());
    }

    public void move(float x, float y) {
        this.center.move(x, y);
        this.center.recalculatePositions();
    }

    public void setAngle(float angle) {
        this.rotate(-this.angle + angle);
    }

    public void rotate(float angle) {
        this.angle += angle;
        if (this.angle < 0f) {
            this.angle += 360f;
        }
        if (this.angle > 360f) {
            this.angle -= 360f;
        }
    }

    public float getAngle() {
        return angle;
    }

    public void debugRender() {
        glPushMatrix();
        {
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            glTranslatef((int) getX(), (int) getY(), 0);
            glRotatef(this.getAngle(), 0, 0, 1);

            // render center
            Color.yellow.bind();
            glBegin(GL_LINE_LOOP);
            glVertex3i(-1, -1, 0);
            glVertex3i(1, -1, 0);
            glVertex3i(+1, +1, 0);
            glVertex3i(-1, +1, 0);
            glEnd();

            // write entity-id
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            FontManager.getStandardFont().drawString((int) (FontManager.getStandardFont().getWidth("ID: " + this.entityID) / -2f), 3, "ID: " + this.entityID, Color.white);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }
}
