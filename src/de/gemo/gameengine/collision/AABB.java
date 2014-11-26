package de.gemo.gameengine.collision;

import org.lwjgl.util.vector.*;

import static org.lwjgl.opengl.GL11.*;

public class AABB {

    private float top, left, bottom, right, near, far;

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
        this.near = Float.MAX_VALUE;
        this.far = Float.MIN_VALUE;
    }

    public void addPoint(Vector2f vector) {
        this.addPoint(vector.getX(), vector.getY(), 0);
    }

    public void addPoint(float x, float y) {
        this.addPoint(x, y, 0);
    }

    public void addPoint(float x, float y, float z) {
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
        if (z < this.near) {
            this.near = z;
        }
        if (z > this.far) {
            this.far = z;
        }
    }

    public boolean collides(AABB other) {
        return !(this.right < other.left || this.left > other.right || this.top > other.bottom || this.bottom < other.top || this.near > other.far || this.far < other.near);
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // set color
            glColor4f(1, 0, 1, 0.5f);

            // BOTTOM
            glBegin(GL_LINE_LOOP);
            {
                glVertex3f(this.left, this.bottom, this.near);
                glVertex3f(this.right, this.bottom, this.near);
                glVertex3f(this.right, this.bottom, this.far);
                glVertex3f(this.left, this.bottom, this.far);
            }
            glEnd();

            // TOP
            glBegin(GL_LINE_LOOP);
            {
                glVertex3f(this.left, this.top, this.near);
                glVertex3f(this.right, this.top, this.near);
                glVertex3f(this.right, this.top, this.far);
                glVertex3f(this.left, this.top, this.far);
            }
            glEnd();

            // LINES
            glBegin(GL_LINES);
            {
                glVertex3f(this.left, this.bottom, this.near);
                glVertex3f(this.left, this.top, this.near);

                glVertex3f(this.right, this.bottom, this.near);
                glVertex3f(this.right, this.top, this.near);

                glVertex3f(this.left, this.bottom, this.far);
                glVertex3f(this.left, this.top, this.far);

                glVertex3f(this.right, this.bottom, this.far);
                glVertex3f(this.right, this.top, this.far);
            }
            glEnd();
        }
        glPopMatrix();
    }
}
