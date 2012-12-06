package de.gemo.game.collision;

import static org.lwjgl.opengl.GL11.glVertex3d;

public class ComplexVector implements Cloneable {
    protected float x, y, z;
    protected final EasyVector parent;
    protected float calcX, calcY, calcZ;

    public ComplexVector(EasyVector parent) {
        this(parent, 0, 0, 0);
    }

    public ComplexVector(EasyVector parent, float x, float y) {
        this(parent, x, y, 0);
    }

    public ComplexVector(EasyVector parent, float x, float y, float z) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void recalculatePositions() {
        this.calcX = parent.getX() + x;
        this.calcY = parent.getY() + y;
        this.calcZ = parent.getZ() + z;
    }

    public void rotate(double rad, double sin, double cos) {
        double tempx = (cos * x) - (sin * y);
        double tempy = (sin * x) + (cos * y);

        // float tempx = parent.getX() + (cos * (x - parent.getX()) - sin * (y - parent.getY()));
        // float tempy = parent.getY() + (sin * (x - parent.getX()) + cos * (y - parent.getY()));
        x = (float) tempx;
        y = (float) tempy;
    }

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public double distanceTo(ComplexVector vector) {
        return Math.sqrt(Math.pow(vector.getX() - this.getX(), 2) + Math.pow(vector.getY() - this.getY(), 2));
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof ComplexVector) {
            ComplexVector v = (ComplexVector) otherObject;
            return getX() == v.getX() && getY() == v.getY();
        }
        return false;
    }

    public boolean equals(ComplexVector otherVector) {
        return getX() == otherVector.getX() && getY() == otherVector.getY();
    }

    @Override
    public int hashCode() {
        return (int) ((int) getX() ^ (int) getY());
    }

    @Override
    public String toString() {
        return "ComplexVector { " + getX() + " , " + getY() + " }";
    }

    public float getSelfX() {
        return x;
    }

    public float getSelfY() {
        return y;
    }

    public float getSelfZ() {
        return z;
    }

    public void setSelfZ(float z) {
        this.z = z;
    }

    public float getX() {
        return calcX;
    }

    public float getY() {
        return calcY;
    }

    public float getZ() {
        return calcZ;
    }

    public void render() {
        glVertex3d(this.getX(), this.getY(), this.getZ());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new ComplexVector(parent, x, y, z);
    }
}
