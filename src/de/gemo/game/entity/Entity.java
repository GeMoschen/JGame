package de.gemo.game.entity;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3i;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.game.core.FontManager;
import de.gemo.game.interfaces.IDebugRenderable;
import de.gemo.game.interfaces.Vector;

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
        GL11.glTranslatef(getX(), getY(), getZ());
        GL11.glRotatef(this.getAngle(), 0, 0, 1);

        // render center
        GL11.glDisable(GL11.GL_BLEND);

        glColor3f(1.0f, 0, 0);
        glBegin(GL_LINE_LOOP);
        glVertex3i(-2, -2, 0);
        glVertex3i(2, -2, 0);
        glVertex3i(+2, +2, 0);
        glVertex3i(-2, +2, 0);
        glEnd();
        GL11.glEnable(GL11.GL_BLEND);

        // write entity-id

        FontManager.getStandardFont().drawString((int) (FontManager.getStandardFont().getWidth("ID: " + this.entityID) / -2f), 3, "ID: " + this.entityID, Color.white);

        // translate back
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glRotatef(-this.getAngle(), 0, 0, 1);
        GL11.glTranslatef(-getX(), -getY(), -getZ());
    }
}
