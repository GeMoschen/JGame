package de.gemo.engine.events.mouse;

public class MouseDownEvent extends AbstractMouseClickEvent {

    public MouseDownEvent(int x, int y, MouseButton button) {
        super(x, y, button, 1);
    }
}
