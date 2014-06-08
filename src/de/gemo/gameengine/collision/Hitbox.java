package de.gemo.gameengine.collision;

import java.io.*;
import java.util.*;

import org.newdawn.slick.*;

import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Hitbox {

    public static Hitbox createRectangle(float halfWidth, float halfHeight) {
        Hitbox hitbox = new Hitbox(0, 0);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, +halfHeight);
        hitbox.addPoint(-halfWidth, +halfHeight);
        return hitbox;
    }

    public static Hitbox createRectangle(Vector2f center, float halfWidth, float halfHeight) {
        return createRectangle(center.getX(), center.getY(), halfWidth, halfHeight);
    }

    public static Hitbox createRectangle(float x, float y, float halfWidth, float halfHeight) {
        Hitbox hitbox = new Hitbox(x, y);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, +halfHeight);
        hitbox.addPoint(-halfWidth, +halfHeight);
        return hitbox;
    }

    private AABB aabb;
    private List<Vector3f> points = new ArrayList<Vector3f>();
    private Vector3f center;
    private float angle = 0f;

    public Hitbox(Vector3f center) {
        this.center = center;
        this.aabb = new AABB();
    }

    public Hitbox(float x, float y) {
        this(new Vector3f(x, y, 0));
    }

    public final List<Vector3f> getPoints() {
        return points;
    }

    public final int getPointCount() {
        return this.points.size();
    }

    public final Vector3f getPoint(int index) {
        return this.points.get(index);
    }

    public final void addPoint(Vector3f vector) {
        this.addPoint(vector.getX(), vector.getY());
    }

    public final void addPoint(float x, float y) {
        Vector3f vector = new Vector3f(this.getCenter().getX() + x, this.getCenter().getY() + y, 0);
        this.points.add(vector);
        this.aabb.addPoint(vector.getX(), vector.getY());
    }

    public final Vector3f getCenter() {
        return this.center;
    }

    public final void setCenter(float x, float y) {
        float difX = x - this.center.getX();
        float difY = y - this.center.getY();
        this.move(difX, difY);
    }

    public final void setCenter(Vector2f vector) {
        this.setCenter(vector.getX(), vector.getY());
    }

    public final void setCenter(Vector3f vector) {
        this.setCenter(vector.getX(), vector.getY());
    }

    public final void move(float x, float y) {
        this.center.move(x, y);
        this.aabb.reset();
        for (Vector3f vector : this.points) {
            vector.move(x, y);
            this.aabb.addPoint(vector.getX(), vector.getY());
        }
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
        this.aabb.reset();
        for (Vector3f vector : this.points) {
            vector.rotateAround(this.getCenter(), sin, cos);
            this.aabb.addPoint(vector.getX(), vector.getY());
        }
    }

    public final void rotateAround(Vector3f center, float angle) {
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
        this.center.rotateAround(center, sin, cos);
        this.aabb.reset();
        for (Vector3f vector : this.points) {
            vector.rotateAround(center, sin, cos);
            this.aabb.addPoint(vector.getX(), vector.getY());
        }
    }

    public void setAngle(Vector3f center, float angle) {
        this.rotateAround(center, -this.angle + angle);
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
            glDisable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // render center
            glPushMatrix();
            {
                glTranslatef(this.center.getX(), this.center.getY(), 0);
                this.renderCenter();
            }
            glPopMatrix();

            // render hitbox
            glPushMatrix();
            {
                Color.green.bind();
                glBegin(GL_LINE_LOOP);
                for (Vector3f vector : this.points) {
                    vector.render();
                }
                glEnd();
            }
            glPopMatrix();

            // render AABB
            // this.aabb.render();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    public void scale(float scaleX, float scaleY) {
        float currentAngle = this.angle;
        this.setAngle(0);
        for (Vector3f vector : this.points) {
            float currentX = vector.getX() - this.center.getX();
            float currentY = vector.getY() - this.center.getY();
            currentX *= scaleX;
            currentY *= scaleY;
            vector.setX(this.center.getX() + currentX);
            vector.setY(this.center.getY() + currentY);
        }
        this.setAngle(currentAngle);
    }

    public void scaleByPixel(float pixel) {
        for (Vector3f vector : this.points) {
            Vector3f vect = Vector3f.sub(vector, center);
            vect = Vector3f.normalize(vect);
            vect = vect.scale(pixel);
            vector.setX(vect.getX() + vector.getX());
            vector.setY(vect.getY() + vector.getY());
        }
    }

    public void scaleX(float scaleX) {
        this.scale(scaleX, 1f);
    }

    public void scaleY(float scaleY) {
        this.scale(1f, scaleY);
    }

    public Hitbox clone() {
        Hitbox otherBox = new Hitbox(this.center.clone());
        for (Vector3f vector : this.points) {
            otherBox.addPoint(Vector3f.sub(vector, this.center));
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

    public AABB getAABB() {
        return aabb;
    }

    public static Hitbox load(Vector3f baseVector, ArrayList<Vector3f> pointList) {
        Hitbox hitbox = new Hitbox(baseVector);
        for (Vector3f vector : pointList) {
            hitbox.addPoint(vector.getX(), vector.getY());
        }
        return hitbox;
    }

    public void export(ObjectOutputStream outputStream) {
        try {
            for (Vector3f vector : this.points) {
                outputStream.writeObject(vector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
