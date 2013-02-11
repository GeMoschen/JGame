package de.gemo.game.events.mouse;

import de.gemo.game.events.gui.MouseButton;

public class MouseDownEvent extends AbstractMouseClickEvent {

    public MouseDownEvent(int x, int y, MouseButton button) {
        super(x, y, button, 1);
    }
}
