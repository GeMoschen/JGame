package de.gemo.engine.events.mouse;

public class MouseReleaseEvent extends AbstractMouseClickEvent {

    public MouseReleaseEvent(int x, int y, MouseButton button) {
        super(x, y, button, 2);
    }
}
