package de.gemo.game.sim.core;

import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.core.*;
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
        level = new Level(113, 85);
        for (int i = 0; i < 200; i++) {
            this.level.addPerson(new Person(level, AbstractTile.TILE_SIZE * i + AbstractTile.HALF_TILE_SIZE, AbstractTile.TILE_SIZE * i + AbstractTile.HALF_TILE_SIZE));
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
        } else if (event.isMiddleButton()) {
            int tileX = (int) (event.getX() / (float) (AbstractTile.TILE_SIZE + 1));
            int tileY = (int) (event.getY() / (float) (AbstractTile.TILE_SIZE + 1));
            if (level.getTile(tileX, tileY).isBlockingPath()) {
                level.setTile(tileX, tileY, TileManager.getTileByName("Empty"));
            } else {
                level.setTile(tileX, tileY, TileManager.getTileByName("Blocked"));
            }
        }
    }

    @Override
    protected void renderGame2D() {
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
