package de.gemo.gameengine.events.mouse;

public class MouseHoldEvent extends AbstractMouseClickEvent {

    public MouseHoldEvent(int x, int y, MouseButton button) {
        super(x, y, button, 5);
    }
}
