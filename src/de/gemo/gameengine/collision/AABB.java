package de.gemo.gameengine.collision;

import org.lwjgl.util.vector.*;

import static org.lwjgl.opengl.GL11.*;

public class AABB {

    private float top, left, bottom, right;

    public AABB() {
        this.reset();
    }

    public float getTop() {
        return top;
    }

    public float getLeft() {
        return left;
    }

    public float getBottom() {
        return bottom;
    }

    public float getRight() {
        return right;
    }

    public void reset() {
        this.top = Float.MAX_VALUE;
        this.bottom = Float.MIN_VALUE;
        this.left = Float.MAX_VALUE;
        this.right = Float.MIN_VALUE;
    }

    public void addPoint(Vector2f vector) {
        this.addPoint(vector.getX(), vector.getY());
    }

    public void addPoint(float x, float y) {
        if (y < this.top) {
            this.top = y;
        }
        if (y > this.bottom) {
            this.bottom = y;
        }
        if (x < this.left) {
            this.left = x;
        }
        if (x > this.right) {
            this.right = x;
        }
    }

    public boolean collides(AABB other) {
        return this.right < other.left || this.left > other.right || this.top < other.bottom || this.bottom > other.top;
    }

    public void render() {
        glPushMatrix();
        {
            glColor4f(1, 0, 1, 1f);

            glBegin(GL_LINE_LOOP);
            {
                glVertex2f(this.left, this.top);
                glVertex2f(this.right, this.top);
                glVertex2f(this.right, this.bottom);
                glVertex2f(this.left, this.bottom);
            }
            glEnd();
        }
        glPopMatrix();
    }
}
