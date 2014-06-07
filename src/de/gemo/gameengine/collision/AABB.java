package de.gemo.gameengine.collision;

import de.gemo.gameengine.units.*;

public class AABB {

    private float top, left, bottom, right;

    public AABB() {
        this.reset();
    }

    public void reset() {
        this.top = Float.MIN_VALUE;
        this.left = Float.MAX_VALUE;
        this.right = Float.MIN_VALUE;
        this.bottom = Float.MAX_VALUE;
    }

    public void addPoint(Vector2f vector) {
        this.addPoint(vector.getX(), vector.getY());
    }

    public void addPoint(float x, float y) {
        if (x < this.top) {
            this.top = x;
        }
        if (x > this.bottom) {
            this.bottom = x;
        }
        if (y < this.left) {
            this.left = y;
        }
        if (y > this.right) {
            this.right = y;
        }
    }
}
