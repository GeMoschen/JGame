package de.gemo.game.terrain.core;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Player implements IPhysicsObject {

    private float playerWidth = 5, playerHeight = 10;
    private Vector2f position, velocity;
    private boolean lookRight = true;
    private TerrainCore core;

    private float shootAngle = 0f;

    private boolean[] movement = new boolean[5];
    private boolean onGround, topBlocked;

    private final static int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, SPACE = 4;

    public Player(Vector2f position) {
        this.position = position.clone();
        this.velocity = new Vector2f(0, 0);
        this.core = (TerrainCore) GameEngine.INSTANCE;
    }

    public Player(float x, float y) {
        this(new Vector2f(x, y));
    }

    public void jump() {
        if ((this.onGround) && (!this.topBlocked) && (this.velocity.getY() > -500.0F)) {
            this.velocity.setY(this.velocity.getY() - 0.23f);
            float jumpX = 0.115f;
            if (this.lookRight) {
                this.velocity.setX(jumpX);
            } else {
                this.velocity.setX(-jumpX);
            }
        }
    }

    @Override
    public void updatePhysics(int delta) {
        // shoot angle
        if ((this.movement[UP] && this.lookRight) || (this.movement[DOWN] && !this.lookRight)) {
            if ((this.lookRight && this.shootAngle > 0) || (!this.lookRight && this.shootAngle > -160)) {
                this.shootAngle -= 2f;
            }
        } else if ((this.movement[DOWN] && this.lookRight) || (this.movement[UP] && !this.lookRight)) {
            if ((!this.lookRight && this.shootAngle < 0) || (this.lookRight && this.shootAngle < 160)) {
                this.shootAngle += 2f;
            }
        }

        // gravity
        float f1 = this.velocity.getX();
        float f2 = this.velocity.getY();

        f2 += +0.0012F * delta;
        this.velocity.setY(f2);

        this.position.setX(this.position.getX() + f1 * delta);

        if ((!onGround) || (f2 <= 0.0F))
            this.position.setY(this.position.getY() + f2 * delta);

        // movement
        if (onGround) {
            float maxX = 0.035f;
            if (this.movement[LEFT]) {
                if (this.lookRight) {
                    this.shootAngle = -this.shootAngle;
                }
                this.lookRight = false;
                this.velocity.setX(-maxX);
            } else if (this.velocity.getX() < 0)
                this.velocity.setX(this.velocity.getX() * 0.5f); // slow down
                                                                 // side-ways
                                                                 // velocity if
                                                                 // we're not
            // moving
            // left

            if (this.movement[RIGHT]) {
                if (!this.lookRight) {
                    this.shootAngle = -this.shootAngle;
                }
                this.lookRight = true;
                this.velocity.setX(maxX);
            } else if (this.velocity.getX() > 0)
                this.velocity.setX(this.velocity.getX() * 0.5f);
        }

        // Collision detection/handling
        // Loop along each edge of the square until we find a solid pixel
        // if there is one, we find out if there's any adjacent to it (loop
        // perpendicular from that pixel into the box)
        // Once we hit empty space, we move the box to that empty space

        onGround = false;
        for (int bottomX = (int) ((int) this.position.getX() - playerWidth / 2); bottomX <= (int) this.position.getX() + playerWidth / 2; bottomX++) {
            if (core.isPixelSolid(bottomX, (int) ((int) this.position.getY() + playerHeight / 2 + 1)) && (this.velocity.getY() > 0)) {
                onGround = true;
                for (int yCheck = (int) ((int) this.position.getY() + playerHeight / 4); yCheck < (int) this.position.getY() + playerHeight / 2; yCheck++) {
                    if (core.isPixelSolid(bottomX, yCheck)) {
                        this.position.setY(yCheck - playerHeight / 2);
                        break;
                    }
                }
                if (this.velocity.getY() > 0) {
                    this.velocity.setY(this.velocity.getY() * -0.1f);
                }
            }
        }

        topBlocked = false;
        // start with the top edge
        for (int topX = (int) ((int) this.position.getX() - playerWidth / 2); topX <= (int) this.position.getX() + playerWidth / 2; topX++) {
            if (core.isPixelSolid(topX, (int) ((int) this.position.getY() - playerHeight / 2 - 1))) { // if
                // the
                // pixel
                // is
                // solid
                topBlocked = true;
                if (this.velocity.getY() < 0) {
                    this.velocity.setY(this.velocity.getY() * -0.1f);
                }
            }
        }
        // loop left edge
        if (this.velocity.getX() < 0) {
            for (int leftY = (int) ((int) this.position.getY() - playerHeight / 2); leftY <= (int) this.position.getY() + playerHeight / 2; leftY++) {
                if (core.isPixelSolid((int) ((int) this.position.getX() - playerWidth / 2), leftY)) {
                    // next move from the edge to the right, inside the box
                    // (stop it at 1/4th the player width)
                    for (int xCheck = (int) ((int) this.position.getX() - playerWidth / 4); xCheck < (int) this.position.getX() - playerWidth / 2; xCheck--) {
                        if (core.isPixelSolid(xCheck, leftY)) {
                            this.position.setX(xCheck + playerWidth / 2f); // push
                                                                           // the
                                                                           // block
                                                                           // over
                            break;
                        }
                    }
                    if (leftY > this.position.getY() && !topBlocked) {
                        this.position.setY(this.position.getY() - 1f);
                    } else {
                        this.velocity.setX(this.velocity.getX() * 0.4f);
                        this.position.setX(this.position.getX() + 0.1f);
                    }
                }
            }
        }
        // do the same for the right edge
        if (this.velocity.getX() > 0) {
            for (int rightY = (int) ((int) this.position.getY() - playerHeight / 2); rightY <= (int) this.position.getY() + playerHeight / 2; rightY++) {
                if (core.isPixelSolid((int) ((int) this.position.getX() + playerWidth / 2), rightY)) {
                    for (int xCheck = (int) ((int) this.position.getX() + playerWidth / 4); xCheck < (int) this.position.getX() + playerWidth / 2 + 1; xCheck++) {
                        if (core.isPixelSolid(xCheck, rightY)) {
                            this.position.setX(xCheck - playerWidth / 2f);
                            break;
                        }
                    }
                    if (rightY > this.position.getY() && !topBlocked) {
                        this.position.setY(this.position.getY() - 1f);
                    } else {
                        this.velocity.setX(this.velocity.getX() * 0.4f);
                        this.position.setX(this.position.getX() - 0.1f);
                    }
                }
            }
        }

        // Boundary Checks
        if (this.position.getX() < 0 && this.velocity.getX() < 0) {
            this.position.setX(0);

            this.velocity.setX(this.velocity.getX() * -1f);
        }
        if (this.position.getY() < 0 && this.velocity.getY() < 0) {
            this.position.setY(0);
            this.velocity.setY(this.velocity.getY() * -1f);
        }
        if (this.position.getX() > 2048 && this.velocity.getX() > 0) {
            this.position.setX(2048);
            this.velocity.setX(this.velocity.getX() * -1f);
        }
        if (this.position.getY() + playerHeight / 2 > 768 && this.velocity.getY() > 0) {
            this.position.setY(768 - this.position.getY() - playerHeight / 2f);
            this.velocity.setY(0);
            onGround = true;
        }

    }

    public void render() {
        glPushMatrix();
        {
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

            // shootangle
            glPushMatrix();
            {
                glRotatef((float) this.shootAngle, 0, 0, 1);
                glTranslatef(0, -50, 0);
                glColor4f(1, 0, 0, 1);
                glBegin(GL_LINES);
                {
                    glVertex2f(+5, 0);
                    glVertex2f(+15, 0);

                    glVertex2f(-5, 0);
                    glVertex2f(-15, 0);

                    glVertex2f(0, +5);
                    glVertex2f(0, +15);

                    glVertex2f(0, -5);
                    glVertex2f(0, -15);
                }
                glEnd();
            }
            glPopMatrix();
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
