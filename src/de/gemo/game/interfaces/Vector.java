package de.gemo.game.interfaces;

import static org.lwjgl.opengl.GL11.glVertex3f;
import de.gemo.game.collision.ComplexVector;

public class Vector {
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

    public void scaleX(float scaleX) {
        this.x *= scaleX;
    }

    public void scaleY(float scaleY) {
        this.y *= scaleY;
    }

    public double distanceTo(ComplexVector vector) {
        return Math.sqrt(Math.pow(vector.getX() - this.getX(), 2) + Math.pow(vector.getY() - this.getY(), 2));
    }

    public Vector clone() {
        return new Vector(x, y, z);
    }

    public void recalculatePositions() {
    }
}
