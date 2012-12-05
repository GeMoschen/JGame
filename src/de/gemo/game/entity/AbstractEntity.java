package de.gemo.game.entity;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.game.collision.EasyVector;
import de.gemo.game.collision.Vector;
import de.gemo.game.core.Game;

public abstract class AbstractEntity {

    private static int currentID = 0;

    public static int getNextFreeID() {
        return currentID++;
    }

    protected final int entityID;
    protected EasyVector center = null;

    public AbstractEntity(Vector center) {
        this(center.getX(), center.getY());
    }

    public AbstractEntity(double x, double y) {
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

    public void setCenter(Vector vector) {
        this.setCenter(vector.getX(), vector.getY());
    }

    public void setCenter(double x, double y) {
        double dX = x - center.getX();
        double dY = y - center.getY();
        this.move(dX, dY);
    }

    public final void setZ(double z) {
        this.center.setZ(z);
    }

    public void move(Vector vector) {
        this.center.move(vector.getX(), vector.getY());
    }

    public void move(double x, double y) {
        this.center.move(x, y);
    }

}
