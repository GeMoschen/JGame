package de.gemo.engine.inputmanager;

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
    private final Engine engine;
    public HashMap<Integer, Boolean> pressedButtons = new HashMap<Integer, Boolean>();
    private HashSet<Integer> holdButtons = new HashSet<Integer>();

    private int currentX = 0, currentY = 0;
    private int dX = 0, dY = 0;

    private final int dim = 1;

    private final Hitbox hitBox, movedHitBox;

    public void grabMouse() {
        // set the cursor
        Mouse.setCursorPosition(this.engine.WIN_WIDTH / 2, this.engine.WIN_HEIGHT / 2);

        int x = (int) (this.engine.WIN_WIDTH / 2f * this.engine.ratioX);
        int y = (int) (this.engine.WIN_HEIGHT / 2f * this.engine.ratioY);

        // move hitbox
        this.hitBox.setCenter(x, y);

        // grab mouse
        Mouse.setGrabbed(true);
    }

    public void ungrabMouse() {
        Mouse.setGrabbed(false);
    }

    public MouseManager(Engine engine) {
        this.engine = engine;
        this.holdButtons = new HashSet<Integer>();
        for (int index = 0; index < 20; index++) {
            pressedButtons.put(index, false);
        }

        // set the cursor
        Mouse.setCursorPosition(this.engine.WIN_WIDTH / 2, this.engine.WIN_HEIGHT / 2);

        int x = (int) (this.engine.WIN_WIDTH / 2f * this.engine.ratioX);
        int y = (int) (this.engine.WIN_HEIGHT / 2f * this.engine.ratioY);

        // move hitbox

        // build hitbox for mouse
        // int x = this.engine.getWindowWidth() / 2;
        // int y = this.engine.getWindowHeight() / 2;
        hitBox = new Hitbox(x, y);
        hitBox.addPoint(0, 0);
        hitBox.addPoint(dim, 0);
        hitBox.addPoint(dim, dim);
        hitBox.addPoint(0, dim);

        // build hitbox for mouse
        movedHitBox = new Hitbox(x, y);
        movedHitBox.addPoint(0, 0);
        movedHitBox.addPoint(dim, 0);
        movedHitBox.addPoint(dim, dim);
        movedHitBox.addPoint(0, dim);

        // set the cursor
        Mouse.setCursorPosition(x, y);
    }

    public void move(int x, int y) {
        movedHitBox.move(x, y);
    }

    public Hitbox getMovedHitBox() {
        return movedHitBox;
    }

    public void update() {
        // catch MouseMovement. NOTE: This can only be done ONCE, that's why we do it here
        dX = Mouse.getDX();
        dY = -Mouse.getDY();

        int correctedX = (int) (Mouse.getX() * this.engine.ratioX);
        int correctedY = (int) ((engine.getWindowHeight() - Mouse.getY()) * this.engine.ratioY);

        // iterate over currently pressed buttons to handle dragged buttons
        if (!Mouse.isGrabbed()) {
            for (int currentKey : this.holdButtons) {
                if (Mouse.isButtonDown(currentKey)) {
                    // hold button
                    engine.onMouseDrag(new MouseDragEvent(correctedX, correctedY, dX, dY, MouseButton.byID(currentKey)));
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

                engine.onMouseDown(new MouseClickEvent(correctedX, correctedY, MouseButton.byID(index)));
                holdButtons.add(index);
            }
            pressedButtons.put(index, currentState);
        }

        if (!Mouse.isGrabbed()) {
            if (currentX != correctedX || currentY != correctedY) {
                // move hitbox
                float correctedDX = dX * this.engine.ratioX;
                float correctedDY = dY * this.engine.ratioY;

                this.hitBox.move(correctedDX, correctedDY);
                this.movedHitBox.move(correctedDX, correctedDY);
                // throw MouseMoveEvent
                engine.onMouseMove(new MouseMoveEvent(correctedX, correctedY, dX, dY));
            }
            currentX = correctedX;
            currentY = correctedY;
        }
    }
    public Hitbox getHitBox() {
        return hitBox;
    }

    public boolean isButtonDown(int button) {
        return pressedButtons.get(button);
    }

    public Vector getMouseVector() {
        return this.hitBox.getCenter();
    }
}
