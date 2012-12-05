package de.gemo.game.collision;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class Hitbox implements Cloneable {
    private List<Vector> points = new ArrayList<Vector>();
    private List<Vector> edges = new ArrayList<Vector>();
    protected Vector center = null;

    public Hitbox(Vector center) {
        this.center = new Vector(center.getX(), center.getY(), center.getZ());
    }

    public Hitbox(double x, double y) {
        this.center = new Vector(x, y);
    }

    public final void buildEdges() {
        Vector p1;
        Vector p2;
        this.edges.clear();
        for (int i = 0; i < points.size(); i++) {
            p1 = points.get(i);
            if (i + 1 >= points.size()) {
                p2 = points.get(0);
            } else {
                p2 = points.get(i + 1);
            }
            this.edges.add(Vector.subtract(p2, p1));
        }
    }

    public final int getEdgeCount() {
        return this.edges.size();
    }

    public final Vector getEdge(int index) {
        return this.edges.get(index);
    }

    public final void addPoint(Vector vector) {
        this.points.add(vector);
    }

    public final void addPoint(double x, double y) {
        this.addPoint(new Vector(this.center.getX() + x, this.center.getY() + y));
    }

    public final List<Vector> getPoints() {
        return points;
    }

    public final int getPointCount() {
        return this.points.size();
    }

    public final Vector getPoint(int index) {
        return this.points.get(index);
    }

    public final Vector getCenter() {
        return this.center;
    }

    public final void setCenter(double x, double y) {
        double difX = this.center.getX() - x;
        double difY = this.center.getY() - y;
        this.moveHitbox(difX, difY);
    }

    public final void setCenter(Vector center) {
        this.setCenter(center.getX(), center.getY());
        this.center.setZ(center.getZ());
    }

    public final void moveHitbox(Vector v) {
        this.moveHitbox(v.getX(), v.getY());
    }

    public final void moveHitbox(double x, double y) {
        this.center.move(x, y);
        for (Vector vector : this.points) {
            vector.move(x, y);
        }
        this.buildEdges();
    }

    public final void rotate(double angle) {
        double rad = Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        for (Vector vector : this.points) {
            vector.rotate(this.center, rad, sin, cos);
        }
        this.buildEdges();
    }

    @Override
    public String toString() {
        String result = "Polygon { ";
        for (int i = 0; i < points.size(); i++) {
            result += points.get(i).toString();
            if (i < points.size() - 1) {
                result += " ; ";
            }
        }
        return result + " }";
    }

    public final void renderCenter() {
        // render center
        glBegin(GL11.GL_LINE_LOOP);
        glColor3f(1.0f, 0, 0);
        glVertex3d(this.center.getX() - 2, this.center.getY() - 2, 0d);
        glVertex3d(this.center.getX() + 2, this.center.getY() - 2, 0d);
        glVertex3d(this.center.getX() + 2, this.center.getY() + 2, 0d);
        glVertex3d(this.center.getX() - 2, this.center.getY() + 2, 0d);
        glEnd();
    }

    public void render() {
        // render center
        this.renderCenter();

        // render boundingbox
        glBegin(GL_LINE_LOOP);
        glColor3f(0, 1.0f, 0);
        for (Vector vector : this.points) {
            vector.render();
        }
        glEnd();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Hitbox otherBox = new Hitbox(this.center);
        for (Vector vector : this.points) {
            otherBox.addPoint(vector);
        }
        otherBox.buildEdges();
        return otherBox;
    }

}
