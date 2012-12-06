package de.gemo.game.entity;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3i;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.game.collision.Vector;
import de.gemo.game.core.Engine;

public abstract class AbstractEntity {

    private static int currentID = 0;

    public static int getNextFreeID() {
        return currentID++;
    }

    protected final int entityID;
    protected Vector center = null;

    public AbstractEntity(Vector center) {
        this(center.getX(), center.getY());
    }

    public AbstractEntity(float x, float y) {
        this.center = new Vector(x, y);
        this.entityID = AbstractEntity.getNextFreeID();
    }

    public void render() {
        glBegin(GL11.GL_LINE_LOOP);
        glColor3f(0f, 1.0f, 0f);
        glVertex3i(-2, -2, -1);
        glVertex3i(+2, -2, -1);
        glVertex3i(+2, +2, -1);
        glVertex3i(-2, +2, -1);
        glEnd();

        GL11.glEnable(GL11.GL_BLEND);
        Engine.font_10.drawString((int) (Engine.font_10.getWidth("ID: " + this.entityID) / -2f), 3, "ID: " + this.entityID, Color.white);
        GL11.glDisable(GL11.GL_BLEND);
    }
    public int getEntityID() {
        return entityID;
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
    }

}
