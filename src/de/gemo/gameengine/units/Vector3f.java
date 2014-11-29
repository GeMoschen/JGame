package de.gemo.gameengine.units;

import java.io.*;

import static org.lwjgl.opengl.GL11.*;

public class Vector3f implements Serializable {

    private static final long serialVersionUID = 1870562186338212289L;

    protected float x, y, z;
    private boolean dirty = true;
    private float length = 0f;

    private float yaw = 0f, roll = 0f, pitch = 0f;

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

    public Vector3f normalize() {
        return Vector3f.normalize(this);
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
        float zDist = other.z - this.z;
        return (float) Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    public float getReducedDistance(Vector3f other) {
        float xDist = other.x - this.x;
        float yDist = other.y - this.y;
        float zDist = other.z - this.z;
        return (float) xDist + yDist + zDist;
    }

    public void rotate(Vector3f vector, float yaw, float roll, float pitch) {
        // calculate new angles
        float newYaw = this.yaw + yaw;
        float newRoll = this.roll + roll;
        float newPitch = this.pitch + pitch;

        // revert yaw and pitch
        this.doYaw(vector, -this.yaw);
        this.doPitch(vector, -this.pitch);
        this.doRoll(vector, -this.roll);

        // rotate to new angles
        this.doRoll(vector, newRoll);
        this.doPitch(vector, newPitch);
        this.doYaw(vector, newYaw);
    }

    public void roll(Vector3f vector, float angle) {
        this.rotate(vector, 0, angle, 0);
    }

    public void yaw(Vector3f vector, float angle) {
        this.rotate(vector, angle, 0, 0);
    }

    public void pitch(Vector3f vector, float angle) {
        this.rotate(vector, 0, 0, angle);
    }

    private void doRoll(Vector3f vector, float angle) {
        double rad = -Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (y - vector.getY()));
        double tempy = vector.getY() + (sin * (x - vector.getX()) + cos * (y - vector.getY()));

        x = (float) tempx;
        y = (float) tempy;
        this.roll += angle;
        this.dirty = true;
    }

    private void doYaw(Vector3f vector, float angle) {
        double rad = Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double tempx = vector.getX() + (cos * (x - vector.getX()) - sin * (z - vector.getZ()));
        double tempz = vector.getZ() + (sin * (x - vector.getX()) + cos * (z - vector.getZ()));

        x = (float) tempx;
        z = (float) tempz;
        this.yaw += angle;
        this.dirty = true;
    }

    private void doPitch(Vector3f vector, float angle) {
        double rad = Math.toRadians(angle);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double tempy = vector.getY() + (cos * (y - vector.getY()) - sin * (z - vector.getZ()));
        double tempz = vector.getZ() + (sin * (y - vector.getY()) + cos * (z - vector.getZ()));

        y = (float) tempy;
        z = (float) tempz;
        this.pitch += angle;
        this.dirty = true;
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

    public void add(float x, float y, float z) {
        this.move(x, y, z);
    }

    public void addX(float x) {
        this.move(x, 0, 0);
    }

    public void addY(float y) {
        this.move(0, y, 0);
    }

    public void addZ(float z) {
        this.move(0, 0, z);
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
     * @return the yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * @return the roll
     */
    public float getRoll() {
        return roll;
    }

    /**
     * @return the pitch
     */
    public float getPitch() {
        return pitch;
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
        float x = vector.getX() - this.getX();
        float y = vector.getY() - this.getY();
        return Math.sqrt(x * x + y * y);
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

    @Override
    public boolean equals(Object obj) {
        if (this != obj) {
            return false;
        }
        if (obj != null && obj instanceof Vector3f) {
            Vector3f other = (Vector3f) obj;
            return other.x == this.x && other.y == this.y && other.z == this.z;
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
        if (dest == null) {
            dest = new Vector3f();
        }
        dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
        return dest;
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
        if (dest == null) {
            dest = new Vector3f();
        }
        dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
        return dest;
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

    /**
     * The cross product of two vectors is calculated as ; x = v1.y * v2.z -
     * v1.z * v2.y ; y = v1.z * v2.x - v1.x * v2.z ; z = v1.x * v2.y - v1.y *
     * v2.x
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @return left x right
     */
    public static Vector3f cross(Vector3f left, Vector3f right) {
        return Vector3f.cross(left, right, null);
    }

    /**
     * The cross product of two vectors is calculated as ; x = v1.y * v2.z -
     * v1.z * v2.y ; y = v1.z * v2.x - v1.x * v2.z ; z = v1.x * v2.y - v1.y *
     * v2.x
     * 
     * @param left
     *            The LHS vector
     * @param right
     *            The RHS vector
     * @return left x right
     */
    public static Vector3f cross(Vector3f left, Vector3f right, Vector3f result) {
        if (result == null) {
            result = new Vector3f();
        }
        result.setX(left.y * right.z - left.z * right.y);
        result.setY(left.z * right.x - left.x * right.z);
        result.setZ(left.x * right.y - left.y * right.x);
        return result;
    }

    public static Vector3f normalize(Vector3f vector) {
        float length = vector.getLength();
        return new Vector3f(vector.x / length, vector.y / length, vector.z / length);
    }
}
