package de.gemo.gameengine.units;

import java.io.*;

import static org.lwjgl.opengl.GL11.*;

public class Vector3f implements Serializable {

    private static final long serialVersionUID = 1870562186338212289L;

    protected float x, y, z;
    private boolean dirty = true;
    private float length = 0f;

    public static Vector3f FromPoint(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dirty = true;
    }

    public void rotate(float sin, float cos) {
        float tempx = (cos * x) - (sin * y);
        float tempy = (sin * x) + (cos * y);
        x = tempx;
        y = tempy;
        this.dirty = true;
    }

    public void rotateAround(Vector3f vector, double sin, double cos) {
        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (y - vector.getY()));
        double tempy = vector.getY() + (sin * (x - vector.getX()) + cos * (y - vector.getY()));
        x = (float) tempx;
        y = (float) tempy;
        this.dirty = true;
    }

    public float getAngle(Vector3f other) {
        float angle = (float) Math.toDegrees(Math.atan2(other.getY() - this.getY(), other.getX() - this.getX()));
        if (angle < 0) {
            angle += 360;
        }
        return angle - 90;
    }

    public float getDistance(Vector3f other) {
        float xDist = other.x - this.x;
        float yDist = other.y - this.y;
        return (float) Math.sqrt(xDist * xDist + yDist * yDist);
    }

    public void rotateAround(Vector3f vector, float angle) {
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
        this.move(x, y, 0);
    }

    public void move(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
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
     */
    public void set(float x, float y) {
        this.set(x, y, 0);
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
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    /**
     * @return the z
     */
    public float getZ() {
        return z;
    }

    /**
     * @param z
     *            the z to set
     */
    public void setZ(float z) {
        this.z = z;
    }

    public void render() {
        glVertex3f(this.x, this.y, this.z);
    }

    public Vector3f scale(float scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        this.dirty = true;
        return this;
    }

    public float getLength() {
        if (this.dirty) {
            this.dirty = false;
            this.length = (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
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

    public void scaleZ(float scaleZ) {
        this.z *= scaleZ;
        this.dirty = true;
    }

    public double distanceTo(Vector3f vector) {
        return Math.sqrt(Math.pow(vector.getX() - this.getX(), 2) + Math.pow(vector.getY() - this.getY(), 2));
    }

    public Vector3f clone() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + " { ";
        result += this.getX() + " ; ";
        result += this.getY() + " ; ";
        result += this.getZ();
        return result + " }";
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
    public static Vector3f add(Vector3f left, Vector3f right) {
        return Vector3f.add(left, right, null);
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
    public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
        else {
            dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
            return dest;
        }
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
    public static Vector3f sub(Vector3f left, Vector3f right) {
        return Vector3f.sub(left, right, null);
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
    public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            return new Vector3f(left.x - right.x, left.y - right.y, left.z + right.z);
        else {
            dest.set(left.x - right.x, left.y - right.y, left.z + right.z);
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
    public static float dot(Vector3f left, Vector3f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z;
    }

    public static Vector3f normalize(Vector3f vector) {
        float length = vector.getLength();
        return new Vector3f(vector.x / length, vector.y / length, vector.z / length);
    }
}
