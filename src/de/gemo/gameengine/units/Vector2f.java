package de.gemo.gameengine.units;

import java.io.*;

import static org.lwjgl.opengl.GL11.*;

public class Vector2f implements Serializable {

    private static final long serialVersionUID = 1870562186338212289L;

    protected float x, y;
    private boolean dirty = true;
    private float length = 0f;

    public static Vector2f FromPoint(float x, float y) {
        return new Vector2f(x, y);
    }

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
        this.dirty = true;
    }

    public void rotate(float sin, float cos) {
        float tempx = (cos * x) - (sin * y);
        float tempy = (sin * x) + (cos * y);
        x = tempx;
        y = tempy;
        this.dirty = true;
    }

    public void rotateAround(Vector2f vector, double sin, double cos) {
        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (y - vector.getY()));
        double tempy = vector.getY() + (sin * (x - vector.getX()) + cos * (y - vector.getY()));

        x = (float) tempx;
        y = (float) tempy;
        this.dirty = true;
    }

    public void rotateAround(Vector2f vector, float angle) {
        double rad = Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (y - vector.getY()));
        double tempy = vector.getY() + (sin * (x - vector.getX()) + cos * (y - vector.getY()));

        x = (float) tempx;
        y = (float) tempy;
        this.dirty = true;
    }

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
        this.dirty = true;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * 
     * @param x
     *            the x to set
     * @param y
     *            the y to set
     * @param z
     *            the z to set
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
        this.dirty = true;
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(float x) {
        this.x = x;
        this.dirty = true;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y
     *            the y to set
     */
    public void setY(float y) {
        this.y = y;
        this.dirty = true;
    }

    public void render() {
        glVertex2f(this.x, this.y);
    }

    public Vector2f scale(float scale) {
        x *= scale;
        y *= scale;
        this.dirty = true;
        return this;
    }

    public float getLength() {
        if (this.dirty) {
            this.dirty = false;
            this.length = (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        }
        return this.length;
    }

    public void truncate(float max) {
        float factor = max / this.getLength();
        factor = (factor < 1.0f) ? factor : 1.0f;
        this.scale(factor);
    }

    public void scaleX(float scaleX) {
        this.x *= scaleX;
        this.dirty = true;
    }

    public void scaleY(float scaleY) {
        this.y *= scaleY;
        this.dirty = true;
    }

    public double distanceTo(Vector2f vector) {
        return Math.sqrt(Math.pow(vector.getX() - this.getX(), 2) + Math.pow(vector.getY() - this.getY(), 2));
    }

    public Vector2f clone() {
        return new Vector2f(x, y);
    }

    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + " { ";
        result += this.getX() + " ; ";
        result += this.getY();
        return result + " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Vector2f) {
            Vector2f other = (Vector2f) obj;
            return other.x == this.x && other.y == this.y;
        }
        return false;
    }

    // ////////////////////////////////////////
    //
    // STATIC METHODS
    //
    // ////////////////////////////////////////

    /**
     * Add a vector to another vector and place the result in a destination
     * vector.
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @param dest
     *            The destination vector, or null if a new vector is to be
     *            created
     * @return the sum of left and right in dest
     */
    public static Vector2f add(Vector2f left, Vector2f right) {
        return Vector2f.add(left, right, null);
    }

    /**
     * Add a vector to another vector and place the result in a destination
     * vector.
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @param dest
     *            The destination vector, or null if a new vector is to be
     *            created
     * @return the sum of left and right in dest
     */
    public static Vector2f add(Vector2f left, Vector2f right, Vector2f dest) {
        if (dest == null)
            return new Vector2f(left.x + right.x, left.y + right.y);
        else {
            dest.set(left.x + right.x, left.y + right.y);
            return dest;
        }
    }

    public float getAngle(Vector2f other) {
        float angle = (float) Math.toDegrees(Math.atan2(other.getY() - this.getY(), other.getX() - this.getX()));
        if (angle < 0) {
            angle += 360;
        }
        return angle - 90;
    }

    public float getAngle(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(y - this.getY(), x - this.getX()));
        if (angle < 0) {
            angle += 360;
        }
        return angle - 90;
    }

    /**
     * Subtract a vector from another vector and place the result in a
     * destination vector.
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @param dest
     *            The destination vector, or null if a new vector is to be
     *            created
     * @return left minus right in dest
     */
    public static Vector2f sub(Vector2f left, Vector2f right) {
        return Vector2f.sub(left, right, null);
    }

    /**
     * Subtract a vector from another vector and place the result in a
     * destination vector.
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @param dest
     *            The destination vector, or null if a new vector is to be
     *            created
     * @return left minus right in dest
     */
    public static Vector2f sub(Vector2f left, Vector2f right, Vector2f dest) {
        if (dest == null)
            return new Vector2f(left.x - right.x, left.y - right.y);
        else {
            dest.set(left.x - right.x, left.y - right.y);
            return dest;
        }
    }

    /**
     * The dot product of two vectors is calculated as v1.x * v2.x + v1.y * v2.y
     * + v1.z * v2.z
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @return left dot right
     */
    public static float dot(Vector2f left, Vector2f right) {
        return left.x * right.x + left.y * right.y;
    }

    public static Vector2f normalize(Vector2f vector) {
        float length = vector.getLength();
        return new Vector2f(vector.x / length, vector.y / length);
    }

    public static Vector2f truncate(Vector2f vector, float max) {
        Vector2f clone = vector.clone();
        clone.truncate(max);
        return clone;
    }

    public static Vector2f invert(Vector2f vector) {
        return new Vector2f(-vector.x, -vector.y);
    }
}
