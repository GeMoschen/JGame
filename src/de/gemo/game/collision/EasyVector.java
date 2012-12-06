package de.gemo.game.collision;

public class EasyVector {
    protected float x, y, z;

    public static EasyVector FromPoint(float x, float y) {
        return new EasyVector(x, y, 0);
    }

    public static EasyVector FromPoint(float x, float y, float z) {
        return new EasyVector(x, y, z);
    }

    public EasyVector() {
        this(0, 0, 0);
    }

    public EasyVector(float x, float y) {
        this(x, y, 0);
    }

    public EasyVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
    }

}
