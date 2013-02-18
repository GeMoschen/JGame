package de.gemo.engine.events.mouse;

public class MouseDragEvent extends AbstractMouseClickEvent {

    private final float difX, difY;
    private final boolean moved;

    public MouseDragEvent(int x, int y, float difX, float difY, MouseButton button) {
        super(x, y, button, 3);
        this.difX = difX;
        this.difY = difY;
        this.moved = (difX != 0 || difY != 0);
    }

    public float getDifX() {
        return difX;
    }

    public float getDifY() {
        return difY;
    }

    public boolean hasMoved() {
        return moved;
    }
}
