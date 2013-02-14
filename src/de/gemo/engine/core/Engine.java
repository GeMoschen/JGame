package de.gemo.engine.core;

import java.awt.Font;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.GradientEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.font.effects.ShadowEffect;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.debug.AbstractDebugMonitor;
import de.gemo.engine.core.debug.StandardDebugMonitor;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.inputmanager.KeyboardManager;
import de.gemo.engine.inputmanager.MouseManager;
import de.gemo.engine.sound.SoundManager;
import de.gemo.game.controller.MyGUIController;

import static org.lwjgl.opengl.GL11.*;

public class Engine {

    public static Engine INSTANCE = null;

    private long lastFrame;
    private int delta;

    private String WIN_TITLE = "Enginetest";

    private float win2viewRatioX = 1, win2viewRatioY = 1;

    public int WIN_WIDTH = 1280;
    public int WIN_HEIGHT = 1024;

    public int VIEW_WIDTH = 1280;
    public int VIEW_HEIGHT = 1024;

    private boolean freeMouse = false;

    private int currentFPS = 0;
    private KeyboardManager keyManager;
    private MouseManager mouseManager;
    private AbstractDebugMonitor debugMonitor;
    private SoundManager soundManager;

    private GUIController activeGUIController = null;
    private HashMap<Integer, GUIController> guiController = new HashMap<Integer, GUIController>();

    public Engine() {
        INSTANCE = this;
        this.createWindow(WIN_WIDTH, WIN_HEIGHT, false);
        this.initOpenGL();
        this.loadFonts();
        this.createGUI();
        this.run();
    }

    public static void close() {
        Engine.INSTANCE.soundManager.stopAll();

        Display.destroy();
        System.exit(0);
    }

    private final void run() {
        lastFrame = this.getTime();
        delta = this.updateDelta();

        int tickTime = 50;
        long startTime = System.currentTimeMillis() + 1000;
        long startTimer = System.currentTimeMillis() + tickTime;
        boolean tick = true;

        this.mouseManager.grabMouse();

        soundManager.playSound(0, 0, 0);

        Display.setVSyncEnabled(this.debugMonitor.isUseVSync());

        while (!Display.isCloseRequested()) {
            delta = updateDelta();
            currentFPS++;

            if (startTimer <= System.currentTimeMillis()) {
                tick = true;
                startTimer = System.currentTimeMillis() + tickTime;
            }

            // clear contents
            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
            keyManager.update();
            mouseManager.update();

            // tick controllers
            for (GUIController controller : this.guiController.values()) {
                controller.doTick(delta);
            }

            if (tick) {
                this.updateGUIControllers(delta);
            }

            glPushMatrix();
            {
                // TODO: render gamefield-content

                // RENDER GUI
                if (this.debugMonitor.isShowGraphics()) {
                    glEnable(GL_BLEND);
                    for (GUIController controller : this.guiController.values()) {
                        controller.render();
                    }
                    glDisable(GL_BLEND);
                }

                // DEBUG RENDER
                if (this.debugMonitor.isShowHitboxes()) {
                    for (GUIController controller : this.guiController.values()) {
                        controller.debugRender();
                    }
                }
            }
            glPopMatrix();

            // draw debug-informations
            glEnable(GL_BLEND);
            {
                if (!freeMouse) {
                    UnicodeFont font = FontManager.getFont(FontManager.VERDANA, Font.PLAIN, 24);
                    String text = "Press Mouse to release it!".toUpperCase();
                    int width = font.getWidth(text) / 2;
                    int height = font.getHeight(text) / 2;
                    font.drawString(this.VIEW_WIDTH / 2 - width, this.VIEW_HEIGHT / 2 - height, text, Color.red);
                }
            }
            glDisable(GL_BLEND);

            // render debugmonitor
            this.debugMonitor.render();

            // update and sync
            Display.update();

            if (startTime < System.currentTimeMillis()) {
                startTime = System.currentTimeMillis() + 1000;
                this.debugMonitor.setFPS(currentFPS);
                this.debugMonitor.setDelta(delta);
                currentFPS = 0;
            }
            tick = false;

        }
        Engine.close();
    }

    private final void updateGUIControllers(float delta) {
        for (GUIController controller : this.guiController.values()) {
            controller.updateController();
        }
    }

