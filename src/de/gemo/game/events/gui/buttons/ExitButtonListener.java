package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.FocusListener;

public class ExitButtonListener extends ButtonMoveListener implements FocusListener {

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
        System.out.println("mouse click");
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        System.out.println("mouse release");
        if (event.isLeftButton()) {
            System.exit(0);
        }
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
        // System.out.println("mouse move: " + event.getX() + " / " + event.getY());
    }

    @Override
    public void onFocusGained(GUIElement element) {
        // System.out.println("focus gained");
    }

    @Override
    public void onFocusLost(GUIElement element) {
        System.out.println("focus lost");
    }

    @Override
    public void onHoverBegin(GUIElement element) {
        System.out.println("hover begin");
    }

    @Override
    public void onHover(GUIElement element) {
        // System.out.println("hover");
    }

    @Override
    public void onHoverEnd(GUIElement element) {
        System.out.println("hover end!");
    }

}
