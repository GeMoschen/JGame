package de.gemo.engine.manager;

import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Mouse;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.events.mouse.MouseButton;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.units.Vector;

public class MouseManager {
    public static MouseManager INSTANCE = null;

    private final Engine engine;
    private HashMap<Integer, Boolean> pressedButtons = new HashMap<Integer, Boolean>();
    private HashSet<Integer> holdButtons = new HashSet<Integer>();

    private HashMap<Integer, Long> lastButtonPresses = new HashMap<Integer, Long>();

    private int currentX = 0, currentY = 0;
    private int correctedX = 0, correctedY = 0;
    private int dX = 0, dY = 0;

    private Hitbox hitBox, movedHitBox, tempHitBox;

    public static MouseManager getInstance(Engine engine) {
        if (INSTANCE == null) {
            return new MouseManager(engine);
        } else {
            throw new RuntimeException("ERROR: MouseManager is already created!");
        }
    }

    private MouseManager(Engine engine) {
        INSTANCE = this;
        this.engine = engine;
        this.holdButtons = new HashSet<Integer>();
        for (int index = 0; index < 20; index++) {
            pressedButtons.put(index, false);
        }

        // set the cursor
        Mouse.setCursorPosition(this.engine.getWindowWidth() / 2, this.engine.getWindowHeight() / 2);

        int x = (int) (this.engine.getWindowWidth() / 2f * this.engine.getWin2viewRatioX());
        int y = (int) (this.engine.getWindowHeight() / 2f * this.engine.getWin2viewRatioY());

        // move hitbox

        // build hitbox for mouse
        hitBox = new Hitbox(x, y);
        hitBox.addPoint(0, 0);
        hitBox.addPoint(1, 0);
        hitBox.addPoint(1, 1);
        hitBox.addPoint(0, 1);

        // build hitbox for mouse
        tempHitBox = new Hitbox(x, y);
        tempHitBox.addPoint(0, 0);
        tempHitBox.addPoint(1, 0);
        tempHitBox.addPoint(1, 1);
        tempHitBox.addPoint(0, 1);

        // build hitbox for mouse
        movedHitBox = new Hitbox(x, y);
        movedHitBox.addPoint(0, 0);
        movedHitBox.addPoint(1, 0);
        movedHitBox.addPoint(1, 1);
        movedHitBox.addPoint(0, 1);

        // set the cursor
        Mouse.setCursorPosition(x, y);
    }

    public void grabMouse() {
        // set the cursor
        Mouse.setCursorPosition(this.engine.getWindowWidth() / 2, this.engine.getWindowHeight() / 2);

        int x = (int) (this.engine.getWindowWidth() / 2f * this.engine.getWin2viewRatioX());
        int y = (int) (this.engine.getWindowHeight() / 2f * this.engine.getWin2viewRatioY());

        // move hitbox

        // build hitbox for mouse
        hitBox = new Hitbox(x, y);
        hitBox.addPoint(0, 0);
        hitBox.addPoint(1, 0);
        hitBox.addPoint(1, 1);
        hitBox.addPoint(0, 1);

        // build hitbox for mouse
        tempHitBox = new Hitbox(x, y);
        tempHitBox.addPoint(0, 0);
        tempHitBox.addPoint(1, 0);
        tempHitBox.addPoint(1, 1);
        tempHitBox.addPoint(0, 1);

        // build hitbox for mouse
        movedHitBox = new Hitbox(x, y);
        movedHitBox.addPoint(0, 0);
        movedHitBox.addPoint(1, 0);
        movedHitBox.addPoint(1, 1);
        movedHitBox.addPoint(0, 1);

        // set the cursor
        Mouse.setCursorPosition(x, y);

        // grab mouse
        Mouse.setGrabbed(true);

        org.lwjgl.input.Mouse.setClipMouseCoordinatesToWindow(false);
    }

    public void ungrabMouse() {
        Mouse.setGrabbed(false);
    }

    public void move(int x, int y) {
        movedHitBox.move(x, y);
    }

    public void update() {
        // catch MouseMovement. NOTE: This can only be done ONCE, that's why we do it here
        dX = Mouse.getDX();
        dY = -Mouse.getDY();

        correctedX = (int) (Mouse.getX() * this.engine.getWin2viewRatioX());
        correctedY = (int) ((engine.getWindowHeight() - Mouse.getY()) * this.engine.getWin2viewRatioY());

        float correctedDX = dX * this.engine.getWin2viewRatioX();
        float correctedDY = dY * this.engine.getWin2viewRatioY();

        // iterate over currently pressed buttons to handle dragged buttons
        if (!Mouse.isGrabbed()) {
            if (currentX != correctedX || currentY != correctedY) {
                // move hitbox
                this.tempHitBox.move(correctedDX, correctedDY);
            }

            for (int currentKey : this.holdButtons) {
                if (Mouse.isButtonDown(currentKey)) {
                    // hold button
                    engine.onMouseDrag(new MouseDragEvent(correctedX, correctedY, correctedDX, correctedDY, MouseButton.byID(currentKey)));
                }
            }
        }

        boolean currentState = false;
        boolean oldState;

        for (int index = 0; index < 5; index++) {
            currentState = Mouse.isButtonDown(index);
            oldState = holdButtons.contains(index);
            if (!currentState && oldState) {
                // throw MouseUpEvent
                engine.onMouseUp(new MouseReleaseEvent(correctedX, correctedY, MouseButton.byID(index)));
                holdButtons.remove(index);
            } else if (currentState && !oldState) {
                // throw MouseDownEvent
                Long last = lastButtonPresses.get(index);

                long time = System.currentTimeMillis();
                if (last == null) {
                    last = time - 300;
                }
                long difference = time - last;
                lastButtonPresses.put(index, time);
                boolean doubleclick = difference < 200;
                engine.onMouseDown(new MouseClickEvent(correctedX, correctedY, MouseButton.byID(index), doubleclick));
                holdButtons.add(index);
            }
            pressedButtons.put(index, currentState);
        }

        if (!Mouse.isGrabbed()) {
            if (currentX != correctedX || currentY != correctedY) {
                this.hitBox.move(correctedDX, correctedDY);
                this.movedHitBox.move(correctedDX, correctedDY);
                engine.onMouseMove(new MouseMoveEvent(correctedX, correctedY, correctedDX, correctedDY));
            }
            currentX = correctedX;
            currentY = correctedY;
        }
    }

    public Hitbox getHitBox() {
        return hitBox;
    }

    public Hitbox getTempHitBox() {
        return tempHitBox;
    }

    public Hitbox getMovedHitBox() {
        return movedHitBox;
    }

    public boolean isButtonDown(int button) {
        return pressedButtons.get(button);
    }

    public Vector getMouseVector() {
        return this.hitBox.getCenter();
    }

    public Vector getTempMouseVector() {
        return this.tempHitBox.getCenter();
    }
}
