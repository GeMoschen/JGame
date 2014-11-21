package de.gemo.game.fov.core;

import de.gemo.gameengine.units.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Camera {

    private Vector3f position, rotation;

    public Camera() {
        this.position = new Vector3f(0, 0, -700); // x, y, z
        this.rotation = new Vector3f(0, 0, 0); // yaw, roll, pitch
    }

    public float getYaw() {
        return this.rotation.getX();
    }

    public float getRoll() {
        return this.rotation.getY();
    }

    public float getPitch() {
        return this.rotation.getZ();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addYaw(float yaw) {
        this.rotation.move(yaw, 0, 0);
    }

    public void addRoll(float roll) {
        this.rotation.move(0, roll, 0);
    }

    public void addPitch(float pitch) {
        this.rotation.move(0, 0, pitch);
    }

    public void move(Vector3f move) {
        this.position.move(move.getX(), move.getY(), move.getZ());
    }

    public void move(float x, float y, float z) {
        this.position.move(x, y, z);
    }

    // moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance) {
        this.position.move(-(distance * (float) Math.sin(Math.toRadians(this.rotation.getY()))), 0, +(distance * (float) Math.cos(Math.toRadians(this.rotation.getY()))));
    }

    // moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance) {
        this.position.move((distance * (float) Math.sin(Math.toRadians(this.rotation.getY()))), 0, -(distance * (float) Math.cos(Math.toRadians(this.rotation.getY()))));
    }

    // strafes the camera left relitive to its current rotation (yaw)
    public void strafeLeft(float distance) {
        this.position.move(+(distance * (float) Math.cos(Math.toRadians(this.rotation.getY()))), 0, +(distance * (float) Math.sin(Math.toRadians(this.rotation.getY()))));
    }

    // strafes the camera right relitive to its current rotation (yaw)
    public void strafeRight(float distance) {
        this.position.move(-(distance * (float) Math.cos(Math.toRadians(this.rotation.getY()))), 0, -(distance * (float) Math.sin(Math.toRadians(this.rotation.getY()))));
    }

    public void goUp(float distance) {
        this.position.move(0, distance, 0);
    }

    public void lookThrough() {
        this.rotation.setX(45);

        // glRotatef(this.rotation.getZ(), 0.0f, 0.0f, 1.0f);
        // roatate the yaw around the Y axis
        glRotatef(this.rotation.getY(), 0.0f, 0.5f, .5f);
        // roatate the roll around the Z axis
        glRotatef(this.rotation.getX(), 1.0f, 0.0f, 0.0f);
        // roatate the pitch around the X axis

        // translate to the position vector's location
        glTranslatef(this.position.getX(), this.position.getY(), this.position.getZ());
    }
}
