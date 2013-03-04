package de.gemo.engine.events.mouse;

public class MouseWheelEvent extends AbstractMouseEvent {

    private final boolean up;

    public MouseWheelEvent(int x, int y, boolean up) {
        super(x, y, 4);
        this.up = up;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return !this.isUp();
    }
}
