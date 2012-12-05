package de.gemo.game.collision;

public class EasyVector {
    protected double x, y, z;

    public static EasyVector FromPoint(double x, double y) {
        return new EasyVector(x, y, 0);
    }

    public static EasyVector FromPoint(double x, double y, double z) {
        return new EasyVector(x, y, z);
    }

    public EasyVector() {
        this(0, 0, 0);
    }

    public EasyVector(double x, double y) {
        this(x, y, 0);
    }

    public EasyVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y
     *            the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * @param z
     *            the z to set
     */
    public void setZ(double z) {
        this.z = z;
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }

}
