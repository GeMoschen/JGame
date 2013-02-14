package de.gemo.engine.core;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.KeyboardManager;
import de.gemo.engine.manager.MouseManager;
import de.gemo.engine.manager.SoundManager;
import de.gemo.game.manager.gui.MyGUIManager1;
import de.gemo.game.manager.gui.MyGUIManager2;

import static org.lwjgl.opengl.GL11.*;

public class Engine {

    public static Engine INSTANCE = null;

    // DELTA
    private long lastFrame;
    private int delta;

    // WINDOW
    private String WIN_TITLE = "Enginetest";
    private int WIN_WIDTH = 1280;
    private int WIN_HEIGHT = 1024;
    private float win2viewRatioX = 1, win2viewRatioY = 1;

    // VIEW
    public int VIEW_WIDTH = 1280;
    public int VIEW_HEIGHT = 1024;

    // IS MOUSE FREE
    private boolean freeMouse = false;

    // TEMP-FPS
    private int tempFPS = 0;

    // MANAGER & MONITOR
    private KeyboardManager keyManager;
    private MouseManager mouseManager;
    private SoundManager soundManager;
    private AbstractDebugMonitor debugMonitor;

    // GUI-MANAGER
    private GUIManager activeGUIManager = null;
    private HashMap<Integer, GUIManager> guiManager = new HashMap<Integer, GUIManager>();
    private List<GUIManager> sortedGUIManagerList = new ArrayList<GUIManager>();

    // ////////////////////////////////////////
    //
    // ENGINE-STUFF
    //
    // ////////////////////////////////////////

    public Engine() {
        INSTANCE = this;
        this.createWindow(WIN_WIDTH, WIN_HEIGHT, false);
        this.initOpenGL();
        this.initManager();
        this.loadFonts();
        this.createGUI();
        this.run();
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
    }

    private final void initManager() {
        keyManager = KeyboardManager.getInstance(this);
        mouseManager = MouseManager.getInstance(this);
        soundManager = SoundManager.getInstance();
        debugMonitor = new StandardDebugMonitor();
    }

    private void loadFonts() {
        FontManager.loadFont(FontManager.VERDANA, Font.PLAIN, 20, new OutlineEffect(2, java.awt.Color.black), new ShadowEffect(java.awt.Color.black, 2, 2, 0.5f), new GradientEffect(new java.awt.Color(255, 255, 255), new java.awt.Color(150, 150, 150), 1f));
        FontManager.loadFont(FontManager.VERDANA, Font.PLAIN, 24);
    }