    private final void createWindow(int windowWidth, int windowHeight, boolean fullscreen) {
        try {
            this.WIN_WIDTH = windowWidth;
            this.WIN_HEIGHT = windowHeight;
            this.win2viewRatioX = (float) ((float) VIEW_WIDTH / (float) WIN_WIDTH);
            this.win2viewRatioY = (float) ((float) VIEW_HEIGHT / (float) WIN_HEIGHT);
            DisplayMode displayMode = new DisplayMode(WIN_WIDTH, WIN_HEIGHT);
            if (fullscreen) {
                displayMode = null;
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                for (int i = 0; i < modes.length; i++) {
                    if (modes[i].getWidth() == WIN_WIDTH && modes[i].getHeight() == WIN_HEIGHT && modes[i].isFullscreenCapable()) {
                        displayMode = modes[i];
                    }
                }
                Display.setFullscreen(fullscreen);
            }
            Display.setDisplayMode(displayMode);
            org.lwjgl.opengl.PixelFormat pixelFormat = new PixelFormat(8, 0, 0, 4);
            Display.setTitle(WIN_TITLE);
            Display.create(pixelFormat);
            // Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
    }

    public final void registerGUIController(GUIController controller) {
        this.guiController.put(controller.getID(), controller);
    }

    public final GUIController getGUIController(int ID) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.getID() == ID) {
                return controller;
            }
        }
        return null;
    }

    public final GUIController getGUIController(String name) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.getName().equalsIgnoreCase(name)) {
                return controller;
            }
        }
        return null;
    }

    private final void createGUI() {
        float halfWidth = VIEW_WIDTH / 2f;
        float halfHeight = VIEW_HEIGHT / 2f;

        Hitbox hitbox = new Hitbox(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, halfHeight);
        this.registerGUIController(new MyGUIController("GUI", hitbox, this.mouseManager.getMouseVector()));
    }

    private final void initOpenGL() {
        // init OpenGL
        glMatrixMode(GL_PROJECTION);

        glLoadIdentity();
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        glShadeModel(GL_SMOOTH);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 1000, -1000);
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        keyManager = new KeyboardManager(this);
        mouseManager = new MouseManager(this);
        soundManager = new SoundManager();
        debugMonitor = new StandardDebugMonitor();
    }

    private void loadFonts() {
        FontManager.loadFont(FontManager.VERDANA, Font.PLAIN, 20, new OutlineEffect(2, java.awt.Color.black), new ShadowEffect(java.awt.Color.black, 2, 2, 0.5f), new GradientEffect(new java.awt.Color(255, 255, 255), new java.awt.Color(150, 150, 150), 1f));
        FontManager.loadFont(FontManager.VERDANA, Font.PLAIN, 24);
    }

    // ////////////////////////////////////////
    //
    // KEYBOARD EVENTS
    //
    // ////////////////////////////////////////

    public final void onKeyPressed(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.handleKeyPressed(event);
        }
    }

    public final void onKeyHold(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.handleKeyHold(event);
        }
    }

    public final void onKeyReleased(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.handleKeyReleased(event);
        }
        switch (event.getKey()) {
            case Keyboard.KEY_F1 : {
                this.debugMonitor.setUseVSync(!this.debugMonitor.isUseVSync());

                Display.setVSyncEnabled(this.debugMonitor.isUseVSync());
                break;
            }
            case Keyboard.KEY_F2 : {
                this.debugMonitor.setShowExtended(!this.debugMonitor.isShowExtended());
                break;
            }
            case Keyboard.KEY_F11 : {
                this.debugMonitor.setShowGraphics(!this.debugMonitor.isShowGraphics());
                break;
            }
            case Keyboard.KEY_F5 : {
                this.soundManager.setVolume(soundManager.getVolume() - 0.1f);
                break;
            }
            case Keyboard.KEY_F6 : {
                this.soundManager.setVolume(soundManager.getVolume() + 0.1f);
                break;
            }
            case Keyboard.KEY_F12 : {
                this.debugMonitor.setShowHitboxes(!this.debugMonitor.isShowHitboxes());
                break;
            }
        }
    }

    // ////////////////////////////////////////
    //
    // MOUSE EVENTS
    //
    // ////////////////////////////////////////

    public final void onMouseMove(MouseMoveEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseMove(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    private final void activateGUIController(GUIController controller) {
        if (this.activeGUIController != null && this.activeGUIController != controller) {
            this.activeGUIController.onMouseOut();
            if (controller != null) {
                controller.onMouseIn();
            }
        }
        this.activeGUIController = controller;
        this.debugMonitor.setActiveGUIController(this.activeGUIController);
    }

    public final void onMouseDown(MouseClickEvent event) {
        if (!freeMouse) {
            this.mouseManager.ungrabMouse();
            freeMouse = !freeMouse;
        }

        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseClick(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    public final void onMouseUp(MouseReleaseEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseRelease(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    public final void onMouseDrag(MouseDragEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseDrag(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    private final long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private final int updateDelta() {
        long currentTime = this.getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        return delta;
    }

    public final int getCurrentDelta() {
        return delta;
    }

    public final int getWindowWidth() {
        return WIN_WIDTH;
    }

    public final int getWindowHeight() {
        return WIN_HEIGHT;
    }

    public final AbstractDebugMonitor getDebugMonitor() {
        return debugMonitor;
    }

    public final GUIController getActiveGUIController() {
        return activeGUIController;
    }

    public final float getWin2viewRatioX() {
        return win2viewRatioX;
    }

    public final float getWin2viewRatioY() {
        return win2viewRatioY;
    }

    public final SoundManager getSoundManager() {
        return soundManager;
    }
}
