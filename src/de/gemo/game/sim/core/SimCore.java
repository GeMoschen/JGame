package de.gemo.game.sim.core;

import java.util.*;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;

import static org.lwjgl.opengl.GL11.*;

public class SimCore extends GameEngine {

    public Level level;

    public SimCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        TileManager.initialize();
        // level = new Level(113, 85);
        level = new Level(10, 10);
    }

    int oldDragX = -1;
    int oldDragY = -1;

    @Override
    public void onMouseDrag(boolean handled, MouseDragEvent event) {
        if (event.isMiddleButton()) {
            int tileX = (int) (event.getX() / (float) (AbstractTile.TILE_SIZE + 1));
            int tileY = (int) (event.getY() / (float) (AbstractTile.TILE_SIZE + 1));
            if (oldDragX != tileX || oldDragY != tileY) {
                oldDragX = tileX;
                oldDragY = tileY;
                if (level.getTile(tileX, tileY).isBlockingPath()) {
                    level.setTile(tileX, tileY, TileManager.getTileByName("Empty"));
                } else {
                    level.setTile(tileX, tileY, TileManager.getTileByName("Blocked"));
                }
            }
        }
    }

    @Override
    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
        if (event.isLeftButton()) {
            for (Person person : this.level.getPersons()) {
                person.setPosition(event.getX(), event.getY());
                person.updatePath();
            }
        } else if (event.isRightButton()) {
            for (Person person : this.level.getPersons()) {
                person.findRandomTarget();
            }
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_SPACE) {
            Random random = new Random();
            for (int i = 0; i < 1; i++) {
                Person person = new Person(level, AbstractTile.TILE_SIZE * random.nextInt(level.getDimX()) + AbstractTile.HALF_TILE_SIZE, AbstractTile.TILE_SIZE * random.nextInt(level.getDimY()) + AbstractTile.HALF_TILE_SIZE);
                this.level.addPerson(person);
                person.findRandomTarget();
            }
            System.out.println("count: " + this.level.getPersons().size());
        }
    }

    @Override
    protected void renderGame2D() {
        int time = (int) (level.time / 1000000f);
        Display.setTitle("FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS() + " - " + time);
        glDisable(GL_TEXTURE_2D);
        glPushMatrix();
        {
            level.renderLevel();
        }
        glPopMatrix();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    @Override
    protected void tickGame(int delta) {
        this.level.tick();
    }
}
