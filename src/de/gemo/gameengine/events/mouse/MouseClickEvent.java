package de.gemo.gameengine.events.mouse;

public class MouseClickEvent extends AbstractMouseClickEvent {

    private final boolean doubleClick;

    public MouseClickEvent(int x, int y, MouseButton button, boolean doubleClick) {
        super(x, y, button, 1);
        this.doubleClick = doubleClick;
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }
}
