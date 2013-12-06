package de.gemo.gameengine.units;

import java.io.Serializable;

import static org.lwjgl.opengl.GL11.*;

public class Vector implements Serializable {

    private static final long serialVersionUID = 1870562186338212289L;

    protected float x, y, z;

    public static Vector FromPoint(float x, float y) {
        return new Vector(x, y, 0);
    }

    public static Vector FromPoint(float x, float y, float z) {
        return new Vector(x, y, z);
    }

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(float x, float y) {
        this(x, y, 0);
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void rotate(float sin, float cos) {
        float tempx = (cos * x) - (sin * y);
        float tempy = (sin * x) + (cos * y);
        x = tempx;
        y = tempy;
    }

    public void rotateAround(Vector vector, double sin, double cos) {
        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (y - vector.getY()));
        double tempy = vector.getY() + (sin * (x - vector.getX()) + cos * (y - vector.getY()));

        x = (float) tempx;
        y = (float) tempy;
    }

    public void rotateAround(Vector vector, float angle) {
        double rad = Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (y - vector.getY()));
        double tempy = vector.getY() + (sin * (x - vector.getX()) + cos * (y - vector.getY()));

        x = (float) tempx;
        y = (float) tempy;
    }

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
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
        this.set(x, y, this.z);
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
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(float x) {
        this.x = x;
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

    public Vector scale(float scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    public void scaleX(float scaleX) {
        this.x *= scaleX;
    }

    public void scaleY(float scaleY) {
        this.y *= scaleY;
    }

    public double distanceTo(Vector vector) {
        return Math.sqrt(Math.pow(vector.getX() - this.getX(), 2) + Math.pow(vector.getY() - this.getY(), 2));
    }

    public Vector clone() {
        return new Vector(x, y, z);
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
     * Add a vector to another vector and place the result in a destination vector.
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @param dest
     *            The destination vector, or null if a new vector is to be created
     * @return the sum of left and right in dest
     */
    public static Vector add(Vector left, Vector right, Vector dest) {
        if (dest == null)
            return new Vector(left.x + right.x, left.y + right.y);
        else {
            dest.set(left.x + right.x, left.y + right.y);
            return dest;
        }
    }

    /**
     * Subtract a vector from another vector and place the result in a destination vector.
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @param dest
     *            The destination vector, or null if a new vector is to be created
     * @return left minus right in dest
     */
    public static Vector sub(Vector left, Vector right, Vector dest) {
        if (dest == null)
            return new Vector(left.x - right.x, left.y - right.y);
        else {
            dest.set(left.x - right.x, left.y - right.y);
            return dest;
        }
    }

    /**
     * The dot product of two vectors is calculated as v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @return left dot right
     */
    public static float dot(Vector left, Vector right) {
        return left.x * right.x + left.y * right.y;
    }

}
