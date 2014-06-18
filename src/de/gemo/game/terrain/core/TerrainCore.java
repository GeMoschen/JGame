package de.gemo.game.terrain.core;

import org.lwjgl.input.*;

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
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        this.world = new World(2 * 512, 1 * 512);
        this.physicsHandler = new PhysicsHandler();
        this.renderHandler = new RenderHandler();
        this.player = new Player(this.world, 500, 100);
        this.physicsHandler.add(this.player);
        this.renderHandler.add(this.player);
        this.renderHandler.add(this.world);
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glClearColor(0f, 0f, 0f, 1f);
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

    }

    @Override
    protected void updateGame(int delta) {
        // System.out.println(GameEngine.INSTANCE.getDebugMonitor().getFPS());
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
        if (event.getKey() == Keyboard.KEY_SPACE) {
            this.player.jump();
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F12) {
            this.world.createTerrain(this.world.getWidth(), this.world.getHeight());
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
            this.world.filledCircle(event.getX() - (int) offset.getX(), event.getY() - (int) offset.getY(), 30, 1, true);
            this.world.updateTexture();
        } else if (event.getButton().equals(MouseButton.LEFT)) {
            this.world.filledCircle(event.getX() - (int) offset.getX(), event.getY() - (int) offset.getY(), 35, 5, 2, false);
            this.world.filledCircle(event.getX() - (int) offset.getX(), event.getY() - (int) offset.getY(), 30, 0, false);
            this.world.updateTexture();
        }
    }
}
