package de.gemo.game.entity;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.game.collision.EasyVector;
import de.gemo.game.core.Game;

public abstract class AbstractEntity {

    private static int currentID = 0;

    public static int getNextFreeID() {
        return currentID++;
    }

    protected final int entityID;
    protected EasyVector center = null;

    public AbstractEntity(EasyVector center) {
        this(center.getX(), center.getY());
    }

    public AbstractEntity(float x, float y) {
        this.center = new EasyVector(x, y);
        this.entityID = AbstractEntity.getNextFreeID();
    }

    public void render() {
        glBegin(GL11.GL_LINE_LOOP);
        glColor3f(0f, 1.0f, 0f);
        glVertex3d(-2, -2, -1);
        glVertex3d(+2, -2, -1);
        glVertex3d(+2, +2, -1);
        glVertex3d(-2, +2, -1);
        glEnd();

        GL11.glEnable(GL11.GL_BLEND);
        Game.font_10.drawString((int) (Game.font_10.getWidth("ID: " + this.entityID) / -2f), 3, "ID: " + this.entityID, Color.white);
        GL11.glDisable(GL11.GL_BLEND);
    }
    public int getEntityID() {
        return entityID;
    }

    public final EasyVector getCenter() {
        return center;
    }

    public void setCenter(EasyVector vector) {
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

    public void move(EasyVector vector) {
        this.center.move(vector.getX(), vector.getY());
    }

    public void move(float x, float y) {
        this.center.move(x, y);
    }

}
