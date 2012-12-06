package de.gemo.game.core;

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
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.game.collision.ComplexHitbox;
import de.gemo.game.events.keyboard.KeyEvent;
import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseUpEvent;
import de.gemo.game.implementation.MyGUIController;
import de.gemo.game.implementation.SecondGUIController;
import de.gemo.game.input.KeyboardManager;
import de.gemo.game.input.MouseManager;

public class Engine {

    public static Engine INSTANCE = null;

    private long lastFrame;
    private int delta;

    private String WIN_TITLE = "Enginetest";

    private int WIN_WIDTH = 1024;
    private int WIN_HEIGHT = 768;

    private int VIEW_WIDTH = 1024;
    private int VIEW_HEIGHT = 768;

    private boolean USE_VSYNC = true;
    private boolean HIDE_TEXT = false;

    private boolean freeMouse = false;

    private int currentFPS = 0;
    private KeyboardManager keyManager;
    private MouseManager mouseManager;

    private GUIController activeGUIController = null;
    private HashMap<Integer, GUIController> guiController;

    public Engine() {
        INSTANCE = this;
        this.createWindow();
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

        while (!Display.isCloseRequested()) {
            delta = updateDelta();
            currentFPS++;

            if (startTimer <= System.currentTimeMillis()) {
                tick = true;
                startTimer = System.currentTimeMillis() + tickTime;
            }

            // clear contents
            glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // block MouseMovement?
            if (!freeMouse) {
                mouseManager.blockMouseMovement();
            }

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

            for (GUIController controller : this.guiController.values()) {
                controller.render();
            }

            // draw debug-informations
            GL11.glEnable(GL11.GL_BLEND);
            TrueTypeFont font = FontManager.getFont("Verdana", Font.BOLD, 12);
            font.drawString(10, 10, "FPS: " + oldFPS + (USE_VSYNC ? " (vsync)" : ""), Color.red);
            if (!HIDE_TEXT) {
                font.drawString(10, 25, "Delta: " + oldCount, Color.red);

                font.drawString(10, 35, "1/2: Scale active button", Color.magenta);
                font.drawString(10, 50, "A/D: rotate active button", Color.magenta);
                font.drawString(10, 65, "W/S: change alpha of active button", Color.magenta);
                font.drawString(10, 80, "Arrowkeys: move active button", Color.magenta);

                font.drawString(10, 105, "F1: toggle vysnc", Color.orange);
                font.drawString(10, 120, "F2: toggle text", Color.orange);
                font.drawString(10, 135, "F11: toggle graphics", Color.orange);
                font.drawString(10, 150, "F12: toggle hitboxes", Color.orange);

                String text = "NONE";
                if (this.activeGUIController != null) {
                    text = this.activeGUIController.getName();
                }
                font.drawString(10, 175, "Active UI: " + text, Color.red);
            }
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPopMatrix();

            // update and sync
            Display.update();

            if (USE_VSYNC) {
                Display.sync(60);
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
            controller.updateVisibility();
        }
    }

    private void createWindow() {
        try {
            Display.setDisplayMode(new DisplayMode(WIN_WIDTH, WIN_HEIGHT));
            Display.setTitle(WIN_TITLE);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
    }

    public final void registerGUIController(GUIController controller) {
        this.guiController.put(controller.getID(), controller);
    }

    private void createGUI() {
        this.guiController = new HashMap<Integer, GUIController>();

        float halfWidth = WIN_WIDTH / 2f;
        float halfHeight = 50;

        ComplexHitbox hitbox = new ComplexHitbox(halfWidth, WIN_HEIGHT - 50);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, halfHeight);
        this.registerGUIController(new MyGUIController("GUI", hitbox, this.mouseManager.getMouseVector()));

        hitbox = new ComplexHitbox(halfWidth, 300);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, -halfHeight);
        hitbox.addPoint(halfWidth, halfHeight);
        hitbox.addPoint(-halfWidth, halfHeight);
        this.registerGUIController(new SecondGUIController("Second GUI", hitbox, this.mouseManager.getMouseVector()));
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
        GL11.glOrtho(0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight(), 0, 1000, -1000);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        keyManager = new KeyboardManager(this);
        mouseManager = new MouseManager(this);
    }

    private void loadFonts() {
        FontManager.loadFont("Verdana", Font.BOLD, 12);
        FontManager.loadFont("Verdana", Font.BOLD, 14);
    }

    // ////////////////////////////////////////
    //
    // KEYBOARD EVENTS
    //
    // ////////////////////////////////////////

    public void onKeyPressed(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.onKeyPressed(event);
        }
    }

    public void onKeyHold(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.onKeyHold(event);
        }
    }

    public void onKeyReleased(KeyEvent event) {
        if (this.activeGUIController != null) {
            this.activeGUIController.onKeyReleased(event);
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
                controller.onMouseMove(event);
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

    public void onMouseDown(MouseDownEvent event) {
        if (!freeMouse) {
            freeMouse = !freeMouse;
        }
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.onMouseDown(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    public void onMouseUp(MouseUpEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.onMouseUp(event);
                return;
            }
        }
        this.activateGUIController(null);
    }

    public void onMouseDrag(MouseDragEvent event) {
        for (GUIController controller : this.guiController.values()) {
            if (controller.isColliding()) {
                this.activateGUIController(controller);
                controller.onMouseDrag(event);
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
