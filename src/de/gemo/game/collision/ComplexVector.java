package de.gemo.game.collision;

public class ComplexVector extends Vector implements Cloneable {
    protected final Vector parent;
    protected float calcX, calcY, calcZ;

    public ComplexVector(Vector parent) {
        this(parent, 0, 0, 0);
    }

    public ComplexVector(Vector parent, float x, float y) {
        this(parent, x, y, 0);
    }

    public ComplexVector(Vector parent, float x, float y, float z) {
        super(x, y, z);
        this.parent = parent;
    }

    public void recalculatePositions() {
        this.calcX = parent.getX() + x;
        this.calcY = parent.getY() + y;
        this.calcZ = parent.getZ() + z;
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

    @Override
    public float getX() {
        return calcX;
    }

    @Override
    public float getY() {
        return calcY;
    }

    @Override
    public float getZ() {
        return calcZ;
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new ComplexVector(parent, x, y, z);
    }
}
