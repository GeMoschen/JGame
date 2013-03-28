package de.gemo.game.skyroads;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import de.gemo.game.engine.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class Player {
    private Vector3f position = new Vector3f(), velocity = new Vector3f(), camPos;
    private Model model;

    private Level level;

    public Player(float x, float y, float z, Vector3f camPos, Model model, Level level) {
        this.camPos = camPos;
        this.setPosition(x, y, z);
        this.model = model;
        this.level = level;
    }

    public Model getModel() {
        return model;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.set(x, y, z);
    }

    public void addX(float x) {
        int z = this.getCubeZ();
        boolean hasCube = this.level.hasCube(this.getCubelX(), z) || this.level.hasCube(this.getCubemX(), z) || this.level.hasCube(this.getCuberX(), z);
        if (hasCube && this.position.y < -0.5f) {
            return;
        }
        this.position.x += x;
        this.camPos.x -= x;
    }

    public void addY(float y) {
        int z = this.getCubeZ();
        boolean hasCube = this.level.hasCube(this.getCubelX(), z) || this.level.hasCube(this.getCubemX(), z) || this.level.hasCube(this.getCuberX(), z);
        boolean wasOnGround = (this.position.y < 0.6f) && (this.position.y > -0.01f);

        this.position.y += y;
        this.camPos.y -= y;

        if ((this.position.y < 0.0F) && (this.isOnGround())) {
            this.position.y = 0.0F;
            this.camPos.y = 5.0F;
            this.velocity.y = (-this.velocity.y * 0.65F);
        }

        boolean isOnGround = (this.position.y < -0.01f);
        if (isOnGround && wasOnGround && hasCube) {
            this.position.y = 0.0F;
            this.camPos.y = 5.0F;
        }
    }

    public void addZ(float z) {
        this.position.z += z;
        this.camPos.z -= z;
    }

    public void handleInput(boolean jump, boolean left, boolean right, boolean up, boolean down) {
        if (up && !down) {
            this.performThrottle();
        } else if (down && !up) {
            this.performBreak();
        }

        if (left && !right) {
            this.performStrafe(true);
        } else if (right && !left) {
            this.performStrafe(false);
        }

        if (jump && this.performJump()) {
            return;
        }
    }

    private void performThrottle() {
        if (this.velocity.z > 0f) {
            addVelocityZ(-(0.003f - getVelocityZ() * 0.03f));
        } else {
            addVelocityZ(-(0.001f - getVelocityZ() * 0.03f));
        }
    }

    private void performBreak() {
        if (this.velocity.z < 0f) {
            addVelocityZ(+(0.003f + getVelocityZ() * 0.03f));
        } else {
            addVelocityZ(+(0.001f + getVelocityZ() * 0.03f));
        }
    }

    private void performStrafe(boolean left) {
        if (this.position.y < -0f) {
            // this.velocity.x = 0f;
            return;
        }
        float speed = 0.009f;
        if (left) {
            addVelocityX(-speed);
        } else {
            addVelocityX(speed);
        }
    }

    private boolean performJump() {
        if (isOnGround()) {
            setVelocityY(0.034f);
            return true;
        }
        return false;
    }

    private boolean isOnGround() {
        int z = this.getCubeZ();
        boolean hasCube = this.level.hasCube(this.getCubelX(), z) || this.level.hasCube(this.getCubemX(), z) || this.level.hasCube(this.getCuberX(), z);
        return hasCube && this.position.y < 0.6f && this.position.y > -0.6f;
    }

    public void addVelocityX(float x) {
        this.velocity.x += x;
        float max = 0.030f;
        if (this.velocity.x > max) {
            this.velocity.x = max;
        }
        if (this.velocity.x < -max) {
            this.velocity.x = -max;
        }
    }

    public void addVelocityY(float y) {
        this.velocity.y += y;
        if (this.velocity.y >= 0.032f) {
            this.velocity.y = 0.031999f;
        }
    }

    public void setVelocityX(float x) {
        this.velocity.x = x;
    }

    public void setVelocityY(float y) {
        this.velocity.y = y;
    }

    public void setVelocityZ(float z) {
        this.velocity.z = z;
    }

    public float getVelocityX() {
        return this.velocity.x;
    }

    public float getVelocityY() {
        return this.velocity.y;
    }

    public float getVelocityZ() {
        return this.velocity.z;
    }

    public void addVelocityZ(float z) {
        this.velocity.z += z;
        float max = 0.15f;
        if (this.velocity.z > max) {
            this.velocity.z = max;
        }
        if (this.velocity.z < -max) {
            this.velocity.z = -max;
        }
    }

    public void update(int delta) {
        if (this.velocity.x < 0.00007f && this.velocity.x > -0.00007f) {
            this.velocity.x = 0;
        }
        if (this.velocity.z > -0.0001f && this.velocity.z < 0.0001f) {
            this.velocity.z = 0;
        }

        this.addX(this.velocity.x * delta);
        this.addY(this.velocity.y * delta);
        this.addZ(this.velocity.z * delta);
        if (this.velocity.y > 0 || (!this.isOnGround())) {
            if (this.velocity.y > -0.5f) {
                this.velocity.y -= 0.0025f;
            }
        }
        this.velocity.x *= 0.60f;
        this.velocity.z *= 0.975f;

        int newZ = this.getCubeZ();
        boolean hasCube = this.level.hasCube(this.getCubelX(), newZ) || this.level.hasCube(this.getCubemX(), newZ) || this.level.hasCube(this.getCuberX(), newZ);
        if (hasCube) {
            if (this.position.y < -0.75f && this.position.y > -2.45f) {
                this.velocity.z = 0;
            }
        }
    }

    public void render() {
        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z -0.2f);
            glRotatef(180f, 0, 1, 0);
            float angle = 15f*this.getVelocityX();
            if(angle > 40) 
                angle = 40;
            
            if(angle < -40)
                angle = -40;
            glRotatef(angle, 0, 0, 1);
            glColor4f(0.5f, 0.5f, 0.5f, 1f);
            model.render();
        }
        glPopMatrix();
    }

    public float getX() {
        return this.position.x;
    }

    public float getY() {
        return this.position.y;
    }

    public float getZ() {
        return this.position.z;
    }

    private int getCubelX() {
        float left = this.position.x - 1f;
        int lX = 0;
        if (left > 0) {
            lX = (int) ((left + 2.25f) / 4f);
        } else {
            lX = (int) ((left - 1.8f) / 4f);
        }
        return lX;
    }

    private int getCubemX() {
        int mX = 0;
        if (this.position.x > -1) {
            mX = (int) ((this.position.x + 2) / 4f);
        } else {
            mX = (int) ((this.position.x - 2) / 4f);
        }
        return mX;
    }

    private int getCuberX() {
        float right = this.position.x + 1f;
        int rX = 0;
        if (right > 0) {
            rX = (int) ((right + 1.8f) / 4f);
        } else {
            rX = (int) ((right - 2.15f) / 4f);
        }
        return rX;
    }

    public int getCubeX() {
        int lX = this.getCubelX();
        int rX = this.getCuberX();
        int mX = this.getCubemX();

        // all the same...
        if (mX == lX && mX == rX) {
            return mX;
        }

        // mX == rX => return lX
        if (mX == rX) {
            return lX;
        }

        // mX == lX => return rX
        if (mX == lX) {
            return rX;
        }

        return mX;
    }

    public int getCubeY() {
        return (int) ((this.position.y - 5) / 10f);
    }

    public int getCubeZ() {
        return (int) -((this.position.z - 7.25f) / 10f);
    }
}
