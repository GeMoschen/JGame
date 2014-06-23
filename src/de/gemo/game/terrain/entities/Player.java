package de.gemo.game.terrain.entities;

import de.gemo.game.terrain.utils.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Player implements IPhysicsObject, IRenderObject {

    private int playerWidth = 5, playerHeight = 10;
    private Vector2f position, velocity;
    private boolean lookRight = true;
    private World world;

    private float shootAngle = 0f;
    private float shootPower = 0f;

    private boolean[] movement = new boolean[5];
    private boolean onGround;
    private SingleTexture crosshair;

    private final static int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, SPACE = 4;

    public Player(World world, Vector2f position) {
        this.world = world;
        this.position = position.clone();
        this.velocity = new Vector2f(0, 0);
        try {
            this.crosshair = TextureManager.loadSingleTexture("resources/crosshair.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player(World world, float x, float y) {
        this(world, new Vector2f(x, y));
    }

    public void jump() {
        if ((this.onGround)) {
            this.velocity.setY(-0.17f * GameEngine.INSTANCE.getCurrentDelta());
            float jumpX = 0.1f * GameEngine.INSTANCE.getCurrentDelta();
            if (this.lookRight) {
                this.velocity.setX(jumpX);
            } else {
                this.velocity.setX(-jumpX);
            }
            this.onGround = false;
        }
    }

    public void shoot() {
        this.shootPower += (GameEngine.INSTANCE.getCurrentDelta() * 0.0006f);
        if (this.shootPower >= 1) {
            this.shootPower = 0;
        }
    }

    public void resetPower() {
        this.shootPower = 0;
    }

    public boolean canFall() {
        int bottomY = (int) (this.position.getY() + this.playerHeight) + 1;
        for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
            if (this.world.isPixelSolid((int) (this.position.getX() + x), bottomY)) {
                return false;
            }
        }

        bottomY = (int) (this.position.getY() + this.playerHeight) + 2;
        for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
            if (this.world.isPixelSolid((int) (this.position.getX() + x), bottomY)) {
                return false;
            }
        }
        return true;
    }

    public Vector2f getCollidingNormal() {
        int bottomY = (int) (this.position.getY() + this.playerHeight) + 1;
        for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
            if (this.world.isPixelSolid((int) (this.position.getX() + x), bottomY)) {
                return this.world.getNormal((int) (this.position.getX() + x), bottomY);
            }
        }
        return new Vector2f(0, 0);
    }

    @Override
    public void updatePhysics(int delta) {
        // shoot angle
        float rotationSpeed = 0.2f;
        if ((this.movement[UP] && this.lookRight) || (this.movement[DOWN] && !this.lookRight)) {
            if ((this.lookRight && this.shootAngle > 0) || (!this.lookRight && this.shootAngle > -170)) {
                this.shootAngle -= rotationSpeed * delta;
            }
        } else if ((this.movement[DOWN] && this.lookRight) || (this.movement[UP] && !this.lookRight)) {
            if ((!this.lookRight && this.shootAngle < 0) || (this.lookRight && this.shootAngle < 170)) {
                this.shootAngle += rotationSpeed * delta;
            }
        }

        if (this.lookRight && this.shootAngle < 0) {
            this.shootAngle = 0;
        } else if (!this.lookRight && this.shootAngle > 0) {
            this.shootAngle = 0;
        }

        // get velocity
        float vX = this.velocity.getX();
        float vY = this.velocity.getY();

        // vY *= 0.92f;
        //
        // gravity
        boolean canFall = this.canFall();
        if (canFall) {
            vY += (0.015F * delta);
            vY = this.getMaxAdvanceY(vY);
        } else {
            // is on ground.. if vY < 0, we are jumping or flying high
            if (vY > 0) {
                vY = -0.0005f;

                Vector2f normal = this.getCollidingNormal();
                if (normal.getY() > -0.15f) {
                    vX += (normal.getX() / 16f);
                }
            }
        }

        // friction
        if (!this.onGround) {
            vX *= 0.97f;
        } else {
            vX *= 0.1f;
        }

        // if (this.onGround) {
        float walkSpeed = 0.07f;
        if (this.movement[LEFT] && !this.movement[RIGHT]) {
            this.lookRight = false;
            vX = -walkSpeed * delta;
        }
        if (this.movement[RIGHT] && !this.movement[LEFT]) {
            this.lookRight = true;
            vX = +walkSpeed * delta;
        }
        // }

        float maxAdvanceX = this.getMaxAdvanceX(vX);
        this.position.move(maxAdvanceX, vY);
        this.velocity.set(vX, vY);

        if (vX != maxAdvanceX) {
            int maxStepSize = 5;
            if (this.canGoThere(maxStepSize, vX)) {
                int upShift = this.getUpshift(maxStepSize, vX);
                if (upShift != 0) {
                    this.position.move((vX - maxAdvanceX) / (upShift * 2), -upShift);
                }
            }
        }

        this.onGround = !this.canFall();
        if (this.onGround && !this.movement[LEFT] && !this.movement[RIGHT]) {
            this.velocity.setX(0);
        }
    }

    private boolean canGoThere(int steps, float vX) {
        if (this.canFall()) {
            return true;
        }

        int minX = (int) (this.position.getX() + vX - this.playerWidth);
        int maxX = (int) (this.position.getX() + vX + this.playerWidth);

        int bottomY = (int) (this.position.getY() + this.playerHeight - steps);
        for (int x = minX; x <= maxX; x++) {
            for (int currentStep = 1; currentStep <= this.playerHeight * 2; currentStep++) {
                if (this.world.isPixelSolid(x, bottomY - currentStep)) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getUpshift(int steps, float vX) {
        if (this.canFall()) {
            return 0;
        }

        int minX = (int) (this.position.getX() + vX - this.playerWidth);
        int maxX = (int) (this.position.getX() + vX + this.playerWidth + 1);

        int bottomY = (int) (this.position.getY() + this.playerHeight);
        int upShift = 0;
        for (int currentStep = 1; currentStep <= steps; currentStep++) {
            boolean rowFree = true;
            for (int x = minX; x <= maxX; x++) {
                if (this.world.isPixelSolid(x, bottomY - currentStep)) {
                    rowFree = false;
                }
            }
            upShift++;
            if (rowFree) {
                return upShift;
            }
        }

        return steps;
    }

    public void line(int x, int y, int x2, int y2) {
        int w = x2 - x;
        int h = y2 - y;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        if (w < 0)
            dx1 = -1;
        else if (w > 0)
            dx1 = 1;
        if (h < 0)
            dy1 = -1;
        else if (h > 0)
            dy1 = 1;
        if (w < 0)
            dx2 = -1;
        else if (w > 0)
            dx2 = 1;
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        if (!(longest > shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h < 0)
                dy2 = -1;
            else if (h > 0)
                dy2 = 1;
            dx2 = 0;
        }
        int numerator = longest >> 1;
        for (int i = 0; i <= longest; i++) {
            if (this.world.isPixelSolid(x, y)) {
                return;
            }
            world.setPixel(x, y, TerrainType.CRATER, true);
            numerator += shortest;
            if (!(numerator < longest)) {
                numerator -= longest;
                x += dx1;
                y += dy1;
            } else {
                x += dx2;
                y += dy2;
            }
        }
    }

    private float getMaxAdvanceX(float vX) {
        if (vX > 0) {
            int rightX = (int) (this.position.getX() + this.playerWidth);
            float advanceX = 0f;
            for (int x = rightX; advanceX <= vX; advanceX++) {
                for (int y = (int) -this.playerHeight; y <= this.playerHeight; y++) {
                    if (this.world.isPixelSolid((int) (x + advanceX), (int) (this.position.getY() + y))) {
                        return Math.max(0, advanceX - 1f);
                    }
                }
            }
        } else if (vX < 0) {
            int leftX = (int) (this.position.getX() - this.playerWidth);
            float advanceX = 0f;
            for (int x = leftX; advanceX >= vX; advanceX--) {
                for (int y = (int) -this.playerHeight; y <= this.playerHeight; y++) {
                    if (this.world.isPixelSolid((int) (x + advanceX), (int) (this.position.getY() + y))) {
                        return Math.min(advanceX + 1f, 0f);
                    }
                }
            }
        }
        return vX;
    }

    private float getMaxAdvanceY(float vY) {
        if (vY > 0) {
            float bottomY = this.position.getY() + this.playerHeight + 1f;
            float advanceY = vY;
            float canAdvance = vY;
            for (float y = bottomY + advanceY; advanceY >= (-vY); advanceY--) {
                for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
                    if (this.world.isPixelSolid((int) (this.position.getX() + x), (int) y)) {
                        canAdvance = advanceY;
                    }
                }
            }
            if (canAdvance < 0.1f) {
                return 0;
            }
            return canAdvance;
        } else if (vY < 0) {
            float topY = this.position.getY() - this.playerHeight - 1f;
            float advanceY = vY;
            float canAdvance = vY;
            for (float y = topY - advanceY; advanceY <= (-vY); advanceY++) {
                for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
                    if (this.world.isPixelSolid((int) (this.position.getX() + x), (int) y)) {
                        canAdvance = advanceY;
                    }
                }
            }
            if (canAdvance > 0.1f) {
                return 0;
            }
            return canAdvance;
        }
        return vY;
    }

    @Override
    public void render() {
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);

        glTranslatef(this.position.getX(), this.position.getY(), 0);
        glColor4f(1, 0, 0, 1);
        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(-this.playerWidth, -this.playerHeight);
            glVertex2f(+this.playerWidth, -this.playerHeight);
            glVertex2f(+this.playerWidth, +this.playerHeight);
            glVertex2f(-this.playerWidth, +this.playerHeight);
        }
        glEnd();

        // view
        glColor4f(0, 1, 0, 1);
        glBegin(GL_LINES);
        {
            glVertex2f(0, 0);
            if (this.lookRight) {
                glVertex2f(this.playerWidth, 0);
            } else {
                glVertex2f(-this.playerWidth, 0);
            }
        }
        glEnd();

        // crosshair
        float crosshairDistance = 75f;
        float x2 = 15f;

        // powersign
        glPushMatrix();
        {
            glRotatef((float) this.shootAngle, 0, 0, 1);
            glBegin(GL_POLYGON);
            {
                glColor4f(0, 1, 0, 0.6f);
                glVertex2f(0, 0);
                glColor4f(1 * this.shootPower, 1 * (1 - this.shootPower), 0, 0.6f);
                glVertex2f(-x2 * this.shootPower, -crosshairDistance * this.shootPower);
                glVertex2f(-x2 / 1.75f * this.shootPower, (-crosshairDistance - x2 / 4f - x2 / 8f) * this.shootPower);
                glVertex2f(0, (-crosshairDistance - x2 / 2f) * this.shootPower);
                glVertex2f(+x2 / 1.75f * this.shootPower, (-crosshairDistance - x2 / 4f - x2 / 8f) * this.shootPower);
                glVertex2f(+x2 * this.shootPower, -crosshairDistance * this.shootPower);
            }
            glEnd();
        }
        glPopMatrix();

        // crosshair
        glPushMatrix();
        {
            glRotatef((float) this.shootAngle, 0, 0, 1);
            glTranslatef(2 + this.playerWidth, -crosshairDistance + this.playerHeight / 2f, 0);

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            this.crosshair.render(1, 1, 1, 1);
        }
        glPopMatrix();
    }

    public void setMovement(boolean... args) {
        for (int i = 0; i < args.length; i++) {
            this.movement[i] = args[i];
        }
    }

    @Override
    public Vector2f getPosition() {
        return this.position;
    }

    @Override
    public Vector2f getVelocity() {
        return this.velocity;
    }

    @Override
    public void setPosition(Vector2f position) {
        this.position.set(position.getX(), position.getY());

    }

    @Override
    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity.getX(), velocity.getY());
    }

    // ///////////////////////////////////////////////////////////////
    //
    // PhysicsObject
    //
    // ///////////////////////////////////////////////////////////////
}