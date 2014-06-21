package de.gemo.game.terrain.entities;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Player implements IPhysicsObject, IRenderObject {

    private float playerWidth = 5, playerHeight = 10;
    private Vector2f position, velocity;
    private boolean lookRight = true;
    private World world;

    private float shootAngle = 0f;
    private float shootPower = 0f;

    private boolean[] movement = new boolean[5];
    private boolean onGround, topBlocked;
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
            this.velocity.setY(-0.1f * GameEngine.INSTANCE.getCurrentDelta());
            System.out.println("JUMP");
            // float jumpX = 0.07f * GameEngine.INSTANCE.getCurrentDelta();
            if (this.lookRight) {
                // this.velocity.setX(jumpX);
            } else {
                // this.velocity.setX(-jumpX);
            }
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
        int bottomY = (int) (this.position.getY() + this.playerHeight);
        for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
            if (this.world.isPixelSolid((int) (this.position.getX() + x), bottomY)) {
                return false;
            }
        }
        return true;
    }

    public Vector2f getCollidingNormal() {
        int bottomY = (int) (this.position.getY() + this.playerHeight);
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

        // friction
        vX *= 0.95f;
        vY *= 0.95f;

        // gravity
        boolean canFall = this.canFall();
        if (canFall) {
            vY += 0.012F * delta;
            vY = this.getMaxAdvanceY(vY);
        } else {
            // is on ground
            vY = 0f;
            Vector2f normal = this.getCollidingNormal();
            if (normal.getY() > -0.80f) {
                vX += (normal.getX() / 4f);
            }

            if (this.movement[LEFT] && !this.movement[RIGHT]) {
                vX = -0.02f * delta;
            }
            if (this.movement[RIGHT] && !this.movement[LEFT]) {
                vX = 0.02f * delta;
            }
        }

        vX = this.getMaxAdvanceX(vX);

        this.position.move(vX, vY);
        this.velocity.set(vX, vY);

        this.onGround = !this.canFall();
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
            int bottomY = (int) (this.position.getY() + this.playerHeight);
            float advanceY = 0f;
            for (int y = bottomY; advanceY <= vY; advanceY++) {
                for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
                    if (this.world.isPixelSolid((int) (this.position.getX() + x), (int) y)) {
                        return advanceY - 2f;
                    }
                }
            }
        } else if (vY < 0) {
            int topY = (int) (this.position.getY() - this.playerHeight);
            float advanceY = 0f;
            for (int y = topY; advanceY >= vY; advanceY--) {
                for (int x = (int) -this.playerWidth; x <= this.playerWidth; x++) {
                    if (this.world.isPixelSolid((int) (this.position.getX() + x), (int) y)) {
                        return advanceY + 2f;
                    }
                }
            }
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