    private final void createGUI() {
        float halfWidth = VIEW_WIDTH / 2f;
        float halfHeight = VIEW_HEIGHT / 2f;

        halfWidth = VIEW_WIDTH / 4f;
        halfHeight = VIEW_HEIGHT / 4f;

        Hitbox hitbox = new Hitbox(0, 0);
        hitbox.addPoint(20, 63);
        hitbox.addPoint(1080, 63);
        hitbox.addPoint(1080, VIEW_HEIGHT - 20);
        hitbox.addPoint(20, VIEW_HEIGHT - 20);
        MyGUIManager2 manager = new MyGUIManager2("GUI2", hitbox, this.mouseManager.getMouseVector(), -1);
        this.registerGUIManager(manager);

        halfWidth = VIEW_WIDTH / 2f;
        halfHeight = VIEW_HEIGHT / 2f;

        hitbox = new Hitbox(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, halfHeight);
        this.registerGUIManager(new MyGUIManager1("GUI", hitbox, this.mouseManager.getMouseVector(), 0));

        this.initGUIManager(this.getGUIManager("GUI2"));
        this.initGUIManager(this.getGUIManager("GUI"));
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
            try {
                delta = updateDelta();
                tempFPS++;

                if (startTimer <= System.currentTimeMillis()) {
                    tick = true;
                    startTimer = System.currentTimeMillis() + tickTime;
                }

                // clear contents
                glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
                glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
                keyManager.update();
                mouseManager.update();

                // tick GUI-Managers
                for (GUIManager manager : this.guiManager.values()) {
                    manager.doTick(delta);
                }

                // update GUI-Managers
                if (tick) {
                    this.updateGUIManagers(delta);
                }

                glPushMatrix();
                {
                    // TODO: render gamefield-content

                    // RENDER GUI
                    if (this.debugMonitor.isShowGraphics()) {
                        glEnable(GL_BLEND);
                        for (GUIManager manager : this.guiManager.values()) {
                            manager.render();
                        }
                        glDisable(GL_BLEND);
                    }

                    // DEBUG RENDER
                    if (this.debugMonitor.isShowHitboxes()) {
                        for (GUIManager manager : this.guiManager.values()) {
                            manager.debugRender();
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
                    this.debugMonitor.setFPS(tempFPS);
                    this.debugMonitor.setDelta(delta);
                    tempFPS = 0;
                }
                tick = false;
            } catch (Exception e) {
                System.out.println("ERROR IN TICK! SHUTTING DOWN...");
                e.printStackTrace();
                Engine.close();
            }
        }
        Engine.close();
    }

    public static void close() {
        Engine.INSTANCE.soundManager.stopAll();

        Display.destroy();
        System.exit(0);
    }

    // ////////////////////////////////////////
    //
    // KEYBOARD EVENTS
    //
    // ////////////////////////////////////////

    public final void onKeyPressed(KeyEvent event) {
        if (this.activeGUIManager != null) {
            this.activeGUIManager.handleKeyPressed(event);
        }
    }

    public final void onKeyHold(KeyEvent event) {
        if (this.activeGUIManager != null) {
            this.activeGUIManager.handleKeyHold(event);
        }
    }

    public final void onKeyReleased(KeyEvent event) {
        if (this.activeGUIManager != null) {
            this.activeGUIManager.handleKeyReleased(event);
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
        for (GUIManager manager : this.sortedGUIManagerList) {
            if (manager.isColliding()) {
                if (manager != this.activeGUIManager) {
                    this.activateGUIManager(manager);
                }
                manager.handleMouseMove(event);
                return;
            }
        }
        this.activateGUIManager(null);
    }

    public final void onMouseDown(MouseClickEvent event) {
        if (!freeMouse) {
            this.mouseManager.ungrabMouse();
            freeMouse = !freeMouse;
        }

        for (GUIManager manager : this.sortedGUIManagerList) {
            if (manager.isColliding()) {
                this.activateGUIManager(manager);
                manager.handleMouseClick(event);
                return;
            }
        }
        this.activateGUIManager(null);
    }

    public final void onMouseUp(MouseReleaseEvent event) {
        for (GUIManager manager : this.sortedGUIManagerList) {
            if (manager.isColliding()) {
                this.activateGUIManager(manager);
                manager.handleMouseRelease(event);
                return;
            }
        }
        this.activateGUIManager(null);
    }

    public final void onMouseDrag(MouseDragEvent event) {
        for (GUIManager manager : this.sortedGUIManagerList) {
            if (manager.isColliding()) {
                this.activateGUIManager(manager);
                manager.handleMouseDrag(event);
                return;
            }
        }
        this.activateGUIManager(null);
    }

    // ////////////////////////////////////////
    //
    // METHODS TO HANDLE DELTA
    //
    // ////////////////////////////////////////

    private final long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public final int getCurrentDelta() {
        return delta;
    }

    private final int updateDelta() {
        long currentTime = this.getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        return delta;
    }

    // ////////////////////////////////////////
    //
    // METHODS TO HANDLE GUI-MANAGERS
    //
    // ////////////////////////////////////////

    private final void updateGUIManagers(float delta) {
        for (GUIManager manager : this.guiManager.values()) {
            manager.updateManager();
        }
    }

    private final void activateGUIManager(GUIManager manager) {
        if (this.activeGUIManager != null && this.activeGUIManager != manager) {
            this.activeGUIManager.onMouseOut();
            if (manager != null) {
                manager.onMouseIn();
            }
        }
        this.activeGUIManager = manager;
        this.debugMonitor.setActiveGUIManager(this.activeGUIManager);
    }

    public final void registerGUIManager(GUIManager manager) {
        this.guiManager.put(manager.getID(), manager);
        this.sortedGUIManagerList = new ArrayList<GUIManager>(this.guiManager.values());
        Collections.sort(this.sortedGUIManagerList);
    }

    public final void initGUIManager(GUIManager manager) {
        manager.initializeManager();
    }

    public final GUIManager getGUIManager(int ID) {
        for (GUIManager manager : this.guiManager.values()) {
            if (manager.getID() == ID) {
                return manager;
            }
        }
        return null;
    }

    public final GUIManager getGUIManager(String name) {
        for (GUIManager manager : this.guiManager.values()) {
            if (manager.getName().equalsIgnoreCase(name)) {
                return manager;
            }
        }
        return null;
    }

    // ////////////////////////////////////////
    //
    // GETTER AND SETTER
    //
    // ////////////////////////////////////////

    public final int getWindowWidth() {
        return WIN_WIDTH;
    }

    public final int getWindowHeight() {
        return WIN_HEIGHT;
    }

    public final float getWin2viewRatioX() {
        return win2viewRatioX;
    }

    public final float getWin2viewRatioY() {
        return win2viewRatioY;
    }

    // ////////////////////////////////////////
    //
    // GETTER AND SETTER FOR MANAGER
    //
    // ////////////////////////////////////////

    public final MouseManager getMouseManager() {
        return mouseManager;
    }

    public final KeyboardManager getKeyManager() {
        return keyManager;
    }

    public final SoundManager getSoundManager() {
        return soundManager;
    }

    public final AbstractDebugMonitor getDebugMonitor() {
        return debugMonitor;
    }

    public final GUIManager getActiveGUIManager() {
        return activeGUIManager;
    }

}
