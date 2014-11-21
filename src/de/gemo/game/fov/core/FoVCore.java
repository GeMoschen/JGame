package de.gemo.game.fov.core;

import java.util.*;

import org.lwjgl.input.*;
import org.newdawn.slick.*;

import de.gemo.game.fov.navigation.*;
import de.gemo.game.fov.units.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class FoVCore extends GameEngine {

    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private NavMesh navMesh;

    private Camera cam = new Camera();
    private Vector2f mouseRightDownVector = new Vector2f();
    private Vector2f mouseLeftDownVector = new Vector2f();

    public FoVCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        int enemyCount = 6;
        int blockCount = 50;

        for (int i = 1; i <= blockCount; i++) {
            int width = 15;
            int height = 15;
            int x = (int) (20 + (Math.random() * (this.VIEW_WIDTH - width - 20)));
            int y = (int) (20 + (Math.random() * (this.VIEW_HEIGHT - height - 20)));
            tiles.add(new Tile(x, y, width, height));
        }

        for (int i = 1; i <= enemyCount; i++) {
            boolean freeSpace = false;
            Vector3f location = null;
            while (!freeSpace) {
                freeSpace = true;
                location = new Vector3f(20 + (float) Math.random() * (this.VIEW_WIDTH - 20), 20 + (float) Math.random() * (this.VIEW_HEIGHT - 20), 0);
                for (Tile tile : this.tiles) {
                    if (CollisionHelper.isVectorInHitbox(location, tile.expanded)) {
                        freeSpace = false;
                        break;
                    }
                }
            }
            enemies.add(new Enemy(location));
        }
        this.navMesh = new NavMesh(this.tiles);
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        super.onKeyReleased(event);
    }

    @Override
    public void onKeyHold(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_Q) {
            this.cam.addRoll(-1);
        } else if (event.getKey() == Keyboard.KEY_E) {
            this.cam.addRoll(1);
        } else if (event.getKey() == Keyboard.KEY_W) {
            this.cam.walkForward(5);
        } else if (event.getKey() == Keyboard.KEY_S) {
            this.cam.walkBackwards(5);
        } else if (event.getKey() == Keyboard.KEY_A) {
            this.cam.strafeLeft(5);
        } else if (event.getKey() == Keyboard.KEY_D) {
            this.cam.strafeRight(5);
        } else if (event.getKey() == Keyboard.KEY_X) {
            this.cam.goUp(5);
        } else if (event.getKey() == Keyboard.KEY_C) {
            this.cam.goUp(-5);
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
                this.cam.walkBackwards(distY / factor);
            } else if (distY < 0) {
                this.cam.walkForward(-distY / factor);
            }

            if (distX > 0) {
                this.cam.strafeRight(distX / factor);
            } else if (distX < 0) {
                this.cam.strafeLeft(-distX / factor);
            }

            this.renderMouseTemp(this.mouseRightDownVector);
        }

        if (event.isLeftButton()) {
            float distX = (event.getX() - mouseLeftDownVector.getX());
            this.cam.addRoll(distX / (factor * 6));
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

            for (Enemy enemy : this.enemies) {
                enemy.update(this.navMesh, this.tiles);
            }
        }

        if (event.isMiddleButton()) {
            // for (Enemy enemy : this.enemies) {
            // // enemy.update(this.navMesh, this.tiles);
            // enemy.setTarget(new Vector3f(event.getX(), event.getY(), 0),
            // navMesh, this.tiles);
            // }
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
                this.cam.goUp(distance);
            } else if (event.getDifY() < 0) {
                this.cam.goUp(-distance);
            }
        }
    }

    private void updateLights() {
        // lights.get(0).setAngle(getAngle(lights.get(0).getLocation(),
        // MouseManager.INSTANCE.getMouseVector()));
        int i = 0;
        for (Enemy enemy : this.enemies) {

            if (i == 0) {
                enemy.setAlerted(true);
            }
            i++;
            enemy.update(this.navMesh, this.tiles);

            // if (!cone.collides(lights.get(0))) {
            // cone.update();
            // // cone.setAlerted(false);
            // } else {
            // // create raycast
            // Hitbox raycast = new Hitbox(0, 0);
            // raycast.addPoint(cone.getLocation());
            // raycast.addPoint(lights.get(0).getLocation());
            //
            // // check for colliding polys
            // boolean canSeeTarget = false;
            // for (Tile block : this.blocks) {
            // if (CollisionHelper.findIntersection(raycast, block.getHitbox())
            // != null) {
            // canSeeTarget = true;
            // break;
            // }
            // }
            // if (canSeeTarget) {
            // cone.update();
            // cone.setAlerted(false);
            // } else {
            // cone.setAlerted(true);
            // cone.setAngle(getAngle(cone.getLocation(),
            // lights.get(0).getLocation()));
            // }
            // }
        }
    }

    @Override
    protected void updateGame(int delta) {
        this.updateLights();
    }

    @Override
    protected void renderGame3D() {
        glPushMatrix();
        this.cam.lookThrough();
        glPushMatrix();
        // blocks
        for (Tile block : tiles) {
            glColor4f(1f, 1f, 1f, 0.5f);
            block.render();
        }

        // AMBIENT
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(0.3f, 0.3f, 0.7f, 0.5f);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, 0);
            glVertex2f(this.VIEW_WIDTH, 0);
            glVertex2f(this.VIEW_WIDTH, this.VIEW_HEIGHT);
            glVertex2f(0, this.VIEW_HEIGHT);
        }
        glEnd();
        glDisable(GL_BLEND);

        // render lightcones
        for (Enemy enemy : enemies) {
            enemy.render(this.tiles, this.VIEW_WIDTH, this.VIEW_HEIGHT);
        }
        // this.navMesh.render();
        glPopMatrix();
        glPopMatrix();

    }

    @Override
    protected void renderGame2D() {
        // this.navMesh.createNavMesh(this.tiles);
        // this.navMesh.render();
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        Font font = FontManager.getStandardFont();
        font.drawString(10, 10, "Pitch: " + this.cam.getYaw());
        font.drawString(85, 10, "Yaw: " + this.cam.getRoll());
        font.drawString(200, 10, "FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS());

        int base = 20;
        font.drawString(10, base, "________________________________________");
        font.drawString(10, base + 15, "                       Controls");
        font.drawString(10, base + 20, "________________________________________");
        font.drawString(10, base + 35, "Rotate:   Q/E or left MB & move");
        font.drawString(10, base + 48, "Move:     W/A/S/D or right MB & move");
        font.drawString(10, base + 61, "Height:    X/C or middle MB & move");
    }
}
