package de.gemo.engine.core;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.inputmanager.KeyboardManager;
import de.gemo.engine.inputmanager.MouseManager;
import de.gemo.game.controller.MyGUIController;

public class Engine {

    public static Engine INSTANCE = null;

    private long lastFrame;
    private int delta;

    private String WIN_TITLE = "Enginetest";

    public float ratioX = 1, ratioY = 1;

    public int WIN_WIDTH = 1280;
    public int WIN_HEIGHT = 1024;

    public int VIEW_WIDTH = 1280;
    public int VIEW_HEIGHT = 1024;

    private boolean USE_VSYNC = true;
    private boolean HIDE_TEXT = false;

    private boolean freeMouse = false;

    private int currentFPS = 0;
    private KeyboardManager keyManager;
    private MouseManager mouseManager;

    private GUIController activeGUIController = null;
    private HashMap<Integer, GUIController> guiController = new HashMap<Integer, GUIController>();

    public Engine() {
        INSTANCE = this;
        this.createWindow(true);
        this.initOpenGL();
        this.loadFonts();
        this.createGUI();
        this.run();
    }

    private void run() {
        int oldFPS = 0;
        int oldCount = 0;

        lastFrame = this.getTime();
        delta = this.updateDelta();

        int tickTime = 50;
        long startTime = System.currentTimeMillis() + 1000;
        long startTimer = System.currentTimeMillis() + tickTime;
        boolean tick = true;

        this.mouseManager.grabMouse();

        while (!Display.isCloseRequested()) {
            delta = updateDelta();
            currentFPS++;

            if (startTimer <= System.currentTimeMillis()) {
                tick = true;
                startTimer = System.currentTimeMillis() + tickTime;
            }

            // clear contents
            glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            keyManager.update();
            mouseManager.update();

            if (tick) {
                this.updateGUIControllers();
            }

            GL11.glPushMatrix();

            // TODO: render gamefield-content

            GL11.glPopMatrix();

            // RENDER GUI
            GL11.glPushMatrix();

            if (Renderer.SHOW_GRAPHICS) {
                GL11.glEnable(GL11.GL_BLEND);
                for (GUIController controller : this.guiController.values()) {
                    controller.render();
                }
                GL11.glDisable(GL11.GL_BLEND);
            }

            if (Renderer.SHOW_HITBOXES) {
                for (GUIController controller : this.guiController.values()) {
                    controller.debugRender();
                }
            }

            // draw debug-informations
            GL11.glEnable(GL11.GL_BLEND);
            if (!freeMouse) {
                TrueTypeFont font = FontManager.getFont(FontManager.VERDANA, Font.PLAIN, 24);
                String text = "Press Mouse to release it!".toUpperCase();
                int width = font.getWidth(text) / 2;
                int height = font.getHeight(text) / 2;
                font.drawString(this.VIEW_WIDTH / 2 - width, this.VIEW_HEIGHT / 2 - height, text, Color.red);
            }

            TrueTypeFont font = FontManager.getStandardFont();
            font.drawString(10, 10, "FPS: " + oldFPS + (USE_VSYNC ? " (vsync)" : ""), Color.red);

            if (!HIDE_TEXT) {
                font.drawString(10, 25, "Delta: " + oldCount, Color.red);

                font.drawString(10, 40, "1/2: Scale active button", Color.magenta);
                font.drawString(10, 70, "W/S: change alpha of active button", Color.magenta);
                font.drawString(10, 55, "LEFT/RIGHT: rotate active button", Color.magenta);
                font.drawString(10, 85, "UP/DOWN: move active button", Color.magenta);

                font.drawString(10, 105, "F1: toggle vysnc", Color.orange);
                font.drawString(10, 120, "F2: toggle text", Color.orange);
                font.drawString(10, 135, "F11: toggle graphics", Color.orange);
                font.drawString(10, 150, "F12: toggle hitboxes", Color.orange);

                String text = "NONE";
                if (this.activeGUIController != null) {
                    text = this.activeGUIController.getName();

                    MyGUIController controller = (MyGUIController) this.activeGUIController;

                    if (controller.getHoveredElement() != null) {
                        font.drawString(10, 180, "Hovered: " + controller.getHoveredElement().getEntityID(), Color.yellow);
                    }
                    if (controller.getFocusedElement() != null) {
                        font.drawString(10, 195, "Focused: " + controller.getFocusedElement().getEntityID(), Color.yellow);
                    }
                    if (controller.hotkeysActive) {
                        font.drawString(10, 210, "Hotkeys active", Color.green);
                    }
                }
                font.drawString(10, 165, "Active UI: " + text, Color.red);
            }
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPopMatrix();

            // update and sync
            Display.update();

            if (USE_VSYNC) {
                Display.sync(100);
            }

            if (startTime < System.currentTimeMillis()) {
                startTime = System.currentTimeMillis() + 1000;
                oldFPS = currentFPS;
                oldCount = delta;
                currentFPS = 0;
            }

            tick = false;
        }

        Display.destroy();
        System.exit(0);
    }
    private void updateGUIControllers() {
        for (GUIController controller : this.guiController.values()) {
            controller.updateController();
        }
    }

