package de.gemo.game.collision;

import static org.lwjgl.opengl.GL11.glVertex3d;

public class Vector implements Cloneable {
    private double x, y, z;

    public static Vector FromPoint(int x, int y) {
        return new Vector((double) x, (double) y, 0);
    }

    public static Vector FromPoint(int x, int y, int z) {
        return new Vector((double) x, (double) y, z);
    }

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(double x, double y) {
        this(x, y, 0);
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void rotate(Vector center, double rad, double sin, double cos) {
        double tempx = center.x + (cos * (x - center.x) - sin * (y - center.y));
        double tempy = center.y + (sin * (x - center.x) + cos * (y - center.y));
        x = tempx;
        y = tempy;
    }

    // public void rotateAround(Point center, double angle) {
    // double tempx = center.x + (Math.cos(Math.toRadians(angle)) * (x - center.x) - Math.sin(Math.toRadians(angle)) * (y - center.y));
    // double tempy = center.y + (Math.sin(Math.toRadians(angle)) * (x - center.x) + Math.cos(Math.toRadians(angle)) * (y - center.y));
    // x = tempx;
    // y = tempy;
    // }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double magnitude = getMagnitude();
        x = x / magnitude;
        y = y / magnitude;
    }

    public Vector getNormalized() {
        double magnitude = getMagnitude();
        return new Vector(x / magnitude, y / magnitude);
    }

    public double dotProduct(Vector vector) {
        return this.x * vector.x + this.y * vector.y;
    }

    public double distanceTo(Vector vector) {
        return Math.sqrt(Math.pow(vector.x - this.x, 2) + Math.pow(vector.y - this.y, 2));
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof Vector) {
            Vector v = (Vector) otherObject;
            return x == v.x && y == v.y;
        }
        return false;
    }

    public boolean equals(Vector otherVector) {
        return x == otherVector.x && y == otherVector.y;
    }

    @Override
    public int hashCode() {
        return (int) ((int) x ^ (int) y);
    }

    @Override
    public String toString() {
        return "Vector { " + x + " , " + y + " }";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y);
    }

    public static Vector subtract(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }

    public static Vector subtract(Vector a) {
        return new Vector(-a.x, -a.y);
    }

    public static Vector multiply(Vector a, double multiplier) {
        return new Vector(a.x * multiplier, a.y * multiplier);
    }

    public void render() {
        glVertex3d(this.x, this.y, this.z);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Vector(x, y, z);
    }
}
