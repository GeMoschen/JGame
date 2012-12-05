package de.gemo.game.events.mouse;

public class MouseUpEvent extends MouseEvent {

    private final int button;

    public MouseUpEvent(int x, int y, int button) {
        super(x, y);
        this.button = button;
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
}
