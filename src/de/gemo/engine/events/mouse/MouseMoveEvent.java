package de.gemo.engine.events.mouse;

public class MouseMoveEvent extends AbstractMouseEvent {

    private final int difX, difY;

    public MouseMoveEvent(int x, int y, int difX, int difY) {
        super(x, y, 0);
        this.difX = difX;
        this.difY = difY;
    }

    public int getDifX() {
        return difX;
    }

    public int getDifY() {
        return difY;
    }
}
