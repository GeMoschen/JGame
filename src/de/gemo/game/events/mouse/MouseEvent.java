package de.gemo.game.events.mouse;

public abstract class MouseEvent {
    private final int x, y;

    public MouseEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
