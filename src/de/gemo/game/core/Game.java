package de.gemo.game.core;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.entity.GUIButton;
import de.gemo.game.entity.GUIElementStatus;
import de.gemo.game.events.gui.ClickBeginEvent;
import de.gemo.game.events.gui.ClickReleaseEvent;
import de.gemo.game.events.gui.HoverBeginEvent;
import de.gemo.game.events.gui.HoverEndEvent;
import de.gemo.game.events.gui.HoverEvent;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.events.keyboard.KeyEvent;
import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseUpEvent;
import de.gemo.game.input.KeyboardManager;
import de.gemo.game.input.MouseManager;

public class Game {

    public static Game INSTANCE = null;

    public final int WIN_WIDTH = 1024;
    public final int WIN_HEIGHT = 768;

    private final int VIEW_WIDTH = 1024;
    private final int VIEW_HEIGHT = 768;

    private static int FPS = 0;
    private KeyboardManager keyManager;
    private MouseManager mouseManager;

    boolean freeMouse = false;

    public static TrueTypeFont font_10, font_12, font_14;

    private long lastFrame;
    private int delta;

    private boolean USE_VSYNC = true;
    private boolean HIDE_TEXT = false;

    ArrayList<GUIButton> buttonList = new ArrayList<GUIButton>();

    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private int getDelta() {
        long currentTime = this.getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        return delta;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (INSTANCE == null) {
            new Game();
        }
    }

    private void initOpenGL() {
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
    }

    public Game() {
        INSTANCE = this;
        try {
            Display.setDisplayMode(new DisplayMode(WIN_WIDTH, WIN_HEIGHT));
            Display.setTitle("JGame");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        // init OpenGL
        glMatrixMode(GL_PROJECTION);

        GL11.glLoadIdentity();
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        long startTime = System.currentTimeMillis() + 1000;

        keyManager = new KeyboardManager(this);
        mouseManager = new MouseManager(this);

        this.loadFonts();

        this.initOpenGL();

        int oldFPS = 0;
        int oldCount = 0;

        lastFrame = this.getTime();
        delta = getDelta();

        this.createGUI();

        int tickTime = 50;
        long startTimer = System.currentTimeMillis() + tickTime;
        boolean tick = true;

        while (!Display.isCloseRequested()) {
            delta = getDelta();
            FPS++;

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
                this.updateCollisions();
            }

            GL11.glPushMatrix();

            // TODO: render gamefield-content

            GL11.glPopMatrix();

            // RENDER GUI
            GL11.glPushMatrix();

            this.renderButtons();

            GL11.glEnable(GL11.GL_BLEND);
            font_14.drawString(10, 10, "FPS: " + oldFPS + (USE_VSYNC ? " (vsync)" : ""), Color.red);

            if (!HIDE_TEXT) {
                font_14.drawString(10, 25, "Delta: " + oldCount, Color.red);

                font_14.drawString(10, 50, "A/D: rotate Exit-Button", Color.magenta);
                font_14.drawString(10, 65, "Arrowkeys: move Exit-Button", Color.magenta);

                font_14.drawString(10, 90, "F1: toggle vysnc", Color.orange);
                font_14.drawString(10, 105, "F2: toggle text", Color.orange);
                font_14.drawString(10, 120, "F11: toggle graphics", Color.orange);
                font_14.drawString(10, 135, "F12: toggle hitboxes", Color.orange);
            }
            GL11.glDisable(GL11.GL_BLEND);

            // GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();

            // update and sync
            Display.update();

            if (USE_VSYNC) {
                Display.sync(60);
            }

            if (startTime < System.currentTimeMillis()) {
                startTime = System.currentTimeMillis() + 1000;
                oldFPS = FPS;
                oldCount = delta;
                FPS = 0;
            }

            tick = false;
        }

        Display.destroy();
        System.exit(0);
    }

    private void createGUI() {
        Texture buttonTexture;

        try {
            buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("test.jpg"));

            GUIButton button = new GUIButton(50 + 32, this.WIN_HEIGHT - 32, 128, 32, buttonTexture);
            button.setZ(-3);
            button.setLabel("Button 1");
            button.setColor(Color.orange);
            button.setAlpha(0.1f);
            buttonList.add(button);

            buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("test.jpg"));
            button = new GUIButton(180 + 32, this.WIN_HEIGHT - 32, 128, 32, buttonTexture);
            button.setZ(-3);
            button.setLabel("Button 2");
            button.setColor(Color.orange);
            button.setAlpha(0.75f);
            buttonList.add(button);

