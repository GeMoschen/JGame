package de.gemo.game.terrain.core;

import org.lwjgl.input.*;
import org.newdawn.slick.*;
import org.newdawn.slick.opengl.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.utils.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.ARBTextureRectangle.*;
import static org.lwjgl.opengl.GL11.*;

public class TerrainCore extends GameEngine {

    private Vector2f offset = new Vector2f();
    private Player player;
    private PhysicsHandler physicsHandler;
    private RenderHandler renderHandler;

    private World world;

    public TerrainCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, false);
    }

    @Override
    protected void createManager() {
        this.world = new World(2048, 768);
        this.physicsHandler = new PhysicsHandler();
        this.renderHandler = new RenderHandler();
        this.player = new Player(this.world, 500, 100);
        this.physicsHandler.add(this.player);
        this.renderHandler.add(this.world);
        this.renderHandler.add(this.player);
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glClearColor(0f, 0f, 0.8f, 1f);
        glDisable(GL_TEXTURE_2D);

        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);

        glPushMatrix();
        {
            glTranslatef(offset.getX(), offset.getY(), 0);
            glColor4f(1, 1, 1, 1);
            this.renderHandler.renderAll();
        }
        glPopMatrix();

        // RENDER FPS
        glPushMatrix();
        {
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            Color.white.bind();
            TextureImpl.bindNone();
            FontManager.getStandardFont().drawString(20, 20, "FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS());
        }
        glPopMatrix();

    }

    @Override
    protected void updateGame(int delta) {
        boolean left = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_LEFT);
        boolean right = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_RIGHT);
        boolean up = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_UP);
        boolean down = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_DOWN);
        boolean space = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_SPACE);
        this.player.setMovement(left, right, up, down, space);
        this.physicsHandler.updateAll(delta);
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_W) {
            this.player.jump();
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyHold(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_SPACE) {
            this.player.shoot();
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F12) {
            this.world.createWorld(this.world.getWidth(), this.world.getHeight());
            this.player.setPosition(new Vector2f(500, 100));
        } else if (event.getKey() == Keyboard.KEY_SPACE) {
            this.player.resetPower();
        } else {
            super.onKeyReleased(event);
        }
    }

    @Override
    public void onMouseMove(boolean handled, MouseMoveEvent event) {
        if (MouseManager.INSTANCE.isButtonDown(MouseButton.MIDDLE.getID())) {
            offset.move(event.getDifX(), event.getDifY());
        }
    }

    @Override
    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
        if (event.getButton().equals(MouseButton.RIGHT)) {
            int midX = event.getX() - (int) offset.getX();
            int midY = event.getY() - (int) offset.getY();
            int radius = 30;
            this.world.filledCircle(midX, midY, radius, TerrainType.TERRAIN, true);
            this.world.getTerrainParts(midX - radius - 4, midY - radius - 4, radius * 2 + 8, radius * 2 + 8, true);
        } else if (event.getButton().equals(MouseButton.LEFT)) {
            int midX = event.getX() - (int) offset.getX();
            int midY = event.getY() - (int) offset.getY();
            int radius = 35;
            int wallThickness = 9;
            this.world.filledCircle(midX, midY, radius, wallThickness, TerrainType.CRATER, false);
            this.world.filledCircle(midX, midY, radius - wallThickness, TerrainType.AIR, false);
            this.world.getTerrainParts(midX - radius - 4, midY - radius - 4, radius * 2 + 8, radius * 2 + 8, true);
        } else if (event.getButton().equals(MouseButton.MIDDLE)) {
            int midX = event.getX() - (int) offset.getX();
            int midY = event.getY() - (int) offset.getY();
            this.player.line((int) this.player.getPosition().getX(), (int) this.player.getPosition().getY(), midX, midY);
            this.world.getTerrainParts(0, 0, this.world.getWidth(), this.world.getHeight(), true);
        }

    }
}
