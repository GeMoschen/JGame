package de.gemo.game.events.mouse;

public abstract class AbstractMouseEvent {
    private final int x, y;
    private final int eventType;

    public AbstractMouseEvent(int x, int y, int eventType) {
        this.x = x;
        this.y = y;
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }

    public boolean isMouseMove() {
        return eventType == 0;
    }

    public boolean isMouseClick() {
        return eventType == 1;
    }

    public boolean isMouseRelease() {
        return eventType == 2;
    }

    public boolean isMouseDrag() {
        return eventType == 3;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