            button = new GUIButton(310 + 32, this.WIN_HEIGHT - 32, 128, 32, buttonTexture);
            button.setZ(-3);
            button.setLabel("Button 3");
            button.setColor(Color.orange);
            button.setAlpha(1f);
            buttonList.add(button);

            button = new GUIButton(this.WIN_WIDTH - 80, this.WIN_HEIGHT - 32, 128, 32, buttonTexture);
            button.setZ(-4);
            button.setLabel("Exit");
            button.setColor(Color.orange);
            button.setActionListener(new ExitButtonListener());
            button.setAlpha(0.25f);
            buttonList.add(button);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void renderButtons() {
        for (GUIButton button : this.buttonList) {
            Renderer.render(button);
        }
    }

    private void loadFonts() {
        Font arialFont = new Font("Verdana", Font.BOLD, 10);
        Game.font_10 = new TrueTypeFont(arialFont, true);

        arialFont = new Font("Verdana", Font.BOLD, 12);
        Game.font_12 = new TrueTypeFont(arialFont, true);

        arialFont = new Font("Verdana", Font.BOLD, 14);
        Game.font_14 = new TrueTypeFont(arialFont, true);
    }

    public void rotate(float angle) {
        this.buttonList.get(3).rotate(angle * delta);
    }

    public void moveRight() {
        this.buttonList.get(3).move(0.25f * delta, 0);
    }

    public void moveLeft() {
        this.buttonList.get(3).move(-0.25f * delta, 0);
    }

    public void moveUp() {
        this.buttonList.get(3).move(0, -0.25f * delta);
    }

    public void moveDown() {
        this.buttonList.get(3).move(0, 0.25f * delta);
    }

    // ////////////////////////////////////////
    //
    // KEYBOARD EVENTS
    //
    // ////////////////////////////////////////

    public void onKeyPressed(KeyEvent event) {

    }

    public void onKeyHold(KeyEvent event) {
        switch (event.getKey()) {
            case Keyboard.KEY_A : {
                rotate(-0.1f);
                break;
            }
            case Keyboard.KEY_D : {
                rotate(0.1f);
                break;
            }
            case Keyboard.KEY_LEFT : {
                moveLeft();
                break;
            }
            case Keyboard.KEY_RIGHT : {
                moveRight();
                break;
            }
            case Keyboard.KEY_UP : {
                moveUp();
                break;
            }
            case Keyboard.KEY_DOWN : {
                moveDown();
                this.mouseManager.move(0, -20);
                break;
            }
        }
    }

    public void onKeyReleased(KeyEvent event) {
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

    }

    public void updateCollisions() {
        boolean isColliding = false;
        for (GUIButton button : this.buttonList) {
            isColliding = CollisionHelper.isVectorInHitbox(mouseManager.getMouseVector(), button.getClickBox());
            if (isColliding && !button.getStatus().equals(GUIElementStatus.ACTIVE)) {
                if (button.getStatus().equals(GUIElementStatus.HOVERING)) {
                    button.fireEvent(new HoverEvent(button));
                } else {
                    button.fireEvent(new HoverBeginEvent(button));
                    button.setStatus(GUIElementStatus.HOVERING);
                }
            }
            if (!isColliding && !button.getStatus().equals(GUIElementStatus.NONE)) {
                button.fireEvent(new HoverEndEvent(button));
                button.setStatus(GUIElementStatus.NONE);
            }
        }
    }

    public void onMouseDown(MouseDownEvent event) {
        if (!freeMouse) {
            freeMouse = !freeMouse;
        }
        int index = 0;
        boolean collide = false;
        for (GUIButton button : this.buttonList) {
            if (CollisionHelper.isVectorInHitbox(mouseManager.getMouseVector(), button.getClickBox())) {
                button.setStatus(GUIElementStatus.ACTIVE);
                button.fireEvent(new ClickBeginEvent(button));
            } else {
                button.setStatus(GUIElementStatus.NONE);
            }
        }

        if (collide) {
            System.out.println("colliding with #" + index);
        }
    }

    public void onMouseUp(MouseUpEvent event) {
        for (GUIButton button : this.buttonList) {
            if (CollisionHelper.isVectorInHitbox(mouseManager.getMouseVector(), button.getClickBox())) {
                if (button.getStatus().equals(GUIElementStatus.ACTIVE)) {
                    button.fireEvent(new ClickReleaseEvent(button));
                }
                button.setStatus(GUIElementStatus.HOVERING);
            } else {
                button.setStatus(GUIElementStatus.NONE);
            }
        }
    }

    public void onMouseDrag(MouseDragEvent event) {
    }

}
