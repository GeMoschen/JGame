package de.gemo.game.events.mouse;

import de.gemo.game.events.gui.MouseButton;

public class MouseReleaseEvent extends AbstractMouseClickEvent {

    public MouseReleaseEvent(int x, int y, MouseButton button) {
        super(x, y, button, 2);
    }
}
