package de.gemo.game.collision.core;

import java.nio.*;
import java.util.*;

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
    private Vector2f mouseLeftDownVector = new Vector2f();
    private Vector2f mouseMiddleDownVector = new Vector2f();
    private Vector2f mouseRightDownVector = new Vector2f();

    private OOBB[] boxes;
    public OOBB selectedOOB = null;

    public static CollisionCore $;

    private int DL_STATIC_WORLD = -1;
    private boolean collisionAABB = false, collisionHull = false;
    private ArrayList<Vector3f> collisions = new ArrayList<Vector3f>();

    public CollisionCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
        $ = this;
    }

    @Override
    protected void createManager() {
        // set TPS
        this.setTicksPerSecond(20);

        // create boxes
        this.boxes = new OOBB[2];
        this.boxes[0] = new OOBB(new Vector3f(0, 0, 0), 10, 30, 20);
        this.boxes[1] = new OOBB(new Vector3f(23, 0, 0), 10, 30, 20);

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
        // controls for camera
        if (event.getKey() == Keyboard.KEY_Q) {
            this.camera.addYaw(-1);
        } else if (event.getKey() == Keyboard.KEY_E) {
            this.camera.addYaw(1);
        } else if (event.getKey() == Keyboard.KEY_W) {
            this.camera.walkForward(5);
        } else if (event.getKey() == Keyboard.KEY_S) {
            this.camera.walkBackwards(5);
        } else if (event.getKey() == Keyboard.KEY_A) {
            this.camera.strafeLeft(5);
        } else if (event.getKey() == Keyboard.KEY_D) {
            this.camera.strafeRight(5);
        } else if (event.getKey() == Keyboard.KEY_X) {
            this.camera.goUp(1);
        } else if (event.getKey() == Keyboard.KEY_C) {
            this.camera.goUp(-1);
        }

        // controls for selected box
        if (this.selectedOOB != null) {
            if (event.getKey() == Keyboard.KEY_NUMPAD4) {
                this.selectedOOB.doYaw(+1f);
            } else if (event.getKey() == Keyboard.KEY_NUMPAD6) {
                this.selectedOOB.doYaw(-1f);
            } else if (event.getKey() == Keyboard.KEY_NUMPAD8) {
                this.selectedOOB.doPitch(+1f);
            } else if (event.getKey() == Keyboard.KEY_NUMPAD2) {
                this.selectedOOB.doPitch(-1f);
            } else if (event.getKey() == Keyboard.KEY_NUMPAD7) {
                this.selectedOOB.doRoll(-1f);
            } else if (event.getKey() == Keyboard.KEY_NUMPAD9) {
                this.selectedOOB.doRoll(+1f);
            } else if (event.getKey() == Keyboard.KEY_UP) {
                this.selectedOOB.move(0, 0, +.3f);
            } else if (event.getKey() == Keyboard.KEY_DOWN) {
                this.selectedOOB.move(0, 0, -.3f);
            } else if (event.getKey() == Keyboard.KEY_LEFT) {
                this.selectedOOB.move(+.3f, 0, 0);
            } else if (event.getKey() == Keyboard.KEY_RIGHT) {
                this.selectedOOB.move(-.3f, 0, 0);
            } else if (event.getKey() == Keyboard.KEY_PRIOR) {
                this.selectedOOB.move(0, +.3f, 0);
            } else if (event.getKey() == Keyboard.KEY_NEXT) {
                this.selectedOOB.move(0, -.3f, 0);
            } else if (event.getKey() == Keyboard.KEY_NUMPAD5) {
                this.selectedOOB.resetRotation();
            }
        }
        super.onKeyHold(event);
    }

    @Override
    public void onMouseHold(boolean handled, MouseHoldEvent event) {
        float factor = 8;
        if (event.isRightButton() && !KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_LMENU)) {
            float distX = (event.getX() - mouseRightDownVector.getX());
            float distY = (int) (event.getY() - mouseRightDownVector.getY());
            if (distY > 0) {
                this.camera.walkBackwards(distY / (factor));
            } else if (distY < 0) {
                this.camera.walkForward(-distY / (factor));
            }

            if (distX > 0) {
                this.camera.strafeRight(distX / (factor));
            } else if (distX < 0) {
                this.camera.strafeLeft(-distX / (factor));
            }

            this.renderMouseTemp(this.mouseRightDownVector);
        }

        if (event.isLeftButton() && KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_LMENU)) {
            float distX = (event.getX() - mouseLeftDownVector.getX());
            this.camera.addYaw(distX / (factor * 6));
            this.renderMouseTemp(this.mouseLeftDownVector);
        }

        if (event.isRightButton() && KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_LMENU)) {
            float distY = (event.getY() - mouseRightDownVector.getY());
            this.camera.goUp(distY / (factor * 2));
            this.renderMouseTemp(this.mouseRightDownVector);
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
        if (event.isMiddleButton()) {
            mouseMiddleDownVector.setX(event.getX());
            mouseMiddleDownVector.setY(event.getY());
        }
    }

    @Override
    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
        if (event.isLeftButton() && Math.abs(this.mouseLeftDownVector.getX() - event.getX()) < 5 && Math.abs(this.mouseLeftDownVector.getY() - event.getY()) < 5) {
            // normal left click...

            // set perspective
            this.setPerspective();

            // look through camera
            this.camera.lookThrough();

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

            Vector3f nearVector = new Vector3f(near.get(0), near.get(1), near.get(2));
            Vector3f farVector = new Vector3f(far.get(0), far.get(1), far.get(2));

            Vector3f collisionVector = null;
            Vector3f nearestVector = null;
            this.selectedOOB = null;
            for (OOBB oobb : this.boxes) {
                collisionVector = CollisionHelper3D.getLineWithBoxCollision(nearVector, farVector, oobb);
                if (CollisionHelper3D.isVectorInHitbox(collisionVector, oobb)) {
                    if (nearestVector == null || collisionVector.distanceTo(nearVector) <= nearestVector.distanceTo(nearVector)) {
                        this.selectedOOB = oobb;
                        nearestVector = collisionVector;
                    }
                }
            }
        }
        super.onMouseUp(handled, event);
    }

    @Override
    protected void updateGame(int delta) {
    }

    @Override
    protected void tickGame(int delta) {
        this.collisionAABB = CollisionHelper3D.collides(this.boxes[0].getAABB(), this.boxes[1].getAABB());
        this.collisionHull = CollisionHelper3D.collides(this.boxes[0], this.boxes[1]);
        if (this.collisionHull) {
            this.collisions = CollisionHelper3D.testCollides(this.boxes[0], this.boxes[1]);
        } else {
            this.collisions.clear();
        }
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        TrueTypeFont font = FontManager.getStandardFont();
        font.drawString(10, 10, "Pitch: " + this.camera.getPitch());
        font.drawString(130, 10, "Yaw: " + this.camera.getYaw());
        font.drawString(240, 10, "Height: " + (-this.camera.getPosition().getY()));
        font.drawString(370, 10, "FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS());

        int base = 20;
        font.drawString(10, base, "________________________________________");
        font.drawString(10, base + 15, "                 Controls (hold ALT)");
        font.drawString(10, base + 20, "________________________________________");
        font.drawString(10, base + 35, "Rotate:   ALT + Left Mouse + Move");
        font.drawString(10, base + 48, "Height:   ALT + Right Mouse + Move");
        font.drawString(10, base + 61, "Move:     Right Mouse + Move");

        font.drawString(10, base + 81, "AABBs colliding: " + this.collisionAABB);
        font.drawString(10, base + 94, "OOBs colliding: " + this.collisionHull);

        String selectionString = "NONE";
        for (int index = 0; index < this.boxes.length; index++) {
            if (this.selectedOOB == this.boxes[index]) {
                selectionString = "Box " + (index + 1);
                break;
            }
        }
        font.drawString(10, base + 107, "Selected OOB: " + selectionString);
    }

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
                for (OOBB oobb : this.boxes) {
                    oobb.render();
                }
            }
            glPopMatrix();

            for (Vector3f vector : collisions) {
                glPushMatrix();
                {
                    glColor4f(0, 1, 1, 1);

                    glTranslatef(vector.getX(), vector.getY(), vector.getZ());
                    glLineWidth(2f);
                    glBegin(GL_LINES);
                    glVertex3f(-1, 0, 0);
                    glVertex3f(+1, 0, -0);
                    glEnd();

                    glBegin(GL_LINES);
                    glVertex3f(0, -1, 0);
                    glVertex3f(0, +1, 0);
                    glEnd();

                    glBegin(GL_LINES);
                    glVertex3f(0, 0, -1);
                    glVertex3f(0, 0, +1);
                    glEnd();
                }
                glPopMatrix();
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

            // Zero-Line
            glLineWidth(1f);
            glColor4f(1, 1, 1, 0.5f);
            glVertex3f(-500, 0, 0);
            glVertex3f(+500, 0, 0);
            glVertex3f(0, 0, -500);
            glVertex3f(0, 0, +500);
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
            glColor4f(0, 0, 0, 1);
            glVertex3f(length, 0, 0);

            // Y
            glColor4f(0, 1, 0, 1);
            glVertex3f(0, 0, 0);
            glColor4f(0, 0, 0, 1);
            glVertex3f(0, length, 0);

            // Z
            glColor4f(0, 0, 1, 1);
            glVertex3f(0, 0, 0);
            glColor4f(0, 0, 0, 1);
            glVertex3f(0, 0, length);
        }
        glEnd();
    }
}
