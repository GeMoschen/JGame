package de.gemo.game.terrain.core;

import de.gemo.game.terrain.entities.EntityPlayer;
import de.gemo.game.terrain.entities.EntityWeapon;
import de.gemo.game.terrain.entities.weapons.EntityBazooka;
import de.gemo.game.terrain.entities.weapons.EntityDynamite;
import de.gemo.game.terrain.entities.weapons.EntityGrenade;
import de.gemo.game.terrain.handler.PhysicsHandler;
import de.gemo.game.terrain.handler.PlayerHandler;
import de.gemo.game.terrain.handler.RenderHandler;
import de.gemo.game.terrain.world.World;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.events.keyboard.KeyEvent;
import de.gemo.gameengine.events.mouse.MouseButton;
import de.gemo.gameengine.events.mouse.MouseMoveEvent;
import de.gemo.gameengine.events.mouse.MouseWheelEvent;
import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.manager.KeyboardManager;
import de.gemo.gameengine.manager.MouseManager;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.textures.SingleTexture;
import de.gemo.gameengine.units.Vector2f;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;
import static org.lwjgl.opengl.GL11.*;

public class TerrainCore extends GameEngine {

    private static int CURRENT_PLAYER_INDEX = 0;
    private static java.util.List<EntityPlayer> _players = new ArrayList<>();
    public static EntityPlayer CURRENT_PLAYER;

    private PhysicsHandler _physicsHandler;
    private RenderHandler _renderHandler;

    private World _world;
    private float _scale = 1f;
    private Vector2f _screenOffset = new Vector2f();

    private long _lastTickTime = System.currentTimeMillis();
    private SingleTexture _backgroundTexture;

