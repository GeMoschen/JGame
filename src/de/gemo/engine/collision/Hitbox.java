package de.gemo.engine.collision;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;

import de.gemo.engine.units.ComplexVector;
import de.gemo.engine.units.Vector;

public class Hitbox {

    private List<ComplexVector> points = new ArrayList<ComplexVector>();
    private Vector center;
    private float angle = 0f;

    public Hitbox(Vector center) {
        this.center = center;
    }

    public Hitbox(float x, float y) {
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
        this.center = vector;
    }

    public final void moveHitbox(Vector vector) {
        this.move(vector.getX(), vector.getY());
    }

    public final void move(float x, float y) {
        this.center.move(x, y);
        this.recalculatePositions();
    }

    public final void rotate(float angle) {
        this.angle += angle;

        if (this.angle < 0f) {
            this.angle += 360f;
        }
        if (this.angle > 360f) {
            this.angle -= 360f;
        }

        float rad = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        for (ComplexVector vector : this.points) {
            vector.rotate(sin, cos);
            vector.recalculatePositions();
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.rotate(-this.angle + angle);
    }

    private final void renderCenter() {
        // render center
        Color.red.bind();
        glBegin(GL_LINE_LOOP);
        glVertex3i(-2, -2, 0);
        glVertex3i(2, -2, 0);
        glVertex3i(+2, +2, 0);
        glVertex3i(-2, +2, 0);
        glEnd();
    }

    public void render() {
        // translate to center
        glPushMatrix();
        {
            glTranslatef(this.center.getX(), this.center.getY(), 0);

            // render center
            this.renderCenter();

            // render boundingbox
            Color.green.bind();
            glBegin(GL_LINE_LOOP);
            for (ComplexVector vector : this.points) {
                vector.render();
            }
            glEnd();
        }
        glPopMatrix();
    }

    public void scale(float scale) {
        float currentAngle = this.angle;
        this.setAngle(0);
        for (ComplexVector vector : this.points) {
            vector.scaleX(scale);
            vector.scaleY(scale);
            vector.recalculatePositions();
        }
        this.setAngle(currentAngle);
    }

    public void scaleX(float scaleX) {
        float currentAngle = this.angle;
        this.setAngle(0);
        for (ComplexVector vector : this.points) {
            vector.scaleX(scaleX);
            vector.recalculatePositions();
        }
        this.setAngle(currentAngle);
    }

    public void scaleY(float scaleY) {
        float currentAngle = this.angle;
        this.setAngle(0);
        for (ComplexVector vector : this.points) {
            vector.scaleY(scaleY);
            vector.recalculatePositions();
        }
        this.setAngle(currentAngle);
    }

    public void recalculatePositions() {
        this.center.recalculatePositions();
        for (ComplexVector vector : this.points) {
            vector.recalculatePositions();
        }
    }

    public Hitbox clone() {
        Hitbox otherBox = new Hitbox(this.center.clone());
        for (ComplexVector vector : this.points) {
            otherBox.addPoint(vector.clone());
        }
        return otherBox;
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

    public static Hitbox load(Vector baseVector, ArrayList<Vector> pointList) {
        Hitbox hitbox = new Hitbox(baseVector);
        for (Vector vector : pointList) {
            hitbox.addPoint(vector.getX(), vector.getY());
        }
        return hitbox;
    }

    public void export(ObjectOutputStream outputStream) {
        try {
            for (Vector vector : this.points) {
                outputStream.writeObject(vector);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
