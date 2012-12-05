package de.gemo.game.events.mouse;

public class MouseDragEvent extends MouseEvent {

    private final int button;
    private final int difX, difY;
    private final boolean moved;

    public MouseDragEvent(int x, int y, int difX, int difY, int button) {
        super(x, y);
        this.difX = difX;
        this.difY = difY;
        this.button = button;
        this.moved = (difX != 0 || difY != 0);
    }

    public int getButton() {
        return this.button;
    }

    public boolean isLeftButton() {
        return this.button == 0;
    }

    public boolean isRightButton() {
        return this.button == 1;
    }

    public boolean isMiddleButton() {
        return this.button == 2;
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
