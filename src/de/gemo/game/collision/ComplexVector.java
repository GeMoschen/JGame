package de.gemo.game.collision;

import static org.lwjgl.opengl.GL11.glVertex3d;

public class ComplexVector implements Cloneable {
    protected double x, y, z;
    protected final EasyVector parent;
    protected double calcX, calcY, calcZ;

    public ComplexVector(EasyVector parent) {
        this(parent, 0, 0, 0);
    }

    public ComplexVector(EasyVector parent, double x, double y) {
        this(parent, x, y, 0);
    }

    public ComplexVector(EasyVector parent, double x, double y, double z) {
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

        // double tempx = parent.getX() + (cos * (x - parent.getX()) - sin * (y - parent.getY()));
        // double tempy = parent.getY() + (sin * (x - parent.getX()) + cos * (y - parent.getY()));
        x = tempx;
        y = tempy;
    }

    public void move(double x, double y) {
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

    public double getSelfX() {
        return x;
    }

    public double getSelfY() {
        return y;
    }

    public double getSelfZ() {
        return z;
    }

    public void setSelfZ(double z) {
        this.z = z;
    }

    public double getX() {
        return calcX;
    }

    public double getY() {
        return calcY;
    }

    public double getZ() {
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
