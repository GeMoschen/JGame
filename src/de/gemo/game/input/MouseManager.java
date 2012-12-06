package de.gemo.game.input;

import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Mouse;

import de.gemo.game.collision.ComplexHitbox;
import de.gemo.game.collision.Vector;
import de.gemo.game.core.Game;
import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseUpEvent;

public class MouseManager {
    private final Game game;
    public HashMap<Integer, Boolean> pressedButtons = new HashMap<Integer, Boolean>();
    private HashSet<Integer> holdButtons = new HashSet<Integer>();

    private int currentX = 0, currentY = 0;
    private int dX = 0, dY = 0;

    private final int dim = 1;

    private final ComplexHitbox hitBox, movedHitBox;

    public void blockMouseMovement() {
        int x = this.game.WIN_WIDTH / 2;
        int y = this.game.WIN_HEIGHT / 2;
        // set the cursor
        Mouse.setCursorPosition(x, y);

        this.hitBox.setCenter(x, y);
    }

    public MouseManager(Game game) {
        this.game = game;
        this.holdButtons = new HashSet<Integer>();
        for (int index = 0; index < 20; index++) {
            pressedButtons.put(index, false);
        }

        // build hitbox for mouse
        int x = this.game.WIN_WIDTH / 2;
        int y = this.game.WIN_HEIGHT / 2;
        hitBox = new ComplexHitbox(x, y);
        hitBox.addPoint(0, 0);
        hitBox.addPoint(dim, 0);
        hitBox.addPoint(dim, dim);
        hitBox.addPoint(0, dim);

        // build hitbox for mouse
        movedHitBox = new ComplexHitbox(x, y);
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

    public ComplexHitbox getMovedHitBox() {
        return movedHitBox;
    }

    public void update() {
        // catch MouseMovement. NOTE: This can only be done ONCE, that's why we do it here
        dX = Mouse.getDX();
        dY = -Mouse.getDY();

        // iterate over currently pressed buttons to handle dragged buttons
        for (int currentKey : this.holdButtons) {
            if (Mouse.isButtonDown(currentKey)) {
                // hold button
                game.onMouseDrag(new MouseDragEvent(Mouse.getX(), game.WIN_HEIGHT - Mouse.getY(), dX, dY, currentKey));
            }
        }

        boolean currentState = false;
        boolean oldState;

        for (int index = 0; index < 5; index++) {
            currentState = Mouse.isButtonDown(index);
            oldState = holdButtons.contains(index);
            if (!currentState && oldState) {
                // throw MouseUpEvent
                game.onMouseUp(new MouseUpEvent(Mouse.getX(), Mouse.getY(), index));
                holdButtons.remove(index);
            } else if (currentState && !oldState) {
                // throw MouseDownEvent
                game.onMouseDown(new MouseDownEvent(Mouse.getX(), Mouse.getY(), index));
                holdButtons.add(index);
            }
            pressedButtons.put(index, currentState);
        }

        if (currentX != Mouse.getX() || currentY != Mouse.getY()) {
            // move hitbox
            this.hitBox.move(dX, dY);
            this.movedHitBox.move(dX, dY);
            // throw MouseMoveEvent
            game.onMouseMove(new MouseMoveEvent(Mouse.getX(), game.WIN_HEIGHT - Mouse.getY(), dX, dY));
        }
        currentX = Mouse.getX();
        currentY = Mouse.getY();
    }

    public ComplexHitbox getHitBox() {
        return hitBox;
    }

    public boolean isButtonDown(int button) {
        return pressedButtons.get(button);
    }

    public Vector getMouseVector() {
        return this.hitBox.getCenter();
    }
}