    private void createWindow(boolean fullscreen) {
        try {
            this.ratioX = (float) ((float) VIEW_WIDTH / (float) WIN_WIDTH);
            this.ratioY = (float) ((float) VIEW_HEIGHT / (float) WIN_HEIGHT);
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
            org.lwjgl.opengl.PixelFormat pixelFormat = new PixelFormat(8, 0, 0, 8);
            Display.setTitle(WIN_TITLE);
            Display.create(pixelFormat);
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

    private void createGUI() {
        float halfWidth = VIEW_WIDTH / 2f;
        float halfHeight = VIEW_HEIGHT / 2f;

        Hitbox hitbox = new Hitbox(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, halfHeight);
        this.registerGUIController(new MyGUIController("GUI", hitbox, this.mouseManager.getMouseVector()));
    }

    private void initOpenGL() {
        // init OpenGL
        glMatrixMode(GL_PROJECTION);

        GL11.glLoadIdentity();
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 1000, -1000);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        keyManager = new KeyboardManager(this);
        mouseManager = new MouseManager(this);
    }

    private void loadFonts() {
        FontManager.loadFont(FontManager.VERDANA, Font.PLAIN, 20);
        FontManager.loadFont(FontManager.VERDANA, Font.PLAIN, 24);
    }

    // ////////////////////////////////////////
    //
    // KEYBOARD EVENTS
    //
    // ////////////////////////////////////////

    public void onKeyPressed(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.handleKeyPressed(event);
        }
    }

    public void onKeyHold(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.handleKeyHold(event);
        }
    }

    public void onKeyReleased(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.handleKeyReleased(event);
        }
        switch (event.getKey()) {
            case Keyboard.KEY_F1 : {
                USE_VSYNC = !USE_VSYNC;
                break;
            }
            case Keyboard.KEY_F2 : {
                HIDE_TEXT = !HIDE_TEXT;
                break;
            }
            case Keyboard.KEY_F11 : {
                Renderer.SHOW_GRAPHICS = !Renderer.SHOW_GRAPHICS;
                break;
            }
            case Keyboard.KEY_F12 : {
                Renderer.SHOW_HITBOXES = !Renderer.SHOW_HITBOXES;
                break;
            }
        }
    }

    // ////////////////////////////////////////
    //
    // MOUSE EVENTS
    //
    // ////////////////////////////////////////

    public void onMouseMove(MouseMoveEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseMove(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    private void activateGUIController(GUIController controller) {
        if (this.activeGUIController != null && this.activeGUIController != controller) {
            this.activeGUIController.onMouseOut();
            if (controller != null) {
                controller.onMouseIn();
            }
        }
        this.activeGUIController = controller;
    }

    public void onMouseDown(MouseClickEvent event) {
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

    public void onMouseUp(MouseReleaseEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseRelease(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    public void onMouseDrag(MouseDragEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.handleMouseDrag(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private int updateDelta() {
        long currentTime = this.getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        return delta;
    }

    public int getCurrentDelta() {
        return delta;
    }

    public int getWindowWidth() {
        return WIN_WIDTH;
    }

    public int getWindowHeight() {
        return WIN_HEIGHT;
    }

}