    public TerrainCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, false);
    }

    public static void nextPlayer() {
        CURRENT_PLAYER_INDEX++;
        if (CURRENT_PLAYER_INDEX >= _players.size()) {
            CURRENT_PLAYER_INDEX = 0;
        }
        CURRENT_PLAYER = _players.get(CURRENT_PLAYER_INDEX);
        while (CURRENT_PLAYER.getHealth() < 1) {
            CURRENT_PLAYER_INDEX+= 2;
            if (CURRENT_PLAYER_INDEX >= _players.size()) {
                CURRENT_PLAYER_INDEX -= _players.size();
            }
            CURRENT_PLAYER = _players.get(CURRENT_PLAYER_INDEX);
        }
    }

    @Override
    protected void createManager() {
        _world = new World(getWindowWidth() * 2, getWindowHeight());
        _physicsHandler = new PhysicsHandler();
        _renderHandler = new RenderHandler();
        _renderHandler.add(_world);
        final PlayerHandler playerHandler = new PlayerHandler();
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final int x = random.nextInt(_world.getWidth() - 50) + 100;
            final int y = random.nextInt(_world.getHeight() - 200) + 100;
            final int teamId = i % 2 == 0 ? 0 : 1;
            final EntityPlayer player = new EntityPlayer(_world, x, y, teamId);
            _players.add(player);
            _physicsHandler.add(player);
            _renderHandler.add(player);
        }
        CURRENT_PLAYER = _players.get(0);
        try {
            _backgroundTexture = TextureManager.loadSingleTexture("resources/background_speedy.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glClearColor(0f, 0f, 0.8f, 1f);
        glDisable(GL_TEXTURE_2D);

        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);

        // draw background
        glPushMatrix();
        {
            glTranslatef(512, 384, 0);
            _backgroundTexture.render(1, 1, 1, 1);
        }
        glPopMatrix();

        glDisable(GL_DEPTH_TEST);
        glPushMatrix();
        {
            float currentScale = _scale;
            final EntityWeapon currentBullet = RenderHandler.CURRENT_BULLET;
            if (currentBullet != null) {
                final Vector2f position = currentBullet.getPosition();
                final float correctY = position.getY() + _world.getHeight() * 4;
                final float factorY = Math.min(1.2f, (_world.getHeight() / correctY) * 4);

                currentScale += (_scale - factorY) * 2;
                currentScale = Math.min(1.75f, Math.max(0.6f, currentScale));
                glTranslatef(-position.getX() * currentScale + getWindowWidth() / 2, -position.getY() * currentScale + getWindowHeight() - getWindowHeight() / 2, 0);
            } else {
                glTranslatef(_screenOffset.getX(), _screenOffset.getY(), 0);
            }
            glScalef(currentScale, currentScale, 1);
            glColor4f(1, 1, 1, 1);
            _renderHandler.renderAll();
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

            font.drawString(20, 20, "FPS: " + GameEngine.$.getDebugMonitor().getFPS());
            font.drawString(20, 35, "Scale: " + _scale);

            int midX = (int) ((MouseManager.$.getCurrentX() - (int) _screenOffset.getX()) * (1f / _scale));
            int midY = (int) ((MouseManager.$.getCurrentY() - (int) _screenOffset.getY()) * (1f / _scale));
            font.drawString(20, 50, "Mouse: " + midX + " / " + midY);

            font.drawString(20, 80, "Controls");
            font.drawString(20, 85, "__________________");
            font.drawString(20, 100, "jump: w");
            font.drawString(20, 115, "move: left/right");
            font.drawString(20, 130, "_angle: up/down");
            font.drawString(20, 145, "shoot: space");
            font.drawString(20, 150, "__________________");
            font.drawString(20, 165, "reset: F12");
            font.drawString(20, 180, "zoom: mousewheel");
            font.drawString(20, 195, "cam: middle mouse + move");

            String text = CURRENT_PLAYER.getCurrentWeaponName();
            if (EntityGrenade.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                if (EntityGrenade.TIMER == 1) {
                    text += " ( " + EntityGrenade.TIMER + " second )";
                } else {
                    text += " ( " + EntityGrenade.TIMER + " seconds )";
                }
            } else if (EntityDynamite.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                if (EntityDynamite.TIMER == 1) {
                    text += " ( " + EntityDynamite.TIMER + " second )";
                } else {
                    text += " ( " + EntityDynamite.TIMER + " seconds )";
                }
            }
            FontManager.getStandardFont(16, Font.BOLD).drawString(20, VIEW_HEIGHT - 30, "Weapon: " + text);
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
        boolean left = KeyboardManager.$.isKeyDown(Keyboard.KEY_LEFT);
        boolean right = KeyboardManager.$.isKeyDown(Keyboard.KEY_RIGHT);
        boolean up = KeyboardManager.$.isKeyDown(Keyboard.KEY_UP);
        boolean down = KeyboardManager.$.isKeyDown(Keyboard.KEY_DOWN);
        boolean space = KeyboardManager.$.isKeyDown(Keyboard.KEY_SPACE);
        CURRENT_PLAYER.setMovement(left, right, up, down, space);
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_W) {
            CURRENT_PLAYER.jump();
        } else if (event.getKey() == Keyboard.KEY_1) {
            CURRENT_PLAYER.setWeapon(EntityBazooka.class);
        } else if (event.getKey() == Keyboard.KEY_2) {
            CURRENT_PLAYER.setWeapon(EntityGrenade.class);
        } else if (event.getKey() == Keyboard.KEY_3) {
            CURRENT_PLAYER.setWeapon(EntityDynamite.class);
        } else if (event.getKey() == Keyboard.KEY_NUMPAD1 && RenderHandler.CURRENT_BULLET == null) {
            if (EntityGrenade.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityGrenade.TIMER = 1;
            } else if (EntityDynamite.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityDynamite.TIMER = 1;
            }
        } else if (event.getKey() == Keyboard.KEY_NUMPAD2 && RenderHandler.CURRENT_BULLET == null) {
            if (RenderHandler.CURRENT_BULLET == null && EntityGrenade.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityGrenade.TIMER = 2;
            } else if (EntityDynamite.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityDynamite.TIMER = 2;
            }
        } else if (event.getKey() == Keyboard.KEY_NUMPAD3 && RenderHandler.CURRENT_BULLET == null) {
            if (EntityGrenade.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityGrenade.TIMER = 3;
            } else if (EntityDynamite.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityDynamite.TIMER = 3;
            }
        } else if (event.getKey() == Keyboard.KEY_NUMPAD4 && RenderHandler.CURRENT_BULLET == null) {
            if (EntityGrenade.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityGrenade.TIMER = 4;
            } else if (EntityDynamite.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityDynamite.TIMER = 4;
            }
        } else if (event.getKey() == Keyboard.KEY_NUMPAD5 && RenderHandler.CURRENT_BULLET == null) {
            if (EntityGrenade.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityGrenade.TIMER = 5;
            } else if (EntityDynamite.class.equals(CURRENT_PLAYER.getCurrentWeaponClass())) {
                EntityDynamite.TIMER = 5;
            }
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyHold(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_SPACE && RenderHandler.CURRENT_BULLET == null) {
            CURRENT_PLAYER.shoot();
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F12) {
            _world.createWorld(_world.getWidth(), _world.getHeight());

            final Random random = new Random();
            for (int i = 0; i < 10; i++) {
                final EntityPlayer player = _players.get(i);
                final int x = random.nextInt(_world.getWidth() - 100) + 50;
                final int y = random.nextInt(_world.getHeight() - 200) + 100;
                player.setPosition(new Vector2f(x, y));
                player.setHealth(100);
                player.jump();
            }
            CURRENT_PLAYER_INDEX = 0;
            CURRENT_PLAYER = _players.get(CURRENT_PLAYER_INDEX);
        } else if (event.getKey() == Keyboard.KEY_SPACE) {
            CURRENT_PLAYER.resetPower();
        } else {
            super.onKeyReleased(event);
        }
    }

    @Override
    public void onMouseMove(boolean handled, MouseMoveEvent event) {
        if (MouseManager.$.isButtonDown(MouseButton.MIDDLE.getID()) && RenderHandler.CURRENT_BULLET == null) {
            if (_screenOffset.getX() + event.getDifX() < VIEW_WIDTH / 2 && _screenOffset.getX() + event.getDifX() > -_world.getWidth() + VIEW_WIDTH / 2) {
                _screenOffset.move(event.getDifX(), event.getDifY());
            }
        }
    }

    @Override
    public void onMouseWheel(boolean handled, MouseWheelEvent event) {
        if (event.isUp()) {
            // calculate current _center
            int midX = (int) (((VIEW_WIDTH / 2) - (int) _screenOffset.getX()) * (1f / _scale));
            int midY = (int) (((VIEW_HEIGHT / 2) - (int) _screenOffset.getY()) * (1f / _scale));

            // scale
            _scale += 0.1f;
            if (_scale > 1.5f) {
                _scale = 1.5f;
            }

            // _center camera
            _screenOffset.setX(-((midX * _scale) - (VIEW_WIDTH / 2f)));
            _screenOffset.setY(-((midY * _scale) - (VIEW_HEIGHT / 2f)));
        } else {
            // calculate current _center
            int midX = (int) (((VIEW_WIDTH / 2) - (int) _screenOffset.getX()) * (1f / _scale));
            int midY = (int) (((VIEW_HEIGHT / 2) - (int) _screenOffset.getY()) * (1f / _scale));

            // scale
            _scale -= 0.1f;
            if (_scale < 0.5f) {
                _scale = 0.5f;
            }

            // _center camera
            _screenOffset.setX(-((midX * _scale) - (VIEW_WIDTH / 2f)));
            _screenOffset.setY(-((midY * _scale) - (VIEW_HEIGHT / 2f)));
        }
    }

    @Override
    protected void tickGame(int delta) {
        long timeSinceLastTick = System.currentTimeMillis() - _lastTickTime;
        int timesToTick = (int) (timeSinceLastTick / GameEngine.$.getTickTime());
        for (int tick = 0; tick < timesToTick; tick++) {
            _physicsHandler.updateAll(delta);
        }
        _lastTickTime = System.currentTimeMillis();
    }
}
