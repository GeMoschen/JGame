package de.gemo.game.sim.core;

import java.awt.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.mouse.*;

import static org.lwjgl.opengl.GL11.*;

public class SimCore extends GameEngine {

    public Level level;
    private Person person;

    public SimCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        TileManager.initialize();
        level = new Level(50, 50);
        person = new Person(level, 100, 100);
    }

    @Override
    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
        if (event.isLeftButton()) {
            this.person.setPosition(event.getX(), event.getY());
            this.person.updatePath();
        } else if (event.isRightButton()) {
            int tileX = (int) (event.getX() / (float) (AbstractTile.TILE_SIZE + 1));
            int tileY = (int) (event.getY() / (float) (AbstractTile.TILE_SIZE + 1));
            this.person.setTarget(new Point(tileX, tileY));
            this.person.updatePath();
        }
    }

    @Override
    protected void renderGame2D() {
        glDisable(GL_TEXTURE_2D);
        glPushMatrix();
        {
            level.renderLevel();
            person.render();
        }
        glPopMatrix();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    @Override
    protected void tickGame(int delta) {
        this.person.update();
    }
}
