package de.gemo.game.collision;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class ComplexHitbox implements Cloneable {
    private List<ComplexVector> points = new ArrayList<ComplexVector>();
    private final Vector center;

    public ComplexHitbox(Vector center) {
        this.center = new Vector(center.getX(), center.getY(), center.getZ());
    }

    public ComplexHitbox(float x, float y) {
        this(new Vector(x, y));
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

    public final void addPoint(float x, float y) {
        this.addPoint(new ComplexVector(this.center, x, y));
    }

    public final Vector getCenter() {
        return this.center;
    }

    public final void setCenter(float x, float y) {
        float difX = this.center.getX() - x;
        float difY = this.center.getY() - y;
        this.move(difX, difY);
    }

    public final void setCenter(Vector vector) {
        this.setCenter(center.getX(), center.getY());
        this.center.setZ(center.getZ());
    }

    public final void moveHitbox(Vector vector) {
        this.move(vector.getX(), vector.getY());
    }

    public final void move(float x, float y) {
        this.center.move(x, y);
        for (ComplexVector vector : this.points) {
            vector.recalculatePositions();
        }
    }

    public final void rotate(float angle) {
        float rad = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
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
        glVertex3f(this.center.getX() - 2, this.center.getY() - 2, 0f);
        glVertex3f(this.center.getX() + 2, this.center.getY() - 2, 0f);
        glVertex3f(this.center.getX() + 2, this.center.getY() + 2, 0f);
        glVertex3f(this.center.getX() - 2, this.center.getY() + 2, 0f);
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
