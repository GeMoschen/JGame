package de.gemo.game.terrain.core;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Player {
    // private Vector2f position, lastPosition;
    // private Vector2f velocity;

    private float playerWidth = 5, playerHeight = 10;
    private float x, y, velX = 0, velY = 0;
    private boolean lookRight = true;
    private TerrainCore core;

    private float shootAngle = 0f;

    private boolean[] movement = new boolean[5];
    private boolean onGround, topBlocked;

    private final static int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, SPACE = 4;

    public Player(Vector2f position) {
        x = position.getX();
        y = position.getY();
        this.core = (TerrainCore) GameEngine.INSTANCE;
    }

    public Player(float x, float y) {
        this(new Vector2f(x, y));
    }

    public void jump() {
        if ((this.onGround) && (!this.topBlocked) && (this.velY > -500.0F)) {
            this.velY -= 0.23F;
            float jumpX = 0.115f;
            if (this.lookRight) {
                this.velX = jumpX;
            } else {
                this.velX = -jumpX;
            }
        }
    }

    public void update(int delta) {
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
        float f1 = this.velX;
        float f2 = this.velY;

        f2 += +0.0012F * delta;
        this.velY = f2;

        this.x = (x + f1 * delta);

        if ((!onGround) || (f2 <= 0.0F))
            this.y = (this.y + f2 * delta);

        // movement
        if (onGround) {
            float maxX = 0.035f;
            if (this.movement[LEFT]) {
                if (this.lookRight) {
                    this.shootAngle = -this.shootAngle;
                }
                this.lookRight = false;
                velX = -maxX;
            } else if (velX < 0)
                velX *= 0.5f; // slow down side-ways velocity if we're not
                              // moving
                              // left

            if (this.movement[RIGHT]) {
                if (!this.lookRight) {
                    this.shootAngle = -this.shootAngle;
                }
                this.lookRight = true;
                velX = maxX;
            } else if (velX > 0)
                velX *= 0.5f;
        }

        // Collision detection/handling
        // Loop along each edge of the square until we find a solid pixel
        // if there is one, we find out if there's any adjacent to it (loop
        // perpendicular from that pixel into the box)
        // Once we hit empty space, we move the box to that empty space

        onGround = false;
        for (int bottomX = (int) ((int) x - playerWidth / 2); bottomX <= (int) x + playerWidth / 2; bottomX++) {
            if (core.isPixelSolid(bottomX, (int) ((int) y + playerHeight / 2 + 1)) && (velY > 0)) {
                onGround = true;
                for (int yCheck = (int) ((int) y + playerHeight / 4); yCheck < (int) y + playerHeight / 2; yCheck++) {
                    if (core.isPixelSolid(bottomX, yCheck)) {
                        y = yCheck - playerHeight / 2;
                        break;
                    }
                }
                if (velY > 0)
                    velY *= -0.1f;
            }
        }

        topBlocked = false;
        // start with the top edge
        for (int topX = (int) ((int) x - playerWidth / 2); topX <= (int) x + playerWidth / 2; topX++) {
            if (core.isPixelSolid(topX, (int) ((int) y - playerHeight / 2 - 1))) { // if
                                                                                   // the
                                                                                   // pixel
                                                                                   // is
                                                                                   // solid
                topBlocked = true;
                if (velY < 0) {
                    velY *= -0.1f;
                }
            }
        }
        // loop left edge
        if (velX < 0) {
            for (int leftY = (int) ((int) y - playerHeight / 2); leftY <= (int) y + playerHeight / 2; leftY++) {
                if (core.isPixelSolid((int) ((int) x - playerWidth / 2), leftY)) {
                    // next move from the edge to the right, inside the box
                    // (stop it at 1/4th the player width)
                    for (int xCheck = (int) ((int) x - playerWidth / 4); xCheck < (int) x - playerWidth / 2; xCheck--) {
                        if (core.isPixelSolid(xCheck, leftY)) {
                            x = xCheck + playerWidth / 2; // push the block over
                            break;
                        }
                    }
                    if (leftY > y && !topBlocked) {
                        y -= 1;
                    } else {
                        velX *= 0.4f;
                        x += 0.1f;
                    }
                }
            }
        }
        // do the same for the right edge
        if (velX > 0) {
            for (int rightY = (int) ((int) y - playerHeight / 2); rightY <= (int) y + playerHeight / 2; rightY++) {
                if (core.isPixelSolid((int) ((int) x + playerWidth / 2), rightY)) {
                    for (int xCheck = (int) ((int) x + playerWidth / 4); xCheck < (int) x + playerWidth / 2 + 1; xCheck++) {
                        if (core.isPixelSolid(xCheck, rightY)) {
                            x = xCheck - playerWidth / 2;
                            break;
                        }
                    }
                    if (rightY > y && !topBlocked) {
                        y -= 1;
                    } else {
                        velX *= 0.4f;
                        x -= 0.1f;
                    }
                }
            }
        }

        // Boundary Checks
        if (x < 0 && velX < 0) {
            x -= x;
            velX *= -1;
        }
        if (y < 0 && velY < 0) {
            y -= y;
            velY *= -1;
        }
        if (x > 2048 && velX > 0) {
            x += 2048 - x;
            velX *= -1;
        }
        if (y + playerHeight / 2 > 768 && velY > 0) {
            y += 768 - y - playerHeight / 2;
            velY = 0;
            onGround = true;
        }

    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);

            glTranslatef(this.x, this.y, 0);
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
                glColor4f(1, 0, 0, 1);
                glBegin(GL_LINES);
                {
                    glVertex2f(0, 0);
                    glVertex2f(0, -20);
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

    // ///////////////////////////////////////////////////////////////
    //
    // PhysicsObject
    //
    // ///////////////////////////////////////////////////////////////
}
