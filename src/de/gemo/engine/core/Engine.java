package de.gemo.engine.core;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import de.gemo.engine.core.debug.AbstractDebugMonitor;
import de.gemo.engine.core.debug.StandardDebugMonitor;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.events.mouse.MouseWheelEvent;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.KeyboardManager;
import de.gemo.engine.manager.MouseManager;
import de.gemo.engine.manager.SoundManager;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.util.glu.GLU.*;

public class Engine implements ClipboardOwner {

    public static Engine INSTANCE;

    // DELTA
    private long lastFrame;
    private int delta;

    // WINDOW
    private String WIN_TITLE = "";
    private int WIN_WIDTH = 1280;
    private int WIN_HEIGHT = 1024;
    private boolean fullscreen = false;
    private float win2viewRatioX = 1, win2viewRatioY = 1;

    // VIEW
    public int VIEW_WIDTH = 1280;
    public int VIEW_HEIGHT = 1024;

    // TEMP-FPS
    private int tempFPS = 0;

    // MANAGER & MONITOR
    private KeyboardManager keyManager = null;
    private MouseManager mouseManager = null;
    private SoundManager soundManager = null;

    // DEBUG-MONITOR
    private AbstractDebugMonitor debugMonitor = null;
    private boolean hasDebugMonitor = false;

    // GUI-MANAGER
    private GUIManager activeGUIManager = null;
    private HashMap<Integer, GUIManager> guiManager = new HashMap<Integer, GUIManager>();
    private List<GUIManager> sortedGUIManagerList = new ArrayList<GUIManager>();
    private boolean removeGUIManagers = false;
    private HashSet<GUIManager> guiManagersToRemove = new HashSet<GUIManager>();

    // ////////////////////////////////////////
    //
    // CONSTRUCTORS
    //
    // ////////////////////////////////////////

    public Engine(String windowTitle, int windowWidth, int windowHeight, int viewWidth, int viewHeight, boolean fullscreen) {
        INSTANCE = this;
        this.WIN_TITLE = windowTitle;
        // set window-dimensions
        this.WIN_WIDTH = windowWidth;
        this.WIN_HEIGHT = windowHeight;
        // set view-dimensions
        this.VIEW_WIDTH = viewWidth;
        this.VIEW_HEIGHT = viewHeight;
        this.fullscreen = fullscreen;
    }

    public Engine(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        this(windowTitle, windowWidth, windowHeight, windowWidth, windowHeight, fullscreen);
    }

    // ////////////////////////////////////////
    //
    // ENGINE-STUFF
    //
    // ////////////////////////////////////////

    public void startUp() {
        this.createWindow();
        this.initOpenGL();
        this.initManager();
        this.initEngine();
        // this.run();
    }

    private final void initEngine() {
        Display.setTitle(this.getWindowTitle());

        this.drawStartupText("Loading userfonts...");
        this.loadFonts();

        this.drawStartupText("Loading usertextures...");
        this.loadTextures();

        this.drawStartupText("Creating usermanagers...");
        this.createManager();

        this.drawStartupText("Creating GUIs...");
        this.createGUI();
    }

