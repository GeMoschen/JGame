package de.gemo.game.collision;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class ComplexHitbox implements Cloneable {
    private List<ComplexVector> points = new ArrayList<ComplexVector>();
    private final EasyVector center;

    public ComplexHitbox(EasyVector center) {
        this.center = new EasyVector(center.getX(), center.getY(), center.getZ());
    }

    public ComplexHitbox(double x, double y) {
        this(new EasyVector(x, y));
    }

    public final List<ComplexVector> getPoints() {
        return points;
    }

    public final int getPointCount() {
        return this.points.size();
    }

    public final ComplexVector getPoint(int index) {
        return this.points.get(index);
    }

    public final void addPoint(ComplexVector vector) {
        vector.recalculatePositions();
        this.points.add(vector);
    }

    public final void addPoint(double x, double y) {
        this.addPoint(new ComplexVector(this.center, x, y));
    }

    public final EasyVector getCenter() {
        return this.center;
    }

    public final void setCenter(double x, double y) {
        double difX = this.center.getX() - x;
        double difY = this.center.getY() - y;
        this.move(difX, difY);
    }

    public final void setCenter(EasyVector vector) {
        this.setCenter(center.getX(), center.getY());
        this.center.setZ(center.getZ());
    }

    public final void moveHitbox(EasyVector vector) {
        this.move(vector.getX(), vector.getY());
    }

    public final void move(double x, double y) {
        this.center.move(x, y);
        for (ComplexVector vector : this.points) {
            vector.recalculatePositions();
        }
    }
    public final void rotate(double angle) {
        double rad = Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        for (ComplexVector vector : this.points) {
            vector.rotate(rad, sin, cos);
            vector.recalculatePositions();
        }
    }

    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + " { ";
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
        for (ComplexVector vector : this.points) {
            vector.render();
        }
        glEnd();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ComplexHitbox otherBox = new ComplexHitbox(this.center);
        for (ComplexVector vector : this.points) {
            otherBox.addPoint(vector);
        }
        return otherBox;
    }

}
