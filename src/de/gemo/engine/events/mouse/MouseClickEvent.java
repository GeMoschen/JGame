package de.gemo.engine.events.mouse;

public class MouseClickEvent extends AbstractMouseClickEvent {

    public MouseClickEvent(int x, int y, MouseButton button) {
        super(x, y, button, 1);
    }
}