    private final void createWindow() {
        try {
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
            org.lwjgl.opengl.PixelFormat pixelFormat = new PixelFormat(4, 0, 0, 4);
            Display.create(pixelFormat);
            // Display.create();
            Display.setTitle("Starting...");
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
    }

    private final void initOpenGL() {
        // init OpenGL
        glShadeModel(GL_SMOOTH);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 100, -100);
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        // Depth test setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        // Some basic settings
        GL11.glClearColor(0f, 0f, 0f, 1f); // Black Background
        GL11.glEnable(GL11.GL_NORMALIZE); // force normal lengths to 1
        GL11.glEnable(GL11.GL_CULL_FACE); // don't render hidden faces
        GL11.glEnable(GL11.GL_TEXTURE_2D); // use textures
        GL11.glEnable(GL11.GL_BLEND); // enable transparency

        // How to handle transparency: average colors together
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Enable alpha test so the transparent backgrounds in texture images don't draw.
        // This prevents transparent areas from affecting the depth or stencil buffer.
        // alpha func will accept only fragments with alpha greater than 0
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0f);
    }

    private final void initManager() {
        Display.setTitle("Init manager...");
        keyManager = KeyboardManager.getInstance(this);
        mouseManager = MouseManager.getInstance(this);
        soundManager = SoundManager.getInstance();
        mouseManager.grabMouse();
        Display.setTitle("Loading standardfonts...");
        FontManager.initFirstFont();
        this.drawStartupText("Loading standardfonts...");
        FontManager.init();
        this.setDebugMonitor(new StandardDebugMonitor());
    }

    public final void run() {
        lastFrame = this.getTime();
        delta = this.updateDelta();

        int tickTime = 50;
        long startTime = System.currentTimeMillis() + 1000;
        long startTimer = System.currentTimeMillis() + tickTime;
        boolean tick = true;

        // enable vsync
        // Display.setVSyncEnabled(this.debugMonitor.isUseVSync());

        // ungrab mouse
        this.mouseManager.ungrabMouse();

        glClearColor(0f, 0f, 0f, 1.0f);
        while (!Display.isCloseRequested()) {

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            try {

                delta = updateDelta();
                tempFPS++;

                if (startTimer <= System.currentTimeMillis()) {
                    tick = true;
                    startTimer = System.currentTimeMillis() + tickTime;
                }

                // clear queued managers
                this.clearGUIManagers();

                keyManager.update();
                mouseManager.update();

                // update game
                this.updateGame(this.delta);

                // tick GUI-Managers
                for (GUIManager manager : this.guiManager.values()) {
                    manager.doTick(delta);
                }

                // update GUI-Managers
                if (tick) {
                    this.updateGUIManagers(delta);
                    this.tickGame();
                }

                // render gamefield-content
                this.setPerspective();
                this.renderGame3D();

                this.setOrtho();
                glPushMatrix();
                {
                    this.renderGame2D();

                    // RENDER GUI
                    if (!this.hasDebugMonitor || this.debugMonitor.isShowGraphics()) {
                        glEnable(GL_BLEND);
                        for (GUIManager manager : this.guiManager.values()) {
                            manager.render();
                        }
                        glDisable(GL_BLEND);
                    }

                    // DEBUG RENDER
                    if (this.hasDebugMonitor && this.debugMonitor.isShowHitboxes()) {
                        for (GUIManager manager : this.guiManager.values()) {
                            manager.debugRender();
                        }

                        this.mouseManager.getMovedHitBox().render();
                        this.mouseManager.getHitBox().render();
                    }

                    glPushMatrix();
                    {
                        // render debugmonitor
                        if (this.hasDebugMonitor && this.debugMonitor.isVisible()) {
                            // this.setup2DSpace();
                            this.debugMonitor.render();
                        }
                    }
                    glPopMatrix();
                }
                glPopMatrix();

                // update and sync
                Display.update();

                if (this.hasDebugMonitor && this.debugMonitor.isUseVSync())
                    Display.sync(60);

                if (startTime < System.currentTimeMillis()) {
                    startTime = System.currentTimeMillis() + 1000;
                    this.debugMonitor.setFPS(tempFPS);
                    this.debugMonitor.setDelta(delta);
                    tempFPS = 0;
                }
                tick = false;

                glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
            } catch (Exception e) {
                System.out.println("ERROR IN TICK! SHUTTING DOWN...");
                e.printStackTrace();

                // COPY TO CLIPOBOARD
                StringSelection stringSelection = new StringSelection(e.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, this);

                // SHOW DIALOG
                String ls = System.getProperty("line.separator");
                JOptionPane.showMessageDialog(null, "ERROR:" + ls + ls + e.toString() + ls + ls + "The error has been copied to your clipboard!");

                Engine.close(true);
            }
        }
        Engine.close();
    }

    /**
     * Set OpenGL to render in 3D perspective. Set the projection matrix using GLU.gluPerspective(). The projection matrix controls how the scene is "projected" onto the screen. Think of it as the lens on a camera, which defines how wide the field of vision is, how deep the scene is, and how what the aspect ratio will be.
     */
    public void setPerspective() {
        // select projection matrix (controls perspective)
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(68f, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 400);
        // return to modelview matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    public void setOrtho() {
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        // select projection matrix (controls view on screen)
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        // set ortho to same size as viewport, positioned at 0,0
        GL11.glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, -500, 500);
        // return to modelview matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        glLoadIdentity();
    }

    public final void setup3DSpace2() {
        glLoadIdentity();
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.03f, 400);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        TextureImpl.bindNone();
        glLoadIdentity();
    }

    public final void setup2DSpace2() {
        glLoadIdentity();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 100, -100);
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private final void shutdown(boolean error) {
        this.drawStartupText("Shutting down...");
        this.onShutdown(error);
        Engine.INSTANCE.soundManager.stopAll();
        Display.destroy();
    }

    public static final void close() {
        Engine.close(false);
    }

    public static final void close(boolean error) {
        Engine.INSTANCE.shutdown(error);
        System.exit(0);
    }

    // ////////////////////////////////////////
    //
    // METHODS FOR CHILDREN
    //
    // ////////////////////////////////////////

    protected void loadTextures() {
    }

    protected void loadFonts() {
    }

    protected void createManager() {
    }

    protected void createGUI() {
    }

    protected void tickGame() {
    }

    protected void updateGame(int delta) {
    }

    protected void renderGame3D() {
    }

    protected void renderGame2D() {
    }

    protected void onShutdown(boolean error) {
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
                // Display.setVSyncEnabled(this.debugMonitor.isUseVSync());
                break;
            }
            case Keyboard.KEY_F2 : {
                if (this.hasDebugMonitor) {
                    this.debugMonitor.setVisible(!this.debugMonitor.isVisible());
                    System.out.println("vis: " + this.debugMonitor.isVisible());
                }
                break;
            }
            case Keyboard.KEY_F3 : {
                if (this.hasDebugMonitor) {
                    this.debugMonitor.setShowExtended(!this.debugMonitor.isShowExtended());
                }
                break;
            }
            case Keyboard.KEY_F11 : {
                if (this.hasDebugMonitor) {
                    this.debugMonitor.setShowGraphics(!this.debugMonitor.isShowGraphics());
                }
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
                if (this.hasDebugMonitor) {
                    this.debugMonitor.setShowHitboxes(!this.debugMonitor.isShowHitboxes());
                }
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
                if (this.activeGUIManager != null) {
                    this.activeGUIManager.handleMouseMove(event);
                }
                return;
            }
        }
        this.activateGUIManager(null);
    }

    public final void onMouseDown(MouseClickEvent event) {
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

    public final void onMouseWheel(MouseWheelEvent event) {
        for (GUIManager manager : this.sortedGUIManagerList) {
            if (manager.isColliding()) {
                this.activateGUIManager(manager);
                manager.handleMouseWheel(event);
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
    // SOME NEEDED METHODS
    //
    // ////////////////////////////////////////

    protected void drawStartupText(String text) {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        glPushMatrix();
        {
            glDisable(GL_TEXTURE_RECTANGLE_ARB);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_LIGHTING);
            glDisable(GL_LIGHT0);

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);

            TrueTypeFont font = FontManager.getFont(FontManager.DEFAULT, Font.PLAIN, 12);
            int x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(text) / 2f);
            int y = (int) (this.VIEW_HEIGHT / 2f - font.getHeight(text) / 2f);
            font.drawString(x, y, text, Color.red);

            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);
        }
        glPopMatrix();

        Display.update();
    }

    // ////////////////////////////////////////
    //
    // METHODS TO HANDLE GUI-MANAGERS
    //
    // ////////////////////////////////////////

    private final void clearGUIManagers() {
        if (!this.removeGUIManagers) {
            return;
        }

        boolean wasActive = false;
        for (GUIManager manager : this.guiManagersToRemove) {
            if (!this.guiManager.containsKey(manager.getID())) {
                continue;
            }
            if (this.activeGUIManager.getID() == manager.getID()) {
                wasActive = true;
            }
            this.guiManager.remove(manager.getID());
        }
        if (wasActive) {
            this.activateGUIManager(null);
        }
        this.removeGUIManagers = false;
        this.sortedGUIManagerList = new ArrayList<GUIManager>(this.guiManager.values());
        Collections.sort(this.sortedGUIManagerList);
    }

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

    public final void unregisterAllGUIManagers() {
        this.removeGUIManagers = true;
        this.guiManagersToRemove.addAll(this.guiManager.values());
    }

    public final void unregisterGUIManager(GUIManager manager) {
        if (manager == null) {
            return;
        }
        this.removeGUIManagers = true;
        this.guiManagersToRemove.add(manager);
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

    public String getWindowTitle() {
        return WIN_TITLE;
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

    public final void setDebugMonitor(AbstractDebugMonitor debugMonitor) {
        this.debugMonitor = debugMonitor;
        this.hasDebugMonitor = (this.debugMonitor != null);
    }

    public final GUIManager getActiveGUIManager() {
        return activeGUIManager;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do nothing...
    }

}
