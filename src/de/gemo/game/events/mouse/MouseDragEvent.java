package de.gemo.game.events.mouse;

import de.gemo.game.events.gui.MouseButton;

public class MouseDragEvent extends AbstractMouseClickEvent {

    private final int difX, difY;
    private final boolean moved;

    public MouseDragEvent(int x, int y, int difX, int difY, MouseButton button) {
        super(x, y, button, 3);
        this.difX = difX;
        this.difY = difY;
        this.moved = (difX != 0 || difY != 0);
    }

    public int getDifX() {
        return difX;
    }

    public int getDifY() {
        return difY;
    }

    public boolean hasMoved() {
        return moved;
    }
}
