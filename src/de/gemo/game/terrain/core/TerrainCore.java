package de.gemo.game.terrain.core;

import java.awt.Font;

import org.lwjgl.input.*;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.entities.weapons.*;
import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.world.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.ARBTextureRectangle.*;
import static org.lwjgl.opengl.GL11.*;

public class TerrainCore extends GameEngine {

    private Vector2f offset = new Vector2f();
    private EntityPlayer player;
    private PhysicsHandler physicsHandler;
    private RenderHandler renderHandler;
    private PlayerHandler playerHandler;

    private World world;
    private float scale = 1f;

    private long lastTickTime = System.currentTimeMillis();

    public TerrainCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, false);
    }

    @Override
    protected void createManager() {
        this.world = new World(2048, 1024);
        this.physicsHandler = new PhysicsHandler();
        this.renderHandler = new RenderHandler();
        this.playerHandler = new PlayerHandler();
        this.player = new EntityPlayer(this.world, 500, 100);
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
            glScalef(scale, scale, 1);
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

            TrueTypeFont font = FontManager.getStandardFont();

            font.drawString(20, 20, "FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS());
            font.drawString(20, 35, "Scale: " + this.scale);

            int midX = (int) ((MouseManager.INSTANCE.getCurrentX() - (int) offset.getX()) * (1f / this.scale));
            int midY = (int) ((MouseManager.INSTANCE.getCurrentY() - (int) offset.getY()) * (1f / this.scale));
            font.drawString(20, 50, "Mouse: " + midX + " / " + midY);

            font.drawString(20, 80, "Controls");
            font.drawString(20, 85, "__________________");
            font.drawString(20, 100, "jump: w");
            font.drawString(20, 115, "move: left/right");
            font.drawString(20, 130, "angle: up/down");
            font.drawString(20, 145, "shoot: space");
            font.drawString(20, 150, "__________________");
            font.drawString(20, 165, "reset: F12");
            font.drawString(20, 180, "zoom: mousewheel");
            font.drawString(20, 195, "cam: middle mouse + move");

            FontManager.getStandardFont(16, Font.BOLD).drawString(20, this.VIEW_HEIGHT - 30, "Weapon: " + this.player.getCurrentWeaponName());
            // font.drawString(20, 200, "__________________");
            // font.drawString(20, 215, "gravity +/-: a/y " + " ( " +
            // EntityBazooka.gravity + " )");
            // font.drawString(20, 230, "maxPower +/-: s/x " + " ( " +
            // EntityBazooka.maxPower + " )");
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
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_W) {
            this.player.jump();
        } else if (event.getKey() == Keyboard.KEY_1) {
            this.player.setWeapon(EntityBazooka.class);
        } else if (event.getKey() == Keyboard.KEY_2) {
            this.player.setWeapon(EntityGrenade.class);
        } else if (event.getKey() == Keyboard.KEY_3) {
            this.player.setWeapon(EntityDynamite.class);
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
            this.player.setHealth(100);
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
    public void onMouseWheel(boolean handled, MouseWheelEvent event) {

        if (event.isUp()) {
            // calculate current center
            int midX = (int) (((this.VIEW_WIDTH / 2) - (int) offset.getX()) * (1f / this.scale));
            int midY = (int) (((this.VIEW_HEIGHT / 2) - (int) offset.getY()) * (1f / this.scale));

            // scale
            this.scale += 0.1f;
            if (this.scale > 1.5f) {
                this.scale = 1.5f;
            }

            // center camera
            this.offset.setX(-((midX * scale) - (this.VIEW_WIDTH / 2f)));
            this.offset.setY(-((midY * scale) - (this.VIEW_HEIGHT / 2f)));
        } else {
            // calculate current center
            int midX = (int) (((this.VIEW_WIDTH / 2) - (int) offset.getX()) * (1f / this.scale));
            int midY = (int) (((this.VIEW_HEIGHT / 2) - (int) offset.getY()) * (1f / this.scale));

            // scale
            this.scale -= 0.1f;
            if (this.scale < 0.5f) {
                this.scale = 0.5f;
            }

            // center camera
            this.offset.setX(-((midX * scale) - (this.VIEW_WIDTH / 2f)));
            this.offset.setY(-((midY * scale) - (this.VIEW_HEIGHT / 2f)));
        }
    }

    @Override
    protected void tickGame(int delta) {
        long timeSinceLastTick = System.currentTimeMillis() - this.lastTickTime;
        int timesToTick = (int) (timeSinceLastTick / GameEngine.INSTANCE.getTickTime());
        for (int tick = 0; tick < timesToTick; tick++) {
            this.physicsHandler.updateAll(delta);
        }
        this.lastTickTime = System.currentTimeMillis();
    }
}
