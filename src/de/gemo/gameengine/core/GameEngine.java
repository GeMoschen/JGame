package de.gemo.gameengine.core;

import java.awt.Font;

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

import de.gemo.gameengine.core.debug.AbstractDebugMonitor;
import de.gemo.gameengine.core.debug.StandardDebugMonitor;
import de.gemo.gameengine.events.keyboard.KeyEvent;
import de.gemo.gameengine.events.mouse.MouseClickEvent;
import de.gemo.gameengine.events.mouse.MouseDragEvent;
import de.gemo.gameengine.events.mouse.MouseMoveEvent;
import de.gemo.gameengine.events.mouse.MouseReleaseEvent;
import de.gemo.gameengine.events.mouse.MouseWheelEvent;
import de.gemo.gameengine.gui.GUIElement;
import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.manager.GUIManager;
import de.gemo.gameengine.manager.KeyboardManager;
import de.gemo.gameengine.manager.MouseManager;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

import static org.lwjgl.opengl.GL11.*;

public class GameEngine {

    public static GameEngine INSTANCE;

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
    private GUIManager guiManager = null;
    private KeyboardManager keyManager = null;
    private MouseManager mouseManager = null;
    private AbstractDebugMonitor debugMonitor = null;
    private boolean hasDebugMonitor = false;

    // ////////////////////////////////////////
    //
    // CONSTRUCTORS
    //
    // ////////////////////////////////////////

    public GameEngine(String windowTitle, int windowWidth, int windowHeight, int viewWidth, int viewHeight, boolean fullscreen) {
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

    public GameEngine(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
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

        // glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        // glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        // Depth test setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        // Some basic settings
        GL11.glClearColor(0f, 0f, 0f, 1f); // Black Background
        // GL11.glEnable(GL11.GL_NORMALIZE); // force normal lengths to 1
        // GL11.glEnable(GL11.GL_CULL_FACE); // don't render hidden faces
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
        this.setDebugMonitor(new StandardDebugMonitor());
        keyManager = KeyboardManager.getInstance(this);
        mouseManager = MouseManager.getInstance(this);
        mouseManager.grabMouse();
        guiManager = GUIManager.getInstance();
        Display.setTitle("Loading standardfonts...");
        FontManager.initFirstFont();
        this.drawStartupText("Loading standardfonts...");
        FontManager.init();
    }

    public final void run() {
        lastFrame = this.getTime();
        delta = this.updateDelta();

        int tickTime = (int) (1000f / 60f);
        long startTime = System.currentTimeMillis() + 1000;
        long startTimer = System.currentTimeMillis() + tickTime;
        boolean tick = true;

        // enable vsync
        try {
            Display.setVSyncEnabled(false);
        } catch (Exception e) {
            System.out.println("WARNING: VSync is not supported. Ignoring...");
        }

        // ungrab mouse
        this.mouseManager.ungrabMouse();

        glClearColor(0f, 0f, 0f, 1.0f);
        while (!Display.isCloseRequested()) {

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            try {
                // clear screen
                glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

                delta = updateDelta();
                tempFPS++;

                if (startTimer <= System.currentTimeMillis()) {
                    tick = true;
                    startTimer = System.currentTimeMillis() + tickTime;
                }

                keyManager.update();
                mouseManager.update();

                // update game
                this.updateGame(this.delta);

                // tick GUI-Managers

                // update GUI-Managers
                if (tick) {
                    this.tickGame(delta);
                }

                // render gamefield-content
                this.setPerspective();
                this.renderGame3D();

                this.setOrtho();
                glPushMatrix();
                {
                    this.renderGame2D();

                    // DEBUG RENDER
                    if (this.hasDebugMonitor && this.debugMonitor.isShowHitboxes()) {
                        this.mouseManager.getMovedHitBox().render();
                        this.mouseManager.getHitBox().render();
                    }

                    // RENDER GUI
                    glPushMatrix();
                    {
                        this.guiManager.renderElements();
                    }
                    glPopMatrix();

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

                // update ...
                Display.update();

                // ... and sync
                if (this.hasDebugMonitor && this.debugMonitor.isUseVSync()) {
                    Display.sync(60);
                }

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

                // SHOW DIALOG
                String ls = System.getProperty("line.separator");
                JOptionPane.showMessageDialog(null, "ERROR:" + ls + ls + e.toString() + ls + ls + "The error has been copied to your clipboard!");

                GameEngine.close(true);
            }
        }
        GameEngine.close();
    }

    /**
     * Set OpenGL to render in 3D perspective. Set the projection matrix using GLU.gluPerspective(). The projection matrix controls how the scene is "projected" onto the screen. Think of it as the lens on a camera, which defines how wide the field of vision is, how deep the scene is, and how what the aspect ratio will be.
     */
    public void setPerspective() {
        // select projection matrix (controls perspective)
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(68f, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 512);
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

    private final void shutdown(boolean error) {
        this.drawStartupText("Shutting down...");
        this.onShutdown(error);
        Display.destroy();
    }

    public static final void close() {
        GameEngine.close(false);
    }

    public static final void close(boolean error) {
        GameEngine.INSTANCE.shutdown(error);
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

    protected void tickGame(int delta) {
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

    public void onKeyPressed(KeyEvent event) {
    }

    public void onKeyHold(KeyEvent event) {
    }

    public void onKeyReleased(KeyEvent event) {
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

    public final void handleMouseMove(MouseMoveEvent event) {
        this.onMouseMove(this.guiManager.onMouseMove(event), event);
    }

    public final void handleMouseDown(MouseClickEvent event) {
        this.onMouseDown(this.guiManager.onMouseDown(event), event);
    }

    public final void handleMouseUp(MouseReleaseEvent event) {
        this.onMouseUp(this.guiManager.onMouseUp(event), event);
    }

    public final void handleMouseWheel(MouseWheelEvent event) {
        this.onMouseWheel(this.guiManager.onMouseWheel(event), event);
    }

    public final void handleMouseDrag(MouseDragEvent event) {
        this.onMouseDrag(this.guiManager.onMouseDrag(event), event);
    }

    public void onMouseMove(boolean handled, MouseMoveEvent event) {
    }

    public void onMouseDown(boolean handled, MouseClickEvent event) {
    }

    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
    }

    public void onMouseWheel(boolean handled, MouseWheelEvent event) {
    }

    public void onMouseDrag(boolean handled, MouseDragEvent event) {
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

    public final boolean addGUIElement(String name, GUIElement element) {
        return this.guiManager.addElement(name, element);
    }

    public final GUIElement getGUIElement(String name) {
        return this.guiManager.getElement(name);
    }

    public final boolean removeGUIElement(String name) {
        return this.guiManager.removeElement(name);
    }
    public final boolean removeGUIElement(GUIElement element) {
        return this.guiManager.removeElement(element);
    }

    public final void clearGUIElements() {
        this.guiManager.clearElements();
    }

    public final GUIManager getGUIManager() {
        return guiManager;
    }

    public final MouseManager getMouseManager() {
        return mouseManager;
    }

    public final KeyboardManager getKeyManager() {
        return keyManager;
    }

    public final AbstractDebugMonitor getDebugMonitor() {
        return debugMonitor;
    }

    public final void setDebugMonitor(AbstractDebugMonitor debugMonitor) {
        this.debugMonitor = debugMonitor;
        this.hasDebugMonitor = (this.debugMonitor != null);
    }
}
