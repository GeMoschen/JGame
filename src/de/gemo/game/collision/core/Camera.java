package de.gemo.game.collision.core;

import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Camera {

    private Vector3f position;
    private float pitch, yaw, roll;

    public static Camera $;

    public Camera() {
        $ = this;
        this.position = new Vector3f(+530, -250, +420); // x, y, z
        this.addPitch(45);
        this.addYaw(135);
        this.roll = 0;
        this.goUp(0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addYaw(float yaw) {
        this.yaw += yaw;
        if (this.yaw >= 360) {
            this.yaw -= 360;
        }
        if (this.yaw < 0) {
            this.yaw += 360;
        }
    }

    public void addRoll(float roll) {
        this.roll += roll;
    }

    public void addPitch(float pitch) {
        this.setPitch(this.pitch + pitch);
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        if (this.pitch > 80) {
            this.pitch = 80;
        }
    }

    public void move(Vector3f move) {
        this.position.move(move.getX(), move.getY(), move.getZ());
    }

    public void move(float x, float y, float z) {
        this.position.move(x, y, z);
    }

    // moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance) {
        this.position.addX(-(distance * (float) Math.sin(Math.toRadians(yaw))));
        this.position.addZ(+(distance * (float) Math.cos(Math.toRadians(yaw))));
    }

    // moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance) {
        this.position.addX(+(distance * (float) Math.sin(Math.toRadians(yaw))));
        this.position.addZ(-(distance * (float) Math.cos(Math.toRadians(yaw))));
    }

    // strafes the camera left relitive to its current rotation (yaw)
    public void strafeLeft(float distance) {
        this.position.addX(-(distance * (float) Math.sin(Math.toRadians(yaw - 90))));
        this.position.addZ(+(distance * (float) Math.cos(Math.toRadians(yaw - 90))));
    }

    // strafes the camera right relitive to its current rotation (yaw)
    public void strafeRight(float distance) {
        this.position.addX(-(distance * (float) Math.sin(Math.toRadians(yaw + 90))));
        this.position.addZ(+(distance * (float) Math.cos(Math.toRadians(yaw + 90))));
    }

    public void goUp(float distance) {
        this.position.move(0, distance, 0);

        if (this.position.getY() > -10) {
            this.position.setY(-10);
        }
        if (this.position.getY() < -800) {
            this.position.setY(-800);
        }

        this.setPitch((-this.position.getY()) / 8);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void lookThrough() {
        // roll
        glRotatef(this.roll, 0f, 0f, 1f);
        // pitch
        glRotatef(this.pitch, 1f, 0f, 0f);
        // yaw
        glRotatef(this.yaw, 0f, 1f, 0f);
        // translate to position
        glTranslatef(this.position.getX(), this.position.getY(), this.position.getZ());
    }
}
