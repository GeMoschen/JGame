package de.gemo.game.collision.core;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.util.glu.*;
import org.newdawn.slick.*;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class CollisionCore extends GameEngine {

    private Camera camera = new Camera();
    private Vector2f mouseRightDownVector = new Vector2f();
    private Vector2f mouseLeftDownVector = new Vector2f();
    private Vector3f nearVector = null, farVector = null, collisionVector = null;

    private Hitbox3D box, box2;
    private int DL_STATIC_WORLD = -1;

    public CollisionCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        this.box = new Hitbox3D(new Vector3f(0, 0, 0), 10, 30, 20);
        this.box2 = new Hitbox3D(new Vector3f(23, 0, 0), 10, 30, 20);

        // create displaylist
        this.DL_STATIC_WORLD = glGenLists(1);
        glNewList(this.DL_STATIC_WORLD, GL_COMPILE);
        this.renderGrid();
        this.renderWorldCenter();
        glEndList();
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        super.onKeyReleased(event);
    }

    @Override
    public void onKeyHold(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_Q) {
            this.camera.addYaw(-1);
        } else if (event.getKey() == Keyboard.KEY_E) {
            this.camera.addYaw(1);
            // } else if (event.getKey() == Keyboard.KEY_W) {
            // this.camera.walkForward(5);
            // } else if (event.getKey() == Keyboard.KEY_S) {
            // this.camera.walkBackwards(5);
            // } else if (event.getKey() == Keyboard.KEY_A) {
            // this.camera.strafeLeft(5);
            // } else if (event.getKey() == Keyboard.KEY_D) {
            // this.camera.strafeRight(5);
        } else if (event.getKey() == Keyboard.KEY_X) {
            this.camera.goUp(1);
        } else if (event.getKey() == Keyboard.KEY_C) {
            this.camera.goUp(-1);
        } else if (event.getKey() == Keyboard.KEY_NUMPAD4) {
            this.box.yaw(-1);
        } else if (event.getKey() == Keyboard.KEY_NUMPAD6) {
            this.box.yaw(+1);
        } else if (event.getKey() == Keyboard.KEY_NUMPAD8) {
            this.box.pitch(-1);
        } else if (event.getKey() == Keyboard.KEY_NUMPAD2) {
            this.box.pitch(+1);
        } else if (event.getKey() == Keyboard.KEY_UP) {
            this.box.move(0, +1, 0);
        } else if (event.getKey() == Keyboard.KEY_DOWN) {
            this.box.move(0, -1, 0);
        } else if (event.getKey() == Keyboard.KEY_LEFT) {
            this.box.move(-1, 0, 0);
        } else if (event.getKey() == Keyboard.KEY_RIGHT) {
            this.box.move(+1, 0, 0);
        } else if (event.getKey() == Keyboard.KEY_A) {
            this.box2.yaw(-1);
        } else if (event.getKey() == Keyboard.KEY_D) {
            this.box2.yaw(+1);
        } else if (event.getKey() == Keyboard.KEY_W) {
            this.box2.pitch(-1);
        } else if (event.getKey() == Keyboard.KEY_S) {
            this.box2.pitch(+1);
        }
        super.onKeyHold(event);
    }

    @Override
    public void onMouseHold(boolean handled, MouseHoldEvent event) {
        float factor = 8;
        if (event.isRightButton()) {
            float distX = (event.getX() - mouseRightDownVector.getX());
            float distY = (int) (event.getY() - mouseRightDownVector.getY());
            if (distY > 0) {
                this.camera.walkBackwards(distY / factor);
            } else if (distY < 0) {
                this.camera.walkForward(-distY / factor);
            }

            if (distX > 0) {
                this.camera.strafeRight(distX / factor);
            } else if (distX < 0) {
                this.camera.strafeLeft(-distX / factor);
            }

            this.renderMouseTemp(this.mouseRightDownVector);
        }

        if (event.isLeftButton()) {
            float distX = (event.getX() - mouseLeftDownVector.getX());
            this.camera.addYaw(distX / (factor * 6));
            this.renderMouseTemp(this.mouseLeftDownVector);
        }
    }

    private void renderMouseTemp(Vector2f vector) {
        // translate to center
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // render hitbox
            glPushMatrix();
            {
                Color.green.bind();
                glBegin(GL_LINE_LOOP);
                glVertex3f(vector.getX() - 2, vector.getY() - 2, 0);
                glVertex3f(vector.getX() + 2, vector.getY() - 2, 0);
                glVertex3f(vector.getX() + 2, vector.getY() + 2, 0);
                glVertex3f(vector.getX() - 2, vector.getY() + 2, 0);
                glEnd();
            }
            glPopMatrix();

            // render hitbox
            glPushMatrix();
            {
                Color.orange.bind();
                glBegin(GL_LINES);
                glVertex3f(vector.getX(), vector.getY(), 0);
                glVertex3f(MouseManager.INSTANCE.getCurrentX(), MouseManager.INSTANCE.getCurrentY(), 0);
                glEnd();
            }
            glPopMatrix();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    @Override
    public void onMouseDown(boolean handled, MouseClickEvent event) {
        if (event.isLeftButton()) {
            mouseLeftDownVector.setX(event.getX());
            mouseLeftDownVector.setY(event.getY());
        }
        if (event.isRightButton()) {
            mouseRightDownVector.setX(event.getX());
            mouseRightDownVector.setY(event.getY());
        }
    }

    @Override
    public void onMouseDrag(boolean handled, MouseDragEvent event) {
        if (event.isMiddleButton()) {
            float distance = 10;
            if (event.getDifY() > 0) {
                this.camera.goUp(distance);
            } else if (event.getDifY() < 0) {
                this.camera.goUp(-distance);
            }
        }
    }

    @Override
    protected void updateGame(int delta) {
    }

    private Vector3f pos = new Vector3f();

    @Override
    protected void renderGame3D() {
        glPushMatrix();
        {
            this.camera.lookThrough();
            glPushMatrix();
            {
                // render world
                glCallList(this.DL_STATIC_WORLD);

                // render boxes
                this.box.render();
                this.box2.render();

                glPushMatrix();
                {
                    glTranslatef(pos.getX(), pos.getY(), pos.getZ());
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_BLEND);
                    glColor4f(0, 1, 0, 1);
                    Sphere sphere = new Sphere();
                    sphere.draw(0.5f, 8, 8);
                }
                glPopMatrix();
            }
            glPopMatrix();

            if (KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_SPACE)) {
                FloatBuffer projection = BufferUtils.createFloatBuffer(16);
                FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
                IntBuffer viewport = BufferUtils.createIntBuffer(16);

                glGetFloat(GL_PROJECTION_MATRIX, projection);
                glGetFloat(GL_MODELVIEW_MATRIX, modelview);
                glGetInteger(GL_VIEWPORT, viewport);
                float win_x = MouseManager.INSTANCE.getCurrentX();
                float win_y = Mouse.getY();

                FloatBuffer near = BufferUtils.createFloatBuffer(3);
                FloatBuffer far = BufferUtils.createFloatBuffer(3);

                GLU.gluUnProject(win_x, win_y, 0f, modelview, projection, viewport, near);
                GLU.gluUnProject(win_x, win_y, 1f, modelview, projection, viewport, far);

                this.nearVector = new Vector3f(near.get(0), near.get(1), near.get(2));
                this.farVector = new Vector3f(far.get(0), far.get(1), far.get(2));
                this.collisionVector = CollisionHelper3D.lineHitsBox(this.nearVector, this.farVector, this.box);
            }
            if (this.nearVector != null) {
                glDisable(GL_LIGHTING);
                glDisable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                // glDisable(GL_DEPTH_TEST);
                glLineWidth(1f);

                glColor4f(1, 0, 0, 1);
                glBegin(GL_LINES);
                glVertex3f(this.nearVector.getX(), this.nearVector.getY(), this.nearVector.getZ());
                glVertex3f(this.farVector.getX(), this.farVector.getY(), this.farVector.getZ());
                glEnd();

                if (this.collisionVector != null) {
                    glColor4f(0, 1, 1, 1);

                    glTranslatef(this.collisionVector.getX(), this.collisionVector.getY(), this.collisionVector.getZ());
                    glLineWidth(2f);
                    glBegin(GL_LINES);
                    glVertex3f(-2, 0, 0);
                    glVertex3f(+2, 0, -0);
                    glEnd();

                    glBegin(GL_LINES);
                    glVertex3f(0, -2, 0);
                    glVertex3f(0, +2, 0);
                    glEnd();

                    glBegin(GL_LINES);
                    glVertex3f(0, 0, -2);
                    glVertex3f(0, 0, +2);
                    glEnd();
                }
            }
        }
        glPopMatrix();
    }

    private void renderGrid() {
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1f);

        glColor4f(1, 1, 1, 0.1f);
        glBegin(GL_LINES);
        {
            for (int x = -500; x <= 500; x += 10) {
                glVertex3f(x, 0, -500);
                glVertex3f(x, 0, 500);
            }

            for (int z = -500; z <= 500; z += 10) {
                glVertex3f(-500, 0, z);
                glVertex3f(500, 0, z);
            }
        }
        glEnd();
    }

    private void renderWorldCenter() {
        int length = 15;
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1.5f);
        glBegin(GL_LINES);
        {
            // X
            glColor4f(1, 0, 0, 1);
            glVertex3f(0, 0, 0);
            glVertex3f(length, 0, 0);

            // Y
            glColor4f(0, 1, 0, 1);
            glVertex3f(0, 0, 0);
            glVertex3f(0, length, 0);

            // Z
            glColor4f(0, 0, 1, 1);
            glVertex3f(0, 0, 0);
            glVertex3f(0, 0, length);
        }
        glEnd();
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        Font font = FontManager.getStandardFont();
        font.drawString(10, 10, "Pitch: " + this.camera.getPitch());
        font.drawString(85, 10, "Yaw: " + this.camera.getYaw());
        font.drawString(200, 10, "FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS());
        font.drawString(300, 10, "Height: " + (-this.camera.getPosition().getY()));

        int base = 20;
        font.drawString(10, base, "________________________________________");
        font.drawString(10, base + 15, "                       Controls");
        font.drawString(10, base + 20, "________________________________________");
        font.drawString(10, base + 35, "Rotate:   Q/E or left MB & move");
        font.drawString(10, base + 48, "Move:     W/A/S/D or right MB & move");
        font.drawString(10, base + 61, "Height:    X/C or middle MB & move");

        font.drawString(10, base + 81, "AABB colliding: " + CollisionHelper3D.collides(this.box.getAABB(), this.box2.getAABB()));
        font.drawString(10, base + 94, "Vertex colliding: " + CollisionHelper3D.collides(this.box, this.box2));
        font.drawString(10, base + 107, "Center colliding: " + CollisionHelper3D.isVectorInHitbox(this.pos, this.box));
    }
}
